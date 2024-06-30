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

/**
 *
 * @author Usuario
 */
public class OrdenanzaDAO {
    private SessionFactory sessionF;
    private Session session;
    private Transaction transaction;
    
    public OrdenanzaDAO(){
        this.sessionF = HibernateUtil.getSessionFactory();
        this.session = sessionF.openSession();
    }
    
    public void add(Ordenanza ordenanza){
        this.transaction = this.session.beginTransaction();
        this.session.saveOrUpdate(ordenanza);
        this.transaction.commit();
    }
    
    public Ordenanza get(int idOrdenanza){
        Ordenanza ordenanza = null;
        String consulta = "FROM Ordenanza o WHERE o.idOrdenanza = ?";
        
        Query query = this.session.createQuery(consulta);
        query.setParameter(0, idOrdenanza);
        List result =  query.list();
        return (Ordenanza) result.get(0);
    }
    
    public LinkedList<Ordenanza> getAll(){
        String consulta = "FROM Ordenanza o";
        
        Query query = this.session.createQuery(consulta);
        List result =  query.list();
        LinkedList <Ordenanza> ordenanzas = new LinkedList<>();
        for(int i = 0; i < result.size(); i++){
            ordenanzas.add((Ordenanza) result.get(i));
        }
        return ordenanzas;
    }
    
    public void delete(Ordenanza ordenanza){
        
    }
}
