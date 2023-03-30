package org.bluejplugin;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.zeroturnaround.zip.ZipUtil;

import java.io.File;
import java.util.ArrayList;

public class QuestionWindow
{
    private final StackPane root;
    private final String SERVER = "http://127.0.0.1:8787/";
    private final char GROUPS = 'H';
    private ComboBox<Character> cmbGroup;
    private TextArea txtQuestion;

    public QuestionWindow()
    {
        try
        {
            Stage stage = new Stage();
            stage.setTitle("Vraag stellen");
            root = new StackPane();
            Scene scene = new Scene(root, 600, 300);
            stage.setScene(scene);
            initComponents();
            stage.show();
        } catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    private void initComponents()
    {
        VBox vbox = new VBox();
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(10, 0, 0, 10));

        ArrayList<Character> groups = new ArrayList<>();
        for (int i = 'A'; i <= GROUPS; i++)
        {
            groups.add((char) (i));
        }
        cmbGroup = new ComboBox<>();
        cmbGroup.getItems().addAll(groups);
        vbox.getChildren().add(new HBox(new Label("Groep: "), cmbGroup));

        txtQuestion = new TextArea();
        vbox.getChildren().add(new HBox(new Label("Vraag: "), txtQuestion));

        Button btnSend = new Button("Verstuur");
        btnSend.setOnAction(e -> sendQuestion());
        vbox.getChildren().add(btnSend);

        root.getChildren().add(vbox);
    }

    private void sendQuestion()
    {
        if (cmbGroup.getValue() == null || txtQuestion.getText().isEmpty())
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Fout");
            alert.setHeaderText("Vul alle velden in");
            alert.showAndWait();
            return;
        }
        try
        {
            String pkgDir = BlueJManager.getInstance().getBlueJ().getCurrentPackage().getDir().getAbsolutePath();
            ZipUtil.pack(new File(pkgDir), new File(pkgDir + ".zip"));
            File zipFile = new File(pkgDir + ".zip");

            MailSender.sendMail("jarne.thys@student.uhasselt.be", "Nieuwe vraag",
                    "Er is een nieuwe vraag gesteld door " + MailSender.name + " in groep " + cmbGroup.getValue() + ".\n\n" + txtQuestion.getText(),
                    zipFile);

            zipFile.delete();

            ((Stage) root.getScene().getWindow()).close();
        } catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }
}
