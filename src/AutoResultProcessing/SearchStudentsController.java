/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AutoResultProcessing;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import java.sql.*;
import java.util.ArrayList;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
 
import javafx.scene.layout.VBox;
/**
 *
 * @author PETER
 */
public class SearchStudentsController implements Initializable, ControlledScreen {
    private PreparedStatement preparedStatement;
    
    ScreenController myController;
    
    public static String lecturerId, examOfficer;
    
    @FXML
    private void goToViewStudentByCourse(ActionEvent event){
        myController.setScreen(AutoResultProcessing.viewStudentByCourse);
        if(!content.getChildren().isEmpty())content.getChildren().clear();
    }
    
    
    @FXML
    private void logout(ActionEvent event){
        myController.setScreen(AutoResultProcessing.login);
        lecturerId = ViewStudentByCoursesController.lecturerId = "";
        searchField.setText("");
        content.getChildren().clear();
        try{
            String queryString = "Update Lecturer SET Status = 'Offline' WHERE firstname = ?";
            preparedStatement = connection.prepareStatement(queryString);
            preparedStatement.setString(1, (lecturerId.equals("") ? examOfficer : lecturerId));
            preparedStatement.executeUpdate();
        }catch(SQLException ex){}
        
    }
    
    @Override
    public void setScreenParent(ScreenController screenParent){
        myController = screenParent;
    }
    
    @FXML
    private Button btnClose;
    
    @FXML
    private TextField searchField;
        
    @FXML
    private AnchorPane container;
    
    @FXML
    private void requestfocus(MouseEvent event){
        container.requestFocus();
    }
    
