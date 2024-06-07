package CONTROLLER;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import models.Database;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class TABLECONTROLLER implements Initializable {

    @FXML
    private TableView<ObservableList<String>> tableView;

    @FXML
    private TableColumn<ObservableList<String>, String> heightCol;

    @FXML
    private TableColumn<ObservableList<String>, String> weightCol;

    @FXML
    private TableColumn<ObservableList<String>, String> bmiResultCol;

    @FXML
    private TableColumn<ObservableList<String>, String> categoryCol;

    @FXML
    private TableColumn<ObservableList<String>, String> dateAddedCol;

    @FXML
    Stage stage;

    @FXML
    private Button deleteButton;

    @FXML
    private ImageView back;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadDataFromDatabase();
    }

    // EXIT & MIN BUTTONS-------------------------------------------------------------------
    public void closeWindow(ActionEvent event) {
        Platform.exit();
    }

    public void minimizeWindow(ActionEvent event) {
        Stage stage = (Stage) tableView.getScene().getWindow();
        stage.setIconified(true);
    }

    // BACK BUTTON-------------------------------------------------------------------
    @FXML
    private void loadBack(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/VIEW/CALCULATOR.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            
            // Get the current stage
            Stage currentStage = (Stage) tableView.getScene().getWindow();
            
            // Set the new scene
            currentStage.setScene(scene);
            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDeleteButton(ActionEvent event) {
        ObservableList<String> selectedItem = tableView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            tableView.getItems().remove(selectedItem);

            deleteFromDatabase(selectedItem);

            showAlert("Success", "Data deleted from history.");
        } else {
            showAlert("Error", "Please select a row.");
        }
    }

    private void deleteFromDatabase(ObservableList<String> deletedData) {
        try {
            Connection connection = Database.DBConnect();
            if (connection != null) {

                String deleteQuery = "DELETE FROM bmi_history WHERE date_added=?";
                PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery);
                preparedStatement.setString(1, deletedData.get(4)); // Assuming date_added is the last column
                preparedStatement.executeUpdate();

                preparedStatement.close();
                connection.close();
            } else {
                showAlert("Error", "Failed to connect to the database.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void loadDataFromDatabase() {
        ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
        try {
            Connection connection = Database.DBConnect();
            if (connection != null) {
                String query = "SELECT * FROM bmi_history";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    ObservableList<String> row = FXCollections.observableArrayList();
                    row.add(resultSet.getString("height"));
                    row.add(resultSet.getString("weight"));
                    row.add(resultSet.getString("bmi"));
                    row.add(resultSet.getString("category"));
                    row.add(resultSet.getString("date_added"));
                    data.add(row);
                }

                resultSet.close();
                preparedStatement.close();
                connection.close();
            } else {
                // Connection is null
                System.out.println("Failed to connect to the database.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        tableView.setItems(data);

        heightCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(0)));
        weightCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(1)));
        bmiResultCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(2)));
        categoryCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(3)));
        dateAddedCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(4)));
        
    }
}
