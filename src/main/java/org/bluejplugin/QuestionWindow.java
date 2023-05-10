package org.bluejplugin;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.zeroturnaround.zip.ZipUtil;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

public class QuestionWindow
{
    private final StackPane root;
    private final HashMap<Character, String> GROUPS;
    private ComboBox<Character> cmbGroup;
    private TextArea txtQuestion;

    public QuestionWindow()
    {
        try
        {
            GROUPS = new HashMap<>();
            Path path = Paths.get(BlueJManager.getInstance().getBlueJ().getUserConfigDir().getAbsolutePath(), "groups.json");
            if (!path.toFile().exists()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("No groups.json file found");
                alert.setContentText("Please create a groups.json file in the BlueJ user config directory. " +
                        "Ask your teacher for help if you have not yet received this file.");
                alert.showAndWait();
            } else
            {
                Reader reader = new FileReader(path.toString());
                JSONParser parser = new JSONParser();
                JSONObject json = (JSONObject) parser.parse(reader);
                json.forEach((k, v) -> GROUPS.put(k.toString().charAt(0), v.toString()));
            }

            Stage stage = new Stage();
            stage.setTitle("Ask a question");
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
        GROUPS.forEach((k, v) -> groups.add(k));
        cmbGroup = new ComboBox<>();
        cmbGroup.getItems().addAll(groups);
        vbox.getChildren().add(new HBox(new Label("Group: "), cmbGroup));

        txtQuestion = new TextArea();
        vbox.getChildren().add(new HBox(new Label("Question: "), txtQuestion));

        Button btnSend = new Button("Send question");
        btnSend.setOnAction(e -> sendQuestion());
        if (GROUPS.size() == 0)
        {
            btnSend.setDisable(true);
        }
        vbox.getChildren().add(btnSend);

        root.getChildren().add(vbox);
    }

    private void sendQuestion()
    {
        if (cmbGroup.getValue() == null || txtQuestion.getText().isEmpty())
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Please fill in all fields");
            alert.showAndWait();
            return;
        }
        try
        {
            String pkgDir = BlueJManager.getInstance().getBlueJ().getCurrentPackage().getDir().getAbsolutePath();
            ZipUtil.pack(new File(pkgDir), new File(pkgDir + ".zip"));
            File zipFile = new File(pkgDir + ".zip");

            String teacher = GROUPS.get(cmbGroup.getValue());
            MailSender.sendMail(teacher, "Java: new question",
                    "A new question has been submitted by " + MailSender.name + " in group " + cmbGroup.getValue() + ".\n\n" + txtQuestion.getText(),
                    zipFile);

            zipFile.delete();

            ((Stage) root.getScene().getWindow()).close();
        } catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }
}
