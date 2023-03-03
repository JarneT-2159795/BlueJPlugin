package org.bluejplugin.actions;

import bluej.extensions2.editor.TextLocation;
import org.bluejplugin.Comment;
import org.bluejplugin.Enums;

/**
 * @author L. Rutten
 * @version 30/03/2015
 */
public class DesignCSAction extends CSAction
{
    static public final String CODE = "design";
    static public final String CSCODE = "variable.notPrivate";
    static public final String CSCODE2 = "one.top.level.class";

    private int errors;

    /**
     * Constructor for DesignCSAction.
     */
    public DesignCSAction(String cde, int maxp)
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
                csc.equals(CSCODE2);
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

