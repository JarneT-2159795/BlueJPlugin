package org.bluejplugin;

import bluej.extensions2.BClass;
import bluej.extensions2.editor.JavaEditor;
import bluej.extensions2.editor.TextLocation;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;

/**
 * In this class we create the GUI and start all the search actions
 *
 * @author Tim Hermans, Raf Marcoen
 * @version 1.0 (10/07/2013)
 */
public class WarningWindow
{
    private final BClass bclass;
    private final JavaEditor editor;
    private ListView<Comment> commentListModel;
    private Actions actions;
    private Label lblScore;
    private final StackPane root;

    /**
     * Creates new form WarningWindow
     *
     * @param aClass The class that is being evaluated
     * @param editor The editor of the class that is being evaluated
     */
    public WarningWindow(BClass aClass, JavaEditor editor)
    {
        bclass = aClass;
        this.editor = editor;

        Stage stage = new Stage();
        stage.setTitle("Evaluatie");
        root = new StackPane();
        Scene scene = new Scene(root, 400, 300);
        stage.setScene(scene);
        resetComponents();
        startEvaluation();
        stage.show();
    }

    /**
     * Method to reset the initial values of the components
     */
    private void resetComponents()
    {
        commentListModel = new ListView<>();
        commentListModel.getItems().add(new Comment("Evaluatie wordt gestart..."));
        initComponents();
    }

    /**
     * Call this method to start the evaluation
     */
    private void startEvaluation()
    {
        actions = new Actions(bclass, editor);
        BlueJManager.getInstance().setActions(actions);
        actions.start();

        updateCommentList();
    }

    /**
     * Function to update the content of the warning list, the List of suggestions
     */
    private void updateCommentList()
    {
        commentListModel.getItems().clear();
        ArrayList<Comment> comments = actions.getComments();
        comments.sort(new commentLocationComparator());

        if (!comments.isEmpty())
        {
            for (Comment s : comments)
            {
                commentListModel.getItems().add(s);
            }
        } else
        {
            commentListModel.getItems().add(new Comment("Er zijn geen opmerkingen gevonden."));
        }

        double points = actions.getPercentage();
        String text;
        if (points < 0.0)
        {
            text = "Er is een fout opgetreden bij het evalueren van je code. Probeer het later opnieuw.";
        } else
        {
            text = "Je code scoort " + points + " punten.";
        }
        lblScore.setText(text);
    }

    /**
     * Initializes the components
     */
    private void initComponents()
    {
        VBox vbox = new VBox();
        lblScore = new Label();

        commentListModel.setCursor(Cursor.DEFAULT);
        commentListModel.addEventHandler(MouseEvent.MOUSE_CLICKED, this::commentClicked);

        lblScore.setText("Label2");

        vbox.setSpacing(5);
        vbox.getChildren().addAll(commentListModel, lblScore);
        root.getChildren().add(vbox);
    }

    /**
     * Method to handle the double click on a comment
     *
     * @param evt The mouse event
     */
    private void commentClicked(MouseEvent evt)
    {
        if (evt.getClickCount() != 2)
            return;
        int index = commentListModel.getSelectionModel().getSelectedIndex();
        commentListModel.getSelectionModel().clearSelection();
        if (index != -1 && index < actions.getComments().size())
        {
            TextLocation start = actions.getComments().get(index).getLocation();
            TextLocation end = new TextLocation(start.getLine(), editor.getLineLength(start.getLine()) - 1);
            editor.setSelection(start, end);
            editor.setVisible(true);
        }
    }
}

