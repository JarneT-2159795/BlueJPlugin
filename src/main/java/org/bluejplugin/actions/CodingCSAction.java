package org.bluejplugin.actions;

import bluej.extensions2.editor.TextLocation;
import org.bluejplugin.Comment;
import org.bluejplugin.Enums;

/**
 * @author L. Rutten
 * @version 30/03/2015
 */
public class CodingCSAction extends CSAction
{
    static public final String CODE = "coding";
    static public final String CSCODE = "empty.statement";
    static public final String CSCODE2 = "hidden.field";
    static public final String CSCODE3 = "modified.control.variable";
    static public final String CSCODE4 = "simplify.expression";
    static public final String CSCODE5 = "simplify.boolreturn";
    static public final String CSCODE6 = "string.literal.equality";
    static public final String CSCODE7 = "parameter.assignment";
    static public final String CSCODE8 = "default.comes.last";
    static public final String CSCODE9 = "multiple.variable.declarations";
    static public final String CSCODE10 = "multiple.variable.declarations.comma";
    static public final String CSCODE11 = "multiple.statements.line";

    private int errors;

    /**
     * Constructor for NamingCSAction.
     */
    public CodingCSAction(String cde, int maxp)
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
                csc.equals(CSCODE6) ||
                csc.equals(CSCODE7) ||
                csc.equals(CSCODE8) ||
                csc.equals(CSCODE9) ||
                csc.equals(CSCODE10) ||
                csc.equals(CSCODE11);
    }

    private void setPoints()
    {
        if (!inapplicable)
        {
            final int MAXERROR = 5;
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

            System.out.println("naming setPoints() " + errors + " " + maxPoints);
            System.out.println("naming points " + points);
        } else
        {
            points = 0;
        }
    }

    @Override
    public Comment handleError(int line, int column, String message)
    {
        System.out.println("NamingCSAction.handleError() " + line
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

