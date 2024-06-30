/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package practicafinalsisii;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import static java.lang.Integer.min;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.util.HashMap;
import java.util.List;
import java.util.regex.*;
import org.apache.poi.ss.usermodel.Cell;
import pojos.Contribuyente;
import pojos.ContribuyenteDAO;
import pojos.Lecturas;
import pojos.LecturasDAO;
import pojos.Lineasrecibo;
import pojos.LineasreciboDAO;
import pojos.Ordenanza;
import pojos.OrdenanzaDAO;
import pojos.Recibos;
import pojos.RecibosDAO;
import pojos.RelContribuyenteOrdenanza;
import pojos.RelContribuyenteOrdenanzaDAO;

/**
 *
 * @author Usuario
 */
public class ExcelManager {

    private String ruta;
    private String[] listaLetras;
    private Pattern pattern;
    private Matcher matcher;
    private Calendar date;
    private LinkedList<Contribuyente> listaErroresDniNie;
    private LinkedList<Contribuyente> listaErroresCCC;
    private LinkedList<String> listaNIFS;
    private LinkedList<String> listaCCCErroneos;
    private LinkedList<Integer> idExcel;
    private LinkedList<RelContribuyenteOrdenanza> relaciones;

    private LinkedList<Contribuyente> listaContribuyentes;
    private LinkedList<Boolean> generar;

    private DecimalFormat decimalFormat = new DecimalFormat("#.##");

    private HashMap<Integer, String> letters = new HashMap<Integer, String>();
    private HashMap<String, Integer> ibanPesos = new HashMap<String, Integer>();

    // Array de ordenanzas
    private LinkedList<Ordenanza> listaOrdenanzas = new LinkedList();
    private LinkedList<Recibos> listaRecibos = new LinkedList();
    private LinkedList<LinkedList<Lineasrecibo>> listaLineasrecibo = new LinkedList();

