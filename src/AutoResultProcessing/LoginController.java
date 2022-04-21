package AutoResultProcessing;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import java.sql.*;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.swing.text.html.ImageView;

/**
 * Login Controller.
 */
public class LoginController extends AnchorPane implements Initializable, ControlledScreen {
    ScreenController myController;
    
    @Override
    public void setScreenParent(ScreenController screenParent){
        myController = screenParent;
    }

    @FXML
    private TextField userId;
    @FXML
    private PasswordField password;
    @FXML
    public Label errorMessage;
    @FXML
    private Button btnClose;     
    @FXML
    private AnchorPane container;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeDB();
        errorMessage.setText("");
    }
    
    @FXML
    private void closeButtonAction(ActionEvent event) {
        Stage stage = (Stage) btnClose.getScene().getWindow();
        stage.close();
    }
        
    @FXML
    private void hover_in(MouseEvent event){
        ImageView closeImage = (ImageView) btnClose.getGraphic();
        closeImage.setEffect(new ColorAdjust(0, -1, 0.71, 0));
    }
    
    @FXML
    private void hover_out(MouseEvent event){
        ImageView closeImage = (ImageView) btnClose.getGraphic();
        closeImage.setEffect(new ColorAdjust(0, -1, 0, 0));
    }
        
    @FXML
    private void requestfocus(MouseEvent event) {
        container.requestFocus();
    }
    
    double yOffset = 0;
    double xOffset = 0;
    
    @FXML
    private void determine(MouseEvent event){
        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        xOffset = stage.getX() - event.getScreenX();
        yOffset = stage.getY() - event.getScreenY();
    }
    
    @FXML
    private void pick(MouseEvent event){
        Scene scene = ((Node)event.getSource()).getScene();
        Stage stage = (Stage) scene.getWindow();
        scene.setCursor(Cursor.CLOSED_HAND);
        stage.setX(event.getScreenX() + xOffset);
        stage.setY(event.getScreenY() + yOffset);
    }
    
    @FXML
    private void drop(MouseEvent event){
        Scene scene = ((Node)event.getSource()).getScene();
        scene.setCursor(Cursor.HAND);
    }
    
    @FXML
    public void processLogin(ActionEvent event) throws SQLException, ClassNotFoundException, IOException {
        String queryString;
        PreparedStatement statement;
        ResultSet result;
        if(isOnline(userId.getText()))
            displayErrorMessage("This User is already Online");
        else{
            if(userId.getText().matches("[\\d]{2,}.*")){

                queryString = "Select Student.Institution, department, matric, lastname, firstname, othername, Student.image, "
                    + "institution.image from student, institution where matric  = ? AND LastName = ? "
                        + "AND Student.institution = Institution.Institution ";
                statement = connection.prepareStatement(queryString);
                statement.setString(1, userId.getText());
                statement.setString(2, password.getText());

                AnchorPane studentPageRoot = (AnchorPane) ScreenController.screens.get(AutoResultProcessing.studentPage);

                result = statement.executeQuery();
                if(result.next()){
                    myController.setScreen(AutoResultProcessing.studentPage);
                    ((Label) studentPageRoot.lookup("#matric")).setText(userId.getText());
                    ((Label) studentPageRoot.lookup("#department")).setText(result.getString("department"));
                    ((Label) studentPageRoot.lookup("#institution")).setText(result.getString("institution").toUpperCase());
                    ((Label) studentPageRoot.lookup("#fullName")).setText(result.getString("lastName")+" . "+ 
                                result.getString("firstName").charAt(0)+" . "+
                                result.getString("otherName"));
                    ((ImageView) studentPageRoot.lookup("#userImage")).setImage(new Image(result.getBlob("Student.image").getBinaryStream()));
                    ((ImageView) studentPageRoot.lookup("#institutionImage")).setImage(new Image(result.getBlob("Institution.image").getBinaryStream()));
                    StudentPageController.matric = ViewStudentByCoursesController.lecturerId = userId.getText();
                    userId.setText("");
                    password.setText("");
                    AutoResultProcessing.institution = result.getString("Student.institution");
                    queryString = "Update Student SET Status = 'Online' WHERE firstname = ?";
                    statement = connection.prepareStatement(queryString);
                    statement.setString(1, userId.getText());
                    statement.executeUpdate();
                }
                else
                    displayErrorMessage("Username/Password is incorrect");
            }
            else if(userId.getText().matches("[\\w]{3,}")){
                queryString = "Select LevelOfEducation, FirstName, OtherName, LastName, LecturerInstitution, "
                    + "LecturerDepartment, Lecturer.Image, Institution.Image from Lecturer, Institution where FirstName = ? "
                        + "AND LastName = ? AND LecturerInstitution = Institution.institution";
                statement = connection.prepareStatement(queryString);
                statement.setString(1, userId.getText());
                statement.setString(2, password.getText());

                AnchorPane viewResultsPageRoot = (AnchorPane) ScreenController.screens.get(AutoResultProcessing.viewResults);
                result = statement.executeQuery();
                if(result.next()){
                    myController.setScreen(AutoResultProcessing.viewResults);
                    ((ImageView) viewResultsPageRoot.lookup("#lecturerImage")).setImage(new Image(result.getBlob("Image").getBinaryStream()));
                    ((Label) viewResultsPageRoot.lookup("#institution")).setText(result.getString("LecturerInstitution").toUpperCase());
                    ((Label) viewResultsPageRoot.lookup("#department")).setText(result.getString("LecturerDepartment"));
                    ((ImageView) viewResultsPageRoot.lookup("#institutionImage")).setImage(new Image(result.getBlob("Institution.Image").getBinaryStream()));
                    ((Label) viewResultsPageRoot.lookup("#lecturerName")).setText((result.getString("LevelOfEducation")+" "
                            +result.getString("lastName") + " . "+ result.getString("firstName").charAt(0) + " . "
                            + result.getString("otherName")).toUpperCase());

                    SearchStudentsController.lecturerId = ViewStudentByCoursesController.lecturerId = userId.getText();
                    
                    userId.setText("");
                    password.setText("");
                    AutoResultProcessing.institution = result.getString("LecturerInstitution");
                    queryString = "Update Lecturer SET Status = 'Online' WHERE firstname = ?";
                    statement = connection.prepareStatement(queryString);
                    statement.setString(1, userId.getText());
                    statement.executeUpdate();
                }
                else
                    displayErrorMessage("Username/Password is incorrect");
            }
        }
    }
    
    private void displayErrorMessage(String errorMessage){
        this.errorMessage.setText(errorMessage);
        new Timeline(new KeyFrame(Duration.seconds(0),new KeyValue(this.errorMessage.opacityProperty(), 0), new KeyValue(this.errorMessage.opacityProperty(), 1)), 
                new KeyFrame(Duration.seconds(4), new KeyValue(this.errorMessage.opacityProperty(), 1, Interpolator.EASE_OUT)),
                new KeyFrame(Duration.seconds(5), new KeyValue(this.errorMessage.opacityProperty(), 0, Interpolator.EASE_BOTH))).play();
    }
      
    private boolean isOnline(String username) throws SQLException{
        String queryString; PreparedStatement preparedStatement; ResultSet rSet; boolean output = false;
        if(userId.getText().matches("[\\w]{3,}")){
            queryString = "Select Status FROM Lecturer WHERE firstname = ?";
            preparedStatement = connection.prepareStatement(queryString);
            preparedStatement.setString(1, userId.getText());
            rSet = preparedStatement.executeQuery();
            output = (rSet.next() && rSet.getString("status").equalsIgnoreCase("Online"));
        }else if(userId.getText().matches("[\\d]{2,}.*")){
            queryString = "Select Status FROM Student WHERE firstname = ?";
            preparedStatement = connection.prepareStatement(queryString);
            preparedStatement.setString(1, userId.getText());
            rSet = preparedStatement.executeQuery();
            output = (rSet.next() && rSet.getString("status").equalsIgnoreCase("Online"));
        }
        
        return output;
    }
    
    Connection connection;
    public void initializeDB() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            System.out.println("Driver loaded");

            connection = DriverManager.getConnection("jdbc:mysql://localhost/studentrecords", "root", "");
            System.out.println("Database Connection");
        } catch (Exception ex) { ex.printStackTrace(); }
    }
}
