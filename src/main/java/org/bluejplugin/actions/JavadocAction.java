package org.bluejplugin.actions;

import bluej.extensions2.editor.JavaEditor;
import bluej.extensions2.editor.TextLocation;
import org.bluejplugin.Actions;
import org.bluejplugin.Comment;
import org.bluejplugin.Javadoc;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * In this class we look for javadoc errors and add the necessary comments
 * to the javadocComments datamember.
 *
 * @author Tim Hermans, Raf Marcoen
 * @version 1.2 (05/09/2013)
 */
public class JavadocAction extends Action
{
    // instance variables
    private static final int CLASS = 0;
    private static final int CONSTRUCTOR = 1;
    private static final int METHOD = 2;
    private static final String ARRAY_VAR = "[a-zA-Z]+\\s*\\x5B*\\s*\\x5D*"; // e.g. int[]
    private static final String ARRAYLIST_VAR = "ArrayList<([a-zA-Z]+)>"; // e.g. ArrayList<int>
    private static final String VARIABLE = "([a-zA-Z]+[0-9]*)+\\s*(\\x5B\\s*\\x5D)?";
    private static final String ACCESS_TYPE = "(public|private|protected)";
    private static final String ONE_OR_MORE_WHITESPACES = "\\s+";
    private static final String ZERO_OR_MORE_WHITESPACES = "\\s*";
    private static final String COMMA = "(\\s*[,]{1}\\s*)*";

    private static final TextLocation NULL_LOCATION = new TextLocation(0, 0);
    private static final String START_DOC = "\\x2F\\x2A\\x2A"; // /**
    private static final String END_DOC = "\\x2A\\x2F"; // */
    private JavaEditor curEditor;
    private Javadoc javadoc;
    private String match;
    private String[] reg;
    private String foundJavadoc = "";
    private ArrayList<String> parameter;
    private String returnType;
    private Pattern commentEnd;
    private Pattern commentStart;


    /**
     * Constructor for objects of class EvaluationAction
     *
     * @param code Source code of the class
     * @param maxPoints Maximum points for this action
     */
    public JavadocAction(String code, int maxPoints)
    {
        super(code, maxPoints);
        init();
    }

    /**
     * This methode will initialize some data members.
     * This method has to be called after making an object of this class.
     */
    public final void init()
    {
        reg = new String[3];
        reg[CLASS] = ACCESS_TYPE + ONE_OR_MORE_WHITESPACES + "class" + ONE_OR_MORE_WHITESPACES + VARIABLE + "([a-zA-Z0-9]\\x2C\\s)*" + "[{]";
        reg[CONSTRUCTOR] = ACCESS_TYPE + ONE_OR_MORE_WHITESPACES + VARIABLE + ZERO_OR_MORE_WHITESPACES + "[(]" +
                ZERO_OR_MORE_WHITESPACES + "(" + "(" + ARRAY_VAR + "|" + ARRAYLIST_VAR + ")" + ONE_OR_MORE_WHITESPACES + VARIABLE + ZERO_OR_MORE_WHITESPACES + COMMA +
                ")*" + "[)]" + ZERO_OR_MORE_WHITESPACES + "[{]";
        reg[METHOD] = ACCESS_TYPE + ONE_OR_MORE_WHITESPACES + "((static|final|virtual)\\s+)*" + ZERO_OR_MORE_WHITESPACES + "(" + ARRAY_VAR + "|" + ARRAYLIST_VAR + ")" + ONE_OR_MORE_WHITESPACES +
                VARIABLE + ZERO_OR_MORE_WHITESPACES + "[(]" + ZERO_OR_MORE_WHITESPACES + "(" + "(" + ARRAY_VAR + "|" + ARRAYLIST_VAR + ")" + ONE_OR_MORE_WHITESPACES + VARIABLE + ZERO_OR_MORE_WHITESPACES + COMMA + ")*" + "[)]" + ZERO_OR_MORE_WHITESPACES + "[{]";
        match = "No match found";

        commentStart = Pattern.compile(START_DOC);
        commentEnd = Pattern.compile(END_DOC);
    }

