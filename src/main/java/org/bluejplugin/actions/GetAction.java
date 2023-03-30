package org.bluejplugin.actions;

import bluej.extensions2.editor.JavaEditor;
import bluej.extensions2.editor.TextLocation;
import org.bluejplugin.Actions;
import org.bluejplugin.Comment;
import org.bluejplugin.Variable;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * In this class we look for getter errors and add the necessary comments
 *
 * @author Tim Hermans, Raf Marcoen
 * @version 1.0 (12/02/2013)
 */
public class GetAction extends Action
{
    private static final TextLocation NULLOCATION = new TextLocation(0, 0);

    private static final String RETURN_TYPE = "([a-zA-Z])+\\s*\\x5B?\\s*\\x5D?"; // e.g. int, String, int[], String[]
    private static final String ONE_OR_MORE_WHITESPACES = "\\s+"; // one or more white space
    private static final String ZERO_OR_MORE_WHITESPACES = "\\s*"; // zero or more white space

    private String curCode;
    private int lastGetter = 0;
    private ArrayList<Variable> variableList;

    /**
     * Constructor for objects of class GetAction.
     *
     * @param code Source code of the class
     * @param maxPoints Maximum number of points for this action
     */
    public GetAction(String code, int maxPoints)
    {
        super(code, maxPoints);
    }

    /**
     * This method detects the getters for the user variables.
     *
     * @param actions The Actions object
     */
    @Override
    public void performAction(Actions actions)
    {
        variableList = actions.getVariableList();
        JavaEditor curEditor = actions.getEditor();
        TextLocation endLocation = curEditor.getTextLocationFromOffset(curEditor.getTextLength());
        curCode = curEditor.getText(NULLOCATION, endLocation);

        int errors = 0;
        for (int i = lastGetter; i < variableList.size(); i++)
        {
            Variable var = variableList.get(i);
            if (!var.getNoGetter() && !isThereAGetter(i))
            {
                errors++;
                int line = variableList.get(i).getLocation().getLine() + 1;
                String comment = "Voor variabele " + variableList.get(i).getVariable() + " is er geen getter, op regel " + line + ".\n";
                actions.addComment(new Comment(comment, variableList.get(i).getLocation()));
                lastGetter = i + 1;
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
            points = (int) (maxPoints * (1.0 - ((double) errors) / ((double) varCount)));
        }
    }

    /**
     * This method checks if the current object is added or not.
     *
     * @param i Integer number indicating the number of the current object
     */
    public boolean isThereAGetter(int i)
    {
        // check if getter already exist
        // getVar or isVar
        String reg = "public" + ONE_OR_MORE_WHITESPACES + RETURN_TYPE + ONE_OR_MORE_WHITESPACES + "(get|is)" +
                variableList.get(i).getVariableC() + ZERO_OR_MORE_WHITESPACES +
                "[(]" + ZERO_OR_MORE_WHITESPACES + "[)]" + ZERO_OR_MORE_WHITESPACES + "\\x7B";
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(curCode);

        return matcher.find();
    }
}

