package org.bluejplugin.actions;

import bluej.extensions2.editor.JavaEditor;
import bluej.extensions2.editor.TextLocation;
import org.bluejplugin.Actions;
import org.bluejplugin.Comment;
import org.bluejplugin.Enums;
import org.bluejplugin.Variable;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * In this class we look for setter errors and add the necessary comments
 *
 * @author Tim Hermans, Raf Marcoen
 * @version 1.0 (12/02/2013)
 */
public class SetAction extends Action
{
    private static final TextLocation NULL_LOCATION = new TextLocation(0, 0);

    private static final String VARIABLE = "([a-zA-Z])+\\s*\\x5B?\\s*\\x5D?";
    private static final String PARAMETER = "([a-zA-Z$_][a-zA-Z0-9$_]*)+\\s*(\\x5B\\s*\\x5D)?";
    private static final String ONE_OR_MORE_WHITESPACES = "\\s+";
    private static final String ZERO_OR_MORE_WHITESPACES = "\\s*";

    private String curCode;

    private int lastSetter = 0;
    private ArrayList<Variable> variableList;

    /**
     * Constructor for objects of class SetAction.
     *
     * @param code      Source code of the class
     * @param maxPoints Maximum number of points for this action
     */
    public SetAction(String code, int maxPoints)
    {
        super(code, maxPoints);
    }

    /**
     * This method detects the setters for the user variables.
     *
     * @param actions The Actions object
     */
    @Override
    public void performAction(Actions actions)
    {
        variableList = actions.getVariableList();
        JavaEditor curEditor = actions.getEditor();
        TextLocation endLocation = curEditor.getTextLocationFromOffset(curEditor.getTextLength());
        curCode = curEditor.getText(NULL_LOCATION, endLocation);

        int errors = 0;
        for (int i = lastSetter; i < variableList.size(); i++)
        {
            Variable var = variableList.get(i);
            if ((!var.getNoSetter()) && ((var.getPpp() != Enums.PPPType.Public)))
            {
                if (!isThereASetter(i))
                {
                    errors++;
                    int line = variableList.get(i).getLocation().getLine() + 1;
                    String comment = "Variable " + variableList.get(i).getVariable() + " has no setter, (line " + line + ").\n";
                    actions.addComment(new Comment(comment, variableList.get(i).getLocation()));
                    lastSetter = i + 1;
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


    /**
     * This method checks if the current object is added or not.
     *
     * @param i Integer number indicating the number of the current object
     */
    public boolean isThereASetter(int i)
    {
        String reg = "public" + ONE_OR_MORE_WHITESPACES + "void" + ONE_OR_MORE_WHITESPACES + "set" +
                variableList.get(i).getVariableC() + ZERO_OR_MORE_WHITESPACES + "[(]" + ZERO_OR_MORE_WHITESPACES +
                VARIABLE + ONE_OR_MORE_WHITESPACES + PARAMETER + ZERO_OR_MORE_WHITESPACES + "[)]" +
                ZERO_OR_MORE_WHITESPACES + "\\x7B";
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(curCode);

        return matcher.find();
    }
}
