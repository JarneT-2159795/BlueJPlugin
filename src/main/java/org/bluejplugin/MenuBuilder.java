package org.bluejplugin;

import bluej.extensions2.*;
import bluej.extensions2.editor.JavaEditor;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.MenuItem;
import javafx.stage.StageStyle;

class MenuBuilder extends MenuGenerator
{
    private JavaEditor curEditor;
    private BClass curClass;
    private BObject curObject;
    private final EventHandler menAction = menuAction();

    public MenuItem getClassMenuItem(BClass aClass)
    {
        curClass = aClass;
        try
        {
            curEditor = curClass.getJavaEditor();
        } catch (ProjectNotOpenException | PackageNotFoundException e)
        {
            System.out.println("MenuBuilder.getClassMenuItem() " + e);
        }
        MenuItem menuItem = new MenuItem();
        menuItem.setText("Evalueer klasse");
        menuItem.setOnAction(menAction);

        return menuItem;
    }

    public EventHandler menuAction()
    {
        return actionEvent ->
        {
            try
            {
                if (curClass == null)
                {
                    System.out.println("MenuAction.actionPerformed() class is null");
                }
                if (curClass.isCompiled())
                {
                    System.out.println("MenuAction.actionPerformed() " + this);

                    /*
                      Show the warning window and start
                      evaluating this class
                     */
                    new WarningWindow(curClass, curEditor);
                } else
                {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.initStyle(StageStyle.UTILITY);
                    alert.setTitle("Fout");
                    alert.setHeaderText("Klasse niet gecompileerd");
                    alert.setContentText("Compileer de klasse voordat je hem evalueert.");
                    alert.showAndWait();
                }
            } catch (Exception exc)
            {
                System.out.println("MenuAction.actionPerformed() " + exc);
            }
        };
    }
}