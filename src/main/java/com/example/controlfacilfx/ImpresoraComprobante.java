package com.example.controlfacilfx;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Paths;

public class ImpresoraComprobante {
    public static void imprimirComprobante(String rutaCompleta) {
        // Obtener el sistema operativo en el que se está ejecutando la aplicación
        String osName = System.getProperty("os.name").toLowerCase();

        try {
            // Abrir el archivo PDF en el navegador predeterminado
            File file = new File(rutaCompleta);
            Desktop desktop = Desktop.getDesktop();
            if (Desktop.isDesktopSupported() && desktop.isSupported(Desktop.Action.BROWSE)) {
                if (osName.contains("win")) {
                    // En Windows, abrir el archivo con el navegador predeterminado
                    desktop.browse(file.toURI());
                } else if (osName.contains("mac")) {
                    // En macOS, abrir el archivo con el navegador predeterminado
                    desktop.browse(new URI("file:///" + rutaCompleta));
                } else {
                    // En otros sistemas operativos, intentar abrir el archivo con el navegador predeterminado
                    desktop.browse(new URI(rutaCompleta));
                }
            }

            // Esperar un tiempo suficiente para que el navegador cargue el archivo
            Thread.sleep(5000);

            // Imprimir el archivo PDF desde el navegador utilizando la tecla de acceso directo Ctrl + P
            // Este paso no se puede automatizar directamente desde Java debido a restricciones de seguridad
            // El usuario debe realizar manualmente la impresión desde el navegador

        } catch (IOException | InterruptedException | java.net.URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
