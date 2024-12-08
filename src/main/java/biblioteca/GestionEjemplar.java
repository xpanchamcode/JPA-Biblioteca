package biblioteca;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GestionEjemplar {
    public static List<Ejemplar> listaEjemplares = new ArrayList<Ejemplar>();

    public GestionEjemplar() throws SQLException {
        listaEjemplares = Menu.DAOEjemplar.readAll();
    }

    public static List<Ejemplar> getListaEjemplares() {
        return listaEjemplares;
    }

    public static void setListaEjemplares(List<Ejemplar> listaNueva) {
        listaEjemplares = listaNueva;
    }

    public static boolean ejemplarExists(int id) {
        boolean existe = false;
        for (Ejemplar ejemplar: listaEjemplares) {
            if(ejemplar.getId() == id) {
                existe = true;
            }
        }
        return existe;
    }

    public static Ejemplar getEjemplar(int id) {
        Ejemplar ejemplarDevuelto = null;
        for (Ejemplar ejemplar: listaEjemplares) {
            if(ejemplar.getId() == id) {
                ejemplarDevuelto = ejemplar;
            }
        }
        return ejemplarDevuelto;
    }


}
