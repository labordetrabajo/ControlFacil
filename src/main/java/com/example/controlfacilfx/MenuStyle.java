package com.example.controlfacilfx;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class MenuStyle {

    public static void applyStyleToMenu( GridPane menuPane) {
        RowConstraints rowConstraints = new RowConstraints();
        rowConstraints.setPercentHeight(20);
        menuPane.getRowConstraints().add(rowConstraints);

        menuPane.getStyleClass().add("menu-pane");

       // stage.setTitle("Menú Principal");
       // stage.show();
    }

    static {
        // Cargar la hoja de estilo al inicio
        String cssPath = MenuStyle.class.getResource("style.css").toExternalForm();
        Application.setUserAgentStylesheet(cssPath);
    }

    public static void applyStyleToMenuProductos(GridPane menuPane) {
        menuPane.getStyleClass().add("menu-productos-pane");
        menuPane.getStyleClass().add("menu-pane");
    }

    public static Button createStyledButton(String text) {
        Button button = new Button(text);
        button.getStyleClass().add("styled-button");

        return button;
    }

    public static void applyStyleToAgregarProductos(GridPane agregarProductoPane) {
        agregarProductoPane.getStyleClass().add("menu-agregarproductos-pane");
        applyStyleToMenu(agregarProductoPane);
    }

    public static void applyStyleToAgregarProductoElements(GridPane agregarProductoPane) {
        agregarProductoPane.getStyleClass().add("menu-agregarproductos-pane");
    }
}
