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
public class RelContribuyenteOrdenanzaDAO {
    private SessionFactory sessionF;
    private Session session;
    private Transaction transaction;
    
    public RelContribuyenteOrdenanzaDAO(){
        this.sessionF = HibernateUtil.getSessionFactory();
        this.session = sessionF.openSession();
    }
    
    public void add(RelContribuyenteOrdenanza contOrd){
        this.transaction = this.session.beginTransaction();
        this.session.saveOrUpdate(contOrd);
        this.transaction.commit();
    }
    
    public RelContribuyenteOrdenanza get(int id){
        RelContribuyenteOrdenanza contOrd = null;
        String consulta = "FROM RelContribuyenteOrdenanza r WHERE r.id = ?";
        
        Query query = this.session.createQuery(consulta);
        query.setParameter(0,id);
        List result =  query.list();
        return (RelContribuyenteOrdenanza) result.get(0);
    }
    
    public LinkedList<RelContribuyenteOrdenanza> getAll(){
        String consulta = "FROM RelContribuyenteOrdenanza r";
        
        Query query = this.session.createQuery(consulta);
        List result =  query.list();
        LinkedList <RelContribuyenteOrdenanza> contOrds = new LinkedList<>();
        for(int i = 0; i < result.size(); i++){
            contOrds.add((RelContribuyenteOrdenanza) result.get(i));
        }
        return contOrds;
    }
    
    public void delete(RelContribuyenteOrdenanza contOrd){
        
    }
    
    public void close(){
        session.close();
    }
}
