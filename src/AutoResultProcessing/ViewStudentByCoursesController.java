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
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import java.sql.*;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Callback;
import javafx.util.Duration;

/**
 * FXML Controller class
 *
 * @author PETER
 */
public class ViewStudentByCoursesController implements Initializable, ControlledScreen {
    ScreenController myController;
    public static String lecturerId;
//  add courseGrid or other to style Lecturer page
//    change color theme
    @Override
    public void setScreenParent(ScreenController screenParent){
        myController = screenParent;
    }

    @FXML
    private Button closeButton;
    
    @FXML
    private void closeButtonAction(ActionEvent event){
        Stage stage = (Stage) closeButton.getScene().getWindow();   
        stage.close();
    }
    
    @FXML
    private void hover_in(MouseEvent event) {
        ImageView closeImage = (ImageView) closeButton.getGraphic();
        closeImage.setEffect(new ColorAdjust(0, 1, 0.25, 0));
    }

    @FXML
    private void hover_out(MouseEvent event) {
        ImageView closeImage = (ImageView) closeButton.getGraphic();
        closeImage.setEffect(null);
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
        scene.setCursor(Cursor.CLOSED_HAND);
        stage.setX(event.getScreenX() + xOffset);
        stage.setY(event.getScreenY() + yOffset);
    }

    @FXML
    private void drop(MouseEvent event) {
        Scene scene = ((Node) event.getSource()).getScene();
        scene.setCursor(Cursor.HAND);
    }
    
    @FXML
    private void goToMainScreen(ActionEvent event) throws SQLException{
        myController.setScreen(AutoResultProcessing.viewResults);
        if(!table.getItems().isEmpty())table.getItems().clear();
        if(!menuSelection.getItems().isEmpty())menuSelection.getItems().clear();
    }

    @FXML
    private void menuHoverIn(MouseEvent event){
        ((ComboBox) event.getSource()).setStyle("-fx-border-color: #008CBA; "
                 + "-fx-border-insets: -3;"
                 + "-fx-border-width: 3;"
                 + "-fx-background-color: gainsboro;");
    }
    
    @FXML
    private void menuHoverOut(MouseEvent event){
        ((ComboBox) event.getSource()).setStyle("-fx-border-color: #008CBA; "
                 + "-fx-border-insets: -1;"
                 + "-fx-border-width: 1;"
                 + "-fx-background-color: gainsboro;");
    }
    
    static Connection connection;

    public void initializeDB() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            System.out.println("Driver loaded");

            connection = DriverManager.getConnection("jdbc:mysql://localhost/studentrecords", "root", "");
            System.out.println("Database Connection");
        } catch (ClassNotFoundException | SQLException ex) { }
    }
    
    @FXML
    private ComboBox<HBox> menuSelection;
    
    private void getCourses() throws SQLException{
        menuSelection.setVisibleRowCount(5);
        String queryString;
        
        if(!lecturerId.equals("")){
            queryString = "select distinct ResultCourseCode, CourseTitle from result, "
                    + "Course, Lecturer where ResultCourseCode = LecturerCourseCode AND "
                    + "ResultCourseCode = courseCode AND Lecturer.FirstName = ? "
                    + "ORDER BY CourseCode ASC";
            statement = connection.prepareStatement(queryString);
            statement.setString(1, lecturerId);
        }
        
        ResultSet rSet = statement.executeQuery();
        HBox hBox;
        Label labelLeft, labelRight;
        while(rSet.next()){
            labelLeft = new Label(rSet.getString("ResultCourseCode"));
            labelLeft.setPrefHeight(22);
            labelLeft.setPrefWidth(57);
            labelLeft.setFont(Font.font("arimo", FontWeight.BOLD, 12));
            labelLeft.setTextFill(Color.WHITE);
            labelLeft.setAlignment(Pos.CENTER);
            labelLeft.setStyle("-fx-background-color: #008CBA;");
            labelRight = new Label(rSet.getString("CourseTitle"));
            labelRight.setPrefHeight(22);
            labelRight.setPrefWidth(272);
            labelRight.setStyle("-fx-background-color: gainsboro;"
                    + "-fx-padding: 0 0 0 5");
            labelRight.setFont(Font.font("Calibri", FontWeight.THIN, 16));
            labelRight.setTextFill(Color.web("#008CBA"));
            labelRight.setAlignment(Pos.CENTER_LEFT);
            hBox = new HBox();
            hBox.setPrefHeight(22);
            hBox.setPrefWidth(menuSelection.getPrefWidth()-47);
            hBox.getChildren().addAll(labelLeft,labelRight);
            hBox.setCursor(Cursor.HAND);
            
            menuSelection.getItems().add(hBox);
        }
    }
    