    ExcelManager(String resourcesSistemasInformacionIIxlsx, Calendar c) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public ExcelManager(String ruta) {

        try {
            this.ruta = ruta;
            this.listaLetras = new String[]{"T", "R", "W", "A", "G", "M", "Y", "F", "P", "D", "X", "B", "N", "J", "Z", "S", "Q", "V", "H", "L", "C", "K", "E"};
            listaErroresDniNie = new LinkedList<Contribuyente>();
            listaNIFS = new LinkedList<>();
            listaErroresCCC = new LinkedList<Contribuyente>();
            listaCCCErroneos = new LinkedList<String>();
            listaContribuyentes = new LinkedList<Contribuyente>();
            generar = new LinkedList<>();
            idExcel = new LinkedList<>();
            relaciones = new LinkedList<>();
            rellenaHashLetters();
            rellenaHashPesos();
        } catch (Exception ex) {
            Logger.getLogger(ExcelManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void rellenaHashLetters() throws Exception {
        try {
            for (int i = 0; i < listaLetras.length; i++) {
                letters.put(i, listaLetras[i]);
            }
        } catch (Exception e) {
            throw new Exception();
        }
    }

    private void rellenaHashPesos() throws Exception {
        try {
            char letra;
            for (int i = 10; i < 36; i++) {
                letra = (char) (i + 55);
                ibanPesos.put("" + letra, i);
            }
        } catch (Exception e) {
            throw new Exception();
        }
    }

    private String checkCCC(String ccc) {
        String dcont1 = "00" + ccc.substring(0, 8);
        String dcont2 = ccc.substring(10);
        String digit1 = calcDigi(dcont1);
        String digit2 = calcDigi(dcont2);

        String actuald1 = "" + ccc.charAt(8);
        String actuald2 = "" + ccc.charAt(9);

        return dcont1.substring(2) + digit1 + digit2 + dcont2;
    }

    private String calcDigi(String num) {
        int sumatorio = 0;
        for (int i = 0; i < num.length(); i++) {
            int trans = Integer.parseInt(num.charAt(i) + "");
            sumatorio += (trans * Math.pow(2, i)) % 11;
        }
        sumatorio = (11 - (sumatorio % 11)) % 11;
        if (sumatorio == 0) {
            return "" + sumatorio;
        }
        if (sumatorio == 10) {
            sumatorio = 1;
        }
        return sumatorio + "";
    }

    private String checkDNI(String dni) throws Exception {

        pattern = Pattern.compile("^[0-9]{8}[A-Za-z]$");
        matcher = pattern.matcher(dni);

        if (matcher.find()) {
            String numero = dni.substring(0, 8);
            String letra = dni.substring(8);

            Integer.parseInt(numero);

            if (letra.equals(letters.get(Integer.parseInt(numero) % 23))) {
            } else {
                dni = numero + letters.get(Integer.parseInt(numero) % 23);
            }

        } else {
            pattern = Pattern.compile("^[0-9]{8}$");
            matcher = pattern.matcher(dni);
            if (matcher.find()) {
                String numero = dni.substring(0, 8);
                String letra = dni.substring(8);

                Integer.parseInt(numero);

                if (letra.equals(letters.get(Integer.parseInt(numero) % 23))) {
                } else {
                    dni = numero + letters.get(Integer.parseInt(numero) % 23);
                }
            } else {
                throw new Exception();
            }

        }
        return dni;
    }

    private String checkNIE(String nie) throws Exception {

        pattern = Pattern.compile("^[XYZxyz][0-9]{7}[A-Za-z]$");
        matcher = pattern.matcher(nie);

        if (matcher.find()) {
            String letra = nie.substring(0, 1);

            if (letra.equals("X")) {
                String nieN = checkDNI(0 + nie.substring(1));
                return "X" + nieN.substring(1);
            } else if (letra.equals("Y")) {
                String nieN = checkDNI(1 + nie.substring(1));
                return "Y" + nieN.substring(1);
            } else if (letra.equals("Z")) {
                String nieN = checkDNI(2 + nie.substring(1));
                return "Z" + nieN.substring(1);
            } else {
                return null;
            }
        } else {
            pattern = Pattern.compile("^[XYZxyz][0-9]{7}$");
            matcher = pattern.matcher(nie);
            if (matcher.find()) {
                String letra = nie.substring(0, 1);

                if (letra.equals("X")) {
                    String nieN = checkDNI(0 + nie.substring(1));
                    return "X" + nieN.substring(1);
                } else if (letra.equals("Y")) {
                    String nieN = checkDNI(1 + nie.substring(1));
                    return "Y" + nieN.substring(1);
                } else if (letra.equals("Z")) {
                    String nieN = checkDNI(2 + nie.substring(1));
                    return "Z" + nieN.substring(1);
                } else {
                    return null;
                }
            }
        }
        throw new Exception();

    }

    private String corrigeDniNie(String nifnie) throws Exception {
        try {
            rellenaHashLetters();
            if (Character.isLetter(nifnie.charAt(0))) {
                return checkNIE(nifnie);
            } else {
                return checkDNI(nifnie);
            }
        } catch (Exception e) {
            throw new Exception();
        }
    }

    private boolean existsDniNie(String nifnie, Contribuyente contribuyente) {
        for (int i = 0; i < this.listaNIFS.size(); i++) {
            if (nifnie.equals(listaNIFS.get(i))) {
                listaErroresDniNie.add(contribuyente);
                return true;
            }
        }
        return false;

    }

    private String generateIban(Contribuyente contribuyente, String paisAcc) throws Exception {
        try {
            rellenaHashPesos();
            int digit1 = ibanPesos.get(paisAcc.charAt(0) + "");
            int digit2 = ibanPesos.get(paisAcc.charAt(1) + "");

            String ccc = contribuyente.getCcc();
            ccc += digit1 + "" + digit2 + "00";

            BigInteger big = new BigInteger(ccc);
            BigInteger[] div = big.divideAndRemainder(new BigInteger("97"));

            int result = div[1].intValue();
            int digitsResult = 98 - result;
            String digitRes = "" + digitsResult;

            if (digitsResult < 10) {
                digitRes = "0" + digitsResult;
            }

            return paisAcc + digitRes + ccc.substring(0, ccc.length() - 6);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e);
        }
    }

    private String generateEmail(Contribuyente contribuyente) {
        String email = "" + contribuyente.getNombre().charAt(0) + contribuyente.getApellido1().charAt(0);
        int count = 0;

        if (contribuyente.getApellido2() != null && !contribuyente.getApellido2().equals("")) {
            email += contribuyente.getApellido2().charAt(0);
        }

        for (int i = 0; i < listaContribuyentes.size(); i++) {
            if (listaContribuyentes.get(i).getEemail().startsWith(email) && listaContribuyentes.get(i).getEemail().contains("Agua2024")) {
                count++;
            }
        }
        if (count < 10) {
            email += "0";
        }
        email += count + "@" + "Agua2024" + ".com";
        System.out.println(email);

        return email;

    }

    public void getInfoContribuyentes(int trimestre, int anho) {
        try {
            XSSFWorkbook wb = new XSSFWorkbook(new File(ruta));
            XSSFSheet sheet = wb.getSheetAt(0);
            Iterator<Row> rowIt = sheet.iterator();
            LinkedList<Lecturas> lecturas = new LinkedList<Lecturas>();
            LinkedList<String> conceptos = new LinkedList<String>();
            int cont = 1;

            if (rowIt.hasNext()) {
                rowIt.next();
            }
            int i = 0, id = 2;
            int month;

            switch (trimestre) {
                case 2:
                    month = 3;
                    break;
                case 3:
                    month = 6;
                    break;
                case 4:
                    month = 9;
                    break;
                default:
                    month = 0;
            }
            Calendar date = Calendar.getInstance();
            date.set(anho, month, 1);

            //Recorremos con el  iterador
            while (rowIt.hasNext()) {
                Row row = rowIt.next();
                cont++;
                if (row.getCell(0) != null) {
                    System.out.println("" + i);
                    String name = row.getCell(0).toString();
                    String surname1 = row.getCell(1).toString();
                    String surname2 = null;
                    Calendar fAlta = Calendar.getInstance();
                    Calendar fBaja = Calendar.getInstance();
                    Date dateAlta = row.getCell(14).getDateCellValue();
                    fAlta.setTime(dateAlta);
                    Date dateBaja = null;
                    if (row.getCell(15) != null && row.getCell(15).getDateCellValue() != null) { //row.getCell(15).getDateCellValue()
                        System.out.println("cont" + cont);

                        dateBaja = row.getCell(15).getDateCellValue();
                        System.out.println("fecha" + dateBaja);

                        fBaja.setTime(dateBaja);
                    }

                    if (row.getCell(2) != null) {
                        surname2 = row.getCell(2).toString();
                    }
                    String nifnie = "";
                    boolean correct = false;
                    boolean errores = false;
                    boolean sinErroresCCC = true;
                    if (row.getCell(3) != null) {

                        nifnie = row.getCell(3).toString();

                        String address = row.getCell(4).toString();
                        String number = row.getCell(5).toString();
                        String paisAcc = row.getCell(6).toString();
                        String codAcc = row.getCell(7).toString();
                        String exencion = row.getCell(10).toString();
                        String bonificacion = row.getCell(11).toString();
                        int lecturaAnterior = (int) row.getCell(12).getNumericCellValue();
                        int lecturaActual = (int) row.getCell(13).getNumericCellValue();
                        String concepto = row.getCell(16).toString();

                        //Añadido a posteriori. Obtencion de las lecturas y el concepto.
                        Contribuyente contribuyente = new Contribuyente();
                        Lecturas lectura = new Lecturas();
                        lectura.setEjercicio(anho + "");
                        lectura.setPeriodo(trimestre + "");
                        lectura.setLecturaAnterior(lecturaAnterior);
                        lectura.setLecturaActual(lecturaActual);

                        //Si la fecha es null, se establece por defecto la actual
                        if (row.getCell(15) != null) {
                            contribuyente.setFechaBaja(dateBaja);
                        }
                        contribuyente.setFechaBaja(null);

                        contribuyente.setNombre(name);
                        contribuyente.setApellido1(surname1);
                        if (surname2 != null) {
                            contribuyente.setApellido2(surname2);
                        }
                        contribuyente.setCcc(codAcc);
                        contribuyente.setFechaAlta(dateAlta);
                        String nifnieCorregido;
                        try {

                            nifnieCorregido = corrigeDniNie(nifnie);
                            if (nifnieCorregido == null) {
                                contribuyente.setNifnie(nifnie);
                                errores = true;

                            } else {
                                contribuyente.setNifnie(nifnieCorregido);

                            }
                            if (!nifnie.equals(nifnieCorregido)) {
                                //listaErroresDniNie.add(contribuyente);
                            } else {
                                boolean e = existsDniNie(nifnie, contribuyente);
                                listaNIFS.add(nifnie);
                                if (e) {
                                    errores = true;
                                }
                            }
                            correct = true;
                        } catch (Exception e) {
                            if (fAlta.compareTo(date) <= 0 && (dateBaja == null || fBaja.compareTo(date) > 0)) {

                                errores = true;
                                contribuyente.setNifnie(nifnie);
                                listaErroresDniNie.add(contribuyente);
                            }
                        }

                        String cccCorregido = this.checkCCC(codAcc);
                        if (!cccCorregido.equals(codAcc)) {
                            sinErroresCCC = false;
                            listaCCCErroneos.add(codAcc);
                            listaErroresCCC.add(contribuyente);
                        }
                        contribuyente.setCcc(cccCorregido);
                        contribuyente.setDireccion(address);

                        contribuyente.setNumero(number);
                        contribuyente.setExencion(exencion.charAt(0));
                        contribuyente.setBonificacion(Double.parseDouble(bonificacion));

                        if (correct) {
                            contribuyente.setEemail(generateEmail(contribuyente));
                            contribuyente.setIban(generateIban(contribuyente, paisAcc));
                        } else {
                            contribuyente.setEemail("");
                            contribuyente.setIban("");
                        }
                        contribuyente.setIdContribuyente(cont);
                        //Setear email llamando generateEmail();
                        if (fAlta.compareTo(date) <= 0 && (dateBaja == null || fBaja.compareTo(date) > 0)) {
                            if (errores == false) {
                                generar.add(sinErroresCCC);
                                listaContribuyentes.add(contribuyente);
                                lectura.setContribuyente(contribuyente);
                                lecturas.add(lectura);
                                lectura.setPeriodo(trimestre + "T");
                                lectura.setEjercicio(anho + "");
                                conceptos.add(concepto);
                                idExcel.add(id);
                            }
                        }
                    }
                }
                id++;
                i++;
            }
            /*FileOutputStream imp = new FileOutputStream(newFile("...\\resources"));
            wb.write(imp);
            imp.flush();
            wb.close();
            imp.close();*/
            wb.close();
            generateErrors("ErroresNifNie", listaErroresDniNie);
            generateErrors("ErroresCCC", listaErroresCCC);
            escribeExcel(this.ruta);
            leerOrdenanza(wb);

            for (i = 0; i < listaContribuyentes.size(); i++) {
                Calendar fAlta = Calendar.getInstance();
                Calendar fBaja = Calendar.getInstance();
                fAlta.setTime(listaContribuyentes.get(i).getFechaAlta());
                if (listaContribuyentes.get(i).getFechaBaja() != null) {
                    fBaja.setTime(listaContribuyentes.get(i).getFechaBaja());
                }

                if (generar.get(i)) {
                    calculaBaseImponible(listaContribuyentes.get(i), conceptos.get(i), lecturas.get(i));
                }

            }
            generateRecibosXML(trimestre, anho);
            PdfCreator pdf = new PdfCreator();
            double sumaBases = 0;
            double sumaIvas = 0;
            double sumaRecibos = 0;
            int counter = 0;
            for (Recibos recibo : listaRecibos) {
                //CORREGIR TIPO CALCULO IMPRIME MAL
                pdf.createPdf(recibo.getContribuyente(), recibo, listaOrdenanzas.get(0).getPueblo(), listaOrdenanzas.get(0).getTipoCalculo(), trimestre + "", anho + "", listaLineasrecibo.get(counter));
                sumaBases += recibo.getTotalBaseImponible();
                sumaIvas += recibo.getTotalIva();
                sumaRecibos += recibo.getTotalRecibo();
                counter++;
            }
            pdf.tableResumen(sumaBases, sumaIvas, sumaRecibos, trimestre + "", anho + "");
            for (Recibos recibo : listaRecibos) {
                if (recibo.getTotalRecibo() > sumaRecibos / listaRecibos.size()) {
                    //pdf.mod(recibo);
                }
            }

            ContribuyenteDAO contDao = new ContribuyenteDAO();

            LecturasDAO lectDao = new LecturasDAO();
            LineasreciboDAO linDao = new LineasreciboDAO();
            OrdenanzaDAO ordDao = new OrdenanzaDAO();
            RecibosDAO recDao = new RecibosDAO();
            RelContribuyenteOrdenanzaDAO relcoDao = new RelContribuyenteOrdenanzaDAO();

            List<Contribuyente> contsBase = contDao.getAll();

            for (Contribuyente contribuyente : listaContribuyentes) {
                for (Contribuyente contBase : contsBase) {
                    if (contribuyente.getNifnie().equals(contBase.getNifnie())) {
                        contribuyente.setIdContribuyente(contBase.getIdContribuyente());
                        break;
                    }
                }
            }
            i = 0;
            for (Contribuyente contribuyente : listaContribuyentes) {
                if (generar.get(i)) {
                    contDao.add(contribuyente);
                }
                i++;
                System.out.println(contribuyente.getIdContribuyente());
            }

            List<Lecturas> lecturasBase = lectDao.getAll();

            for (Lecturas lectura : lecturas) {
                for (Lecturas lecBase : lecturasBase) {
                    if (lectura.getContribuyente().getNifnie().equals(lecBase.getContribuyente().getNifnie()) && lectura.getPeriodo().equals(lecBase.getPeriodo()) && lectura.getEjercicio().equals(lecBase.getEjercicio())) {
                        lectura.setId(lecBase.getId());
                        break;
                    }
                }
            }
            i = 0;
            for (Lecturas lectura : lecturas) {
                if (generar.get(i) && lectura.getId() == 0) {
                    lectDao.add(lectura);
                }
                i++;
                System.out.println(lectura.getId());
            }

            List<Recibos> recibosBase = recDao.getAll();

            for (Recibos recibo : listaRecibos) {
                for (Recibos recBase : recibosBase) {
                    if (recibo.getContribuyente().getNifnie().equals(recibo.getContribuyente().getNifnie()) && recibo.getFechaRecibo().equals(recBase.getFechaRecibo())) {
                        recibo.setNumeroRecibo(recBase.getNumeroRecibo());
                        break;
                    }
                }
            }
            int proxNum = recibosBase.size();
            for (Recibos recibo : listaRecibos) {
                if (recibo.getNumeroRecibo() == 0) {
                    //recibo.setNumeroRecibo(proxNum++);
                }
            }

            for (Recibos recibo : listaRecibos) {

                recDao.add(recibo);
                System.out.println(recibo.getNumeroRecibo());
                System.out.println("EL PUTO RECIBO DE MIERDA: " + recibo.getFechaRecibo());
            }

            List<Lineasrecibo> lineasBase = linDao.getAll();

            for (List<Lineasrecibo> lineasR : listaLineasrecibo) {
                for (Lineasrecibo linea : lineasR) {
                    for (Lineasrecibo lineaBase : lineasBase) {
                        if (linea.getRecibos().getNumeroRecibo()==lineaBase.getRecibos().getNumeroRecibo() && linea.getConcepto().equals(lineaBase.getConcepto()) && linea.getSubconcepto().equals(lineaBase.getSubconcepto())) {
                            linea.setId(linea.getId());
                            break;
                        }
                    }
                }
            }
            proxNum = lineasBase.size();
            for (List<Lineasrecibo> lineasR : listaLineasrecibo) {
                for (Lineasrecibo linea : lineasR) {
                    if (linea.getId() == 0) {
                        //linea.setId(proxNum++);

                    }
                }
            }
            for (LinkedList<Lineasrecibo> listlin : listaLineasrecibo) {
                for (Lineasrecibo linea : listlin) {
                    
                    linDao.add(linea);
                    System.out.println(linea.getId());
                }
            }

            List<Ordenanza> ordenanzaBase = ordDao.getAll();

            for (Ordenanza ord : listaOrdenanzas) {
                for (Ordenanza ordBase : ordenanzaBase) {
                    if (ord.getIdOrdenanza() == ordBase.getIdOrdenanza() && ord.getConcepto().equals(ordBase.getConcepto()) && ord.getSubconcepto().equals(ordBase.getSubconcepto())) {
                        ord.setId(ordBase.getId());
                        break;
                    }
                }
            }

            for (Ordenanza ord : listaOrdenanzas) {
                if(ord.getId()== null){
                    ordDao.add(ord);
                }
            }

            List<RelContribuyenteOrdenanza> relContribuyente = relcoDao.getAll();

            for (RelContribuyenteOrdenanza rel : relaciones) {
                for (RelContribuyenteOrdenanza relBase : relContribuyente) {
                    if (rel.getContribuyente().equals(relBase.getContribuyente()) && rel.getOrdenanza().equals(relBase.getOrdenanza())) {
                        rel.setId(relBase.getId());
                        break;
                    }
                }
            }

            for (RelContribuyenteOrdenanza rel : relaciones) {

                relcoDao.add(rel);

            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void escribeExcel(String ruta) throws Exception {
        try {
            Contribuyente contribuyentes = new Contribuyente();
            FileInputStream f = new FileInputStream(ruta);
            XSSFWorkbook wb = new XSSFWorkbook(f);
            XSSFSheet hoja = wb.getSheetAt(0);
            Iterator<Row> rows = hoja.iterator();
            if (rows.hasNext()) {
                rows.next();
            }
            Iterator<Contribuyente> contIt = listaContribuyentes.iterator();

            int contador = 2, i = 0;
            while (rows.hasNext()) {
                Row row = rows.next();
                if (i < idExcel.size() && contador == idExcel.get(i)) {
                    if (row.getCell(0) != null && contIt.hasNext()) {
                        Contribuyente c = contIt.next();
                        Cell cell = row.createCell(3);
                        cell.setCellValue(c.getNifnie());
                        if (corrigeDniNie(c.getNifnie()) != null) {
                            cell = row.createCell(7);
                            cell.setCellValue(c.getCcc());
                            cell = row.createCell(8);
                            cell.setCellValue(c.getIban());
                            cell = row.createCell(9);
                            cell.setCellValue(c.getEemail());
                        }

                        i++;
                    }

                }
                contador++;

            }
            try ( //f.close();
                    FileOutputStream imp = new FileOutputStream(new File(ruta))) {
                wb.write(imp);
                imp.flush();
                imp.close();
                wb.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void generateErrors(String nameFile, LinkedList<Contribuyente> listaErrores) throws Exception {
        if (listaErrores.size() == 0) {
            return;
        }
        String raiz;
        if (nameFile.equals("ErroresNifNie")) {
            raiz = "Contribuyentes";
        } else {
            raiz = "Cuentas";
        }
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        DOMImplementation implementacion = builder.getDOMImplementation();
        Document document = implementacion.createDocument(null, raiz, null);
        document.setXmlVersion("1.0");
        Element root = document.getDocumentElement();
        for (int i = 0; i < listaErrores.size(); i++) {
            Element worker = document.createElement("Contribuyente");
            worker.setAttribute("id", listaErrores.get(i).getIdContribuyente() + "");
            if (nameFile.equals("ErroresNifNie")) {

                Element dninie = document.createElement("NIF_NIE");
                if (listaErrores.get(i).getNifnie() != null) {
                    Text textDni = document.createTextNode(listaErrores.get(i).getNifnie().toString());
                    worker.appendChild(dninie);
                    dninie.appendChild(textDni);
                }
                Element name = document.createElement("Nombre");
                Text textName = document.createTextNode(listaErrores.get(i).getNombre().toString());
                worker.appendChild(name);
                name.appendChild(textName);

                Element ap1 = document.createElement("PrimerApellido");
                Text textAp1 = document.createTextNode(listaErrores.get(i).getApellido1().toString());
                worker.appendChild(ap1);
                ap1.appendChild(textAp1);
                if (listaErrores.get(i).getApellido2() != null) {
                    Element ap2 = document.createElement("SegundoApellido");
                    Text textAp2 = document.createTextNode(listaErrores.get(i).getApellido2().toString());
                    worker.appendChild(ap2);
                    ap2.appendChild(textAp2);
                }
            } else {
                //Crear arraylist para almacenar los CCC erroneos
                Element name = document.createElement("Nombre");
                Text textName = document.createTextNode(listaErrores.get(i).getNombre());
                worker.appendChild(name);
                name.appendChild(textName);

                Element ap1 = document.createElement("Apellido1");
                String apellido1 = listaErrores.get(i).getApellido1();
                Text textAp1 = document.createTextNode(apellido1);
                worker.appendChild(ap1);
                ap1.appendChild(textAp1);

                Element ap2 = document.createElement("Apellido2");
                if (listaErrores.get(i).getApellido2() != null) {
                    String apellido2 = listaErrores.get(i).getApellido2();
                    ap2.appendChild(document.createTextNode(apellido2));
                }
                worker.appendChild(ap2);

                Element cccWrong = document.createElement("CCCErroneo");
                Text textCCC = document.createTextNode(listaCCCErroneos.get(i));
                worker.appendChild(cccWrong);
                cccWrong.appendChild(textCCC);

                Element iban = document.createElement("IBANCorrecto");
                Text textIban = document.createTextNode(listaErrores.get(i).getIban());
                worker.appendChild(iban);
                iban.appendChild(textIban);
            }

            root.appendChild(worker);
        }
        Source source = new DOMSource(document);
        Result result = new StreamResult(new java.io.File("./resources/" + nameFile + ".xml"));
        Transformer trans = TransformerFactory.newInstance().newTransformer();
        trans.transform(source, result);
    }

    private void generateRecibosXML(int trimestre, int anho) throws Exception {
        if (listaContribuyentes.size() == 0) {
            return;
        }
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        DOMImplementation implementacion = builder.getDOMImplementation();
        Document document = implementacion.createDocument(null, "Recibos", null);
        document.setXmlVersion("1.0");
        Element root = document.getDocumentElement();
        for (Recibos recibo : listaRecibos) {
            if (recibo.getContribuyente().getNifnie().length() == 9) {
                Element re = document.createElement("Recibo");
                re.setAttribute("idRecibo", recibo.getNumeroRecibo() + "");
                root.appendChild(re);

                Element exception = document.createElement("Exención");
                Text valueExencion = document.createTextNode(recibo.getExencion() + "");
                exception.appendChild(valueExencion);
                re.appendChild(exception);

                Element fila = document.createElement("idFilaExcel");
                Text valueFila = document.createTextNode(recibo.getContribuyente().getIdContribuyente() + "");
                fila.appendChild(valueFila);
                re.appendChild(fila);

                Element nombre = document.createElement("nombre");
                Text valueNombre = document.createTextNode(recibo.getContribuyente().getNombre() + "");
                nombre.appendChild(valueNombre);
                re.appendChild(nombre);

                Element apellido1 = document.createElement("pimerApellido");
                Text valueApellido1 = document.createTextNode(recibo.getContribuyente().getApellido1() + "");
                apellido1.appendChild(valueApellido1);
                re.appendChild(apellido1);

                if (recibo.getContribuyente().getApellido2() != null) {
                    Element apellido2 = document.createElement("segundoApellido");
                    Text valueApellido2 = document.createTextNode(recibo.getContribuyente().getApellido2() + "");
                    apellido2.appendChild(valueApellido2);
                    re.appendChild(apellido2);
                }

                Element nifnie = document.createElement("NIF");
                Text valueNifnie = document.createTextNode(recibo.getContribuyente().getNifnie() + "");
                nifnie.appendChild(valueNifnie);
                re.appendChild(nifnie);

                Element iban = document.createElement("IBAN");
                Text valueIban = document.createTextNode(recibo.getContribuyente().getIban() + "");
                iban.appendChild(valueIban);
                re.appendChild(iban);

                Element lecturaActual = document.createElement("lecturaActual");
                Text valueLecActual = document.createTextNode(recibo.getLecturaActual() + "");
                lecturaActual.appendChild(valueLecActual);
                re.appendChild(lecturaActual);

                Element lecturaAnterior = document.createElement("lecturaAnterior");
                Text valueLecAnterior = document.createTextNode(recibo.getLecturaAnterior() + "");
                lecturaAnterior.appendChild(valueLecAnterior);
                re.appendChild(lecturaAnterior);

                Element consumo = document.createElement("consumo");
                Text valueConsumo = document.createTextNode(recibo.getConsumom3() + "");
                consumo.appendChild(valueConsumo);
                re.appendChild(consumo);

                Element baseImponible = document.createElement("baseImponibleRecibo");
                Text valueBaseImponible = document.createTextNode(decimalFormat.format(recibo.getTotalBaseImponible()) + "");
                baseImponible.appendChild(valueBaseImponible);
                re.appendChild(baseImponible);

                Element iva = document.createElement("ivaRecibo");
                Text valueIva = document.createTextNode(decimalFormat.format(recibo.getTotalIva()) + "");
                iva.appendChild(valueIva);
                re.appendChild(iva);

                Element reciboTotal = document.createElement("totalRecibo");
                Text valueReciboTot = document.createTextNode(decimalFormat.format(recibo.getTotalRecibo()) + "");
                reciboTotal.appendChild(valueReciboTot);
                re.appendChild(reciboTotal);

                root.setAttribute("fechaPadron", trimestre + "T de " + anho);
                root.appendChild(re);
            }
        }

        Source source = new DOMSource(document);
        Result result = new StreamResult(new java.io.File("./resources/" + "recibos" + ".xml"));
        Transformer trans = TransformerFactory.newInstance().newTransformer();
        trans.transform(source, result);
    }

    /*
     Inicializa cada una de las ordenanzas y las añade a la variable listaOrdenanzas
     */
    public void leerOrdenanza(XSSFWorkbook wb) throws Exception {
        try {
            XSSFSheet hoja = wb.getSheetAt(1);
            Iterator<Row> rowIt = hoja.iterator();

            if (rowIt.hasNext()) {
                rowIt.next();
            }

            //Recorremos con el  iterador
            while (rowIt.hasNext()) {
                Row row = rowIt.next();

                if (row.getCell(0) != null) {
                    Ordenanza ord = new Ordenanza();

                    ord.setPueblo(getCellValue(row, 0));
                    ord.setTipoCalculo(getCellValue(row, 1));
                    ord.setIdOrdenanza((int) row.getCell(2).getNumericCellValue());
                    ord.setConcepto(getCellValue(row, 3));
                    ord.setSubconcepto(getCellValue(row, 4));
                    ord.setDescripcion(getCellValue(row, 5));
                    ord.setAcumulable(getCellValue(row, 6));
                    if (row.getCell(7) != null) {
                        ord.setPrecioFijo((int) row.getCell(7).getNumericCellValue());

                    }
                    if (row.getCell(8) != null) {
                        ord.setM3incluidos((int) row.getCell(8).getNumericCellValue());

                    }

                    String valuePrecio = getCellValue(row, 9);
                    if (valuePrecio != null) {
                        ord.setPreciom3(Double.parseDouble(valuePrecio));
                    }
                    String valueP = getCellValue(row, 10);
                    if (valueP != null) {
                        ord.setPorcentaje(Double.parseDouble(valueP));
                    }
                    if (row.getCell(11) != null) {
                        ord.setConceptoRelacionado((int) row.getCell(11).getNumericCellValue());
                    } else {
                        ord.setConceptoRelacionado(0);
                    }
                    ord.setIva(Double.parseDouble(getCellValue(row, 12)));

                    listaOrdenanzas.add(ord);
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error al leer las ordenanzas");
        }

    }

    private void calculaBaseImponible(Contribuyente contribuyente, String conceptos, Lecturas lectura) {
        // Corta los conceptos por espacios
        String[] conceptosArray = conceptos.split(" ");
        Recibos recibo = new Recibos();
        LinkedList<Lineasrecibo> lineasRecibo = new LinkedList<>();
        this.listaLineasrecibo.add(lineasRecibo);
        // Recorre cada valor de los conceptos
        ArrayList<Integer> array = new ArrayList<>();

        for (String conceptoString : conceptosArray) {
            array.add(Integer.parseInt(conceptoString));
        }
        Collections.sort(array);
        for (int i = 0; i < array.size(); i++) {
            // Pasa a int el concept
            int conceptoInt = array.get(i);

            LinkedList<Ordenanza> ordLocal = new LinkedList();

            for (Ordenanza ord : listaOrdenanzas) {
                if (ord.getIdOrdenanza() == conceptoInt) {
                    ordLocal.add(ord);
                }
            }

            if (ordLocal.size() > 1) {
                methodAgua(contribuyente, ordLocal, recibo, lectura, lineasRecibo);
            } else {
                if (ordLocal.get(0).getConcepto().equals("Agua")) {
                    RelContribuyenteOrdenanza r = new RelContribuyenteOrdenanza();
                    r.setContribuyente(contribuyente);
                    r.setOrdenanza(ordLocal.get(0));
                    relaciones.add(r);
                    double importe = ordLocal.get(0).getPrecioFijo();
                    int consumo = lectura.getLecturaActual() - lectura.getLecturaAnterior();
                    if (ordLocal.get(0).getAcumulable().charAt(0) != 'S') {
                        consumo -= ordLocal.get(0).getM3incluidos();
                    }
                    consumo = Math.max(consumo, 0);
                    importe += ordLocal.get(0).getPreciom3() * consumo;
                    generaLineaRecibo(importe, ordLocal.get(0), contribuyente, lineasRecibo, recibo, false);
                } else if (ordLocal.get(0).getConceptoRelacionado() == 0) {
                    //Tiene precio fijo 
                    RelContribuyenteOrdenanza r = new RelContribuyenteOrdenanza();
                    r.setContribuyente(contribuyente);
                    r.setOrdenanza(ordLocal.get(0));
                    relaciones.add(r);
                    generaLineaRecibo(ordLocal.get(0).getPrecioFijo(), ordLocal.get(0), contribuyente, lineasRecibo, recibo, false);
                } else {
                    String conceptoSS = "";
                    for (Ordenanza o : listaOrdenanzas) {
                        if (o.getIdOrdenanza() == ordLocal.get(0).getConceptoRelacionado()) {
                            conceptoSS = o.getConcepto();
                            break;
                        }
                    }
                    boolean calculado = false;
                    for (Lineasrecibo l : lineasRecibo) {
                        if (l.getConcepto().equals(conceptoSS)) {
                            calculado = true;
                            break;
                        }
                    }
                    if (!calculado) {
                        array.add(ordLocal.get(0).getIdOrdenanza());
                    } else {
                        RelContribuyenteOrdenanza r = new RelContribuyenteOrdenanza();
                        r.setContribuyente(contribuyente);
                        r.setOrdenanza(ordLocal.get(0));
                        relaciones.add(r);
                        for (String conceptoB : conceptosArray) {

                            if (conceptoB.equals(ordLocal.get(0).getConceptoRelacionado() + "")) {
                                double importe = 0.0;
                                String concepto = getConcepto(ordLocal.get(0).getConceptoRelacionado());
                                //importe = getValueImporteFijo(ordLocal.get(0).getConceptoRelacionado());
                                for (Lineasrecibo lineaR : lineasRecibo) {
                                    if (lineaR.getConcepto().equals(concepto)) {
                                        importe += lineaR.getBaseImponible();
                                    }
                                }
                                importe = importe * ordLocal.get(0).getPorcentaje() / 100.0;
                                generaLineaRecibo(importe, ordLocal.get(0), contribuyente, lineasRecibo, recibo, true);
                            }
                        }
                    }
                }

            }
            // Crear línea del Recibo con el precio impuesto
        }
        //Se rellenan los datos del recibo
        recibo.setNifContribuyente(contribuyente.getNifnie());
        recibo.setDireccionCompleta(contribuyente.getDireccion());
        recibo.setNombre(contribuyente.getNombre());
        recibo.setApellidos(contribuyente.getApellido1() + " " + contribuyente.getApellido2());
        recibo.setFechaRecibo(new Date());
        recibo.setLecturaAnterior(lectura.getLecturaAnterior());
        recibo.setLecturaActual(lectura.getLecturaActual());
        recibo.setConsumom3(lectura.getLecturaActual() - lectura.getLecturaAnterior());
        recibo.setFechaPadron(contribuyente.getFechaAlta());

        double totalBaseImponible = 0;
        double totalIVA = 0;
        for (Lineasrecibo lr : lineasRecibo) {

            totalBaseImponible += lr.getBaseImponible();
            totalIVA += lr.getImporteIva();
        }
        if (contribuyente.getExencion() != 'S') {
            recibo.setTotalBaseImponible(totalBaseImponible);
            recibo.setTotalIva(totalIVA);
            recibo.setTotalRecibo(totalBaseImponible + totalIVA);
        }
        recibo.setIban(contribuyente.getIban());
        recibo.setEmail(contribuyente.getEemail());
        recibo.setExencion(contribuyente.getExencion().toString());
        recibo.setContribuyente(contribuyente);
        listaRecibos.add(recibo);
    }

    private void methodAgua(Contribuyente contribuyente, LinkedList<Ordenanza> ordenanzas, Recibos recibo, Lecturas lectura, List<Lineasrecibo> lineasR) {
        String acumulable;
        int lecturaOficial = lectura.getLecturaActual() - lectura.getLecturaAnterior();
        int metrosMax = 0;
        int m3Incluidos = 0;
        int importe = 0;
        boolean lecturaConsumida = false;
        for (Ordenanza ord : ordenanzas) {
            RelContribuyenteOrdenanza r = new RelContribuyenteOrdenanza();
            r.setContribuyente(contribuyente);
            r.setOrdenanza(ord);
            relaciones.add(r);
            //lectura = lectura
            acumulable = ord.getAcumulable();
            //Creamos lineaRecibo
            Lineasrecibo l = new Lineasrecibo();
            lineasR.add(l);
            //ACUMULABLE
            if (acumulable.equals("S")) {
                metrosMax += ord.getM3incluidos();
                double precioFinal = 0;//.getPrecioFijo();
                double importeIVA = 0;// precioFinal*ord.getIva()/100.0;
                double importeBonificacion = 0;
                if (ord.getSubconcepto().equals("Fijo")) {

                    if (metrosMax >= lecturaOficial) {
                        l.setM3incluidos(lecturaOficial);

                        lecturaConsumida = true;
                    } else {
                        l.setM3incluidos(0);

                    }
                    precioFinal = ord.getPrecioFijo();

                    importeBonificacion = precioFinal * contribuyente.getBonificacion() / 100.0;
                    precioFinal -= importeBonificacion;
                    importeIVA = precioFinal * ord.getIva() / 100.0;

                } else {
                    if (metrosMax >= lecturaOficial && !lecturaConsumida) {
                        l.setM3incluidos(lecturaOficial);
                        precioFinal = lecturaOficial * ord.getPreciom3();
                        importeBonificacion = precioFinal * contribuyente.getBonificacion() / 100.0;
                        precioFinal -= importeBonificacion;
                        importeIVA = precioFinal * ord.getIva() / 100.0;

                        lecturaConsumida = true;
                    } else {
                        l.setM3incluidos(0);

                    }
                }
                l.setBaseImponible(precioFinal);
                l.setImporteBonificacion(importeBonificacion);
                l.setImporteIva(importeIVA);

                //NO ACUMULABLE
            } else {
                int consumoLinea = Math.min(lecturaOficial, ord.getM3incluidos());
                lecturaOficial -= consumoLinea;
                double precioFinal = 0;//.getPrecioFijo();
                double importeIVA = 0;// precioFinal*ord.getIva()/100.0;
                double importeBonificacion = 0;
                lecturaOficial = Math.max(0, lecturaOficial);
                if (ord.getSubconcepto().equals("Fijo")) {

                    l.setM3incluidos(consumoLinea);

                    precioFinal = ord.getPrecioFijo();
                    importeBonificacion = precioFinal * contribuyente.getBonificacion() / 100.0;
                    precioFinal -= importeBonificacion;
                    importeIVA = precioFinal * ord.getIva() / 100.0;

                } else {
                    l.setM3incluidos(consumoLinea);
                    precioFinal = consumoLinea * ord.getPreciom3();

                    importeBonificacion = precioFinal * contribuyente.getBonificacion() / 100.0;
                    precioFinal -= importeBonificacion;
                    importeIVA = precioFinal * ord.getIva() / 100.0;
                }
                l.setBaseImponible(precioFinal);
                l.setImporteBonificacion(importeBonificacion);
                l.setImporteIva(importeIVA);
            }
            l.setPorcentajeIva(ord.getIva());
            l.setConcepto(ord.getConcepto());
            l.setSubconcepto(ord.getSubconcepto());
            l.setRecibos(recibo);
        }
    }

    /*
     Devuelve el valor de la celda en caso de que está no este vacía.
        
     */
    public String getCellValue(Row row, int i) {
        if (row.getCell(i) != null) {
            return row.getCell(i).toString();
        }

        return null;
    }

    private void generaLineaRecibo(double precioFijo, Ordenanza ordenanza, Contribuyente contribuyente, List<Lineasrecibo> lineasRecibo, Recibos recibo, boolean bonificado) {
        Lineasrecibo linea = new Lineasrecibo();
        //Faltan setters

        double bonificacion = precioFijo * contribuyente.getBonificacion() / 100.0;
        if (!bonificado) {
            precioFijo -= bonificacion;
        }
        double iva = precioFijo * ordenanza.getIva() / 100.0;
        linea.setBaseImponible(precioFijo);
        linea.setBonificacion(contribuyente.getBonificacion());
        linea.setImporteBonificacion(bonificacion);
        linea.setImporteIva(iva);

        //Faltan setters
        linea.setConcepto(ordenanza.getConcepto());
        linea.setPorcentajeIva(ordenanza.getIva());
        if (ordenanza.getM3incluidos() != null) {
            linea.setM3incluidos(ordenanza.getM3incluidos());
        }

        linea.setRecibos(recibo);
        linea.setSubconcepto(ordenanza.getSubconcepto());

        lineasRecibo.add(linea);
    }

    private String getConcepto(Integer conceptoRelacionado) {
        for (Ordenanza ordenanza : listaOrdenanzas) {
            if (conceptoRelacionado == ordenanza.getIdOrdenanza()) {
                return ordenanza.getConcepto();
            }
        }
        return null;
    }

    private double getValueImporteFijo(Integer conceptoRelacionado) {
        try {
            for (Ordenanza ordenanza : listaOrdenanzas) {
                if (conceptoRelacionado == ordenanza.getIdOrdenanza() && ordenanza.getSubconcepto().equals("Fijo") && ordenanza.getPrecioFijo() != null) {
                    return ordenanza.getPrecioFijo();
                }
            }
            return 0.0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
