/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AutoResultProcessing;

import AutoResultProcessing.SearchStudentsController.Results;
import static AutoResultProcessing.SearchStudentsController.getGrade;
import AutoResultProcessing.StudentPageController.Course;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import javafx.util.Duration;

/**
 * FXML Controller class
 *
 * @author PETER
 */
public class StudentPageController implements Initializable, ControlledScreen {
    public static String matric = "";
    ScreenController myController;

    @Override
    public void setScreenParent(ScreenController screenParent) {
        myController = screenParent;
    }
    /**
     * Initializes the controller class.
     */
    
    @FXML
    private Button btnClose;
    @FXML
    private Button logoutBtn;
    @FXML
    private Button registerBtn;
    @FXML
    private Button viewResultBtn;
    @FXML
    private ComboBox<CourseListItem> courseCbo;
    @FXML
    private TableView<Course> registrationTb;
    @FXML
    private Button returnBtn;
    @FXML
    private Pane container;
    
    @FXML
    private void closeButtonAction(ActionEvent event) {
        Stage stage = (Stage) btnClose.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void hover_in(MouseEvent event) {
        Button actionBtn = ((Button)event.getSource());
        ImageView closeImage = (ImageView) actionBtn.getGraphic();
        actionBtn.setEffect(new DropShadow(BlurType.THREE_PASS_BOX, Color.BLACK, 10, 0, 0, 0));
        closeImage.setEffect(new ColorAdjust(0, 0, 0.70, 0));
    }

    @FXML
    private void hover_out(MouseEvent event) {
        Button actionBtn = ((Button)event.getSource());
        ImageView closeImage = (ImageView) actionBtn.getGraphic();
        actionBtn.setEffect(null);
        closeImage.setEffect(new ColorAdjust(0, 0, 0, 0));
    }

    @FXML
    private void requestfocus(MouseEvent event) {
        container.requestFocus();
    }

    double yOffset = 0;
    double xOffset = 0;

    @FXML
    private void determine(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        xOffset = stage.getX() - event.getScreenX();
        yOffset = stage.getY() - event.getScreenY();
    }

    @FXML
    private void pick(MouseEvent event) {
        Scene scene = ((Node) event.getSource()).getScene();
        Stage stage = (Stage) scene.getWindow();
        ((Node) event.getSource()).setCursor(Cursor.CLOSED_HAND);
        stage.setX(event.getScreenX() + xOffset);
        stage.setY(event.getScreenY() + yOffset);
    }

    @FXML
    private void drop(MouseEvent event) {
        ((Node) event.getSource()).setCursor(Cursor.OPEN_HAND);
    }
    
    @FXML
    private Label institutionLbl;
    
    @FXML
    private Label departmentLbl;
    
    @FXML
    private Label matricLbl;
    
    @FXML
    private Label fullNameLbl;
    
    @FXML
    private ImageView userImageIV;
    
    @FXML
    private ImageView institutionImageIV;
    
    @FXML
    private ScrollPane resultPane;
    
    @FXML
    private void logout(ActionEvent event){
        myController.setScreen(AutoResultProcessing.login);
        Pane loginPageRoot = (Pane) ScreenController.screens.get(AutoResultProcessing.login);
        ((Label)loginPageRoot.lookup("#label2")).setText("");
        registrationTb.setItems(null);
        matric = "";
        try {
            String queryString = "Update Student SET Status = 'Offline' WHERE matric = ?";
            PreparedStatement statement = connection.prepareStatement(queryString);
            statement.setString(1, matric);
            statement.executeUpdate();
        } catch (SQLException ex) {
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb){
        // TODO
        institutionLbl.setPrefWidth(292);
        courseCbo.setOnShowing(e ->{
            try{
                getCourseList();
            }catch(SQLException ex){}
        });
        initializeDB();
        registrationTb.setRowFactory(new Callback<TableView<Course>, TableRow<Course>>(){
        
            @Override
            public TableRow<Course> call(TableView<Course> tableView){
                final TableRow<Course> row = new TableRow<>();
                final ContextMenu contextMenu = new ContextMenu();
                final MenuItem rmMenuItem = new MenuItem("Delete Course");
                rmMenuItem.setOnAction(e -> {
                    String queryString = "delete from Result where ResultCourseCode = ? AND ResultSession = ? AND ResultMatric = ?";
                    try {
                        PreparedStatement statement = connection.prepareStatement(queryString);
                        TablePosition pos = registrationTb.getSelectionModel().getSelectedCells().get(0);
                        int row1 = pos.getRow();
                        Course item = registrationTb.getItems().get(row1);
                        String courseCode = item.getCourseCode();
                        statement.setString(1, courseCode);
                        statement.setString(2, AutoResultProcessing.currentSession);
                        statement.setString(3, matric);
                        statement.execute();
                    }catch(SQLException ex){}
                    registrationTb.getItems().remove(row.getItem());
                });
                contextMenu.getItems().add(rmMenuItem);
                
                row.contextMenuProperty().bind(Bindings.when(row.emptyProperty()).then((ContextMenu) null).otherwise(contextMenu));
                return row;
            }
        });

        registerBtnSt = new ScaleTransition(Duration.millis(500));
        registerBtnSt.setFromX(1);
        registerBtnSt.setFromY(1);
        registerBtnSt.setToX(0.8);
        registerBtnSt.setToY(0.8);
        registerBtnSt.interpolatorProperty().set(Interpolator.EASE_IN);
        
        registerBtnFt = new FadeTransition(Duration.millis(400), registerBtn);
        registerBtnFt.setFromValue(1);
        registerBtnFt.setToValue(0);
       
        viewResultBtnFt = new FadeTransition(Duration.millis(300));
        viewResultBtnFt.setFromValue(1);
        viewResultBtnFt.setToValue(0);

        viewResultBtnSt = new ScaleTransition(Duration.millis(400));
        viewResultBtnSt.setFromX(1);
        viewResultBtnSt.setFromY(1);
        viewResultBtnSt.setToX(0.5);
        viewResultBtnSt.setToY(0.5);

        viewResultBtnPt = new ParallelTransition(viewResultBtn, viewResultBtnSt, viewResultBtnFt);
        viewResultBtnPt.interpolatorProperty().set(Interpolator.EASE_IN);
       
        logoutBtnFt = new FadeTransition(Duration.millis(400));
        logoutBtnFt.setFromValue(1);
        logoutBtnFt.setToValue(0);

        logoutBtnSt = new ScaleTransition(Duration.millis(300));
        logoutBtnSt.setFromX(1);
        logoutBtnSt.setFromY(1);
        logoutBtnSt.setToX(0.5);
        logoutBtnSt.setToY(0.5);

        logoutBtnPt = new ParallelTransition(logoutBtn, logoutBtnFt, logoutBtnSt);
        logoutBtnSt.interpolatorProperty().set(Interpolator.EASE_IN);
    }
    
    //dropdown items from list for courses - courseCbo
    private void getCourseList() throws SQLException {
        if(!courseCbo.getItems().isEmpty())
            courseCbo.getItems().clear();
        //add failed courses to registration list
        String queryString = "select distinct courseCode, courseTitle, Course.status, resultScore "
                + "from Course, student, result where courseLevel <= StudentLevel AND matric = ? "
                + "AND ResultScore < '40' AND resultCourseCode = courseCode AND matric = resultMatric "
                + "AND courseSemester = ? and not exists(select ResultCourseCode from result where resultSession = ? AND ResultCourseCode = courseCode) "
                + "ORDER BY courseLevel ASC, courseSemester ASC";
                
        PreparedStatement statement = connection.prepareStatement(queryString);
        statement.setString(1, matric); 
        statement.setString(2, AutoResultProcessing.currentSemester);
        statement.setString(3, AutoResultProcessing.currentSession);
        ResultSet rSet = statement.executeQuery();
        while (rSet.next()) {
            CourseListItem courseItem = new CourseListItem(courseCbo, rSet.getString("courseCode"), 
                    rSet.getString("courseTitle"), rSet.getString("status"), rSet.getInt("resultScore"));

            courseCbo.getItems().add(courseItem);
        }
        
        //add course for student's level yet to be registered
        queryString = "select distinct courseCode, courseTitle, Course.status from course, student where matric = ? "
                + "AND courseLevel <= studentLevel AND CourseSemester = ? AND Student.institution = course.institution "
                + "AND not exists(select ResultCourseCode from result where resultCourseCode = courseCode)";
        statement = connection.prepareStatement(queryString);
        statement.setString(1, matric); 
        statement.setString(2, AutoResultProcessing.currentSemester);
        rSet = statement.executeQuery();
        while(rSet.next()){
            CourseListItem courseItem = new CourseListItem(courseCbo, rSet.getString("courseCode"), 
                    rSet.getString("courseTitle"), rSet.getString("status"));
            courseCbo.getItems().add(courseItem);
        }
    }

    @FXML
    private void selectCourse(ActionEvent event) throws SQLException {
        if(!courseCbo.getItems().isEmpty())
            addCourse(((CourseListItem) courseCbo.getValue()).getCourseCode());
    }
    
    private boolean courseUnitEvaluation() throws SQLException{
        boolean returnValue = true;
        String queryString = "select semestercompulsoryunits, semesterelectiveunits from courseRequirements,"
                + " student where student.department = courseRequirements.department "
                + "AND matric = ? AND semester = ? AND level = studentLevel";
        PreparedStatement statement = connection.prepareStatement(queryString);
        statement.setString(1, matric);
        statement.setString(2, AutoResultProcessing.currentSemester);
        ResultSet result = statement.executeQuery();
        if(result.next()){
            queryString = "select SUM(IF(status = 'C', CourseUnit,0)) from Result, Course "
                    + "where ResultMatric = ? and ResultCourseCode = CourseCode and "
                    + "courseSemester = ? and ResultSession = ? ";
            PreparedStatement statement1 = connection.prepareStatement(queryString);
            statement1.setString(1, matric);
            statement1.setString(2, AutoResultProcessing.currentSemester);
            statement1.setString(3, AutoResultProcessing.currentSession);
            ResultSet rSet = statement1.executeQuery();
            
            queryString = "select SUM(IF(status = 'E', CourseUnit,0)) from Result, Course "
                    + "where ResultMatric = ? and ResultCourseCode = CourseCode and "
                    + "courseSemester = ? and ResultSession = ? ";
            PreparedStatement statement2 = connection.prepareStatement(queryString);
            statement2.setString(1, matric);
            statement2.setString(2, AutoResultProcessing.currentSemester);
            statement2.setString(3, AutoResultProcessing.currentSession);
            ResultSet rSet1 = statement2.executeQuery();
            
            if(rSet.next() && rSet1.next()){
                System.out.println("SemCompUnits: "+result.getInt(1)+"\n StudRegCompUnits: "+rSet.getInt(1)
                        +"\n SemElectUnits: "+ result.getInt(2)+"\n StudRegElectUnits: "+rSet1.getInt(1));
                if(result.getInt(1) >= rSet.getInt(1) && result.getInt(2) >= rSet1.getInt(1)){
                    returnValue = true;
                }
            }
            
        }
        return returnValue;
    }
    
    @FXML
    private void addCourse(String courseCode) throws SQLException{
        //query should consider course dependency and maximum units
        if(checkPreRequisite(courseCode) || courseUnitEvaluation()){
            String queryString = "insert into Result (ResultSession, ResultCourseCode, ResultMatric) values (?, ?, ?)";

            PreparedStatement statement = connection.prepareStatement(queryString);
            statement.setString(1, AutoResultProcessing.currentSession);
            statement.setString(2, courseCode);
            statement.setString(3, matric);
            statement.execute();
            viewCourses(matric);
        }
        else
            showFailedPreRequisite(prerequisites, courseCode);
    }
    
    private void showFailedPreRequisite(ArrayList<String> courses, String courseCode){
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.TRANSPARENT);
        VBox pane = new VBox(10);
        Label label = new Label();
        String cse = "You need to register and pass these prerequisite \n course(s) [";
        for(String s: courses)
            cse += s+", ";
        cse += "] before registering "+courseCode;
        label.setText(cse);
        label.setLayoutX(10);
        label.setLayoutY(10);
        Button button = new Button("Close");
        button.setCursor(Cursor.HAND);
        button.setLayoutX(120);
        button.setLayoutY(50);
        button.setOnAction(e -> stage.close());
        pane.getChildren().addAll(label, button);
        Scene scene = new Scene(pane, 300, 100);
        stage.setScene(scene);
        stage.showAndWait();
    }
    
    ArrayList<String> prerequisites;
    private boolean checkPreRequisite(String courseCode) throws SQLException{
        prerequisites = new ArrayList<>();
        String queryString = "select PreRequisite from course where courseCode = ?";
        PreparedStatement statement = connection.prepareStatement(queryString);
        statement.setString(1, courseCode);
        System.out.println("Selected Course Code: "+courseCode);
        ResultSet rSet = statement.executeQuery();
        rSet.next();
        if(rSet.getString("prerequisite") != null){
            if(rSet.getString("prerequisite").matches("[\\S]{6},.*"))
                prerequisites.addAll(Arrays.asList(rSet.getString("prerequisite").split(",")));
            else
                prerequisites.add(rSet.getString("prerequisite"));
        }else
            return true;
       
        queryString = "select max(ResultScore) from result where ResultCourseCode = ? AND ResultMatric = ?";
        statement = connection.prepareStatement(queryString);
        
        boolean[] passedPrerequisites = new boolean[prerequisites.size()];
        for(String s: prerequisites){
            statement.setString(1, s);
            statement.setString(2, matric);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()){
                System.out.println(s+" Result: "+resultSet.getString(1));
//                confirm each prerequisite has be passed
                if(Integer.parseInt(resultSet.getString(1)) < 40){
                    passedPrerequisites[prerequisites.indexOf(s)] = false;
                }else
                    passedPrerequisites[prerequisites.indexOf(s)] = true;
                    
            }
        }
        
        prerequisites.removeIf(prdct -> passedPrerequisites[prerequisites.indexOf(prdct)] == true);
        
        boolean status = false;
        for(boolean b: passedPrerequisites){
            if(!b){
                status = b;
                break;
            }
        }
        return status;
    }
    
