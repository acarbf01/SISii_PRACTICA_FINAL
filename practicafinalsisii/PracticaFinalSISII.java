/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package practicafinalsisii;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.regex.*;
import org.hibernate.*;
import pojos.*;
import practicafinalsisii.ExcelManager;

/**
 *
 * @author Usuario
 */
public class PracticaFinalSISII {

    /**
     * @param args the command line arguments
     */
    private SessionFactory sessionFactory;
    private Session session;

    public PracticaFinalSISII() throws Exception {
        this.sessionFactory = HibernateUtil.getSessionFactory();
    }


    /*
    public Contribuyente contribuyente(String dni) throws Exception {
        try {
            this.session = sessionFactory.openSession();
            String consulta = "SELECT contribuyente FROM Contribuyente contribuyente WHERE contribuyente.nifnie=:param";
            Query query = this.session.createQuery(consulta);
            query.setParameter("param", dni);
            List<Contribuyente> list = query.list();
            System.out.println("");
            return list.get(0);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("No hay contribuyente con este DNI");
        }
    }

    public void updateRecibos(Contribuyente cont) throws Exception {
        try {
            this.session = this.sessionFactory.openSession();
            this.transaction = this.session.beginTransaction();
            String consulta = "UPDATE Recibos recibo SET recibo.totalRecibo=250 WHERE recibo.nifContribuyente =:param2";//Se pueden modificar valores estaticos en una consulta, pero para variables no
            Query query = this.session.createQuery(consulta);
            query.setParameter("param2", cont.getNifnie());
            query.executeUpdate();
            this.transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public double eliminateBaseImp() {
        try {
            Session s = this.sessionFactory.openSession();
            String consulta = "SELECT avg(recibo.totalBaseImponible) FROM Recibos recibo";
            Query queryS = this.session.createQuery(consulta);
            double value = (double) queryS.list().get(0);
            //Borro linea recibo
             this.session = sessionFactory.openSession();
            consulta = "SELECT recibo FROM Recibos recibo ";
            Query query = this.session.createQuery(consulta);
          
            List<Recibos> list = query.list();
            for(Recibos r: list){
                if(r.getTotalBaseImponible()<value){
                    Transaction tx = s.beginTransaction();
                    consulta = "DELETE Lineasrecibo l WHERE l.recibos.numeroRecibo =:param";
                    s.createQuery(consulta).setParameter("param", r.getNumeroRecibo()).executeUpdate();
                    tx.commit();
                }
            }
        
            //Borro recibo
            Transaction tx2 = s.beginTransaction();
            consulta = "DELETE Recibos recibo WHERE recibo.totalBaseImponible <:param";
            s.createQuery(consulta).setParameter("param", value).executeUpdate();
            tx2.commit();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
     */
    public static void main(String[] args) {
        // TODO code application logic here
        try {
            /*Scanner scan = new Scanner(System.in);
            PracticaFinalSISII p = new PracticaFinalSISII();
            String DNI = scan.nextLine();
            Contribuyente c = p.contribuyente(DNI);
            System.out.println("Nombre:" + c.getNombre() + " Apellidos:" + c.getApellido1() + " " + c.getApellido2() + " NIF/NIE:" + c.getNifnie() + " Direccion:" + c.getDireccion()
                    + " Numero:" + c.getNumero() + " Pais Ccc:" + c.getPaisCcc() + " Iban:" + c.getIban() + " Email:" + c.getEemail() + " Exencion:" + c.getExencion()
                    + " Bonificacion:" + c.getBonificacion() + " Fecha Alta:" + c.getFechaAlta() + " Fecha Baja:" + c.getFechaBaja() 
                    + " Ordenanzas:" + c.getRelContribuyenteOrdenanzas() + " Lecturas:" + c.getLecturases() + " Recibos:" + c.getReciboses());
            p.updateRecibos(c);
            p.eliminateBaseImp();*/
            Scanner scan = new Scanner(System.in);
            String line = scan.nextLine();
            int trimestre = Integer.parseInt(line.substring(0,1));
            int anho = Integer.parseInt(line.substring(line.indexOf(" ")+1));
            ExcelManager ex = new ExcelManager("./resources/SistemasAgua.xlsx");
            ex.getInfoContribuyentes(trimestre, anho);
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }

    }

}