//    @FXML
//    private void selectCourse(ActionEvent event) throws SQLException{
//        try{
//            getStudents(((Label)menuSelection.getValue().getChildren().get(0)).getText());
//        }catch(NullPointerException ex){}
//    }
    
    @FXML
    private TableView<Result> table;
    
    @FXML
    private TableColumn matricNoColumn;
    
    @FXML
    private TableColumn departmentColumn;
    
    @FXML
    private TableColumn fullNameColumn;
    
    @FXML
    private TableColumn scoreColumn;
    
    PreparedStatement statement;
    
    @FXML
    private void getStudents(String courseCode) throws SQLException{
        matricNoColumn.setCellValueFactory(new PropertyValueFactory<>("matricNo"));
        departmentColumn.setCellValueFactory(new PropertyValueFactory<>("department"));
        fullNameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        scoreColumn.setCellValueFactory(new PropertyValueFactory<>("score"));
        
        String getStudents = "select Resultmatric, department, firstname, OtherName, "
                + "lastname, ResultScore from Student, result where Resultmatric = matric "
                + "and ResultCourseCode = ? and ResultScore is not null and institution = ? ORDER BY Matric ASC, department ASC";
        
        statement = connection.prepareStatement(getStudents);
        statement.setString(1, courseCode);
        statement.setString(2, AutoResultProcessing.institution);
        System.out.println("Institution is: "+AutoResultProcessing.institution);
        
        ResultSet rSet = statement.executeQuery();
        ObservableList<Result> data = FXCollections.observableArrayList();
        while(rSet.next()){
            data.add(new Result(rSet.getString("ResultMatric"), 
                    rSet.getString("department").toUpperCase(),
                    rSet.getString("firstname")+". "+rSet.getString("otherName").charAt(0)+". "+rSet.getString("lastname"),
                    Double.parseDouble(rSet.getString("Resultscore"))));
        }
        table.setItems(data);
        studentMatric.setText("");
        studentScore.setText("");
    }
    
    public static class Result{
        private final SimpleStringProperty matricNo;
        private final SimpleStringProperty department;
        private final SimpleStringProperty fullName;
        private final SimpleDoubleProperty score;
        
        private Result(String matricNo, String department, String fullName, Double score){
            this.matricNo = new SimpleStringProperty(matricNo);
            this.department = new SimpleStringProperty(department);
            this.fullName = new SimpleStringProperty(fullName);
            this.score = new SimpleDoubleProperty(score);
        }
        
        public String getMatricNo(){
            return matricNo.get();
        }
        
        public String getDepartment(){
            return department.get();
        }
        
        public String getFullName(){
            return fullName.get();
        }
        
        public double getScore(){
            return score.get();
        }
        
        public void setMatricNo(String matricNo){
            this.matricNo.set(matricNo);
        }
        
        public void setDepartment(String department){
            this.department.set(department);
        }
        
        public void setFullName(String fullName){
            this.fullName.set(fullName);
        }
        
        public void setScore(double score){
            this.score.set(score);
        }
        
    }

    @FXML
    private Button addButton;
    
    @FXML
    private Button commitButton;
    
    @FXML
    private ImageView commitImage;
    
    @FXML
    private ImageView addImage;
    
    @FXML
    private void hoverIn(MouseEvent event){
        Button currentButton = (Button)event.getSource();
        currentButton.setStyle("-fx-background-color: #008CBA; -fx-text-fill: white");
    }
    
    @FXML
    private void hoverOut(MouseEvent event){
        Button currentButton = (Button)event.getSource();
        currentButton.setStyle("-fx-background-color: white; -fx-text-fill: #008CBA; -fx-border-color: #008CBA; -fx-border-width: 3;");
    }
    
    @FXML
    private TextField studentMatric;
    
    @FXML
    private TextField studentScore;
    
    @FXML
    private void addStudentResult(ActionEvent event) throws SQLException{
        String CourseCode = ((Label)menuSelection.getValue().getChildren().get(0)).getText();
        String score = studentScore.getText();
        String matric = studentMatric.getText();
        String queryString = "Update Result set ResultScore = ? where ResultCourseCode = ? AND ResultMatric = ? AND ResultSession = ?;";
      
        System.out.println("Statement: "+queryString);
        statement = connection.prepareStatement(queryString);
        
        statement.setString(1, score);
        statement.setString(2, CourseCode);
        statement.setString(3, matric);
        statement.setString(4, sessionSelection.getValue());
        
        statement.executeUpdate();
        
        System.out.println("Statement1 Database updated at matricNo: "+studentMatric.getText()
                    +" and Course: "+((Label)menuSelection.getValue().getChildren().get(0)).getText());

        Timeline addFadeInOut = new Timeline(new KeyFrame(Duration.millis(400), new KeyValue(addImage.opacityProperty(), 1, Interpolator.EASE_OUT)), 
        new KeyFrame(Duration.millis(800), new KeyValue(addImage.opacityProperty(), 0, Interpolator.EASE_IN)));
        addFadeInOut.setDelay(Duration.millis(500));
        addFadeInOut.play();

        connection.commit();
        getStudents(CourseCode);
    }
    
    class EditingCell extends TableCell<Result, Double> {

        private TextField textField;

        public EditingCell() {}

        @Override
        public void startEdit() {
            super.startEdit();
            System.out.println("Edit Triggered");
            if (textField == null) {
                createTextField();
            }

            setGraphic(textField);
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            textField.selectAll();
        }

        @Override
        public void cancelEdit() {
            super.cancelEdit();
            System.out.println("Edit Cancelled");
            
            setText(String.valueOf(getItem()));
            setContentDisplay(ContentDisplay.TEXT_ONLY);
        }

        @Override
        public void updateItem(Double item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setText(null);
                setGraphic(null);
            } else {
                setText(getString());
                setContentDisplay(ContentDisplay.TEXT_ONLY);
                try{
                    updateRecord(getString(), getIndex());
                }catch(SQLException ex){
                    ex.printStackTrace();
                }
            }
        }
        
        private void updateRecord(String result, int index) throws SQLException{
            String queryString = "update Result set ResultScore = ? where ResultCourseCode = ? and "
                    + "ResultMatric = ? ";
            
            statement = connection.prepareStatement(queryString);
            
            statement.setString(1, result);
            statement.setString(2, ((Label)menuSelection.getValue().getChildren().get(0)).getText());
            statement.setString(3, matricNoColumn.getCellData(index)+"");
            statement.executeUpdate();
            System.out.println("Database updated at matricNo: "+matricNoColumn.getCellData(index)
                    +" and Course: "+((Label)menuSelection.getValue().getChildren().get(0)).getText()+" & score: "+ result);
        }
        
        private void createTextField() {
            textField = new TextField(getString());
            textField.setMinWidth(this.getWidth() - this.getGraphicTextGap() * 2);
            textField.setOnKeyPressed((KeyEvent t) -> {
                if (t.getCode() == KeyCode.ENTER) {
                    commitEdit(Double.parseDouble(textField.getText()));
                } else if (t.getCode() == KeyCode.ESCAPE) {
                    cancelEdit();
                }
            });
        }

        private String getString() {
            return getItem() == null ? "" : getItem().toString();
        }
    }

    @FXML
    private ComboBox<String> sessionSelection;
    
    private void getSession() throws SQLException{
        String queryString = "SELECT DISTINCT ResultSession from Result";
        statement = connection.prepareStatement(queryString);
        ResultSet rSet = statement.executeQuery();
       
        if(sessionSelection.getItems().isEmpty())
            while(rSet.next())
                sessionSelection.getItems().add(rSet.getString("ResultSession"));
               
            
    }
    
    @FXML
    private void lecturerSessionSelect(String session, String courseCode) {
        if(!table.getItems().isEmpty())
            table.getItems().clear();

        Callback<TableColumn, TableCell> cellFactory
                = (TableColumn p) -> new EditingCell();

            scoreColumn.setCellFactory(cellFactory);
        
        try{
            matricNoColumn.setCellValueFactory(new PropertyValueFactory<>("matricNo"));
            departmentColumn.setCellValueFactory(new PropertyValueFactory<>("department"));
            fullNameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
            scoreColumn.setCellValueFactory(new PropertyValueFactory<>("score"));

            String getStudents = "select Resultmatric, department, firstname, OtherName, "
                    + "lastname, ResultScore from Student, result where Resultmatric = matric "
                    + "and ResultCourseCode = ? and ResultSession = ? and institution = ? and "
                    + "resultscore is not null ORDER BY Matric ASC, department ASC";

            statement = connection.prepareStatement(getStudents);
            statement.setString(1, courseCode);
            statement.setString(2, session);
            statement.setString(3, AutoResultProcessing.institution);
            ResultSet rSet = statement.executeQuery();
            ObservableList<Result> data = FXCollections.observableArrayList();
            while (rSet.next()) {
                data.add(new Result(rSet.getString("ResultMatric"),
                        rSet.getString("department").toUpperCase(),
                        rSet.getString("firstname") + ". " + rSet.getString("otherName").charAt(0) + ". " + rSet.getString("lastname"),
                        Double.parseDouble(rSet.getString("Resultscore"))));
            }
            table.setItems(data);
            studentMatric.setText("");
            studentScore.setText("");
            
        }catch(SQLException ex){}
    }
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initializeDB();
        
        try{
            connection.setAutoCommit(false);
        }catch(SQLException ex){}        

        table.placeholderProperty().set(new Label("No Student Score Recorded"));

        menuSelection.setOnMouseEntered(e -> {
            try{
                if (menuSelection.getItems().isEmpty())
                        getCourses();
            }catch(SQLException ex){}
                });

        scoreColumn.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<Result, Double>>() {

            @Override
            public void handle(TableColumn.CellEditEvent<Result, Double> t) {
                rowIndex = t.getTablePosition().getRow();
                ((Result) t.getTableView()
                        .getItems()
                        .get(rowIndex)).setScore(t.getNewValue());
            }
        });
        
        menuSelection.setOnShowing(e->{
            if(!menuSelection.getItems().isEmpty()){
                ObservableList<HBox> retainer = FXCollections.observableArrayList(menuSelection.getItems());
                menuSelection.getItems().clear();
                menuSelection.getItems().addAll(retainer);
            }
            
        });
        
        sessionSelection.setOnMouseEntered(e -> {
            try{
                if(sessionSelection.getItems().isEmpty())
                    getSession();
            }catch(SQLException ex){}
        });
        
        sessionSelection.setOnAction(e -> {
            try{
                if(((Label)menuSelection.getValue().getChildren().get(0)).getText().matches("[\\S]{6}")){
                    lecturerSessionSelect(sessionSelection.getValue(), ((Label)menuSelection.getValue().getChildren().get(0)).getText());
                }
            }catch(NullPointerException ex){}
        });

        Timeline commitFadeInOut = new Timeline(new KeyFrame(Duration.millis(400), new KeyValue(commitImage.opacityProperty(), 1, Interpolator.EASE_OUT)));
        commitFadeInOut.setDelay(Duration.millis(500));

        commitFadeInOut.setOnFinished((ActionEvent t) -> {
            new Timeline(new KeyFrame(Duration.millis(800), new KeyValue(commitImage.opacityProperty(), 0, Interpolator.EASE_IN))).play();
        });

        commitButton.setOnMouseClicked(e ->{
            try{
                connection.commit();
                commitFadeInOut.play();
            }catch(SQLException ex){
                ex.printStackTrace();
            }
        });
        
        //add context menu to delete results from lecturers record table
        table.setRowFactory(new Callback<TableView<Result>, TableRow<Result>>() {
            @Override
            public TableRow<Result> call(TableView<Result> tableView) {
                final TableRow<Result> row = new TableRow<>();
                final ContextMenu contextMenu = new ContextMenu();
                final MenuItem rmMenuItem = new MenuItem("Delete Result");
                rmMenuItem.setOnAction(e -> {
                    String queryString = "update Result set ResultScore = null where ResultCourseCode = ? and ResultMatric = ? and ResultSession = ?";
                    try {
                        PreparedStatement statement = connection.prepareStatement(queryString);
                        TablePosition pos = table.getSelectionModel().getSelectedCells().get(0);
                        int row1 = pos.getRow();
                        Result item = table.getItems().get(row1);
                        String studentMatric = item.getMatricNo();
                        statement.setString(1, ((Label)menuSelection.getValue().getChildren().get(0)).getText());
                        statement.setString(2, studentMatric);
                        statement.setString(3, sessionSelection.getValue());
                        statement.execute();
                    } catch (SQLException ex) {
                    }
                    table.getItems().remove(row.getItem());
                });
                contextMenu.getItems().add(rmMenuItem);

                row.contextMenuProperty().bind(Bindings.when(row.emptyProperty()).then((ContextMenu) null).otherwise(contextMenu));
                return row;
            }
        });
    }
    
    int rowIndex;

}