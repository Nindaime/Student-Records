/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AutoResultProcessing;
import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.collections.FXCollections;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Callback;
import javax.imageio.ImageIO;
/**
 *
 * @author PETER
 */
public class StudentResult extends Pane{
    final TableView<SearchStudentsController.Results> table = new TableView<>();
    final ObservableList<SearchStudentsController.Results> data;
    Label level, semester;
    Text gpa, cgpa;
    String matricNo;
    StudentResult(ObservableList<SearchStudentsController.Results> data, boolean editable, String matricNo, String level, String semester, String gpa, String cgpa){
        this.data = data;
        this.matricNo = matricNo;
        this.semester = new Label(semester);
        table.placeholderProperty().set(new Label("Examinations Yet To Be Undertaken"));
        
        TableColumn courseCode = new TableColumn("Course Code");
        courseCode.setPrefWidth(95);
        courseCode.setCellValueFactory(new PropertyValueFactory<>("resultCourseCode"));
        TableColumn courseTitle = new TableColumn("Course Title");
        courseTitle.setPrefWidth(275);
        courseTitle.setCellValueFactory(new PropertyValueFactory<>("resultCourseTitle"));
        TableColumn units = new TableColumn("Units");
        units.setPrefWidth(60);
        units.setCellValueFactory(new PropertyValueFactory<>("resultUnits"));
        TableColumn grade = new TableColumn<>("Grade");
        grade.setPrefWidth(60);
        grade.setCellValueFactory(new PropertyValueFactory<>("resultGrade"));
        
        if(editable){
            Callback<TableColumn, TableCell> cellFactory
                    = (TableColumn p) -> new EditingCell();
            grade.setEditable(true);
            table.setEditable(true);
            grade.setCellFactory(cellFactory);
        }
        
        table.setItems(data);
        table.getColumns().addAll(courseCode, courseTitle, units, grade);
        table.getColumns().forEach(cnsmr -> ((TableColumn)cnsmr).setStyle("-fx-alignment: center"));
        
        String ladderColor = "";
        Rectangle screenRect = new Rectangle(411, 245, 5, 5);
        try{
            BufferedImage capture = new Robot().createScreenCapture(screenRect);
            ImageIO.write(capture, "png", new File("snapshot.png"));
            WritableImage snapshot = SwingFXUtils.toFXImage(capture, null);
            ladderColor = "#"+snapshot.getPixelReader().getColor(1, 1).toString().substring(2, 8);//color after blend mode applied to background
        }catch(IOException | AWTException ex){}
        
        this.level = new Label(level+": ");
        this.level.setLayoutX(5);
        this.level.setLayoutY(3);
        this.level.setStyle("-fx-font: lighter 36 Signika; -fx-fill: ladder("+ladderColor+", white 49%,rgba(0,0,0,0.8) 50%); -fx-effect: dropshadow( three-pass-box , ladder("+ladderColor+", black 49%,white 50%), 5, 0, 0, 1);");
        this.semester.setLayoutX(75);
        this.semester.setLayoutY(14);
        this.semester.setStyle("-fx-font: 24 Lato; -fx-fill: ladder("+ladderColor+", white 49%,rgba(0,0,0,0.8) 50%); -fx-effect: dropshadow( three-pass-box , ladder("+ladderColor+", black 49%,white 50%), 5, 0, 0, 1);");
        this.gpa = new Text("GPA: "+gpa);
        this.gpa.setLayoutX(28);
        this.gpa.setLayoutY(287);
        this.gpa.setStyle("-fx-font: 18 Signika; -fx-fill: ladder("+ladderColor+", white 49%,rgba(0,0,0,0.8) 50%); -fx-effect: dropshadow( three-pass-box , ladder("+ladderColor+", black 49%,white 50%), 5, 0, 0, 1);");
        this.cgpa = new Text("CGPA: "+cgpa);
        this.cgpa.setLayoutX(416);
        this.cgpa.setLayoutY(287);
        this.cgpa.setStyle("-fx-font: 18 Signika; -fx-fill: ladder("+ladderColor+", white 49%,rgba(0,0,0,0.8) 50%); -fx-effect: dropshadow( three-pass-box , ladder("+ladderColor+", black 49%,white 50%), 5, 0, 0, 1);");
        table.setLayoutX(25);
        table.setLayoutY(50);
        table.setPrefHeight(200);
        DropShadow dShadow = new DropShadow(8.5, Color.BLACK);
        dShadow.setHeight(18);
        dShadow.setWidth(18);
        table.setEffect(dShadow);
        setStyle("-fx-background-color: rgba(0, 0, 0, 0.15)");
        setMinHeight(300);
        
        getChildren().addAll(this.level, this.semester, table, this.gpa, this.cgpa);
    }
    
