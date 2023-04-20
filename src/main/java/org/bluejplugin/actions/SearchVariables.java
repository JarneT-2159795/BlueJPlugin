package org.bluejplugin.actions;

import bluej.extensions2.editor.JavaEditor;
import bluej.extensions2.editor.TextLocation;
import org.bluejplugin.Actions;
import org.bluejplugin.Enums;
import org.bluejplugin.Variable;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * In this class we look for getter/setter errors and add the necessary comments
 *
 * @author Tim Hermans, Raf Marcoen
 * @version 1.0 (12/02/2013)
 */
public class SearchVariables extends Action
{
    private static final TextLocation NULL_LOCATION = new TextLocation(0, 0);

    private static final String ARRAY_VAR = "([a-zA-Z]+\\s*\\x5B?\\s*\\x5D?)";
    private static final String ARRAYLIST_VAR = "ArrayList<([a-zA-Z]+)>";
    private static final String INTENTIONAL = "(\\x40Intentional\\s*\\x28(noGetter|noSetter|publicVariable|longName|\\s*\\x2C\\s*|\\x3D\\s*true|\\x3D\\s*false|\\s*)*\\x29)|(\\x40Intentional)";
    private static final String PARAMETER = "([a-zA-Z$_][a-zA-Z0-9$_]*)\\s*(\\x5B\\s*\\x5D)?";
    private static final String ACCESS_TYPE = "(public|private|protected)\\s+(?!class)";
    private static final String ONE_OR_MORE_WHITESPACES = "\\s+";
    private static final String ZERO_OR_MORE_WHITESPACES = "\\s*";
    private static final String COMMA = "[\\s*[,]{1}\\s*]*";

    private static final String OPTIONAL_INIT = "(\\s*=\\s*(([0-9]*)|(\\x7B[^\\x7D]*\\x7D)|(new\\s+[^\\x29]*\\x29)|(\\x22[^\\x22]*\\x22)))?";
    private static final String REQUIRED_INIT = "(\\s*=\\s*([0-9]*)|(\\x7B[^\\x7D]*\\x7D)|(new\\s+[^\\x29]*\\x29)|(\\x22[^\\x22]*\\x22))";

    private static final String OPTIONAL_ATTRIBUTE = "[(static|final|\\s*)*\\s+]?";
    private static final String REQUIRED_ATTRIBUTE = "(\\s+(static|final|\\s+)*)";
    private static final String WHITESPACE_START_OF_LINE = "^\\s";

    private JavaEditor curEditor;
    private String match = "no match found";
    private Pattern pattern;
    private Pattern patternPrev;
    private Pattern patternWhitespaceStartOfLine;

    /**
     * Constructor for objects of class SearchVariables.
     *
     * @param code      Source code of the class
     * @param maxPoints Maximum points for this action
     */
    public SearchVariables(String code, int maxPoints)
    {
        super(code, maxPoints);
        init();
    }

    /**
     * Method to init some data members.
     */
    public final void init()
    {
        // Create a regular expression to compare the code with
        String reg = ACCESS_TYPE + OPTIONAL_ATTRIBUTE + "(" + ARRAY_VAR + "|" + ARRAYLIST_VAR + ")" +
                ONE_OR_MORE_WHITESPACES + PARAMETER + OPTIONAL_INIT + ZERO_OR_MORE_WHITESPACES +
                "(" + COMMA + ZERO_OR_MORE_WHITESPACES + PARAMETER + OPTIONAL_INIT + ZERO_OR_MORE_WHITESPACES + ")*"
                + ZERO_OR_MORE_WHITESPACES + "[;]";

        // Compile the pattern
        pattern = Pattern.compile(reg);
        patternPrev = Pattern.compile(INTENTIONAL);
        patternWhitespaceStartOfLine = Pattern.compile(WHITESPACE_START_OF_LINE);
    }

    /**
     * This method searches the editor for global variables
     *
     * @param actions The actions object
     */
    @Override
    public void performAction(Actions actions)
    {
        curEditor = actions.getEditor();

        // Set end of the code by getting the current textlength in the editor
        TextLocation endLocation = curEditor.getTextLocationFromOffset(curEditor.getTextLength());
        // The code between these borders is the input for a matcher
        String curCode = curEditor.getText(NULL_LOCATION, endLocation);

        // Put the code in a matcher
        Matcher matcher = pattern.matcher(curCode);

        try
        {
            while (matcher.find())
            {
                // The index in the code where the match started
                int startOffset = matcher.start();

                TextLocation startLoc = curEditor.getTextLocationFromOffset(startOffset);

                // Group the match
                match = matcher.group();

                // Creating getter & setter object
                validateTokens(actions, startLoc, searchIntentional(startLoc));
            }
        } catch (Exception e)
        {
            System.out.println("SearchVariables.performAction: " + e.getMessage());
        }

        inapplicable = true;
    }

