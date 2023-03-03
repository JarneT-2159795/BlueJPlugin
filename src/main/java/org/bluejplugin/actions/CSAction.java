package org.bluejplugin.actions;

import org.bluejplugin.Actions;
import org.bluejplugin.Comment;

/**
 * @author Leo Rutten
 * @version 23/02/2015
 */
abstract public class CSAction extends Action
{
    /**
     * Constructor for objects of class CSAction.
     */
    public CSAction(String cde, int maxp)
    {
        super(cde, maxp);
    }

    @Override
    public void performAction(Actions actions)
    {
        System.out.println("CSAction.performAction()");
    }

    public CSAction cssearch(String cscde)
    {
        if (isCSCode(cscde))
        {
            return this;
        } else
        {
            return null;
        }
    }

    abstract String getCSCode();

    abstract boolean isCSCode(String csc);

    abstract public Comment handleError(int line, int column, String message);
}

