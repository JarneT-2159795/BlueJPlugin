package org.bluejplugin;

import bluej.extensions2.ClassNotFoundException;
import bluej.extensions2.*;
import javafx.stage.Stage;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Manages the BlueJ object for a BlueJ extension
 *
 * @author Rick Giles
 * @version $Id: BlueJManager.java,v 1.6 2007/08/19 03:13:53 stedwar2 Exp $
 */
public final class BlueJManager
{
    /**
     * singleton
     */
    private static BlueJManager sInstance = null;

    /**
     * extra information for the actions
     */
    private Actions actions;

    /**
     * Prevent users from constructing BlueJManager objects.
     */
    private BlueJManager()
    {
        actions = null;
    }

    /**
     * Returns the singleton BlueJManager.
     *
     * @return the singleton BlueJManager.
     */
    public static synchronized BlueJManager getInstance()
    {
        if (sInstance == null)
        {
            sInstance = new BlueJManager();
        }
        return sInstance;
    }

    public Actions getActions()
    {
        return actions;
    }

    public void setActions(Actions ac)
    {
        actions = ac;
    }
}


