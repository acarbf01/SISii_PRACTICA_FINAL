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
public class LineasreciboDAO {
    private SessionFactory sessionF;
    private Session session;
    private Transaction transaction;

    public LineasreciboDAO(){
        this.sessionF = HibernateUtil.getSessionFactory();
        this.session = sessionF.openSession();
    }    
    
    public void add(Lineasrecibo lineasR){
        this.transaction = this.session.beginTransaction();
        this.session.saveOrUpdate(lineasR);
        this.transaction.commit();
    }
    
    public Lineasrecibo get(int id){
        Contribuyente contribuyente = null;
        String consulta = "FROM Lineasrecibo l WHERE l.id = ?";
        
        Query query = this.session.createQuery(consulta);
        query.setParameter(0,id);
        List result =  query.list();
        return (Lineasrecibo) result.get(0);
    }
    
    public LinkedList<Lineasrecibo> getAll(){
        String consulta = "FROM Lineasrecibo l";
        
        Query query = this.session.createQuery(consulta);
        List result =  query.list();
        LinkedList <Lineasrecibo> lineasrecibos = new LinkedList<>();
        for(int i = 0; i < result.size(); i++){
            lineasrecibos.add((Lineasrecibo) result.get(i));
        }
        return lineasrecibos;
    }
    
    public void delete(Lineasrecibo lineasrecibo){
        
    }
}
