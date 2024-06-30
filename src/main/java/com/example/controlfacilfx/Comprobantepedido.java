package com.example.controlfacilfx;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceRGB;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionURI;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDBorderStyleDictionary;

public class Comprobantepedido {
    public static void generarComprobantePDF(ArrayList<Producto> productos,
                                             double total,
                                             String metodoPago,
                                             String comprobante,
                                             double porcentaje,
                                             double totalVenta,
                                             String detallesCliente,
                                             String estado,
                                             String horarioentrega,
                                             String fechaentrega,
                                             String hipervinculo,
                                             String vehiculo,
                                             String direccionentrega) {

        // Obtener la fecha y hora actual
        Date fechaActual = new Date();
        SimpleDateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat formatoHora = new SimpleDateFormat("HH:mm:ss");
        String fecha = formatoFecha.format(fechaActual);
        String hora = formatoHora.format(fechaActual);

        // Obtener la conexión a la base de datos
        try (Connection connection = ConexionBD.obtenerConexion()) {

            // Preparar la consulta SQL para insertar los datos del comprobante
            String query = "INSERT INTO registropedidos (fecha, hora, productos, cliente, total, metododepago, porcentaje, precio_final, estado, horario_entrega, fecha_entrega, hipervinculo, vehiculo, direccion_entrega) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
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
                preparedStatement.setString(9, estado); // Usamos el parámetro estado
                preparedStatement.setString(10, horarioentrega);
                preparedStatement.setString(11, fechaentrega);
                preparedStatement.setString(12, hipervinculo);
                preparedStatement.setString(13, vehiculo);
                preparedStatement.setString(14, direccionentrega);

                // Ejecutar la consulta
                preparedStatement.executeUpdate();

                // Generar el comprobante PDF
                crearComprobantePDF(fechaActual, productos, total, metodoPago, porcentaje, totalVenta, detallesCliente, estado, horarioentrega, fechaentrega, hipervinculo, vehiculo, direccionentrega);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static final int MAX_PRODUCTOS_POR_PAGINA = 20; // Máximo de productos por página

    private static void crearComprobantePDF(Date fechaActual, ArrayList<Producto> productos,
                                            double total,
                                            String metodoPago,
                                            double porcentaje,
                                            double totalVenta,
                                            String detallesCliente,
                                            String estado,
                                            String horarioentrega,
                                            String fechaentrega,
                                            String hipervinculo,
                                            String vehiculo,
                                            String direccionentrega) {

        // Generar nombre de archivo único basado en la fecha y hora actual con guiones bajos
        SimpleDateFormat sdf = new SimpleDateFormat("dd_MM_yyyy_HHmm");
        String nombreArchivo = "C:/ControlFacil/comprobantespedido/comprobante_" + sdf.format(fechaActual) + ".pdf";

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);
            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            contentStream.setFont(PDType1Font.HELVETICA, 12);
            contentStream.beginText();
            contentStream.newLineAtOffset(50, 750);

            float yPosition = 750; // posición Y inicial

            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 18);
            contentStream.showText("Comprobante de Venta");
            yPosition -= 20;
            contentStream.newLineAtOffset(0, -20);

            SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            String fechaHoraActual = formatoFecha.format(fechaActual);
            contentStream.setFont(PDType1Font.HELVETICA, 12);
            contentStream.showText("Fecha y Hora: " + fechaHoraActual);
            yPosition -= 20;
            contentStream.newLineAtOffset(0, -20);
            contentStream.showText("******************************************");
            yPosition -= 40;
            contentStream.newLineAtOffset(0, -40);

            // Detalles del Cliente
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
            contentStream.showText("Detalles del Cliente:");
            yPosition -= 20;
            contentStream.newLineAtOffset(0, -20);
            contentStream.setFont(PDType1Font.HELVETICA, 12);
            String[] lineasDetalles = detallesCliente.split("\\n");
            for (String linea : lineasDetalles) {
                if (linea.toLowerCase().startsWith("id")) {
                    continue;
                }
                contentStream.showText(linea);
                yPosition -= 20;
                contentStream.newLineAtOffset(0, -20);
            }
            yPosition -= 20;
            contentStream.newLineAtOffset(0, -20);
            contentStream.showText("******************************************");
            yPosition -= 40;
            contentStream.newLineAtOffset(0, -40);

            // Productos
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
            contentStream.showText("Productos:");
            yPosition -= 20;
            contentStream.newLineAtOffset(0, -20);
            contentStream.setFont(PDType1Font.HELVETICA, 12);

            int productosPorPagina = 0;
            for (Producto producto : productos) {
                contentStream.showText(producto.getNombre() + " x " + producto.getCantidad() + " " + producto.getUnidad() + ": $" + (producto.getPrecio() * producto.getCantidad()));
                yPosition -= 20;
                contentStream.newLineAtOffset(0, -20);

                productosPorPagina++;
                if (productosPorPagina >= MAX_PRODUCTOS_POR_PAGINA) {
                    contentStream.endText();
                    contentStream.close();
                    page = new PDPage(PDRectangle.A4);
                    document.addPage(page);
                    contentStream = new PDPageContentStream(document, page);
                    contentStream.setFont(PDType1Font.HELVETICA, 12);
                    contentStream.beginText();
                    contentStream.newLineAtOffset(50, 750);
                    yPosition = 750;
                    productosPorPagina = 0;
                }
            }
            yPosition -= 20;
            contentStream.newLineAtOffset(0, -20);
            contentStream.showText("******************************************");
            yPosition -= 40;
            contentStream.newLineAtOffset(0, -40);

            contentStream.showText("Total: $" + total);
            yPosition -= 20;
            contentStream.newLineAtOffset(0, -20);
            contentStream.showText("Método de Pago: " + metodoPago);
            yPosition -= 20;
            contentStream.newLineAtOffset(0, -20);
            contentStream.showText("Porcentaje: " + porcentaje * 100 + "%");
            yPosition -= 20;
            contentStream.newLineAtOffset(0, -20);

            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
            contentStream.showText("Total final: $" + totalVenta);
            yPosition -= 20;
            contentStream.newLineAtOffset(0, -20);
            contentStream.showText("******************************************");
            yPosition -= 40;
            contentStream.newLineAtOffset(0, -40);

            // Otros detalles
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
            contentStream.showText("Estado:");
            yPosition -= 20;
            contentStream.setFont(PDType1Font.HELVETICA, 12);
            contentStream.newLineAtOffset(0, -20);
            contentStream.showText(estado);
            yPosition -= 20;
            contentStream.newLineAtOffset(0, -20);

            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
            contentStream.showText("Horario de Entrega:");
            yPosition -= 20;
            contentStream.setFont(PDType1Font.HELVETICA, 12);
            contentStream.newLineAtOffset(0, -20);
            contentStream.showText(horarioentrega);
            yPosition -= 20;
            contentStream.newLineAtOffset(0, -20);

            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
            contentStream.showText("Fecha de Entrega:");
            yPosition -= 20;
            contentStream.setFont(PDType1Font.HELVETICA, 12);
            contentStream.newLineAtOffset(0, -20);
            contentStream.showText(fechaentrega);
            yPosition -= 20;
            contentStream.newLineAtOffset(0, -20);

            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
            contentStream.showText("Link producto:");
            yPosition -= 20;
            contentStream.setFont(PDType1Font.HELVETICA, 12);
            contentStream.newLineAtOffset(0, -20);

            // Color azul para el hipervínculo
            PDColor blueColor = new PDColor(new float[] { 0, 0, 1 }, PDDeviceRGB.INSTANCE);
            contentStream.setNonStrokingColor(blueColor);

            // Crear el enlace
            float linkX = 50;  // Coordenada X del enlace
            float linkY = yPosition;  // Coordenada Y del enlace
            float linkWidth = 200;  // Ancho del enlace
            float linkHeight = 15;  // Alto del enlace

            contentStream.showText("Link producto:");
            yPosition -= 20;
            contentStream.newLineAtOffset(0, -20);

            PDAnnotationLink txtLink = new PDAnnotationLink();
            PDRectangle position = new PDRectangle();
            position.setLowerLeftX(linkX);
            position.setLowerLeftY(linkY);
            position.setUpperRightX(linkX + linkWidth);
            position.setUpperRightY(linkY + linkHeight);
            txtLink.setRectangle(position);
            PDActionURI action = new PDActionURI();
            action.setURI(hipervinculo);
            txtLink.setAction(action);
            page.getAnnotations().add(txtLink);

            // Restaurar el color de relleno a negro para el contenido restante
            PDColor blackColor = new PDColor(new float[] { 0, 0, 0 }, PDDeviceRGB.INSTANCE);
            contentStream.setNonStrokingColor(blackColor);

            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
            contentStream.showText("Vehículo:");
            yPosition -= 20;
            contentStream.setFont(PDType1Font.HELVETICA, 12);
            contentStream.newLineAtOffset(0, -20);
            contentStream.showText(vehiculo);
            yPosition -= 20;
            contentStream.newLineAtOffset(0, -20);

            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
            contentStream.showText("Dirección de Entrega:");
            yPosition -= 20;
            contentStream.setFont(PDType1Font.HELVETICA, 12);
            contentStream.newLineAtOffset(0, -20);
            contentStream.showText(direccionentrega);

            contentStream.endText();
            contentStream.close();

            document.save(nombreArchivo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}