    @FXML
    private TableColumn regTbCourseStatusCol;
    
    @FXML
    private TableColumn regTbCourseCodeCol;
    
    @FXML
    private TableColumn regTbCourseTitleCol;
    
    @FXML
    private TableColumn regTbCourseUnitCol;
    
    private void viewCourses(String matric) throws SQLException{
        regTbCourseCodeCol.setCellValueFactory(new PropertyValueFactory<>("courseCode"));
        regTbCourseTitleCol.setCellValueFactory(new PropertyValueFactory<>("courseTitle"));
        regTbCourseUnitCol.setCellValueFactory(new PropertyValueFactory<>("courseUnit"));
        regTbCourseStatusCol.setCellValueFactory(new PropertyValueFactory<>("courseStatus"));
        //add semester and session parameter
        String queryString = "select ResultCourseCode, CourseTitle, CourseUnit, status from Result, Course "
                + "where ResultMatric = ? and ResultCourseCode = CourseCode and "
                + "courseSemester = ? and ResultSession = ? ORDER BY courseCode ASC";
        PreparedStatement statement = connection.prepareStatement(queryString);
        statement.setString(1, matric);
        statement.setString(2, AutoResultProcessing.currentSemester);
        statement.setString(3, AutoResultProcessing.currentSession);
        ResultSet rSet = statement.executeQuery();
        ObservableList<Course> data = FXCollections.observableArrayList();
        while(rSet.next())
            data.add(new Course(rSet.getString("ResultCourseCode"), rSet.getString(2), rSet.getString(3), rSet.getString(4)));
        
        registrationTb.setItems(data);
    }
    
