/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AutoResultProcessing;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;

/**
 *
 * @author PETER-PC
 */
public class GenerateResult extends Pane {
    final TableView<CourseResult> tableTeacherResult;
    final TableView<CourseResult> tableSpreadSheet;
    final ObservableList<CourseResult> data;
    Label Institution, CourseName, ResultSemester_Session, Department, LecturerName, Signature;
    
    public GenerateResult(String courseCode){
        tableTeacherResult = tableSpreadSheet = new TableView<>();
        data = FXCollections.observableArrayList();
        String queryString;
        TableColumn matric, firstname, lastname, resultScore, gradeLetter;
        matric = new TableColumn("Matric No");
        matric.setCellValueFactory(new PropertyValueFactory<>("matric"));
        firstname = new TableColumn("First Name");
        firstname.setCellValueFactory(new PropertyValueFactory<>("firstname"));
        lastname = new TableColumn("Surname");
        lastname.setCellValueFactory(new PropertyValueFactory<>("lastname"));
        resultScore = new TableColumn("Score");
        resultScore.setCellValueFactory(new PropertyValueFactory<>("resultScore"));
        gradeLetter = new TableColumn("Grade");
        gradeLetter.setCellValueFactory(new PropertyValueFactory<>("resultGrade"));
        tableTeacherResult.getColumns().addAll(matric, lastname, firstname, resultScore, gradeLetter);
        
        //add table column to auto increment index
        try{
//            queryString = "SELECT LecturerInstitution, ResultCourseCode, ResultSession, CourseSemester, CourseTitle, LecturerDepartment, "
//                    + "concat(Lecturer.lastname,' ', Lecturer.firstname) as LecturerName";
            queryString = "SELECT Matric, firstname, lastname, ResultScore, "
                    + "get_grade_point_as_Letter(ResultScore) as gradeLetter from Student, Course, Result WHERE "
                    + "matric = ResultMatric and CourseCode = ? and CourseCode = ResultCoursCode";

            statement = connection.prepareStatement(queryString);
            statement.setString(1, courseCode);
            ResultSet rSet = statement.executeQuery();
            while(rSet.next()){
                CourseResult result = new CourseResult(rSet.getString("matric"), 
                        rSet.getString("firstname"), rSet.getString("lastname"), 
                        rSet.getInt("ResultScore"), rSet.getString("gradeLetter"));
                data.add(result);
            }
            tableTeacherResult.setItems(data);
            tableSpreadSheet.setItems(data);
        }catch(SQLException ex){}
    }
    
    public static class CourseResult{
        private final SimpleStringProperty matric, firstname, lastname, resultGrade;
        private final SimpleIntegerProperty resultScore;

        public CourseResult(String matric, String firstname, String lastname, int score, String grade) {
            this.matric = new SimpleStringProperty(matric);
            this.firstname = new SimpleStringProperty(firstname);
            this.lastname = new SimpleStringProperty(lastname);
            this.resultScore = new SimpleIntegerProperty(score);
            this.resultGrade = new SimpleStringProperty(grade);
        }

        @Override
        public String toString() {
            return String.format(matric.getValue() + " " + firstname.getValue() +" "+ 
                    lastname.getValue() + " " + resultScore.getValue() + " " + resultGrade.getValue() + "\n");
        }

        public String getMatric() { return matric.get(); }

        public String getFirstName() { return firstname.get(); }
        
        public String getLastName() { return lastname.get(); }

        public int getResultScore() { return resultScore.get(); }

        public String getResultGrade() { return resultGrade.get(); }

        public void setMatric(String matric) { this.matric.set(matric); }

        public void setFirstName(String firstname) {  this.firstname.set(firstname); }
        
        public void setLastName(String lastname) {  this.lastname.set(lastname); }

        public void setResultScore(int score) { resultScore.set(score);  }

        public void setResultGrade(String grade) { resultGrade.set(grade);  }
    }
    
    Connection connection;
    PreparedStatement statement;
    private void initializeDB(){
        try{
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost/studentrecords", "root", "");
        }catch(SQLException | ClassNotFoundException ex){}
    }
}
