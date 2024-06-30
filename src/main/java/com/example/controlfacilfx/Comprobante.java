package com.example.controlfacilfx;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.File;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

public class Comprobante {
    public static void generarComprobantePDF(ArrayList<Producto> productos,
                                             double total,
                                             String metodoPago,
                                             String comprobante,
                                             double porcentaje,
                                             double totalVenta,
                                             String detallesCliente) {

        // Obtener la fecha y hora actual
        Date fechaActual = new Date();
        SimpleDateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat formatoHora = new SimpleDateFormat("HH:mm:ss");
        String fecha = formatoFecha.format(fechaActual);
        String hora = formatoHora.format(fechaActual);

        // Obtener la conexión a la base de datos
        try (Connection connection = ConexionBD.obtenerConexion()) {

            // Preparar la consulta SQL para insertar los datos del comprobante
            String query = "INSERT INTO registrodeventas (fecha, hora, productos, cliente, total, metododepago, porcentaje, precio_final) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {

                // Convertir la lista de productos a una cadena
                StringBuilder productosString = new StringBuilder();
                for (Producto producto : productos) {
                    productosString.append(producto.getNombre()).append(" x ").append(producto.getCantidad()).append(" ").append(producto.getUnidad()).append(": $").append(producto.getPrecio() * producto.getCantidad()).append("\n");
                }

                // Separar los detalles del cliente por espacios
                String[] detallesClienteArray = detallesCliente.split("\\s+");

                // Crear una cadena para almacenar solo nombre, apellido y documento
                StringBuilder detallesClienteFormateados = new StringBuilder();
                for (int i = 0; i < Math.min(detallesClienteArray.length, 6); i++) {
                    detallesClienteFormateados.append(detallesClienteArray[i]);
                    if (i < 2) { // Agregar un espacio entre cada detalle excepto antes del documento
                        detallesClienteFormateados.append(" ");
                    }
                }
                // Insertar los datos en la consulta preparada
                preparedStatement.setString(1, fecha);
                preparedStatement.setString(2, hora);
                preparedStatement.setString(3, productosString.toString());
                preparedStatement.setString(4, detallesClienteFormateados.toString()); // Utilizamos detallesClienteFormateados en lugar de detallesCliente
                preparedStatement.setDouble(5, total);
                preparedStatement.setString(6, metodoPago);
                preparedStatement.setDouble(7, porcentaje);
                preparedStatement.setDouble(8, totalVenta);

                // Ejecutar la consulta
                preparedStatement.executeUpdate();

                // Generar el comprobante PDF
                generarComprobantePDF(fechaActual, productos, total, metodoPago, porcentaje, totalVenta, detallesCliente);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static final int MAX_PRODUCTOS_POR_PAGINA = 20; // Máximo de productos por página
    private static void generarComprobantePDF(Date fechaActual, ArrayList<Producto> productos,
                                              double total,
                                              String metodoPago,
                                              double porcentaje,
                                              double totalVenta,
                                              String detallesCliente) {

        SimpleDateFormat sdf = new SimpleDateFormat("dd_MM_yyyy_HHmm");
        String nombreArchivo = "C:/ControlFacil/comprobantes/comprobante_" + sdf.format(fechaActual) + ".pdf";

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 18);
            contentStream.beginText();
            contentStream.newLineAtOffset(50, 750);
            contentStream.showText("Comprobante de Venta");
            contentStream.newLineAtOffset(0, -20);
            contentStream.setFont(PDType1Font.HELVETICA, 12);
            contentStream.showText("Fecha y Hora: " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(fechaActual));
            contentStream.newLineAtOffset(0, -20);
            contentStream.showText("******************************************");

            contentStream.newLineAtOffset(0, -40);

            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
            contentStream.showText("Detalles del Cliente:");
            contentStream.newLineAtOffset(0, -20);

            contentStream.setFont(PDType1Font.HELVETICA, 12);
            String[] lineasDetalles = detallesCliente.split("\\n");
            for (String linea : lineasDetalles) {
                if (linea.toLowerCase().startsWith("id")) {
                    continue;
                }
                contentStream.showText(linea.trim());
                contentStream.newLineAtOffset(0, -20);
            }

            contentStream.newLineAtOffset(0, -20);
            contentStream.showText("******************************************");

            contentStream.newLineAtOffset(0, -40);

            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
            contentStream.showText("Productos:");
            contentStream.newLineAtOffset(0, -20);

            int productosPorPagina = 0;
            for (Producto producto : productos) {
                contentStream.showText(producto.getNombre() + " x " + producto.getCantidad() + " " + producto.getUnidad() + ": $" + (producto.getPrecio() * producto.getCantidad()));
                contentStream.newLineAtOffset(0, -20);

                productosPorPagina++;

                if (productosPorPagina >= MAX_PRODUCTOS_POR_PAGINA) {
                    // Si alcanzamos el límite de productos por página, agregamos una nueva página
                    contentStream.endText();
                    contentStream.close();

                    page = new PDPage();
                    document.addPage(page);
                    contentStream = new PDPageContentStream(document, page);
                    contentStream.setFont(PDType1Font.HELVETICA, 12);
                    contentStream.beginText();
                    contentStream.newLineAtOffset(50, 750);

                    productosPorPagina = 0; // Reiniciamos el contador de productos por página
                }
            }

            contentStream.newLineAtOffset(0, -20);
            contentStream.showText("******************************************");

            contentStream.newLineAtOffset(0, -40);

            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
            contentStream.showText("Total:");
            contentStream.newLineAtOffset(0, -20);

            contentStream.showText("Suma Total: $" + total);
            contentStream.newLineAtOffset(0, -20);

            contentStream.showText("Método de Pago: " + metodoPago);
            contentStream.newLineAtOffset(0, -20);

            contentStream.showText("Porcentaje: " + porcentaje * 100 + "%");
            contentStream.newLineAtOffset(0, -20);

            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
            contentStream.showText("Total final: $" + totalVenta);
            contentStream.setFont(PDType1Font.HELVETICA, 12);
            contentStream.newLineAtOffset(0, -20);

            contentStream.newLineAtOffset(0, -20);
            contentStream.showText("======Gracias por su compra=====");

            contentStream.endText();
            contentStream.close();

            document.save(nombreArchivo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