    static Connection connection;
    public void initializeDB(){
        try{
            Class.forName("com.mysql.jdbc.Driver");
            System.out.println("Driver loaded");
            
            connection = DriverManager.getConnection("jdbc:mysql://localhost/studentrecords", "root", "");
            System.out.println("Database Connection");
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
        
    }
    
    @FXML
    private void addButtonHoverIn(MouseEvent event){
        ((Button)event.getSource()).setStyle("-fx-border-color: white; "
                + "-fx-background-radius: 100; "
                + "-fx-background-color:  #008CBA; "
                + "-fx-border-radius: 100; "
                + "-fx-border-width: 1.5; "
                + "-fx-border-insets: -1;"
                + "-fx-padding: 0;");
    }
    
    @FXML
    private void addButtonHoverOut(MouseEvent event){
        ((Button)event.getSource()).setStyle("-fx-border-color: transparent; "
                + "-fx-background-radius: 100; "
                + "-fx-background-color:  #008CBA; "
                + "-fx-padding: 0;");
    }
    
    @FXML
    private VBox content;

    public void findStudents(){
        if(!content.getChildren().isEmpty()) 
            content.getChildren().clear();
        
        try{
            String queryString;
            if(!lecturerId.equals("")){
                queryString = "SELECT distinct department, Student.firstName, Student.otherName, Student.lastName, "
                        + "Matric, Studentlevel, Student.image FROM student, lecturer WHERE (Matric LIKE ? "
                        + "OR Student.firstName LIKE ? OR Student.lastName LIKE ? OR Student.otherName LIKE ? "
                        + "OR Studentlevel LIKE ?) AND (Lecturer.firstName = ? AND LecturerDepartment = department)";
            }else{
                queryString = "SELECT distinct department, Student.firstName, Student.otherName, Student.lastName, "
                        + "Matric, Studentlevel, Student.image FROM student WHERE Matric LIKE ? "
                        + "OR Student.firstName LIKE ? OR Student.lastName LIKE ? OR Student.otherName LIKE ? "
                        + "OR Studentlevel LIKE ?";
            }
            preparedStatement = connection.prepareStatement(queryString);
            preparedStatement.setString(1, searchField.getText()+"%");
            preparedStatement.setString(2, searchField.getText()+"%");
            preparedStatement.setString(3, searchField.getText()+"%");
            preparedStatement.setString(4, searchField.getText()+"%");
            preparedStatement.setString(5, searchField.getText()+"%");
            if(!lecturerId.equals(""))
                preparedStatement.setString(6, lecturerId);
                
            String MatricNo; HBox root;
            ResultSet resultSet = preparedStatement.executeQuery();
            ArrayList<String> matricList = new ArrayList<>();
            if(resultSet.next()){
                resultSet.beforeFirst();
                while(resultSet.next()){
                    MatricNo = resultSet.getString("Matric");
                    root = new SearchResult(resultSet.getString("Studentlevel"), 
                            MatricNo, 
                            resultSet.getString("department"),
                            resultSet.getString("lastName")+" . "+ 
                            resultSet.getString("otherName").charAt(0)+" . "+
                            resultSet.getString("firstName"),
                            resultSet.getBlob("image"),
                            searchField.getText());

                    matricList.add(MatricNo);
                    content.getChildren().add(root);
                }
                content.getChildren().stream().forEach((v) -> {
                    v.setOnMouseClicked(e ->  showGrades(matricList.get(content.getChildren().indexOf(v))));
                });
            }else{
                root = new SearchResult();
                content.getChildren().add(root);
            }

        }
        catch(SQLException ex){ ex.printStackTrace(); }
    }
    
        
    public static char getGrade(String result) {
        double score = Double.parseDouble(result);

        if (score >= 70) 
            return 'A';
         else if (score >= 60) 
            return 'B';
         else if (score >= 50) 
            return 'C';
         else if (score >= 45) 
            return 'D';
         else if (score >= 40) 
            return 'E';
         else 
            return 'F';
    }
    
    private PreparedStatement preparedStatement2; 
    double cgpa, cummulativeGradeUnits, cummulativeSemesterUnits;
    String semester;
    
    private void showSemester(String semester, String level, String matricNo){
        ObservableList<Results> mainData = FXCollections.observableArrayList();   
//        mainData.clear(); confirm if this line of code is redundant
        try{
            String queryString = "SELECT ResultCourseCode, CourseTitle, CourseUnit, ResultScore FROM Course, Result "
                    + "WHERE ResultMatric = ? AND CourseCode = ResultCourseCode AND CourseSemester = ? "
                    + "AND ResultScore is not null AND Courselevel = ? ORDER BY CourseCode ASC ";

            preparedStatement2 = connection.prepareStatement(queryString);
            
            preparedStatement2.setString(1, matricNo);
            preparedStatement2.setString(2, semester);
            preparedStatement2.setString(3, level);

            ResultSet result = preparedStatement2.executeQuery();
            ArrayList<Character> grades = new ArrayList<>();
            ArrayList<Integer> units = new ArrayList<>();
            int j = 0;
            
            while(result.next()){
                Results result1 = new Results(result.getString("ResultCourseCode"),
                                                result.getString("CourseTitle"),
                                                result.getInt("CourseUnit"),
                                                getGrade(result.getString("ResultScore"))+"");
                mainData.add(result1);
                grades.add(getGrade(result.getString("ResultScore")));
                units.add(result.getInt("CourseUnit"));
            }
            
            for(Character s: grades){
                System.out.println("s: "+s);
            }
            
            for(int i: units){
                System.out.println("i: "+i);
            }
            double cummulativeSemesterGradeUnits = 0;//sum of units * grade per semester
            
            for(int i = 0; i < grades.size(); i++){
                Character s = grades.get(i);
                switch (s) {
                    case 'A':
                        cummulativeSemesterGradeUnits += 5 * units.get(i);
                        System.out.println("For A:" + (5 * units.get(i)) + "; unit: " + units.get(i));
                        break;
                    case 'B':
                        cummulativeSemesterGradeUnits += 4 * units.get(i);
                        System.out.println("For B:" + (4 * units.get(i)) + "; unit: " + units.get(i));
                        break;
                    case 'C':
                        cummulativeSemesterGradeUnits += 3 * units.get(i);
                        System.out.println("For C:" + (3 * units.get(i)) + "; unit: " + units.get(i));
                        break;
                    case 'D':
                        cummulativeSemesterGradeUnits += 2 * units.get(i);
                        System.out.println("For D:" + (2 * units.get(i)) + "; unit: " + units.get(i));
                        break;
                    case 'E':
                        cummulativeSemesterGradeUnits += 1 * units.get(i);
                        System.out.println("For E:" + (1 * units.get(i)) + "; unit: " + units.get(i));
                        break;
                    case 'F':
                        cummulativeSemesterGradeUnits += 0 * units.get(i);
                        System.out.println("For F:" + (0 * units.get(i)) + "; unit: " + units.get(i));
                        break;
                }
            }
            
//            for(Character s: grades){
//                int index = grades.indexOf(s);
//                System.out.println("Index is: "+index);
//                switch(s){
//                    case 'A':
//                        cummulativeSemesterGradeUnits += 5 * units.get(index);
//                        System.out.println("For A:"+(5 * units.get(index))+"; unit: "+units.get(index));
//                        break;
//                    case 'B':
//                        cummulativeSemesterGradeUnits += 4 * units.get(index);
//                        System.out.println("For B:"+(4 * units.get(index))+"; unit: "+units.get(index));
//                        break;
//                    case 'C':
//                        cummulativeSemesterGradeUnits += 3 * units.get(index);
//                        System.out.println("For C:"+(3 * units.get(index))+"; unit: "+units.get(index));
//                        break;
//                    case 'D':
//                        cummulativeSemesterGradeUnits += 2 * units.get(index);
//                        System.out.println("For D:"+(2 * units.get(index))+"; unit: "+units.get(index));
//                        break;
//                    case 'E':
//                        cummulativeSemesterGradeUnits += 1 * units.get(index);
//                        System.out.println("For E:"+(1 * units.get(index))+"; unit: "+units.get(index));
//                        break;
//                    case 'F':
//                        cummulativeSemesterGradeUnits += 0 * units.get(index);
//                        System.out.println("For F:"+(0 * units.get(index))+"; unit: "+units.get(index));
//                        break;
//                }
//            }
            
            int semesterUnits = 0;
//            for(int i: units)
//                semesterUnits += i;
            semesterUnits = units.stream().map((i) -> i).reduce(semesterUnits, Integer::sum);
            
            cummulativeGradeUnits += cummulativeSemesterGradeUnits;
            System.out.println("Semester CourseUnitCummulative: "+cummulativeSemesterGradeUnits);
            System.out.println("Semester Unit Aggregate: "+semesterUnits);
            double gpa = (double) (cummulativeSemesterGradeUnits / semesterUnits);
            cummulativeSemesterUnits += semesterUnits;
            cgpa =  cummulativeGradeUnits / cummulativeSemesterUnits;
       
            Pane studentResult = new StudentResult(mainData, true, matricNo, level, semester, String.format("%.2f", gpa), String.format("%.2f", cgpa));
            content.getChildren().add(studentResult);
//            ((TableView<Results>)studentResult.getChildren().get(2)).getColumns().get(3).setOnEditCommit(e -> showGrades(matricNo));
        }catch(SQLException ex){
            ex.printStackTrace();
        }
    }
    
    public PreparedStatement preparedStatement1;
    //reorder results to start from current level to first level
    public void showGrades(String matricNo){
        System.out.println("Show Grades Invoked");
        semester = "";
        cgpa = 0;
        cummulativeGradeUnits = 0;
        cummulativeSemesterUnits = 0;
        if(!content.getChildren().isEmpty())
            content.getChildren().clear();
        
        try{
            String queryString = "SELECT Studentlevel FROM Student WHERE Matric = ? ";

            preparedStatement1 = connection.prepareStatement(queryString);
            preparedStatement1.setString(1, matricNo);
            
            ResultSet resultSet = preparedStatement1.executeQuery();
            
            String levelString = "";
            if(resultSet.next()){
                levelString = resultSet.getString("Studentlevel");
                semester = AutoResultProcessing.currentSemester;
            }
                
            String levelCounter = "100";
            while(!(levelCounter.compareTo(levelString) > 0)){
                switch(levelCounter){
                    case "100":
                        showSemester("first", "100", matricNo);
                        if(!levelCounter.equals(levelString))
                            showSemester("second", "100", matricNo);
                        else if(semester.equalsIgnoreCase("second")) //If there's a second semester result
                            showSemester("second", "100", matricNo);
                        levelCounter = "200";
                        break;
                    case "200":
                        showSemester("first", "200", matricNo);
                        if(!levelCounter.equals(levelString))
                            showSemester("second", "200", matricNo);
                        else if(semester.equalsIgnoreCase("second")) //If there's a second semester result
                            showSemester("second", "200", matricNo);
                        levelCounter = "300";
                        break;
                    case "300":
                        showSemester("first", "300", matricNo);
                        if(!levelCounter.equals(levelString))
                            showSemester("second", "300", matricNo);
                        else if(semester.equalsIgnoreCase("second")) //If there's a second semester result
                            showSemester("second", "300", matricNo);
                        levelCounter = "400";
                        break;
                    case "400":
                        showSemester("first", "400", matricNo);
                        if(!levelCounter.equals(levelString))
                            showSemester("second", "400", matricNo);
                        else if(semester.equalsIgnoreCase("second")) //If there's a second semester result
                            showSemester("second", "400", matricNo);
                        levelCounter = "500";
                        break;
                    case "500":
                        showSemester("first", "500", matricNo);
                        if(!levelCounter.equals(levelString))
                            showSemester("second", "500", matricNo);
                        else if(semester.equalsIgnoreCase("second")) //If there's a second semester result
                            showSemester("second", "500", matricNo);
                        levelCounter = "600";
                        break;
                    case "600":
                        showSemester("first", "600", matricNo);
                        if(!levelCounter.equals(levelString))
                            showSemester("second", "600", matricNo);
                        else if(semester.equalsIgnoreCase("second")) //If there's a second semester result
                            showSemester("second", "600", matricNo);
                        break;
                }
            }
        }catch(SQLException ex){}

    }
    
    @FXML
    private void closeButtonAction(ActionEvent event) {
        Stage stage = (Stage) btnClose.getScene().getWindow();
        stage.close();
    }
    
    @FXML
    private void hover_in(MouseEvent event){
        ImageView closeImage = (ImageView) btnClose.getGraphic();
        closeImage.setEffect(new ColorAdjust(0, 1, 0.25, 0));
    }
    
    @FXML
    private void hover_out(MouseEvent event){
        ImageView closeImage = (ImageView) btnClose.getGraphic();
        closeImage.setEffect(null);
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
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initializeDB(); //Connect to Database
        searchField.textProperty().addListener(o ->  findStudents());
    }    
    
    //Data Format of Result
    public static class Results{
        private final SimpleStringProperty resultCourseCode;
        private final SimpleStringProperty resultCourseTitle;
        private final SimpleIntegerProperty resultUnits;
        private final SimpleStringProperty resultGrade;
        
        public Results(String courseCode, String courseTitle, int units, String grade){
            this.resultCourseCode = new SimpleStringProperty(courseCode);
            this.resultCourseTitle = new SimpleStringProperty(courseTitle);
            this.resultUnits = new SimpleIntegerProperty(units);
            this.resultGrade = new SimpleStringProperty(grade);
        }
        
        @Override
        public String toString(){
            return String.format(resultCourseCode.getValue() +" "+resultCourseTitle.getValue()+" "+resultUnits.getValue()+" "+resultGrade.getValue()+"\n");
        }
        public String getResultCourseCode(){ return resultCourseCode.get(); }
        
        public String getResultCourseTitle(){ return resultCourseTitle.get(); }
        
        public int getResultUnits(){ return resultUnits.get(); }
        
        public String getResultGrade(){ return resultGrade.get(); }

        public void setResultCourseCode(String courseCode){resultCourseCode.set(courseCode);}

        public void setResultCourseTitle(String courseTitle){resultCourseTitle.set(courseTitle);}

        public void setResultUnits(int units){resultUnits.set(units);}

        public void setResultGrade(String grade){resultGrade.set(grade);}
    }
}


