package Controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.Node;

public class LoginController implements Initializable {

    @FXML
    Stage stage;
    
    @FXML
    private Label signup;

    @FXML
    private Button login; 

    @FXML
    private AnchorPane anchorRoot;

    @FXML
    private StackPane parentContainer;


    // EXIT & MIN BUTTONS-------------------------------------------------------------------
    public void closeWindow(ActionEvent event) {
        Platform.exit();
    }
    
    public void minimizeWindow(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setIconified(true);
    }

    @FXML
    private void loadSecond(ActionEvent event) throws IOException {
       Parent root = FXMLLoader.load(getClass().getResource("/View/Calculator.fxml"));
        Scene scene = login.getScene();
        root.translateXProperty().set(scene.getHeight());

        parentContainer.getChildren().add(root);

        Timeline timeline = new Timeline();
        KeyValue kv = new KeyValue(root.translateXProperty(), 0, Interpolator.EASE_IN);
        KeyFrame kf = new KeyFrame(Duration.seconds(.5), kv);

        timeline.getKeyFrames().add(kf);
        timeline.setOnFinished(t -> {
            parentContainer.getChildren().remove(anchorRoot);
        });

        timeline.play();
    }

    @FXML
    private void signup(MouseEvent event) throws IOException {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/Signup.fxml"));
            Parent root = loader.load();
            
            Scene scene = signup.getScene();
            Stage currentStage = (Stage) scene.getWindow();
    
            scene.setRoot(root);
            
            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // TODO Auto-generated method stub
    }

}