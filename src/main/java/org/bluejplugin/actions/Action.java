package org.bluejplugin.actions;

import org.bluejplugin.Actions;

/**
 * An Action represents some test to be executed on
 * a source code of a class.
 *
 * @author L. Rutten
 * @version 30/01/2015
 */
abstract public class Action
{
    private final String code;
    /**
     * If active, this check must be executed.
     */
    private final boolean active;
    protected int points;
    protected int maxPoints;
    /**
     * If inapplicable, this check must be executed but
     * the points of this check cannot be summed up.
     */
    protected boolean inapplicable;

    public Action(String code, int maxPoints)
    {
        this.code = code;
        this.points = 0;
        this.maxPoints = maxPoints;

        active = true;
        inapplicable = false;
    }

    abstract public void performAction(Actions actions);

    public String getCode()
    {
        return code;
    }

    public int getPoints()
    {
        return points;
    }

    public int getMaxPoints()
    {
        return maxPoints;
    }

    public boolean isActive()
    {
        return active;
    }

    public boolean isInapplicable()
    {
        return inapplicable;
    }

    public Action search(String code)
    {
        if (this.code.equals(code))
        {
            return this;
        } else
        {
            return null;
        }
    }
}

