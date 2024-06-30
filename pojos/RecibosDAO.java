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
public class RecibosDAO {
    private SessionFactory sessionF;
    private Session session;
    private Transaction transaction;
    
    public RecibosDAO(){
        this.sessionF = HibernateUtil.getSessionFactory();
        this.session = sessionF.openSession();
    }
    
    public void add(Recibos recibo){
        this.transaction = this.session.beginTransaction();
        this.session.saveOrUpdate(recibo);
        this.transaction.commit();
    }
    
    public Recibos get(String nifnie){
        Recibos recibo = null;
        String consulta = "FROM Recibos r WHERE r.numeroRecibo = ?";
        
        Query query = this.session.createQuery(consulta);
        query.setParameter(0,nifnie);
        List result = query.list();
        return (Recibos) result.get(0);
    }
    
    public LinkedList<Recibos> getAll(){
        String consulta = "FROM Recibos r";
        
        Query query = this.session.createQuery(consulta);
        List result =  query.list();
        LinkedList <Recibos> recibos = new LinkedList<>();
        for(int i = 0; i < result.size(); i++){
            recibos.add((Recibos) result.get(i));
        }
        return recibos;
    }
    
    public void delete(Recibos recibo){
        
    }
}
