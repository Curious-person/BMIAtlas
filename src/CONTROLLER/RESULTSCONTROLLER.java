package CONTROLLER;

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
import javafx.stage.Stage;
import models.Database;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;


public class RESULTSCONTROLLER implements Initializable {

     @FXML
    private TextField heighttext, weighttext;

    @FXML
    private Button calculate;

    @FXML
    private Label results, calculation, calculation1;

    @FXML
    private Label resultLabel, results2;

    @FXML
    private Button addhistorybtn, retrybtn, viewhistorybtn;

    @FXML
    Stage stage;

    private Scene scene;


    double height, weight;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadDataFromDatabase();
    }

    // EXIT & MIN BUTTONS-------------------------------------------------------------------
    public void closeWindow(ActionEvent event) {
        Platform.exit();
    }

    public void minimizeWindow(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setIconified(true);
    }

    public void switchtotable(ActionEvent event) throws IOException {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/VIEW/TABLE.fxml"));
            Parent root = loader.load();
            
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void addToHistory(ActionEvent event) {
        addtohistory();
    }

    private void addtohistory() {
        try {
            Connection connection = Database.DBConnect();
            if (connection != null) {
    
                String selectLatestQuery = "SELECT * FROM bmi_results ORDER BY bmi_id DESC LIMIT 1";
                PreparedStatement selectStatement = connection.prepareStatement(selectLatestQuery);
                ResultSet resultSet = selectStatement.executeQuery();
    
                if (resultSet.next()) {
                    double height = resultSet.getDouble("height");
                    double weight = resultSet.getDouble("weight");
                    double bmi = resultSet.getDouble("bmi");
                    String category = resultSet.getString("category");
                    int bmiId = resultSet.getInt("bmi_id");
    
                    String insertQuery = "INSERT INTO bmi_history (height, weight, bmi, category) VALUES (?, ?, ?, ?)";
                    PreparedStatement insertStatement = connection.prepareStatement(insertQuery);
                    insertStatement.setDouble(1, height);
                    insertStatement.setDouble(2, weight);
                    insertStatement.setDouble(3, bmi);
                    insertStatement.setString(4, category);
                    insertStatement.executeUpdate();
    
                    insertStatement.close();
                    resultSet.close();
                    selectStatement.close();
    
                    String deleteQuery = "DELETE FROM bmi_results WHERE bmi_id = ?";
                    PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery);
                    deleteStatement.setInt(1, bmiId); // Use the stored bmi_id
                    deleteStatement.executeUpdate();
                    deleteStatement.close();
    
                    // Show alert
                    showAlert("Success", "Data added to history");
    
                    loadDataFromDatabase();
                }
    
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    

    @FXML
    private void btncalculate1(ActionEvent event) {
        String heightText = heighttext.getText();
        String weightText = weighttext.getText();

        if (heightText.isEmpty()) {
            showAlert("Error", "Please input height.");
            return;
        }

        if (weightText.isEmpty()) {
            showAlert("Error", "Please input weight.");
            return;
        }

        try {
            height = Double.parseDouble(heightText);
            weight = Double.parseDouble(weightText);

            // Check if height and weight are positive numbers
            if (height <= 0 || weight <= 0) {
                showAlert("Error", "Height and weight must be positive.");
                return;
            }

            // Calculate BMI
            double result = calculateResult(height, weight);
            String category = calculateCategory(result);

            Connection connection = Database.DBConnect();
            if (connection != null) {
                int latestBmiId = getLatestBmiId(connection);

                if (latestBmiId > 0) {
                    // Update existing data
                    String updateQuery = "UPDATE bmi_results SET height=?, weight=?, bmi=?, category=? WHERE bmi_id=?";
                    PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
                    updateStatement.setDouble(1, height);
                    updateStatement.setDouble(2, weight);
                    updateStatement.setDouble(3, result);
                    updateStatement.setString(4, category);
                    updateStatement.setInt(5, latestBmiId);
                    updateStatement.executeUpdate();

                    updateStatement.close();
                } else {
                    // Insert new data
                    String insertQuery = "INSERT INTO bmi_results (height, weight, bmi, category) VALUES (?, ?, ?, ?)";
                    PreparedStatement insertStatement = connection.prepareStatement(insertQuery);
                    insertStatement.setDouble(1, height);
                    insertStatement.setDouble(2, weight);
                    insertStatement.setDouble(3, result);
                    insertStatement.setString(4, category);
                    insertStatement.executeUpdate();

                    insertStatement.close();
                }

                connection.close();

                // Update UI to reflect changes
                loadDataFromDatabase();
            }
        } catch (NumberFormatException e) {
            results.setVisible(true);
            results.setText("Please input a number");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    
    private int getLatestBmiId(Connection connection) throws SQLException {
        int latestBmiId = 0;
        String query = "SELECT MAX(bmi_id) AS latest_id FROM bmi_results";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            latestBmiId = resultSet.getInt("latest_id");
        }
        resultSet.close();
        preparedStatement.close();
        return latestBmiId;
    }
    
    private String calculateCategory(double result) {
        if (result < 18.5) {
            return "Underweight";
        } else if (result < 25.0) {
            return "Normal weight";
        } else if (result < 30.0) {
            return "Overweight";
        } else {
            return "Obese";
        }
    }

    private double calculateResult(double height, double weight) {
        double heightconvert = height / 100; 
        double r = (weight) / (heightconvert * heightconvert);
        r = Math.round(r * 10.0) / 10.0;
        return r;
    }

    private void loadDataFromDatabase() {
        try {
            Connection connection = Database.DBConnect();
            if (connection != null) {
                String query = "SELECT * FROM bmi_results ORDER BY bmi_id DESC LIMIT 1";
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
                    results2.setText(category);
                    results.setText(category);
                } else {
                    System.out.println("No data found in bmi_results table.");
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