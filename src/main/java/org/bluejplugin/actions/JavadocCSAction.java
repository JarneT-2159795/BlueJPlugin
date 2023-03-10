package org.bluejplugin.actions;

import bluej.extensions2.editor.TextLocation;
import org.bluejplugin.Comment;
import org.bluejplugin.Enums;

/**
 * @author L. Rutten
 * @version 23/02/2015
 */
public class JavadocCSAction extends CSAction
{
    static public final String CODE = "javadoc";
    static public final String CSCODE = "javadoc.missing";
    static public final String CSCODE2 = "javadoc.return.expected";
    static public final String CSCODE3 = "javadoc.expectedTag";
    static public final String CSCODE4 = "javadoc.unusedTag";
    static public final String CSCODE5 = "javadoc.duplicateTag";
    static public final String CSCODE6 = "type.missingTag";

    private int errors;

    /**
     * Constructor for JavadocCSAction.
     */
    public JavadocCSAction(String cde, int maxp)
    {
        super(cde, maxp);
        errors = 0;
        //setPoints();
        points = maxPoints;
    }

    @Override
    public String getCSCode()
    {
        return CSCODE;
    }

    @Override
    public boolean isCSCode(String csc)
    {
        return csc.equals(CSCODE) ||
                csc.equals(CSCODE2) ||
                csc.equals(CSCODE3) ||
                csc.equals(CSCODE4) ||
                csc.equals(CSCODE5) ||
                csc.equals(CSCODE6);
    }

    private void setPoints()
    {
        if (!inapplicable)
        {
            final int MAXERROR = 8;
            double er;
            if (errors > MAXERROR)
            {
                er = MAXERROR;
            } else
            {
                er = errors;
            }
            double mxp = maxPoints;

            points = (int) ((1.0 - er / MAXERROR) * mxp);

            System.out.println("javadoc setPoints() " + errors + " " + maxPoints);
            System.out.println("javadoc points " + points);
        } else
        {
            points = 0;
        }
    }

    @Override
    public Comment handleError(int line, int column, String message)
    {
        System.out.println("JavadocCSAction.handleError() " + line
                + " " + column);
        errors++;
        setPoints();

        if (column > 0)
        {
            column--;
        }

        return new Comment(message + " (regel " + line + ")",
                new TextLocation(line - 1, column));
    }
}

