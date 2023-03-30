package org.bluejplugin.actions;

import bluej.extensions2.editor.JavaEditor;
import bluej.extensions2.editor.TextLocation;
import org.bluejplugin.Actions;
import org.bluejplugin.Comment;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * In this class we look for .equals errors and add the necessary comments
 *
 * @author Tim Hermans, Raf Marcoen
 * @version 1.0 (10/05/2013)
 */
public class EqualAction extends Action
{
    private static final String WHITESPACE = "\\s*"; // 0 or more whitespace
    private static final String PARAMETER = "([a-zA-Z$_][a-zA-Z0-9$_]*)"; // any variable name
    private static final String STRING_WITH_QUOTES = "(\\x22[^\\x22]*\\x22)"; // "string"
    private final TextLocation NULL_LOCATION = new TextLocation(0, 0);

    /**
     * Constructor for this class
     *
     * @param code Source code of the class
     * @param maxPoints The maximum amount of points for this action
     */
    public EqualAction(String code, int maxPoints)
    {
        super(code, maxPoints);
    }

    /**
     * Call method to perform a search-action for the ".equals" type of error
     *
     * @param actions The actions object
     */
    @Override
    public void performAction(Actions actions)
    {
        // Set current editor
        JavaEditor curEditor = actions.getEditor();

        // End of the code
        TextLocation endLoc = curEditor.getTextLocationFromOffset(curEditor.getTextLength());

        // Get whole source file
        String curCode = curEditor.getText(NULL_LOCATION, endLoc);

        // if ("string" == parameter) or if (parameter == "string")
        String stringFirst = "if" + WHITESPACE + "[(]" + WHITESPACE + STRING_WITH_QUOTES + WHITESPACE + "[=]*" + WHITESPACE + PARAMETER + WHITESPACE + "[)]";
        String stringSecond = "if" + WHITESPACE + "[(]" + WHITESPACE + PARAMETER + WHITESPACE + "[=]*" + WHITESPACE + STRING_WITH_QUOTES + WHITESPACE + "[)]";

        // Compile the regular expression
        Pattern pattern = Pattern.compile(stringFirst + "|" + stringSecond);

        // Match with code
        Matcher matcher = pattern.matcher(curCode);
        boolean matchFound = matcher.find();
        int errors = 0;

        try
        {
            while (matchFound)
            {
                // The index in the code where the match started
                int startOffset = matcher.start();
                TextLocation startLoc = curEditor.getTextLocationFromOffset(startOffset);

                // Creating the comment
                addComment(actions, startLoc);
                errors++;

                // Find next match
                matchFound = matcher.find();
            }
        } catch (Exception e)
        {
            System.out.println("Error in EqualAction: " + e.getMessage());
        }

        if (errors > 5)
        {
            errors = 5;
        }
        points = (int) (maxPoints - ((double) errors) * maxPoints / 5.0);
    }

    /**
     * Function to add a suggestion to the comment list
     *
     * @param actions The actions object
     * @param location The location of the error
     */
    private void addComment(Actions actions, TextLocation location)
    {
        int position = location.getLine() + 1;
        String tekst = "Je moet de functie \".equals(...)\" gebruiken om strings te vergelijken, op regel " + position;
        actions.addComment(new Comment(tekst, location));
    }
}

