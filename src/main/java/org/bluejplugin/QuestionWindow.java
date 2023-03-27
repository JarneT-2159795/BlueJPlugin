package org.bluejplugin;

import bluej.extensions2.PackageNotFoundException;
import bluej.extensions2.ProjectNotOpenException;
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

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class QuestionWindow
{
    private final StackPane root;
    private final String SERVER = "http://localhost:5000/";
    private final char GROUPS;
    private TextField txtName;
    private ComboBox<Character> cmbGroup;
    private TextArea txtQuestion;

    public QuestionWindow()
    {
        try
        {
            URL url = new URL(SERVER + "groups");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null)
            {
                response.append(inputLine);
            }
            in.close();
            connection.disconnect();
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(response.toString());
            GROUPS = json.get("groups").toString().charAt(0);

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

        txtName = new TextField();
        vbox.getChildren().add(new HBox(new Label("Naam: "), txtName));

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
        if (txtName.getText().isEmpty() || cmbGroup.getValue() == null || txtQuestion.getText().isEmpty())
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
            try (FileWriter fileWriter = new FileWriter(pkgDir + "/question.txt")) {
                fileWriter.write("Name: " + txtName.getText() + "\n" + "Group: " + cmbGroup.getValue() + "\n" + "Question: " + "\n" + txtQuestion.getText());
            } catch (IOException e) {
                System.err.println("Error writing to file: " + e.getMessage());
                return;
            }
            ZipUtil.pack(new File(pkgDir), new File(pkgDir + ".zip"));
            File zipFile = new File(pkgDir + ".zip");
            FileInputStream fis = new FileInputStream(zipFile);

            URL url = new URL(SERVER + "question");
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Content-Type", "application/octet-stream");
            httpURLConnection.setRequestProperty("Content-Disposition", "attachment; filename=\"" + zipFile.getName() + "\"");

            OutputStream outputStream = httpURLConnection.getOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush();
            outputStream.close();
            fis.close();

            zipFile.delete();
            File questionFile = new File(pkgDir + "/question.txt");
            questionFile.delete();

            if (httpURLConnection.getResponseCode() != 200)
            {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Fout");
                alert.setHeaderText("Er is iets fout gegaan");
                alert.showAndWait();
                return;
            }
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succes");
            alert.setHeaderText("Vraag is verstuurd");
            alert.showAndWait();

            ((Stage) root.getScene().getWindow()).close();
        } catch (Exception e)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Fout");
            alert.setHeaderText("Er is iets fout gegaan");
            alert.showAndWait();
            throw new RuntimeException(e);
        }
    }
}
