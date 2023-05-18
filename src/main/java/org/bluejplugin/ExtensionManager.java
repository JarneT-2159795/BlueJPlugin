package org.bluejplugin;

import bluej.extensions2.BlueJ;
import bluej.extensions2.Extension;
import bluej.extensions2.ExtensionException;
import bluej.extensions2.ExternalFileLauncher;
import bluej.extensions2.event.PackageEvent;
import bluej.extensions2.event.PackageListener;
import javafx.concurrent.Task;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Entry point for the BlueJ extension.
 */
public class ExtensionManager extends Extension implements PackageListener
{
    /**
     * This method is called when the extension is loaded
     *
     * @param bluej The BlueJ object that represents the current BlueJ instance
     */
    public void startup(BlueJ bluej)
    {
        // Set the BlueJ object for the extension
        BlueJManager.getInstance().setBlueJ(bluej);

        // Listen for BlueJ events at the "package" level
        bluej.addPackageListener(this);

        // Add items to BlueJ's menus
        MenuBuilder myMenus = new MenuBuilder();
        bluej.setMenuGenerator(myMenus);

        // Add UI for the extension in the Preferences panel of BlueJ
        bluej.setPreferenceGenerator(Preferences.getInstance(bluej));

        // Provide launchers for HTML and PDF files that BlueJ can call on external files of these types
        // This is the launcher object that will be used for both HTML and PDF extensions
        ExternalFileLauncher.OpenExternalFileHandler browserOpener = new ExternalFileLauncher.OpenExternalFileHandler()
        {
            @Override
            public void openFile(String fileName) throws Exception
            {
                // This method will be called by BlueJ when an attempt to open the file is made.
                // (provided no other extension has overwritten the launcher for the specified file type.)
                AtomicBoolean hasErrorOccurred = new AtomicBoolean(false);
                Task<Void> task = new Task<>()
                {
                    @Override
                    public Void call()
                    {
                        File externalFile = new File(fileName);
                        try
                        {
                            Desktop.getDesktop().browse(externalFile.toURI());
                        } catch (IOException e)
                        {
                            hasErrorOccurred.set(true);
                            this.cancel();
                        }
                        return null;
                    }
                };

                // Launch the browser outside the main BlueJ java FX thread.
                Thread thread = new Thread(task);
                thread.start();

                // Wait sufficiently long to get the application opening and if not returning just continue
                thread.join(10000);
                if (hasErrorOccurred.get())
                {
                    throw new Exception("A problem occurred opening " + fileName);
                }
            }
        };
        // Prepare a list of launchers for HTML and PDF files.
        List<ExternalFileLauncher> list = new ArrayList<>();
        ExternalFileLauncher htmlLauncher = new ExternalFileLauncher("*.html", browserOpener);
        ExternalFileLauncher pdfLauncher = new ExternalFileLauncher(".pdf", browserOpener);
        list.add(htmlLauncher);
        list.add(pdfLauncher);
        // Set the launchers for registration to BlueJ
        bluej.addExternalFileLaunchers(list);
    }

    /**
     * A package has been opened. Print the name of the project it is part of.
     * System.out is redirected to the BlueJ debug log file.
     * The location of this file is given in the Help/About BlueJ dialog box.
     *
     * @param ev The package event
     */
    public void packageOpened(PackageEvent ev)
    {
        try
        {
            System.out.println("Project " + ev.getPackage().getProject().getName()
                    + " opened.");
        } catch (ExtensionException e)
        {
            System.out.println("Project closed by BlueJ");
        }
    }

    /**
     * A package is closing.
     *
     * @param ev The package event
     */
    public void packageClosing(PackageEvent ev)
    {
    }

    /**
     * This method must decide if this Extension is compatible with the
     * current release of the BlueJ Extensions API
     *
     * @return true if the extension is compatible with the current release of the BlueJ Extensions API
     */
    public boolean isCompatible()
    {
        return (getExtensionsAPIVersionMajor() >= 3);
    }

    /**
     * Returns the version number of this extension
     *
     * @return The version number of this extension
     */
    public String getVersion()
    {
        return ("1.0.0");
    }

    /**
     * Returns the user-visible name of this extension
     *
     * @return The user-visible name of this extension
     */
    public String getName()
    {
        return ("IIW Code Evaluator");
    }

    /**
     * This method is called when the extension is unloaded
     */
    public void terminate()
    {
        System.out.println("Evaluation extension terminates");
    }

    /**
     * Returns a description of this extension
     *
     * @return A description of this extension
     */
    public String getDescription()
    {
        return ("An evaluation extension designed for the students of the IIW course.");
    }

    /**
     * Returns a URL where you can find info on this extension.
     * The real problem is making sure that the link will still be alive
     * in three years...
     */
    public URL getURL()
    {
        try
        {
            return new URL("https://github.com/JarneT-2159795/BlueJPlugin");
        } catch (Exception e)
        {
            // The link is either dead or otherwise unreachable
            System.out.println("Evaluation extension: getURL: Exception=" + e.getMessage());
            return null;
        }
    }
}