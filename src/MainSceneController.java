
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;


public class MainSceneController {

    @FXML
    private TextField heighttext, weighttext, agetext;

    @FXML
    private Button calculate;

    @FXML
    private Label results, calculation;

     @FXML
    private CheckBox femalecheckbox, malecheckbox;

    double height, weight, age;


    public void btncalculate(ActionEvent event) {
        try {
            height = Double.parseDouble(heighttext.getText());
            weight = Double.parseDouble(weighttext.getText());
            age = Double.parseDouble(agetext.getText());
            

            double result = calculateResult(height, weight);
            calculation.setText(Double.toString(result));
            
            
            switch (calculateCategory(age, result)) {
                case "Underweight":
                    results.setVisible(true);
                    results.setText("Underweight timbang mo lods");
                    calculation.setVisible(true);
                    break;
                case "Normal weight":
                    results.setVisible(true);
                    results.setText("Normal weight timbang mo lods");
                    calculation.setVisible(true);
                    break;
                case "Overweight":
                    results.setVisible(true);
                    results.setText("Overweight timbang mo lods");
                    calculation.setVisible(true);
                    break;
                case "Obese":
                    results.setVisible(true);
                    results.setText("Obese timbang mo lods");
                    calculation.setVisible(true);
                    break;
                case "babae":
                    results.setVisible(true);
                    results.setText("babae timbang mo lods");
                    calculation.setVisible(true);
                    break;
                default:
                    results.setVisible(true);
                    results.setText("Invalid input");
                    calculation.setVisible(true);
                    break;
            }


        } catch (NumberFormatException e) {
            results.setVisible(true);
            results.setText("Please input a number");
        } catch (Exception e) {
            // results.setVisible(true);
            // results.setText("Error: " + e);
            System.out.println(e);
        }
    }
    
    private String calculateCategory(double age, double result) {
        if (malecheckbox.isSelected()) {
            if (age >= 18.0) {
                if (result < 18.5) {
                    return "Underweight";
                } else if (result < 25.0) {
                    return "Normal weight";
                } else if (result < 30.0) {
                    return "Overweight";
                } else {
                    return "Obese";
                }
    
            } if (age >= 2 && age < 18) { // Consider children and adolescents aged 2 to 17
                if (result < 5) {
                    return "Underweight"; // Adjust threshold based on age-specific BMI percentiles
                } else if (result >= 5 && result < 85) {
                    return "Normal weight";
                } else if (result >= 85 && result < 95) {
                    return "Overweight";
                } else {
                    return "Obese";
                }
            } else {
                return "Invalid or inadequate input";
            }
        } else if (femalecheckbox.isSelected()) {
            return "babae";
        } 
        return "Please select gender";
   
    }

    private double calculateResult(double height, double weight) {

        double heightconvert = height/100; 
        double r = (weight) / (heightconvert * heightconvert);
        r = Math.round(r * 100.00/100.00);
        return r;
    }

}
