/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package practicafinalsisii;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.element.Image;
//import com.itextpdf.kernel.pdf.PdfName.Color;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.Calendar;
import pojos.Contribuyente;
import pojos.Lineasrecibo;
import pojos.Ordenanza;
import java.util.LinkedList;
import pojos.Recibos;
/**
 *
 * @author Usuario
 */
public class PdfCreator {
    private DecimalFormat decimalFormat = new DecimalFormat("#.##");
    public void createPdf(Contribuyente contribuyente, Recibos recibo, String pueblo, String tipoCalculo, String trimestre, String anho, LinkedList<Lineasrecibo> linea) throws FileNotFoundException{
        String nombreN = contribuyente.getNifnie() + contribuyente.getNombre().replaceAll(" ", "") + contribuyente.getApellido1().replaceAll(" ", "");
            if (contribuyente.getApellido2() != null) {
                nombreN += contribuyente.getApellido2().replaceAll(" ", "");
            }
        PdfWriter writer = new PdfWriter("./resources/recibos/" + nombreN + ".pdf");
        PdfDocument pdfDoc = new PdfDocument(writer);
        com.itextpdf.layout.Document doc = new com.itextpdf.layout.Document(pdfDoc, PageSize.LETTER);
        doc.add(firstTable(pueblo, tipoCalculo, contribuyente, recibo));
        doc.add(tablaDestinatario(recibo));
        doc.add(new Paragraph("\n"));
        doc.add(thirdTable(recibo));
        doc.add(new Paragraph("\n"));
        doc.add(fourthTable(trimestre, anho));
        doc.add(new Paragraph("\n"));
        doc.add(tablaDatosRecibo(recibo, linea));
        doc.add(sixthTable(contribuyente, recibo));
        doc.add(new Paragraph("\n"));
        doc.add(tableTotalBaseImponibleTotalIva(recibo));
        doc.add(new Paragraph("\n"));
        doc.add(lastTable(recibo));
        
        doc.close();
    }
    
    
    private Table firstTable(String pueblo, String tipoCalculo, Contribuyente c, Recibos r){
        //Creamos tabla
        Table tabla = new Table(2);
        tabla.setWidth(500);
        
        //Creamos parrafos
        Paragraph city = new Paragraph(pueblo);
        Paragraph cif = new Paragraph("P24001017F");
        Paragraph dir1 = new Paragraph("Calle de la Iglesia, 13");
        Paragraph dir2 = new Paragraph("24280 Astorga León");
        
        Paragraph iban = new Paragraph("IBAN:" + c.getIban());
        Paragraph calc = new Paragraph("Tipo de Calculo: " + tipoCalculo);
        //Adaptar con calendar la fecha
        Calendar alta = Calendar.getInstance();
        alta.setTime(c.getFechaAlta());
        String fecha = alta.get(Calendar.DAY_OF_MONTH) + "/" + (alta.get(Calendar.MONTH) + 1) + "/" + alta.get(Calendar.YEAR);
        Paragraph dateAlta = new Paragraph("Fecha de alta:" + fecha);
        
        //creamos y seteamos parametros de la celda
        Cell cell1 = new Cell();
        cell1.setBorder(new SolidBorder(1));
        cell1.setWidth(250);
        cell1.setTextAlignment(TextAlignment.CENTER);
        
        Cell cell2 = new Cell();
        cell2.setBorder(Border.NO_BORDER);
        cell2.setWidth(250);
        cell2.setTextAlignment(TextAlignment.RIGHT);
        
        //Añadimos los parrafos a la celda
        cell1.add(city);
        cell1.add(cif);
        cell1.add(dir1);
        cell1.add(dir2);
        
        cell2.add(iban);
        cell2.add(calc);
        cell2.add(dateAlta);
        
        //Añadimos la celda a la tabla
        tabla.addCell(cell1);
        tabla.addCell(cell2);
        
        return tabla;
    }
    
    
    
