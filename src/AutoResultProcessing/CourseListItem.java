/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AutoResultProcessing;

import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;

/**
 *
 * @author PETER
 */
public class CourseListItem extends HBox{
    Label labelLeft, labelRight, labelCenter;
    String courseCode = "";
    
    public CourseListItem(ComboBox courseCbo, String courseCode, String courseTitle, String courseStatus, int resultScore){
        this.courseCode = courseCode;
        labelLeft = new Label(courseCode);
        labelLeft.getStyleClass().add("labelLeft");
        labelCenter = new Label(courseTitle);
        labelCenter.getStyleClass().add("labelCenter");
        labelRight = new Label(("C".equals(courseStatus)) ? "Compulsory " : "Elective");
        labelRight.getStyleClass().add("labelRight");
        setPrefWidth(courseCbo.getPrefWidth() - 47);
        getChildren().addAll(labelLeft, labelCenter, labelRight);
        getStyleClass().add("failed");
        Tooltip.install(this, new Tooltip("Previous Score: "+resultScore));
    }
    
    public CourseListItem(ComboBox courseCbo, String courseCode, String courseTitle, String courseStatus){
        this.courseCode = courseCode;
        labelLeft = new Label(courseCode);
        labelLeft.getStyleClass().add("labelLeft");
        labelCenter = new Label(courseTitle);
        labelCenter.getStyleClass().add("labelCenter");
        labelRight = new Label(("C".equals(courseStatus)) ? "Compulsory " : "Elective");
        labelRight.getStyleClass().add("labelRight");
        setPrefWidth(courseCbo.getPrefWidth() - 47);
        getChildren().addAll(labelLeft, labelCenter, labelRight);
    }
    
    public String getCourseCode(){
        return courseCode;
    }

}