    /**
     * This method validates the different tokens in the right categories.
     *
     * @param actions     The actions object
     * @param startOffset The start offset of the text
     * @param boolArray   The boolean array with the @Intentional values
     */
    public void validateTokens(Actions actions, TextLocation startOffset, boolean[] boolArray)
    {
        // Create tokens with the split tokenizer
        String[] token = match.split(
                "(" + REQUIRED_INIT + ")|" + REQUIRED_ATTRIBUTE + "|(\\s*\\x2C\\s*)|(\\s*\\x3B\\s*)|(\\s)");

        String identifier = token[1];

        // Walk through the different tokens we just created
        for (int j = 2; j < token.length; j++)
        {
            if (!(token[j].equals("")))
            {
                Enums.PPPType typePPP;
                switch (token[0])
                {
                    case "private":
                        typePPP = Enums.PPPType.Private;
                        break;
                    case "protected":
                        typePPP = Enums.PPPType.Protected;
                        break;
                    default:
                        typePPP = Enums.PPPType.Public;
                }

                Variable var = new Variable(startOffset, token[j], typePPP);

                if (boolArray[0]) var.setNoSetter();
                if (boolArray[1]) var.setNoGetter();
                if (boolArray[2]) var.setPublicVariable();
                if (boolArray[3]) var.setLongName();
                actions.addVariable(var);
            }
        }
    }

    /**
     * Searches the previous line  for @Intentional
     *
     * @param startLoc The start location of the text
     * @return A boolean array with the @Intentional values for noSetter, noGetter, PublicVariable, longName
     */
    public boolean[] searchIntentional(TextLocation startLoc)
    {
        // noSetter, noGetter, PublicVariable, longName
        boolean[] boolArray = {false, false, false, false};

        TextLocation prevLineStart = new TextLocation(startLoc.getLine() - 2, 0);
        TextLocation prevLineEnd = new TextLocation(startLoc.getLine(), 0);
        String prev = curEditor.getText(prevLineStart, prevLineEnd);
        Matcher matcherPrev = patternPrev.matcher(prev);
        Matcher matcherWhitespaceStartOfLine = patternWhitespaceStartOfLine.matcher(prev);

        //if find  van ^\\s* dan  ... anders vorige regel tot regel 0 of 1
        while (!matcherWhitespaceStartOfLine.find())
        {
            if (prevLineStart.getLine() == 0)
                break;

            prevLineStart.setLine(prevLineStart.getLine() - 1);
            prevLineEnd.setLine(prevLineEnd.getLine() - 1);
            prev = curEditor.getText(prevLineStart, prevLineEnd);
            matcherWhitespaceStartOfLine = patternWhitespaceStartOfLine.matcher(prev);
        }

        if (matcherPrev.find())
        {
            String matchPrev = matcherPrev.group();
            ArrayList<String> tokenTwee;
            tokenTwee = new ArrayList<>();

            String[] token = matchPrev.split(
                    "(\\x40Intentional)|(\\x2C)|(\\s)|(\\x3D)|(\\x28)|(\\x29)");

            for (String s : token)
            {
                if (!(s.equals("")))
                {
                    tokenTwee.add(s);
                }
            }

            if (tokenTwee.isEmpty())
            {
                boolArray[0] = true;
                boolArray[1] = true;
                boolArray[3] = true;
            } else
            {
                for (int j = 0; j < tokenTwee.size() - 1; j++)
                {
                    if (tokenTwee.get(j + 1).equals("true"))
                    {
                        if (tokenTwee.get(j).equals("noSetter")) boolArray[0] = true;

                        if (tokenTwee.get(j).equals("noGetter")) boolArray[1] = true;

                        if (tokenTwee.get(j).equals("publicVariable"))
                        {
                            boolArray[0] = true;
                            boolArray[1] = true;
                            boolArray[2] = true;
                        }
                        if (tokenTwee.get(j).equals("longName")) boolArray[3] = true;
                    }
                }
            }
        }
        return boolArray;
    }
}
