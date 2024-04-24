module com.example.controlfacilfx {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires poi;
    requires poi.ooxml;
    requires java.desktop;
    requires org.apache.pdfbox;

    opens com.example.controlfacilfx to javafx.fxml;
    exports com.example.controlfacilfx;
}