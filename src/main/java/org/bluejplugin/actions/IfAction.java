package org.bluejplugin.actions;

import bluej.extensions2.editor.JavaEditor;
import bluej.extensions2.editor.TextLocation;
import org.bluejplugin.Actions;
import org.bluejplugin.Comment;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * In this class we look for getter/setter errors and add the necessary comments
 *
 * @author Tim Hermans, Raf Marcoen
 * @version 1.0 (08/05/2013)
 */
public class IfAction extends Action
{
    private static final TextLocation NULL_LOCATION = new TextLocation(0, 0);
    private static final String ZERO_OR_MORE_WHITESPACES = "\\s*"; // zero or more spaces
    private static final String VARIABLE = "([a-zA-Z$_][a-zA-Z0-9$_]*)";
    private static final String waarde = "([a-zA-Z0-9]+)";
    private static final String TRUE_FALSE = "(true|false)";

    private final String SINGLE_EQUAL, VAR_EQUALS_BOOL, BOOL_EQUALS_VAR;

    /**
     * Constructor for objects of class GetSetAction.
     *
     * @param code      Source code of the class
     * @param maxPoints The maximum amount of points that can be earned with this action
     */
    public IfAction(String code, int maxPoints)
    {
        super(code, maxPoints);

        // Check if "=" is used instead of "=="
        this.SINGLE_EQUAL = "if" + ZERO_OR_MORE_WHITESPACES + "[(]" + ZERO_OR_MORE_WHITESPACES + VARIABLE + ZERO_OR_MORE_WHITESPACES + "[=]" + ZERO_OR_MORE_WHITESPACES + waarde + ZERO_OR_MORE_WHITESPACES + "[)]";

        // Check if "== true|false" is used
        this.VAR_EQUALS_BOOL = "if" + ZERO_OR_MORE_WHITESPACES + "[(]" + ZERO_OR_MORE_WHITESPACES + VARIABLE + ZERO_OR_MORE_WHITESPACES + "==" + ZERO_OR_MORE_WHITESPACES + TRUE_FALSE + ZERO_OR_MORE_WHITESPACES + "[)]";
        this.BOOL_EQUALS_VAR = "if" + ZERO_OR_MORE_WHITESPACES + "[(]" + ZERO_OR_MORE_WHITESPACES + TRUE_FALSE + ZERO_OR_MORE_WHITESPACES + "==" + ZERO_OR_MORE_WHITESPACES + VARIABLE + ZERO_OR_MORE_WHITESPACES + "[)]";
    }

    /**
     * This method detects incorrect if statements.
     *
     * @param actions The Actions object
     */
    @Override
    public void performAction(Actions actions)
    {
        JavaEditor curEditor = actions.getEditor();
        Pattern p1 = Pattern.compile(SINGLE_EQUAL);
        Pattern p2 = Pattern.compile(VAR_EQUALS_BOOL);
        Pattern p3 = Pattern.compile(BOOL_EQUALS_VAR);
        TextLocation endLocation = curEditor.getTextLocationFromOffset(curEditor.getTextLength());
        String curCode = curEditor.getText(NULL_LOCATION, endLocation);
        Matcher m1 = p1.matcher(curCode);
        Matcher m2 = p2.matcher(curCode);
        Matcher m3 = p3.matcher(curCode);

        int errors = 0;

        try
        {
            while (m1.find())
            {
                // The index in the code where the match started
                int startOffset = m1.start();
                TextLocation startLoc = curEditor.getTextLocationFromOffset(startOffset);
                int comment = startLoc.getLine() + 1;

                actions.addComment(new Comment("Een \"=\" heeft niet dezelfde functie als een \"==\", op regel " + comment, startLoc));
                errors++;
            }
            while (m2.find())
            {
                // The index in the code where the match started
                int startOffset = m2.start();
                TextLocation startLoc = curEditor.getTextLocationFromOffset(startOffset);
                int comment = startLoc.getLine() + 1;

                actions.addComment(new Comment("Het is niet nodig om in een if statement \"== true\" of \"== false\" te schrijven, op regel " + comment, startLoc));
                errors++;
            }
            while (m3.find())
            {
                // The index in the code where the match started
                int startOffset = m3.start();
                TextLocation startLoc = curEditor.getTextLocationFromOffset(startOffset);
                int comment = startLoc.getLine() + 1;

                actions.addComment(new Comment("Het is niet nodig om in een if statement \"true ==\" of \"false ==\" te schrijven, op regel " + comment, startLoc));
                errors++;
            }
        } catch (Exception e)
        {
            System.out.println("Error in IfAction: " + e.getMessage());
        }

        if (errors > 5)
        {
            errors = 5;
        }
        points = (int) (maxPoints - ((double) errors) * maxPoints / 5.0);
    }
}
