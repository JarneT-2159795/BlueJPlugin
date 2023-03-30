package org.bluejplugin;

import bluej.extensions2.editor.TextLocation;

import java.util.ArrayList;

/**
 * Class to build the javadoc comments.
 *
 * @author Tim Hermans, Raf Marcoen (thanx to Bart Evens)
 * @version 1.0 (11/02/2013)
 */
public class Javadoc
{
    private static final int CLASS = 0;
    private static final int CONSTRUCTOR = 1;
    private static final int METHODE = 2;
    private String methodName;
    private String className;
    private String constructorName;
    private String returnType;
    private final ArrayList<String> parameter;
    private final TextLocation startLocation;
    private final TextLocation originalLocation;
    private final int type;

    /**
     * Constructor for objects of class Javadoc.
     *
     * @param startLoc TextLocation indicating the start position
     * @param originalLoc TextLocation indicating the original position
     * @param typePPP Integer indicating private|protected|public
     */
    public Javadoc(TextLocation startLoc, TextLocation originalLoc, int typePPP)
    {
        parameter = new ArrayList<>();
        returnType = null;
        methodName = null;
        className = null;
        constructorName = null;
        type = typePPP;
        startLocation = startLoc;
        originalLocation = originalLoc;
    }

    /**
     * Method to return the original location of the error
     *
     * @return TextLocation containing the position of the corresponding error
     */
    public TextLocation getOriginalLocation()
    {
        return originalLocation;
    }

    /**
     * Method to return the Javadoc text
     *
     * @return String containing the Javadoc
     */
    public String getJavadoc()
    {
        String jd = "";
        int line = originalLocation.getLine() + 1;
        if (type == CLASS)
            jd = "De javadoc ontbreekt voor de klasse " +
                    className;
        else if (type == CONSTRUCTOR)
        {
            jd = "De javadoc ontbreekt voor de constructor " +
                    constructorName;
        } else if (type == METHODE)
        {
            jd = "De javadoc ontbreekt voor de methode " +
                    methodName;
        }
        if (!(type == CLASS))
        {
            if (parameter.isEmpty())
            {
                if (!(returnType == null))
                {
                    jd += ": geen @return gevonden";
                }
            } else
            {
                jd += "=> parameter(s): ";

                for (String s : parameter)
                {
                    jd += s + ", ";
                }

                jd = jd.substring(0, jd.length() - 2);

                if (!(returnType == null))
                {
                    jd += ". Ook @return ontbreekt";
                }
            }
        }

        return jd + ", op regel " + line + "\n";
    }

    /**
     * Setter for name of the method.
     *
     * @param n String containing the name of the method
     */
    public void setMethodName(String n)
    {
        methodName = n;
    }

    /**
     * Setter for return type of the method.
     *
     * @param t Return type of the method
     */
    public void setReturnType(String t)
    {
        returnType = t;
    }

    /**
     * Setter for a parameter name of the method.
     *
     * @param p Parameter name
     */
    public void setParameter(String p)
    {
        parameter.add(p);
    }

    /**
     * Setter for the class name.
     *
     * @param name String containing the new name
     */
    public void setClassName(String name)
    {
        className = name;
    }

    /**
     * Setter for the constructor name.
     *
     * @param name String containing the new name
     */
    public void setConstructorName(String name)
    {
        constructorName = name;
    }
}