    class EditingCell extends TableCell<SearchStudentsController.Results, String> {

//        private TextField textField;
        private ChoiceBox<String> choiceBox;

        public EditingCell() {}

        @Override
        public void startEdit() {
            super.startEdit();

            if (choiceBox == null) {
                createChoiceBox();
            }

            setGraphic(choiceBox);
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        }

        @Override
        public void cancelEdit() {
            super.cancelEdit();
            setText(String.valueOf(getItem()));
            setContentDisplay(ContentDisplay.TEXT_ONLY);
        }
        
        @Override
        public void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setText(null);
                setGraphic(null);
            } else{
                if (isEditing()) {
                    if (choiceBox != null) {
                        choiceBox.setValue(getString());
                    }
                    setGraphic(choiceBox);
                    setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                } else {
                    setText(getString());
                    setContentDisplay(ContentDisplay.TEXT_ONLY);
                    try {
                        switch(getString()){
                            case "A":
                                    updateRecord("70", getIndex());
                                break;
                            case "B":
                                    updateRecord("60", getIndex());
                                break;
                            case "C":
                                    updateRecord("50", getIndex());
                                break;
                            case "D":
                                    updateRecord("45", getIndex());
                                break;
                            case "E":
                                    updateRecord("40", getIndex());
                                break;
                            case "F":
                                    updateRecord("39", getIndex());
                                break;
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
            } 
        }

        private void updateRecord(String result, int index) throws SQLException {
            String queryString = "select YearOfAdmission from Student where matric = ?";
            PreparedStatement statement = SearchStudentsController.connection.prepareStatement(queryString);
            statement.setString(1, matricNo);
            ResultSet rSet = statement.executeQuery();
            
            String resultSession = "";
            if(rSet.next()){
                int YOA = Integer.parseInt(rSet.getString("YearOfAdmission"));
                int resultLevel = Integer.parseInt(level.getText().substring(0, 3));
                int currentSession = ((resultLevel/100) - 1) + YOA;
                resultSession = currentSession+"/"+(++currentSession);
            }
            
            queryString = "update Result set ResultScore = ? where ResultCourseCode = ? and "
                    + "ResultMatric = ? and ResultSession = ?";

            statement = SearchStudentsController.connection.prepareStatement(queryString);

            statement.setString(1, result);
            statement.setString(2, data.get(index).getResultCourseCode());
            statement.setString(3, matricNo);
            statement.setString(4, resultSession);
            statement.executeUpdate();            
        }

        private void createChoiceBox() {
            choiceBox = new ChoiceBox<>();
            choiceBox.setItems(FXCollections.observableArrayList("A", "B", "C", "D", "E", "F"));
            choiceBox.setValue(String.valueOf(getItem()));
            choiceBox.setMinWidth(this.getWidth() - this.getGraphicTextGap() * 2);
            choiceBox.setOnAction((ActionEvent t) -> {
                if (choiceBox.getValue().matches(getString())) 
                    cancelEdit();
                else
                    commitEdit(choiceBox.getValue());
                
            });
        }

        private String getString() {
            return getItem() == null ? "" : getItem();
        }
    }

}