    private Table thirdTable(Recibos r){
        //Creamos tabla
        Table tabla = new Table(3);
        tabla.setWidth(500);
        
        //Creamos parrafos
        Paragraph lecturaAct = new Paragraph("Lectura Actual: " + r.getLecturaActual());
        Paragraph lecturaAnt = new Paragraph("Tipo de Calculo: " + r.getLecturaAnterior());
        Paragraph consumo = new Paragraph("Consumo: " + r.getConsumom3() + "metros cúbicos.");
        
        //Creamos y seteamos parametros de la celda
        Cell cell1 = new Cell();
        cell1.setBorderTop(new SolidBorder(1));
        cell1.setBorderBottom(new SolidBorder(1));
        cell1.setBorderLeft(new SolidBorder(1));
        Cell cell2 = new Cell();
        cell2.setBorderTop(new SolidBorder(1));
        cell2.setBorderBottom(new SolidBorder(1));
        Cell cell3 = new Cell();
        cell3.setBorderTop(new SolidBorder(1));
        cell3.setBorderBottom(new SolidBorder(1));
        cell3.setBorderRight(new SolidBorder(1));
        
        //Añadimos los parrafos a la celda
        cell1.add(lecturaAct);
        cell2.add(lecturaAnt);
        cell3.add(consumo);
        
        //Añadimos la celda a la tabla
        tabla.addCell(cell1);
        tabla.addCell(cell2);
        tabla.addCell(cell3);
        
        return tabla;
    }
    
    private Table fourthTable(String trimestre, String anho){
        //Creamos tabla
        Table tabla = new Table(1);
        tabla.setWidth(500);
        
        //Comprobar el trimestre y seleccionar el correspondiente
        String trimestreTexto = "";
        switch(trimestre){
            case "1":
                trimestreTexto = "Primer";
                break;
            case "2":
                trimestreTexto = "Segundo";
                break;
            case "3":
                trimestreTexto = "Tercer";
                break;
            case "4":
                trimestreTexto = "Cuarto";
                break;
        }
        //Creamos parrafos
        Paragraph recibo = new Paragraph("Recibo agua: " + trimestreTexto + " trimestre de " + anho);
        recibo.setBold();
        
        //Creamos y seteamos parametros de la celda
        Cell cell = new Cell();
        cell.setBorder(Border.NO_BORDER);
        cell.setTextAlignment(TextAlignment.CENTER);
        
        //Añadimos los parrafos a la celda
        cell.add(recibo);
        
        //Añadimos la celda a la tabla
        tabla.addCell(cell);
        
        return tabla;
    }
    
    private Table sixthTable(Contribuyente c, Recibos r){
        //Creamos tabla
        Table tabla;
        if(c.getBonificacion() == 0){
            tabla = new Table(6);
        }else{
            tabla = new Table(7);
        }
        tabla.setWidth(500);
        
        //Creamos parrafos
        Paragraph empty = new Paragraph("");
        Paragraph base = new Paragraph(decimalFormat.format(r.getTotalBaseImponible()) + "");
        Paragraph iva = new Paragraph(decimalFormat.format(r.getTotalIva()) + "");
        
        for(int i = 0; i < 6; i++){
            //Creamos y seteamos parametros de la celda
            Cell cell = new Cell();
            cell.setBorderTop(new SolidBorder(2));
            cell.setBorderBottom(Border.NO_BORDER);
            cell.setBorderLeft(Border.NO_BORDER);
            cell.setBorderRight(Border.NO_BORDER);
            cell.setTextAlignment(TextAlignment.CENTER);
            
            //Añadimos los parrafos a la celda
            if(i == 1){
                cell.add(new Paragraph("TOTALES"));
            }else if(i == 3){
                cell.add(base);
            }else{
                cell.add(empty);
            }
            
            if(i == 5){
                if(c.getBonificacion() == 0){
                    cell.add(iva);
                }else{
                     //Creamos y seteamos parametros de la celda
                    cell.add(iva);
                    Cell cell2 = new Cell();
                    cell2.setBorderTop(new SolidBorder(2));
                    cell2.setBorderBottom(Border.NO_BORDER);
                    cell2.setBorderLeft(Border.NO_BORDER);
                    cell2.setBorderRight(Border.NO_BORDER);
                    cell2.setTextAlignment(TextAlignment.RIGHT);
                    cell2.add(empty);
                    tabla.addCell(cell2);
                }
            }
            
            tabla.addCell(cell);
        }
        
        return tabla;
    }
    
