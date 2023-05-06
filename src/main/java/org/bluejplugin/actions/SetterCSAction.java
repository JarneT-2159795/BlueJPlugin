package org.bluejplugin.actions;

import bluej.extensions2.editor.TextLocation;
import org.bluejplugin.Comment;

/**
 * @author L. Rutten
 * @version 23/02/2015
 */
public class SetterCSAction extends CSAction
{
    static public final String CODE = "cssetter";
    static public final String CS_CODE = "cs.setter";
    private int nrVariables;
    private int errors;

    /**
     * Constructor for SetSetterCSAction.
     */
    public SetterCSAction(String cde, int maxp)
    {
        super(cde, maxp);
        nrVariables = 0;
        errors = 0;
    }

    @Override
    public String getCSCode()
    {
        return CS_CODE;
    }

    @Override
    public boolean isCSCode(String csc)
    {
        return csc.equals(CS_CODE);
    }

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

            points = (int) ((1.0 - er / nrv) * mxp);

            System.out.println("setPoints() " + errors + " " + nrVariables + " " + maxPoints);
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
        return new Comment(message + " (line " + line + ")",
                new TextLocation(line - 1, column - 1));
    }
}

