package org.bluejplugin;

import bluej.extensions2.editor.TextLocation;


/**
 * In this class we have a collection of all variables we need.
 * the type, name, location,...
 *
 * @author Tim Hermans, Raf Marcoen
 * @version 1.0 (22/05/2013)
 */
public class Variable
{
    private final Enums.PPPType ppp;
    private final String parameter;
    private final String parameterC;
    private final TextLocation originalLocation;
    private boolean noGetter = false;
    private boolean noSetter = false;
    private boolean publicVariable = false;
    private boolean longName = false;

    /**
     * Constructor for objects of class Variable
     *
     * @param location Location of the variable in the file
     * @param par      Name of the variable
     * @param ppp      Access type of the variable
     */
    public Variable(TextLocation location, String par, Enums.PPPType ppp)
    {
        this.originalLocation = location;
        this.parameter = par;
        this.ppp = ppp;
        parameterC = par.substring(0, 1).toUpperCase() + par.substring(1);
    }

    /**
     * This method returns the variable name
     *
     * @return String Name of the variable
     */
    public String getVariable()
    {
        return parameter;
    }

    /**
     * This method returns the variable
     *
     * @return String Name of the variable
     */
    public String getVariableC()
    {
        return parameterC;
    }

    /**
     * This method returns the location of the variable
     *
     * @return TextLocation Location of the variable
     */
    public TextLocation getLocation()
    {
        return originalLocation;
    }

    /**
     * This method returns the typePPP of the variable
     *
     * @return Enums.PPPType The type of the variable
     */
    public Enums.PPPType getPpp()
    {
        return ppp;
    }

    /**
     * This method sets noGetter on true
     */
    public void setNoGetter()
    {
        noGetter = true;
    }

    /**
     * This method sets noSetter on true
     */
    public void setNoSetter()
    {
        noSetter = true;
    }

    /**
     * This method sets publicVariable on true
     */
    public void setPublicVariable()
    {
        publicVariable = true;
    }

    /**
     * This method sets longName on true
     */
    public void setLongName()
    {
        longName = true;
    }

    /**
     * This method gets noGetter
     *
     * @return noGetter
     */
    public boolean getNoGetter()
    {
        return noGetter;
    }

    /**
     * This method gets the value of noSetter
     *
     * @return noSetter
     */
    public boolean getNoSetter()
    {
        return noSetter;
    }

    /**
     * This method gets the value of publicVariable
     *
     * @return publicVariable
     */
    public boolean getPublicVariable()
    {
        return publicVariable;
    }

    /**
     * This method gets the value of longName
     *
     * @return longName
     */
    public boolean getLongName()
    {
        return longName;
    }
}

