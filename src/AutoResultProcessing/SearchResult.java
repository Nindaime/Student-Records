/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AutoResultProcessing;

import java.sql.Blob;
import java.sql.SQLException;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.effect.BlurType;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
/**
 *
 * @author PETER
 */
public class SearchResult extends HBox{
    VBox vBox;
    Image image;
    Label level, matricNo, department, fullName, searchParameter;
    
    public SearchResult(){
        setPrefHeight(80);
        setPadding(new Insets(10));
        Text noResult = new Text(0, 0, "No Student Found");
        noResult.setFont(Font.font("Monospaced", FontWeight.THIN, 40));
        this.setAlignment(Pos.CENTER);
        DropShadow effect = new DropShadow(BlurType.ONE_PASS_BOX, Color.RED, 2.5, 0, 0, 0);
        effect.setWidth(6);
        effect.setHeight(6);
        noResult.setEffect(effect);
        this.setLayoutX(10);
        noResult.setFill(Color.WHITE);
        setStyle("-fx-background-color: #dedede");
        getChildren().add(noResult);
        
        this.setOnMouseEntered(e -> {
            this.setStyle("-fx-background-color: #f1f1f1;");
            this.setCursor(Cursor.HAND);
            });//hover effect
        this.setOnMouseExited(e -> this.setStyle("-fx-background-color: #dedede;"));
        //show student results
    }
    
    public SearchResult(String level, String matricNo, String department, String fullName, Blob image, String searchParameter) throws SQLException{
        setSpacing(10);
        setPrefHeight(80);
        setPadding(new Insets(10));
        setAlignment(Pos.TOP_LEFT);
        
        this.level = new Label(level);
        this.level.setStyle("-fx-text-fill: white; -fx-font: 15 SansSerif;");
        this.matricNo = new Label(matricNo);
        this.matricNo.setStyle("-fx-text-fill: white; -fx-font: 15 SansSerif;");
        this.department = new Label(department);
        this.department.setStyle("-fx-text-fill: white; -fx-font: 15 SansSerif;");
        this.fullName = new Label(fullName);
        this.fullName.setStyle("-fx-text-fill: white; -fx-font: 15 SansSerif;");
        this.searchParameter = new Label((searchParameter.length() == 0) ? "":(searchParameter.charAt(0)+"").toUpperCase()+searchParameter.substring(1, searchParameter.length()));
        this.searchParameter.setStyle("-fx-text-fill: white; -fx-font: 36 SansSerif;");
        DropShadow shadow = new DropShadow();
        shadow.setBlurType(BlurType.THREE_PASS_BOX);
        shadow.setHeight(2);
        shadow.setWidth(2);
        shadow.setRadius(2);
        this.searchParameter.setEffect(shadow);
        
        this.image = new Image(image.getBinaryStream());
        ImageView imageView = new ImageView(this.image);
        imageView.setFitHeight(90);
        imageView.setFitWidth(75);
        DropShadow imageShadow = new DropShadow();
        imageShadow.setSpread(0.58);
        imageShadow.setBlurType(BlurType.THREE_PASS_BOX);
        imageShadow.setWidth(4);
        imageShadow.setHeight(4);
        imageShadow.setRadius(4.5);
        imageView.setEffect(imageShadow);
        
        vBox = new VBox(3);
        vBox.getChildren().addAll(this.level, this.matricNo, this.department, this.fullName);
        
        getChildren().addAll(imageView, vBox, this.searchParameter);
        HBox.setMargin(this.searchParameter, new Insets(25,0,0,40));
        setStyle("-fx-background-color: lightgrey");

        this.setOnMouseEntered(e -> {
            this.setStyle("-fx-background-color: #f1f1f1;");
            this.setCursor(Cursor.HAND);
            });//hover effect
        this.setOnMouseExited(e -> this.setStyle("-fx-background-color: lightgrey"));
    }
       
}