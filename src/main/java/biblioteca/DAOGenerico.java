package biblioteca;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

import java.util.ArrayList;

public class DAOGenerico<T> {
    EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("miUnidadPersistencia");
    EntityManager entityManager = entityManagerFactory.createEntityManager();
    EntityTransaction transaction = entityManager.getTransaction();
    private Class<T> tipoClase;
    private String nombreTabla;

    public DAOGenerico(Class<T> tipoClase, String nombreTabla) {
        this.tipoClase = tipoClase;
        this.nombreTabla = nombreTabla;
    }

    public T readObjeto (Integer id){
        return entityManager.find(tipoClase, id);
    }

    public T readObjeto (String idS){
        return entityManager.find(tipoClase, idS);
    }

    public ArrayList<T> readAll (){
        return (ArrayList<T>) entityManager.createQuery("select t from "+nombreTabla+" t").getResultList();
    }

    public boolean addObjeto (T objeto){
        transaction.begin();
        try{
            entityManager.persist(objeto);
            transaction.commit();
            return true;
        } catch (Exception e) {
            System.out.println("Error al agregar el objeto: "+e.getMessage());
            transaction.rollback();
            return false;
        }
    }

    public boolean removeObjeto (T objeto){
        transaction.begin();
        try{
            entityManager.remove(objeto);
            transaction.commit();
            return true;
        }
        catch (Exception e) {
            System.out.println("Error al eliminar el objeto: "+e.getMessage());
            transaction.rollback();
            return false;
        }
    }

    public boolean updateObjeto (T objeto){
        transaction.begin();
        try {
            entityManager.merge(objeto);
            transaction.commit();
            return true;
        }catch (Exception e) {
            System.out.println("Error al actualizar el objeto: "+e.getMessage());
            transaction.rollback();
            return false;
        }
    }
}
