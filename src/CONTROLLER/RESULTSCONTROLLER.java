package Controller;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import models.Database;

public class ResultsController implements Initializable {
    @FXML
    private TextField heighttext, weighttext;

    @FXML
    Stage stage;

    @FXML
    private Label calculation, calculation1, results;
    
    @FXML
    private Button addtohistory; 

    @FXML
    private AnchorPane anchorRoot;

    @FXML
    private StackPane parentContainer;

    @FXML
    private ImageView history;
    

    // EXIT & MIN BUTTONS-------------------------------------------------------------------
    public void closeWindow(ActionEvent event) {
        Platform.exit();
    }
    
    public void minimizeWindow(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setIconified(true);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadDataFromDatabase();
    }


    @FXML
    private void addtohistory(ActionEvent event) throws IOException {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/History.fxml"));
            Parent root = loader.load();
            
            Scene scene = addtohistory.getScene();
            Stage currentStage = (Stage) scene.getWindow();
    
            scene.setRoot(root);
            
            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void history(MouseEvent event) throws IOException {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/History.fxml"));
            Parent root = loader.load();
            
            Scene scene = history.getScene();
            Stage currentStage = (Stage) scene.getWindow();
    
            scene.setRoot(root);
            
            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

        private void loadDataFromDatabase() {
        try {
            Connection connection = Database.DBConnect();
            if (connection != null) {
                String query = "SELECT * FROM bmi_calculation ORDER BY calculationID DESC LIMIT 1";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    double height = resultSet.getDouble("height");
                    double weight = resultSet.getDouble("weight");
                    double bmi = resultSet.getDouble("bmi");
                    String category = resultSet.getString("category");

                    heighttext.setText(String.valueOf(height));
                    weighttext.setText(String.valueOf(weight));
                    calculation.setText(String.valueOf(bmi));
                    calculation1.setText(String.valueOf(bmi));
                    results.setText(category);
                } else {
                    System.out.println("No data found in bmi_calculation table.");
                }
                resultSet.close();
                preparedStatement.close();
                connection.close();
            } else {
                System.out.println("Failed to connect to the database.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