    private Table tableTotalBaseImponibleTotalIva(Recibos recibo){
        Table table7 = new Table(2);
        table7.setWidth(500);
        
        Cell cell = new Cell();
        cell.setBorder(Border.NO_BORDER);
        
        Paragraph totalBaseImponible = new Paragraph("TOTAL BASE IMPONIBLE......................................");
        totalBaseImponible.setTextAlignment(TextAlignment.LEFT);
        cell.add(totalBaseImponible);
        
        Paragraph totalIva = new Paragraph("TOTAL IVA..............................................................");
        totalIva.setTextAlignment(TextAlignment.LEFT);
        cell.add(totalIva);
        
        table7.addCell(cell);
        
        cell = new Cell();
        cell.setBorder(Border.NO_BORDER);
        
        totalBaseImponible = new Paragraph(decimalFormat.format(recibo.getTotalBaseImponible()) + "");
        totalBaseImponible.setTextAlignment(TextAlignment.RIGHT);
        cell.add(totalBaseImponible);
        
        totalIva = new Paragraph(decimalFormat.format(recibo.getTotalIva()) + "");
        totalIva.setTextAlignment(TextAlignment.RIGHT);
        cell.add(totalIva);
        
        table7.addCell(cell);
        
        return table7;
    }
    
    private Table lastTable(Recibos r){
        //Creamos tabla
        Table tabla = new Table(2);
        tabla.setWidth(500);
        
        //Creamos parrafos
        Paragraph recibo = new Paragraph("TOTAL RECIBO.................");
        Paragraph num = new Paragraph(decimalFormat.format(r.getTotalRecibo()) + "");
        
        //Creamos y seteamos parametros de la celda
        Cell cell1 = new Cell();
        cell1.setBorderTop(new SolidBorder(3));
        cell1.setBorderBottom(Border.NO_BORDER);
        cell1.setBorderLeft(Border.NO_BORDER);
        cell1.setBorderRight(Border.NO_BORDER);
        cell1.setTextAlignment(TextAlignment.LEFT);
        
        Cell cell2 = new Cell();
        cell2.setBorderTop(new SolidBorder(3));
        cell2.setBorderBottom(Border.NO_BORDER);
        cell2.setBorderLeft(Border.NO_BORDER);
        cell2.setBorderRight(Border.NO_BORDER);
        cell2.setTextAlignment(TextAlignment.RIGHT);
        
        //Añadimos los parrafos a la celda
        cell1.add(recibo);
        cell2.add(num);
        
        //Añadimos las celdas a la tabla
        tabla.addCell(cell1);
        tabla.addCell(cell2);
        
        return tabla;
    }
    
    private Table tablaDestinatario(Recibos recibo){
        Table table2 = new Table(2);
        
        table2.setWidth(500);
        
        Paragraph destinatario = new Paragraph("Destinatario");
        destinatario.setBold();
        destinatario.setTextAlignment(TextAlignment.LEFT);
        
        Paragraph nombre = new Paragraph(recibo.getNombre());
        nombre.setTextAlignment(TextAlignment.RIGHT);
        
        Paragraph dni = new Paragraph(recibo.getNifContribuyente().toString());
        dni.setTextAlignment(TextAlignment.RIGHT);
        
        Paragraph direccion = new Paragraph(recibo.getDireccionCompleta());
        direccion.setTextAlignment(TextAlignment.RIGHT);
        
        
        Cell cell = new Cell();
        cell.setBorder(Border.NO_BORDER);
        cell.setWidth(250);
        table2.addCell(cell);
        
        cell = new Cell();
        cell.setBorder(new SolidBorder(1));
        cell.setWidth(250); 
        
        cell.add(destinatario);
        cell.add(nombre);
        cell.add(dni);
        cell.add(direccion);
        
        table2.addCell(cell);
        
        return table2;        
    }
    
