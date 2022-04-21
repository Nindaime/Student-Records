/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AutoResultProcessing;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
/**
 *
 * @author PETER
 */
public class AutoResultProcessing extends Application {
    public static String login = "LoginPage";
    public static String loginFile = "Login.fxml";
    public static String viewResults = "SearchStudents";
    public static String viewResultsFile = "SearchStudents.fxml";
    public static String viewStudentByCourse = "ViewStudentsByCourses";
    public static String viewStudentByCourseFile = "ViewStudentByCourses.fxml";
    public static String studentPage = "StudentPage";
    public static String studentPageFile = "StudentPage.fxml";
    public static String currentSemester = "second";
    public static String currentSession = "2017/2018";
    public static String institution = "";

    @Override
    public void start(Stage stage) throws Exception {
        ScreenController mainContainer = new ScreenController();
        mainContainer.loadScreen(login, loginFile);
        mainContainer.loadScreen(studentPage, studentPageFile);
        mainContainer.loadScreen(viewResults, viewResultsFile);
        mainContainer.loadScreen(viewStudentByCourse, viewStudentByCourseFile);
        
        mainContainer.setScreen(login);
        
        Scene scene = new Scene(mainContainer);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setScene(scene);
        stage.show();
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
/**
 * re-color student result background
 */