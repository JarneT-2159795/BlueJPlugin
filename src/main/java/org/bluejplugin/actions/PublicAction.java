package org.bluejplugin.actions;

import org.bluejplugin.Actions;
import org.bluejplugin.Comment;
import org.bluejplugin.Enums;
import org.bluejplugin.Variable;

import java.util.ArrayList;

/**
 * In this class we look for public variable errors and add the necessary comments
 *
 * @author Tim Hermans, Raf Marcoen
 * @version 1.0 (08/05/2013)
 */
public class PublicAction extends Action
{
    /**
     * Constructor for objects of class GetSetAction.
     *
     * @param code      Source code of the class
     * @param maxPoints Maximum points for this action
     */
    public PublicAction(String code, int maxPoints)
    {
        super(code, maxPoints);
    }

    /**
     * This method detects if the user variables are public.
     *
     * @param actions Actions object
     */
    @Override
    public void performAction(Actions actions)
    {
        ArrayList<Variable> variableList = actions.getVariableList();
        int errors = 0;

        for (Variable variable : variableList)
        {
            if (!variable.getPublicVariable())
            {
                if (variable.getPpp() == Enums.PPPType.Public)
                {
                    int line = variable.getLocation().getLine() + 1;
                    String comment =
                            "\"public\" variabelen zijn niet afgeschermd tegen manipulatie vanuit andere klassen, op regel " + line;
                    actions.addComment(new Comment(comment, variable.getLocation()));
                    errors++;
                }
            }
        }

        int varCount = variableList.size();
        if (varCount == 0)
        {
            // No variables
            points = 0;
            inapplicable = true;
        } else
        {
            // Variables exist
            points = (int) (maxPoints * (1.0 - ((double) errors) / ((double) varCount)));
        }
    }
}
