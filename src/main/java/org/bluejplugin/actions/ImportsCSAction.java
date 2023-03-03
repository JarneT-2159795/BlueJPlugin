package org.bluejplugin.actions;

import bluej.extensions2.editor.TextLocation;
import org.bluejplugin.Comment;
import org.bluejplugin.Enums;

/**
 * @author L. Rutten
 * @version 1/04/2015
 */
public class ImportsCSAction extends CSAction
{
    static public final String CODE = "imports";
    static public final String CSCODE = "import.avoidStar";
    static public final String CSCODE2 = "import.unused";
    static public final String CSCODE3 = "import.duplicate";
    static public final String CSCODE4 = "import.lang";
    static public final String CSCODE5 = "import.same";

    private int errors;

    /**
     * Constructor for ImportsCSAction.
     */
    public ImportsCSAction(String cde, int maxp)
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
                csc.equals(CSCODE5);
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

