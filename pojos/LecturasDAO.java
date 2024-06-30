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
public class LecturasDAO {
    private SessionFactory sessionF;
    private Session session;
    private Transaction transaction;
    
    public LecturasDAO(){
        this.sessionF = HibernateUtil.getSessionFactory();
        this.session = sessionF.openSession();
    }
    
    public void add(Lecturas lectura){
        this.transaction = this.session.beginTransaction();
        this.session.saveOrUpdate(lectura);
        this.transaction.commit();
    }
    
    public Lecturas get(int idLecturas){
        Lecturas lectura = null;
        String consulta = "FROM Lecturas l WHERE l.Id = ?";
        
        Query query = this.session.createQuery(consulta);
        query.setParameter(0,idLecturas);
        List result =  query.list();
        return (Lecturas) result.get(0);
    }
    
    public LinkedList<Lecturas> getAll(){
        String consulta = "FROM Lecturas l";
        
        Query query = this.session.createQuery(consulta);
        List result =  query.list();
        LinkedList <Lecturas> lecturas = new LinkedList<>();
        for(int i = 0; i < result.size(); i++){
            lecturas.add((Lecturas) result.get(i));
        }
        return lecturas;
    }
    
    public void delete(Lecturas lectura){
        
    }
}
