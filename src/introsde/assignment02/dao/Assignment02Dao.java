package introsde.assignment02.dao;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public enum Assignment02Dao {
  instance;
  private EntityManagerFactory emf;

  private Assignment02Dao() {
    if (emf!=null) {
      emf.close();
    }
    
    try {
      emf = Persistence.createEntityManagerFactory("introsde-jpa");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public EntityManager createEntityManager() {
    try {
      return emf.createEntityManager();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;    
  }

  public void closeConnections(EntityManager em) {
    em.close();
  }

  public EntityTransaction getTransaction(EntityManager em) {
    return em.getTransaction();
  }

  public EntityManagerFactory getEntityManagerFactory() {
    return emf;
  }  
}