/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pojos;

import java.util.LinkedList;
import java.util.List;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import pojos.Contribuyente;

/**
 *
 * @author Usuario
 */
public class ContribuyenteDAO {
    private SessionFactory sessionF;
    private Session session;
    private Transaction transaction;
    
    public ContribuyenteDAO(){
        this.sessionF = HibernateUtil.getSessionFactory();
        this.session = sessionF.openSession();
    }
    
    public void add(Contribuyente contribuyente){
        this.transaction = this.session.beginTransaction();
        this.session.save(contribuyente);
        this.transaction.commit();
    }
    
    public Contribuyente get(String nifnie){
        Contribuyente contribuyente = null;
        String consulta = "FROM Contribuyente c WHERE c.nifnie = ?";
        
        Query query = this.session.createQuery(consulta);
        
        query.setParameter(0,nifnie);//Esto indica que la primera interrogación (posicion 0) tendra el valor de la variable nifnie
        List result = query.list();
        return (Contribuyente) result.get(0);
    }
    
    public List<Contribuyente> getAll(){
        String consulta = "FROM Contribuyente c";
        
        Query query = this.session.createQuery(consulta);
        List result =  query.list();
        LinkedList <Contribuyente> contribuyentes = new LinkedList<>();
        for(int i = 0; i < result.size(); i++){
            contribuyentes.add((Contribuyente) result.get(i));
        }
        return contribuyentes;
    }
    
    public void delete(Contribuyente contribuyente){
        
    }
    
    public void close(){
        session.close();
    }
}
/*
    Consulta donde se solicitan los recibos cuyo contribuyente tenga el dni pasado en setParameter();

    String consulta = "FROM lineasrecibo l WHERE l.recibos=(FROM Recibos r WHERE r.nifContribuyente=?);
    Query q = new Query(consulta);
    q.setParameter(0, NIF);

    List lineas = querylist();
    ArrayList<Lineasrecibo> lineasRecibo = new ArrayList<>();
    for(int i = 0; i < lineas.size(); i++){
        Lineasrecibo.add((Lineasrecibo) lineas.get(i));
}
*/