    private Table tablaDatosRecibo(Recibos recibo, LinkedList<Lineasrecibo> lineasRecibo){
        Table table5 = new Table(7);
        if(recibo.getContribuyente().getBonificacion()==0){
            table5 = new Table(6);
        }
        table5.setWidth(500);
        
        Paragraph concept = new Paragraph("Concepto");
        Paragraph subConcept = new Paragraph("Subconcepto");
        Paragraph mCubicosIncluidos = new Paragraph("M3 incluídos");
        Paragraph baseImponible = new Paragraph("B.Imponible");
        Paragraph iva= new Paragraph("IVA %");
        Paragraph importe = new Paragraph("Importe");
        Paragraph descuento = new Paragraph("Descuento");
        
        Cell cell = new Cell();
        cell.setBorderLeft(Border.NO_BORDER);
        cell.setBorderRight(Border.NO_BORDER);
        cell.setBorderTop(new SolidBorder(3));
        cell.setBorderBottom(new SolidBorder(3));
        cell.add(concept);
        table5.addCell(cell);
        
        cell = new Cell();
         cell.setBorderLeft(Border.NO_BORDER);
        cell.setBorderRight(Border.NO_BORDER);
        cell.setBorderTop(new SolidBorder(3));
        cell.setBorderBottom(new SolidBorder(3));
        cell.add(subConcept);
        table5.addCell(cell);
        
        cell = new Cell();
        cell.setBorderLeft(Border.NO_BORDER);
        cell.setBorderRight(Border.NO_BORDER);
        cell.setBorderTop(new SolidBorder(3));
        cell.setBorderBottom(new SolidBorder(3));
        cell.add(mCubicosIncluidos);
        table5.addCell(cell);
        
        cell = new Cell();
        cell.setBorderLeft(Border.NO_BORDER);
        cell.setBorderRight(Border.NO_BORDER);
        cell.setBorderTop(new SolidBorder(3));
        cell.setBorderBottom(new SolidBorder(3));
        cell.add(baseImponible);
        table5.addCell(cell);
        
        cell = new Cell();
        cell.setBorderLeft(Border.NO_BORDER);
        cell.setBorderRight(Border.NO_BORDER);
        cell.setBorderTop(new SolidBorder(3));
        cell.setBorderBottom(new SolidBorder(3));
        cell.add(iva);
        table5.addCell(cell);
        
        cell = new Cell();
        cell.setBorderLeft(Border.NO_BORDER);
        cell.setBorderRight(Border.NO_BORDER);
        cell.setBorderTop(new SolidBorder(3));
        cell.setBorderBottom(new SolidBorder(3));
        cell.add(importe);
        table5.addCell(cell);
        
        if (recibo.getContribuyente().getBonificacion()!=0){
            cell = new Cell();
            cell.setBorderLeft(Border.NO_BORDER);
            cell.setBorderRight(Border.NO_BORDER);
            cell.setBorderTop(new SolidBorder(3));
            cell.setBorderBottom(new SolidBorder(3));
            cell.add(descuento);
            table5.addCell(cell); 
        
        }
        
        
        for(Lineasrecibo lineaRecibo : lineasRecibo){
            cell = new Cell();
            cell.setBorder(Border.NO_BORDER);
            concept = new Paragraph(lineaRecibo.getConcepto());
            concept.setTextAlignment(TextAlignment.CENTER);
            cell.add(concept);
            table5.addCell(cell);

            cell = new Cell();
            cell.setBorder(Border.NO_BORDER);
            subConcept = new Paragraph(lineaRecibo.getSubconcepto());
            subConcept.setTextAlignment(TextAlignment.CENTER);
            cell.add(subConcept);
            table5.addCell(cell);

            cell = new Cell();
            cell.setBorder(Border.NO_BORDER);
            mCubicosIncluidos = new Paragraph(decimalFormat.format(lineaRecibo.getM3incluidos()) + "");
            mCubicosIncluidos.setTextAlignment(TextAlignment.CENTER);
            cell.add(mCubicosIncluidos);
            table5.addCell(cell);

            cell = new Cell();
            cell.setBorder(Border.NO_BORDER);
            baseImponible = new Paragraph(decimalFormat.format(lineaRecibo.getBaseImponible()) + "");
            baseImponible.setTextAlignment(TextAlignment.CENTER);
            cell.add(baseImponible);
            table5.addCell(cell);

            cell = new Cell();
            cell.setBorder(Border.NO_BORDER);
            iva = new Paragraph(decimalFormat.format(lineaRecibo.getPorcentajeIva()) + "");
            iva.setTextAlignment(TextAlignment.CENTER);
            cell.add(iva);
            table5.addCell(cell);

            cell = new Cell();
            cell.setBorder(Border.NO_BORDER);
            importe = new Paragraph(decimalFormat.format(lineaRecibo.getImporteIva()) + "");
            importe.setTextAlignment(TextAlignment.CENTER);
            cell.add(importe);
            table5.addCell(cell);

            if(recibo.getContribuyente().getBonificacion()!=0){
                cell = new Cell();
                cell.setBorder(Border.NO_BORDER);
                
                descuento = new Paragraph(decimalFormat.format(recibo.getContribuyente().getBonificacion()) + "");
                descuento.setTextAlignment(TextAlignment.CENTER);
                cell.add(descuento);
                table5.addCell(cell);

            }
            
        }
        
        
       return table5; 
    }
    