    public static class Course{
        private final SimpleStringProperty courseCode;
        private final SimpleStringProperty courseTitle;
        private final SimpleStringProperty courseUnit;
        private final SimpleStringProperty courseStatus;
        
        private Course(String courseCode, String courseTitle, String courseUnit, String courseStatus){
            this.courseCode = new SimpleStringProperty(courseCode);
            this.courseTitle = new SimpleStringProperty(courseTitle);
            this.courseUnit = new SimpleStringProperty(courseUnit);
            this.courseStatus = new SimpleStringProperty(courseStatus);
        }
        
        public String getCourseCode(){   return courseCode.get();    }
        
        public String getCourseTitle(){    return courseTitle.get();    }
        
        public String getCourseUnit(){  return courseUnit.get();    }
        
        public String getCourseStatus(){    return courseStatus.get();     }
        
        public void setCourseCode(String courseCode){   this.courseCode.set(courseCode);    }
        
        public void setCourseTitle(String courseTitle){     this.courseTitle.set(courseTitle);    }
        
        public void setCourseUnit(String courseUnit){   this.courseUnit.set(courseUnit);    }
        
        public void setCourseStatus(String courseStatus){   this.courseStatus.set(courseStatus);   }
    }

    Connection connection;

    public void initializeDB() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            System.out.println("Driver loaded");
            connection = DriverManager.getConnection("jdbc:mysql://localhost/studentrecords", "root", "");
            System.out.println("Database Connection");
        } catch (ClassNotFoundException | SQLException ex) {
        }
    }
    
    FadeTransition returnBtnFt, viewResultBtnFt, logoutBtnFt, registrationTbFt, courseCboFt, resultPaneFt, registerBtnFt;
    ScaleTransition registerBtnSt, logoutBtnSt, courseCboSt, registrationTbSt, viewResultBtnSt;
    TranslateTransition registerBtnTt, returnBtnTt;
    ParallelTransition registerBtnPt, viewResultBtnPt, logoutBtnPt, registrationTbPt, courseCboPt, returnBtnPt;
    
    @FXML
    private void registerStudentAnimation(ActionEvent event) throws SQLException{
        if(registerBtn.getTranslateX() != 315){
            currentState = "RegisterButton";
            returnBtn.setVisible(true);
            
            if(returnBtn.getLayoutX() != 14) returnBtn.setLayoutX(14);
            returnBtnFt = new FadeTransition(Duration.millis(400), returnBtn);
            returnBtnFt.setFromValue(0);
            returnBtnFt.setToValue(1);
            returnBtnFt.setDelay(Duration.millis(800));
            returnBtnFt.interpolatorProperty().set(Interpolator.EASE_OUT);
            returnBtnFt.play();

            registerBtnTt = new TranslateTransition(Duration.millis(400));
            registerBtnTt.setFromX(0);
            registerBtnTt.setFromY(0);
            registerBtnTt.setToX(315);
            registerBtnTt.setToY(180);
            registerBtnTt.interpolatorProperty().set(Interpolator.EASE_OUT);

            registerBtnPt = new ParallelTransition(registerBtn, registerBtnSt, registerBtnTt);
            registerBtnPt.interpolatorProperty().set(Interpolator.EASE_BOTH);
            registerBtnPt.setOnFinished(e -> registerBtn.setText("Return"));
            registerBtnPt.play();

            viewResultBtnPt.play();
            viewResultBtn.setVisible(false);

            logoutBtn.setVisible(false);
            logoutBtnPt.play();

            courseCbo.setVisible(true);
            courseCboFt = new FadeTransition(Duration.millis(500));
            courseCboFt.setFromValue(0);
            courseCboFt.setToValue(1);

            courseCboSt = new ScaleTransition(Duration.millis(500));
            courseCboSt.setFromX(0.9);
            courseCboSt.setFromY(0.7);
            courseCboSt.setToX(1);
            courseCboSt.setToY(1);

            courseCboPt = new ParallelTransition(courseCbo, courseCboFt, courseCboSt);
            courseCboPt.setDelay(Duration.millis(400));
            courseCboPt.interpolatorProperty().set(Interpolator.EASE_OUT);
            courseCboPt.play();

            registrationTb.setVisible(true);
            registrationTbFt = new FadeTransition(Duration.millis(500));
            registrationTbFt.setFromValue(0);
            registrationTbFt.setToValue(1);

            registrationTbSt = new ScaleTransition(Duration.millis(500));
            registrationTbSt.setFromX(0.9);
            registrationTbSt.setFromY(0.9);
            registrationTbSt.setToX(1);
            registrationTbSt.setToY(1);

            registrationTbPt = new ParallelTransition(registrationTb, registrationTbFt, registrationTbSt);
            registrationTbPt.setDelay(Duration.millis(400));
            registrationTbPt.interpolatorProperty().set(Interpolator.EASE_OUT);
            registrationTbPt.play();
           
            viewCourses(matric);    
        }
        else{
            returnBtn.setVisible(false);
            registerBtnPt.setRate(-1);
            registerBtnPt.setOnFinished(e -> registerBtn.setText("Register"));
            registerBtnPt.play();
            returnBtnFt.setRate(-1);
            returnBtnFt.play();
            viewResultBtn.setVisible(true);
            viewResultBtnPt.setRate(-1);
            viewResultBtnPt.play();
            logoutBtn.setVisible(true);
            logoutBtnPt.setRate(-1);
            logoutBtnPt.play();
            registrationTbPt.setRate(-1);
            registrationTbPt.play();
            registrationTb.setVisible(false);
            courseCboPt.setRate(-1);
            courseCboPt.play();
            courseCbo.setVisible(false);
            viewCourses("");
        }
    }
    String currentState = "";
    
    @FXML
    private void returnAnimation(ActionEvent event) throws SQLException{
        switch(currentState){
            case "RegisterButton":
                returnBtn.setVisible(false);
                registerBtnPt.setRate(-1);
                registerBtnPt.setOnFinished(e-> registerBtn.setText("Regsiter"));
                registerBtnPt.play();
                returnBtnFt.setRate(-1);
                returnBtnFt.play();
                viewResultBtn.setVisible(true);
                viewResultBtnPt.setRate(-1);
                viewResultBtnPt.play();
                logoutBtn.setVisible(true);
                logoutBtnPt.setRate(-1);
                logoutBtnPt.play();
                registrationTbPt.setRate(-1);
                registrationTbPt.play();
                registrationTb.setVisible(false);
                courseCboPt.setRate(-1);
                courseCboPt.play();
                courseCbo.setVisible(false);
                viewCourses("");
                break;
            case "ViewResultButton":
                returnBtnPt.setRate(-1);
                returnBtnPt.play();
                resultPaneFt.setRate(-1);
                resultPaneFt.play();
                resultPane.setVisible(false);
                registerBtnPt.setRate(-1);
                registerBtnPt.play();
                registerBtn.setVisible(true);
                logoutBtnPt.setRate(-1);
                logoutBtnPt.play();
                logoutBtn.setVisible(true);
                viewResultBtnPt.setRate(-1);
                viewResultBtnPt.play();
                viewResultBtn.setVisible(true);
                content.setPrefHeight(0);
                break;
        }
    }
    
    @FXML
    private void viewResultAnimation(ActionEvent event) throws SQLException{
        currentState = "ViewResultButton";
        returnBtn.setVisible(true);
        returnBtnFt = new FadeTransition(Duration.millis(400), returnBtn);
        returnBtnFt.setFromValue(0);
        returnBtnFt.setToValue(1);
        returnBtnFt.setDelay(Duration.millis(800));
        returnBtnFt.interpolatorProperty().set(Interpolator.EASE_OUT);
        returnBtnFt.setDelay(Duration.millis(200));
        returnBtnFt.setRate(1);
        returnBtnTt = new TranslateTransition(Duration.millis(500));
        if(returnBtn.getLayoutX() != 486) returnBtn.setLayoutX(486);
        returnBtnTt.setFromY(0);
        returnBtnTt.setToY(-40);
        returnBtnTt.interpolatorProperty().set(Interpolator.EASE_OUT);
        returnBtnPt = new ParallelTransition(returnBtn, returnBtnTt, returnBtnFt, 
                new Timeline(new KeyFrame(Duration.millis(500), new KeyValue(returnBtn.blendModeProperty(), BlendMode.EXCLUSION))));
        returnBtnPt.play();
        System.out.println("ReturnBtnLayoutX: "+returnBtn.getLayoutX());
        System.out.println("ReturnBtnTranslateX: "+returnBtn.getTranslateX());
        
        registerBtnPt = new ParallelTransition(registerBtn, registerBtnFt, registerBtnSt);
        registerBtnPt.interpolatorProperty().set(Interpolator.EASE_OUT);
        registerBtnPt.play();
        registerBtn.setVisible(false);
        viewResultBtnPt.play();
        viewResultBtn.setVisible(false);
        logoutBtnPt.play();
        logoutBtn.setVisible(false);
        resultPane.setVisible(true);
        resultPaneFt = new FadeTransition(Duration.millis(500), resultPane);
        resultPaneFt.setDelay(Duration.millis(400));
        resultPaneFt.setFromValue(0);
        resultPaneFt.setToValue(1);
        resultPaneFt.play();
        showGrades(matric);
    }
    
    private PreparedStatement preparedStatement2;
    double cgpa, cummulativeGradeUnits, cummulativeSemesterUnits;
    String semester;

    private void showSemester(String semester, String level, String matricNo){
        ObservableList<Results> mainData = FXCollections.observableArrayList();   
        mainData.clear();
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
                                                getGrade(result.getString("ResultScore"))+"");// Check if CourseCode can replace StudentResult.CourseCode
                
                mainData.add(result1);
                grades.add(getGrade(result.getString("ResultScore")));
                units.add(result.getInt("CourseUnit"));
            }
            
            double cummulativeSemesterGradeUnits = 0;//sum of units * grade per semester
            for(Character s: grades){
                switch(s){
                    case 'A':
                        cummulativeSemesterGradeUnits += 5 * units.get(grades.indexOf(s));
                        System.out.println("For A:"+(5 * units.get(grades.indexOf(s)))+"; unit: "+units.get(grades.indexOf(s)));
                        break;
                    case 'B':
                        cummulativeSemesterGradeUnits += 4 * units.get(grades.indexOf(s));
                        System.out.println("For B:"+(4 * units.get(grades.indexOf(s)))+"; unit: "+units.get(grades.indexOf(s)));
                        break;
                    case 'C':
                        cummulativeSemesterGradeUnits += 3 * units.get(grades.indexOf(s));
                        System.out.println("For C:"+(3 * units.get(grades.indexOf(s)))+"; unit: "+units.get(grades.indexOf(s)));
                        break;
                    case 'D':
                        cummulativeSemesterGradeUnits += 2 * units.get(grades.indexOf(s));
                        System.out.println("For D:"+(2 * units.get(grades.indexOf(s)))+"; unit: "+units.get(grades.indexOf(s)));
                        break;
                    case 'E':
                        cummulativeSemesterGradeUnits += 1 * units.get(grades.indexOf(s));
                        System.out.println("For E:"+(1 * units.get(grades.indexOf(s)))+"; unit: "+units.get(grades.indexOf(s)));
                        break;
                    case 'F':
                        cummulativeSemesterGradeUnits += 0 * units.get(grades.indexOf(s));
                        System.out.println("For F:"+(0 * units.get(grades.indexOf(s)))+"; unit: "+units.get(grades.indexOf(s)));
                        break;
                }
            }
            
            int semesterUnits = 0;
            semesterUnits = units.stream().map((i) -> i).reduce(semesterUnits, Integer::sum);
            
            cummulativeGradeUnits += cummulativeSemesterGradeUnits;
            System.out.println("Semester CourseUnitCummulative: "+cummulativeSemesterGradeUnits);
            System.out.println("Semester Unit Aggregate: "+semesterUnits);
            double gpa = (double) (cummulativeSemesterGradeUnits / semesterUnits);
            cummulativeSemesterUnits += semesterUnits;
            cgpa =  cummulativeGradeUnits / cummulativeSemesterUnits;
       
            Pane studentResult = new StudentResult(mainData, false, matricNo, level, semester, String.format("%.2f", gpa), String.format("%.2f", cgpa));
            content.getChildren().add(studentResult);
        }catch(SQLException ex){
            ex.printStackTrace();
        }
    }
    
    @FXML 
    private VBox content;

    public PreparedStatement preparedStatement1;

    private void showGrades(String matricNo) throws SQLException{
        semester = "";
        cgpa = 0;
        cummulativeGradeUnits = 0;
        cummulativeSemesterUnits = 0;
        content.getChildren().clear();
        
            String queryString = "SELECT Studentlevel, ResultCourseCode, CourseSemester FROM "
                    + "Student, Result, Course WHERE Matric = ? AND CourseCode = ResultCourseCode "
                    + "AND Studentlevel = Courselevel ORDER BY CourseSemester DESC";

            preparedStatement1 = connection.prepareStatement(queryString);
            preparedStatement1.setString(1, matricNo);
            
            ResultSet resultSet = preparedStatement1.executeQuery();
            
            String levelString = "";
            if(resultSet.next()){
                levelString = resultSet.getString("Studentlevel");
                semester = resultSet.getString("CourseSemester");
            }
                
            System.out.println(levelString);
            String levelCounter = "100";
            while(!(levelCounter.compareTo(levelString) > 0)){
                switch(levelCounter){
                    case "100":
                        showSemester("first", "100", matricNo);
                        //If there's a second semester result
                        if(semester.equalsIgnoreCase("second"))
                            showSemester("second", "100", matricNo);
                        levelCounter = "200";
                        break;
                    case "200":
                        showSemester("first", "200", matricNo);
                        //If there's a second semester result
                        if(semester.equalsIgnoreCase("second"))
                            showSemester("second", "200", matricNo);
                        levelCounter = "300";
                        break;
                    case "300":
                        showSemester("first", "300", matricNo);
                        //If there's a second semester result
                        if(semester.equals("second"))
                            showSemester("second", "300", matricNo);
                        levelCounter = "400";
                        break;
                    case "400":
                        showSemester("first", "400", matricNo);
                        //If there's a second semester result
                        if(semester.equals("second"))
                            showSemester("second", "400", matricNo);
                        levelCounter = "500";
                        break;
                    case "500":
                        showSemester("first", "500", matricNo);
                        //If there's a second semester result
                        if(semester.equals("second"))
                            showSemester("second", "500", matricNo);
                        levelCounter = "600";
                        break;
                    case "600":
                        showSemester("first", "600", matricNo);
                        //If there's a second semester result
                        if(semester.equals("second"))
                            showSemester("second", "600", matricNo);
                        break;
                }
            }
    }

}
