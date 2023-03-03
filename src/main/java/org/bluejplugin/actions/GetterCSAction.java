package org.bluejplugin.actions;

import bluej.extensions2.editor.TextLocation;
import org.bluejplugin.Comment;
import org.bluejplugin.Enums;

/**
 * @author L. Rutten
 * @version 4/02/2015
 */
public class GetterCSAction extends CSAction
{
    static public final String CODE = "csgetter";
    static public final String CSCODE = "cs.getter";
    private int nrVariables;
    private int errors;

    /**
     * Constructor for GetSetterCSAction.
     */
    public GetterCSAction(String cde, int maxp)
    {
        super(cde, maxp);
        nrVariables = 0;
        errors = 0;
    }

    @Override
    public String getCSCode()
    {
        return CSCODE;
    }

    @Override
    public boolean isCSCode(String csc)
    {
        return csc.equals(CSCODE);
    }

    /**
     * This method is called by the companion class GetterCheck.
     */
    public void setNrVariables(int vars)
    {
        System.out.println("nr vars " + vars);
        nrVariables = vars;
        if (nrVariables == 0)
        {
            inapplicable = true;
        } else
        {
            setPoints();
        }
    }

    private void setPoints()
    {
        if (!inapplicable)
        {
            double er = errors;
            double nrv = nrVariables;

            er = er * 2.5;
            if (er > nrVariables) er = nrv;

            double mxp = maxPoints;

            points = (int) ((1.0 - er * 2.5 / nrv) * mxp);

            System.out.println("GetterCSAction setPoints() " + errors + " " + nrVariables + " " + maxPoints);
            System.out.println("points " + points);
        } else
        {
            points = 0;
        }
    }

    @Override
    public Comment handleError(int line, int column, String message)
    {
        System.out.println("CSAction.handleError() " + line
                + " " + column);
        errors++;
        setPoints();
        return new Comment(message + " (regel " + line + ")",
                new TextLocation(line - 1, column - 1));
    }
}

