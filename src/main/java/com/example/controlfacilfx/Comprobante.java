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

    private static void generarComprobantePDF(Date fechaActual, ArrayList<Producto> productos,
                                              double total,
                                              String metodoPago,
                                              double porcentaje,
                                              double totalVenta,
                                              String detallesCliente) {
        // Generar nombre de archivo único basado en la fecha y hora actual con guiones bajos
        SimpleDateFormat sdf = new SimpleDateFormat("dd_MM_yyyy_HHmm");
        String nombreArchivo = "C:/ControlFacil/comprobantes/comprobante_" + sdf.format(fechaActual);

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                // Usar la fuente Helvetica
                contentStream.setFont(PDType1Font.HELVETICA, 12);

                // Establecer posición inicial del texto
                float x = 50;
                float y = 700; // Ajustar la posición vertical

                contentStream.beginText();
                contentStream.newLineAtOffset(x, y);

                SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                String fechaHoraActual = formatoFecha.format(fechaActual);

                // Encabezado estilizado
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 18);
                contentStream.setLeading(20);
                contentStream.showText("Comprobante de Venta");
                contentStream.newLine();
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.showText("Fecha y Hora: " + fechaHoraActual);
                contentStream.newLine();
                contentStream.showText("******************************************");

                // Incremento en la posición Y para separar líneas
                y -= 40;

                // Mostrar detalles del cliente
                contentStream.newLineAtOffset(0, -20); // Salto de línea
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
                contentStream.newLine();
                contentStream.showText("Detalles del Cliente:");
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.newLineAtOffset(0, -20); // Salto de línea

                // Dividir el texto de los detalles del cliente en líneas
                String[] lineasDetalles = detallesCliente.split("\\n");

                // Mostrar cada línea de detalles del cliente en una nueva línea
                for (String linea : lineasDetalles) {
                    // Omitir el ID del cliente si está presente
                    if (linea.toLowerCase().startsWith("id")) {
                        continue; // Saltar la línea que comienza con "ID"
                    }

                    // Capitalizar la primera letra de cada palabra en la línea
                    String[] palabras = linea.split("\\s+");
                    StringBuilder lineaFormateada = new StringBuilder();
                    for (String palabra : palabras) {
                        if (!palabra.isEmpty()) { // Saltar palabras vacías
                            lineaFormateada.append(Character.toUpperCase(palabra.charAt(0)))
                                    .append(palabra.substring(1).toLowerCase())
                                    .append(" ");
                        }
                    }
                    // Mostrar la línea formateada
                    contentStream.showText(lineaFormateada.toString().trim());
                    contentStream.newLineAtOffset(0, -20); // Salto de línea

                }
                contentStream.newLine();
                contentStream.showText("******************************************");

                // Mostrar detalles del cliente
                contentStream.newLineAtOffset(0, -20); // Salto de línea
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
                contentStream.newLine();
                contentStream.showText("Productos:");
                contentStream.setFont(PDType1Font.HELVETICA, 12); // Restaurar la fuente normal
                contentStream.newLineAtOffset(0, -10); // Salto de línea

                for (Producto producto : productos) {
                    contentStream.newLineAtOffset(0, -20); // Salto de línea
                    contentStream.showText(producto.getNombre() + " x " + producto.getCantidad() + " " + producto.getUnidad() + ": $" + (producto.getPrecio() * producto.getCantidad()));
                }

                contentStream.newLineAtOffset(0, -20); // Salto de línea
                contentStream.showText("******************************************");

                contentStream.newLineAtOffset(0, -10); // Salto de línea
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
                contentStream.newLine();
                contentStream.showText("Total:");
                contentStream.setFont(PDType1Font.HELVETICA, 12);

                contentStream.newLineAtOffset(0, -20); // Salto de línea
                contentStream.showText("Suma Total: $" + total);

                // Mostrar el método de pago
                contentStream.newLineAtOffset(0, -20); // Salto de línea
                contentStream.showText("Método de Pago: " + metodoPago);

                contentStream.newLineAtOffset(0, -20); // Salto de línea
                contentStream.showText("Porcentaje: " + porcentaje + "%");

                contentStream.newLineAtOffset(0, -10); // Salto de línea
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contentStream.newLine();
                contentStream.showText("Total final: $" + totalVenta);
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.newLineAtOffset(0, -10); // Salto de línea
                contentStream.newLine();
                contentStream.showText("======Gracias por su compra=====");

                contentStream.endText();
            }

            // Guardar el documento con el nombre de archivo generado
            document.save(nombreArchivo + ".pdf");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