    public void tableResumen(double totalBases, double totalIvas, double totalRecibos, String trimestre, String anho) throws FileNotFoundException{
        PdfWriter writer = new PdfWriter("./resources/recibos/resumen.pdf");
        PdfDocument pdfDoc = new PdfDocument(writer);
        com.itextpdf.layout.Document doc = new com.itextpdf.layout.Document(pdfDoc, PageSize.LETTER);
        
        //Creamos tabla
        Table tabla = new Table(1);
        tabla.setWidth(500);
        
        //Creamos parrafos
        String trimestreTexto = "";
        switch(trimestre){
            case "1":
                trimestreTexto = "Primer";
                break;
            case "2":
                trimestreTexto = "Segundo";
                break;
            case "3":
                trimestreTexto = "Tercer";
                break;
            case "4":
                trimestreTexto = "Cuarto";
                break;
        }
        Paragraph titulo = new Paragraph("RESUMEN PADRON DE AGUA " + trimestreTexto + " trimestre de " + anho);
        Paragraph base = new Paragraph("TOTAL BASE IMPONIBLE....................." + decimalFormat.format(totalBases));
        Paragraph iva = new Paragraph("TOTAL IVA................................" + decimalFormat.format(totalIvas));
        Paragraph reciboTotal = new Paragraph("TOTAL RECIBOS..............................." + decimalFormat.format(totalRecibos));
        
        titulo.setTextAlignment(TextAlignment.LEFT);
        base.setTextAlignment(TextAlignment.LEFT);
        iva.setTextAlignment(TextAlignment.LEFT);
        reciboTotal.setTextAlignment(TextAlignment.LEFT);
        
        //Creamos celdas y seteamos sus parametros
        Cell cell = new Cell();
        cell.setBorder(new SolidBorder(2));
        
        //Añadimos los parrafos a la celda
        cell.add(titulo);
        cell.add(base);
        cell.add(iva);
        cell.add(reciboTotal);
        
        //Añadimos la celda a la tabla
        tabla.addCell(cell);
        
        //Añadimos al documento y lo cerramos
        doc.add(tabla);
        doc.close();
    }
    
    /*
    public void mod(Recibos recibo) throws FileNotFoundException{
        PdfWriter writer = new PdfWriter("./resources/recibos/mod.pdf");
        PdfDocument pdfDoc = new PdfDocument(writer);
        com.itextpdf.layout.Document doc = new com.itextpdf.layout.Document(pdfDoc, PageSize.LETTER);
        
        //Creamos tabla
        Table tabla = new Table(1);
        tabla.setWidth(500);
        
        Cell cell = new Cell();
        cell.setBorder(new SolidBorder(1));
        
        //Creamos parrafos
        Paragraph name = new Paragraph(recibo.getContribuyente().getNombre());
        Paragraph ap1 = new Paragraph(recibo.getContribuyente().getApellido1());
        cell.add(name);
        cell.add(ap1);
        if(recibo.getContribuyente().getApellido2() != null){
            Paragraph ap2 = new Paragraph(recibo.getContribuyente().getApellido2());
            cell.add(ap2);
        }
        Paragraph nif = new Paragraph(recibo.getContribuyente().getNifnie() + "");
        Paragraph fechA = new Paragraph(recibo.getContribuyente().getFechaAlta()+ "");
        Paragraph bonif = new Paragraph(recibo.getContribuyente().getBonificacion()+ "");
        Paragraph impTot = new Paragraph(recibo.getTotalRecibo()+ "");
        Paragraph m3 = new Paragraph(recibo.getConsumom3()+ "");
        Paragraph base = new Paragraph(recibo.getTotalBaseImponible()+ "");
        Paragraph iva = new Paragraph(recibo.getTotalIva()+ "");
        
        cell.add(nif);
        cell.add(fechA);
        cell.add(bonif);
        cell.add(impTot);
        cell.add(m3);
        cell.add(base);
        cell.add(iva);
        
        tabla.addCell(cell);
        doc.add(tabla);
        doc.close();
    }
    */
}
