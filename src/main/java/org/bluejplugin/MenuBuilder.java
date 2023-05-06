package org.bluejplugin;

import bluej.extensions2.*;
import bluej.extensions2.editor.JavaEditor;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.MenuItem;
import javafx.stage.StageStyle;

class MenuBuilder extends MenuGenerator
{
    private final EventHandler questionHandler = questionAction();
    private JavaEditor curEditor;
    private BClass curClass;
    private final EventHandler evaluateHandler = evaluateAction();

    public MenuItem getToolsMenuItem(BPackage aPackage)
    {
        MenuItem menuItem = new MenuItem();
        menuItem.setText("Ask a question");
        menuItem.setOnAction(questionHandler);

        return menuItem;
    }

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
        menuItem.setText("Evaluate class");
        menuItem.setOnAction(evaluateHandler);

        return menuItem;
    }

    public EventHandler evaluateAction()
    {
        return actionEvent ->
        {
            try
            {
                if (curClass == null)
                {
                    System.out.println("MenuAction.actionPerformed() class is null");
                }
                try
                {
                    if (curClass.isCompiled())
                    {
                        /*
                          Show the warning window and start
                          evaluating this class
                         */
                        new WarningWindow(curClass, curEditor);
                    } else
                    {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.initStyle(StageStyle.UTILITY);
                        alert.setTitle("Error");
                        alert.setHeaderText("Class is not compiled");
                        alert.setContentText("Compile the class and try again");
                        alert.showAndWait();
                    }
                } catch (Exception e)
                {
                    throw new RuntimeException(e);
                }
            } catch (Exception exc)
            {
                System.out.println("MenuAction.actionPerformed() " + exc);
            }
        };
    }

    public EventHandler questionAction()
    {
        return actionEvent ->
        {
            try
            {
                new QuestionWindow();
            } catch (Exception e)
            {
                System.out.println("MenuBuilder.questionAction() " + e);
            }
        };
    }
}