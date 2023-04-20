package org.bluejplugin;

import bluej.extensions2.BClass;
import bluej.extensions2.editor.JavaEditor;
import org.bluejplugin.actions.*;

import java.util.ArrayList;

/**
 * In this class we create the GUI and start all the search actions
 *
 * @author L. Rutten
 * @version 29/01/2015
 */
public class Actions
{
    private final BClass bClass;
    private final JavaEditor editor;
    private final ArrayList<Comment> comments;
    private final ArrayList<Variable> varList;
    private final ArrayList<Action> actionList;

    /**
     * Constructor for objects of class Actions
     *
     * @param bClass the class to be evaluated
     * @param editor the editor of the class
     */
    public Actions(BClass bClass, JavaEditor editor)
    {
        this.editor = editor;
        this.bClass = bClass;
        actionList = new ArrayList<>();

        // Add all the actions selected in the preferences
        if (Preferences.getInstance().getJavadoc())
            actionList.add(new JavadocAction("patt.javadoc", 30));
        if (Preferences.getInstance().getSearchVariables())
            actionList.add(new SearchVariables("patt.variables", 10));
        if (Preferences.getInstance().getGet())
            actionList.add(new GetAction("patt.getter", 30));
        if (Preferences.getInstance().getSet())
            actionList.add(new SetAction("patt.setter", 30));
        if (Preferences.getInstance().getPublic())
            actionList.add(new PublicAction("patt.public", 30));
        if (Preferences.getInstance().getCamelCase())
            actionList.add(new CamelCaseAction("patt.camelcase", 20));
        if (Preferences.getInstance().getIf())
            actionList.add(new IfAction("patt.if", 30));
        if (Preferences.getInstance().getEqual())
            actionList.add(new EqualAction("patt.equal", 30));
        if (Preferences.getInstance().getPmd())
            actionList.add(new PmdAction("patt.pmd", 30, bClass));

        comments = new ArrayList<>();
        varList = new ArrayList<>();
    }

    public JavaEditor getEditor()
    {
        return editor;
    }

    public BClass getBClass()
    {
        return bClass;
    }

    public ArrayList<Variable> getVariableList()
    {
        return varList;
    }

    public ArrayList<Comment> getComments()
    {
        return comments;
    }

    public void addComment(Comment com)
    {
        comments.add(com);
    }

    public void addVariable(Variable var)
    {
        varList.add(var);
    }

    public double getPercentage()
    {
        int points = 0;
        int max = 0;
        int ctr = 0;

        for (Action a : actionList)
        {
            if (a.isActive() && !a.isInapplicable())
            {
                points += a.getPoints();
                max += a.getMaxPoints();
                ctr++;
            }
        }

        if (ctr == 0)
        {
            // No real tests
            return -10.0;
        } else
        {
            return 100.0 * ((double) points) / ((double) max);
        }
    }

    /**
     * Call this method to start the evaluation
     */
    private void startEvaluation()
    {
        for (Action a : actionList)
        {
            if (a.isActive())
            {
                a.performAction(this);
            }
        }
    }

    public void start()
    {
        startEvaluation();
    }

    public Action search(String cde)
    {
        for (Action a : actionList)
        {
            if (a.getCode().equals(cde))
            {
                return a;
            } else
            {
                Action a2 = a.search(cde);
                if (a2 != null)
                {
                    return a2;
                }
            }
        }
        return null;
    }
}

