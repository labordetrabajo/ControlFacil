package com.example.controlfacilfx;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class Contacto extends Application {

    private String programador;
    private String telefono;

    public Contacto(String programador, String telefono) {
        this.programador = programador;
        this.telefono = telefono;
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Contacto");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20, 20, 20, 20));
        grid.setVgap(10);
        grid.setHgap(10);

        Label programadorLabel = new Label("Programador:");
        programadorLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #000000;");
        GridPane.setConstraints(programadorLabel, 0, 0);
        Label programadorValue = new Label(programador);
        GridPane.setConstraints(programadorValue, 1, 0);

        Label lucasLabel = new Label("Lucas Laborde");
        lucasLabel.setStyle("-fx-font-size: 17px; -fx-font-weight: bold; -fx-text-fill: #2E8B57;");
        GridPane.setConstraints(lucasLabel, 2, 0);
        ImageView lucas = new ImageView(new Image(getClass().getResourceAsStream("/contacto.png")));
        lucas.setFitWidth(25);
        lucas.setFitHeight(25);
        lucasLabel.setGraphic(lucas);

        Label telefonoLabel = new Label("Teléfono:");
        telefonoLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #000000;");
        GridPane.setConstraints(telefonoLabel, 0, 1);

        Label telefonoValue = new Label(telefono);
       GridPane.setConstraints(telefonoValue, 1, 1);

        Label telefonoNumber = new Label("1562631035");
        telefonoNumber.setStyle("-fx-font-size: 17px; -fx-font-weight: bold; -fx-text-fill: #2E8B57;");
        ImageView telefono2 = new ImageView(new Image(getClass().getResourceAsStream("/telefono.png")));
        telefono2.setFitWidth(25);
        telefono2.setFitHeight(25);
        telefonoNumber.setGraphic(telefono2);
        GridPane.setConstraints(telefonoNumber, 2, 1);

        grid.getChildren().addAll(programadorLabel, programadorValue, lucasLabel, telefonoLabel, telefonoValue, telefonoNumber);

        Scene scene = new Scene(grid, 500, 150); // Ajusta el tamaño de la escena aquí
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