    /**
     * Method that performs the action
     *
     * @param actions The Actions object
     */
    @Override
    public void performAction(Actions actions)
    {
        curEditor = actions.getEditor();

        // Code ends at the end of the file
        TextLocation endLoc = curEditor.getTextLocationFromOffset(curEditor.getTextLength());
        // Get all the code
        String curCode = curEditor.getText(NULL_LOCATION, endLoc);

        int expectedDocCount = 0;
        int errors = 0;

        // For each javadoc type: class, constructor, method
        for (int i = 0; i < 3; i++)
        {
            // Compile the regular expression
            Pattern p = Pattern.compile(reg[i]);

            // Match with code
            Matcher m = p.matcher(curCode);
            boolean matched = m.find();
            parameter = new ArrayList<>();
            returnType = null;

            // While a match was found
            while (matched)
            {
                // Find next match
                try
                {
                    // Group the match
                    this.match = m.group();
                    expectedDocCount++;

                    // Create javadoc with the right startLocation
                    // Comment right above the problem
                    TextLocation startLocation = curEditor.getTextLocationFromOffset(m.start());

                    // Comment at the end of the file
                    TextLocation loc = new TextLocation(curEditor.getLineCount() - 1, 0);
                    javadoc = new Javadoc(loc, startLocation, i);

                    //Find the javadoc
                    find(javadoc.getOriginalLocation());
                    parameter.clear();
                    returnType = null;

                    // Create separate tokens
                    String[] token = this.match.split("(\\s+(static|final|//s+)*)|\\s*[(]\\s*|\\s*[)]\\s*|\\s*[,]\\s*|\\s*[{]\\s*|\\s+");
                    validateTokens(i, token);

                    // Create comment if not all parameters are present, or if there is no javadoc
                    if (!checkIfParamsArePresent(i))
                    {
                        errors++;
                        actions.addComment(new Comment(javadoc.getJavadoc(), javadoc.getOriginalLocation()));
                    } else if (foundJavadoc.equals(""))
                    {
                        errors++;
                        actions.addComment(new Comment(javadoc.getJavadoc(), javadoc.getOriginalLocation()));
                    }
                    matched = m.find();
                } catch (Exception e)
                {
                    matched = false;
                    System.out.println("Error in JavadocAction: " + e.getMessage());
                }
            }
        }

        if (expectedDocCount == 0)
        {
            // No javadoc necessary
            points = 0;
            inapplicable = true;
        } else
        {
            // Javadoc must be written
            points = (int) (maxPoints * (1.0 - ((double) errors) / ((double) expectedDocCount)));
        }
    }

    /**
     * This method looks for javadocs, and puts it in 'foundJavadoc'
     *
     * @param startLoc The location where the javadoc should start
     */
    private void find(TextLocation startLoc)
    {
        TextLocation prevLineStart = new TextLocation(startLoc.getLine() - 2, 0);
        TextLocation prevLineEnd = new TextLocation(startLoc.getLine(), 0);
        String prev = curEditor.getText(prevLineStart, prevLineEnd);

        Matcher matchCommentEnd = commentEnd.matcher(prev);

        if (!matchCommentEnd.find())
        {
            // No javadoc found
            foundJavadoc = "";
            return;
        }

        Matcher matchCommentStart = commentStart.matcher(prev);

        // if find  van /** dan  ... anders vorige regel tot regel 0 of 1
        while (!matchCommentStart.find())
        {
            if (prevLineStart.getLine() == 0)
                break;

            prevLineStart.setLine(prevLineStart.getLine() - 1);
            prev = curEditor.getText(prevLineStart, prevLineEnd);
            matchCommentStart = commentStart.matcher(prev);
        }

        // Assign javadoc to a variable
        foundJavadoc = prev;
    }

    /**
     * This method checks whether all parameters are present or not
     *
     * @param type defines the type of javadoc: class, constructor, method
     *
     * @return whether all parameters are present or not
     */
    private boolean checkIfParamsArePresent(int type)
    {
        switch (type)
        {
            case 0:
                return true;
            case 1:
                for (String s : parameter)
                {
                    if (!paramPresent(s, "param"))
                    {
                        javadoc.setParameter(s);
                        return false;
                    }
                }
                break;
            case 2:
                if (!paramPresent(returnType, "return"))
                {
                    javadoc.setReturnType(returnType);
                    return false;
                }
        }
        return true;
    }

    /**
     * This method tells you whether a parameter is present or not
     *
     * @param parameter the parameter you are looking for
     * @param type return or parameter
     *
     * @return whether a parameter is present or not
     */
    private boolean paramPresent(String parameter, String type)
    {
        String paramText = "";
        switch (type)
        {
            case "param":
                paramText = "@param\\s*" + parameter;
                break;
            case "return":
                if ((parameter == null | parameter.equals("void")))
                {
                    return true;
                } else
                {
                    paramText = "@return\\s*";
                }
                break;
        }
        Pattern pat = Pattern.compile(paramText);
        // match with text
        Matcher mat = pat.matcher(foundJavadoc);
        return mat.find();
    }

    /**
     * Validates the tokens in the right categories.
     *
     * @param type defines the type of javadoc: class, constructor, method
     * @param token ArrayList with the tokens
     */
    private void validateTokens(int type, String[] token)
    {
        ArrayList<String> newTokens = new ArrayList<>();

        for (String s : token)
        {
            if (!(s.equals("") | s.equals(" ")))
            {
                newTokens.add(s);
            }
        }
        // Index 1 is the return type
        returnType = newTokens.get(1);
        javadoc.setConstructorName(newTokens.get(1));

        if (newTokens.size() > 2)
        {
            javadoc.setMethodName(newTokens.get(2));
            javadoc.setClassName(newTokens.get(2));
        }

        for (int j = 0; j < newTokens.size(); j++)
        {
            // j == 3 then we have optional parameters between ()
            if (j == 3)
            {
                // No parameters
                // Constructors have no return type, so their parameters start at position 3
                if (type == CONSTRUCTOR)
                {
                    parameter.add(newTokens.get(j));
                    j = j + 1;
                }
                if (newTokens.get(3).equals("") | newTokens.get(3).equals(" "))
                {
                    break;
                }
                // Else we have the type of the parameter (not needed)
            }
            // If there are parameters, they start at index 4,
            // the token after a parameter is the type of the next parameters,
            // so we skip one index
            else if (j >= 4)
            {
                parameter.add(newTokens.get(j));
                // Skip next
                j = j + 1;
            }
        }
    }
}

