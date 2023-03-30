package org.bluejplugin.actions;

import org.bluejplugin.Actions;
import org.bluejplugin.Comment;
import org.bluejplugin.Variable;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * In this class we look for camelcase errors and add the necessary comments
 *
 * @author Tim Hermans, Raf Marcoen
 * @version 1.0 (22/05/2013)
 */
public class CamelCaseAction extends Action
{

    /**
     * Constructor for objects of class CamelCaseAction
     *
     * @param code Source code of the class
     * @param maxPoints Maximum points for this action
     */
    public CamelCaseAction(String code, int maxPoints)
    {
        super(code, maxPoints);
    }

    /**
     * This method detects CamelCase for the user variables
     *
     * @param actions Actions object
     */
    @Override
    public void performAction(Actions actions)
    {
        String UPPER_CASE = "([A-Z])";
        Pattern pattern = Pattern.compile(UPPER_CASE);
        ArrayList<Variable> variableList = actions.getVariableList();
        int errors = 0;

        for (Variable var : variableList)
        {
            String parameter = var.getVariable();

            // Name should contain at least one upper case letter and should not be longer than 7 characters
            if (parameter.length() > 7 && !var.getLongName())
            {
                Matcher matcher = pattern.matcher(parameter);
                if (!matcher.find())
                {
                    int line = var.getLocation().getLine() + 1;
                    String text =
                            "De variabele '" + parameter + "' is langer dan 7 letters en heeft geen hoofdletters. Het gebruik van camelCase maakt je variabelen en methoden beter leesbaar. (regel " + line + ")";
                    actions.addComment(new Comment(text, var.getLocation()));
                    errors++;
                }
            }
        }

        int varCount = variableList.size();
        if (varCount == 0)
        {
            // no variables
            points = 0;
            inapplicable = true;
        } else
        {
            // variables exist
            points = (int) (maxPoints * (1.0 - ((double) errors * 3) / ((double) varCount)));
        }
    }
}
