package biblioteca;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class GestionLibro {
    public static List<Libro> listaLibros = new ArrayList<Libro>();

    public GestionLibro() throws SQLException {
        listaLibros = Menu.DAOLibro.readAll();
    }

    public static List<Libro> getListaLibros() {
        return listaLibros;
    }

    public static void setListaLibros(List<Libro> listaNueva) {
        listaLibros = listaNueva;
    }

    public static boolean libroExists(String isbn) {
        boolean existe = false;
        for (Libro libro: listaLibros) {
            if(libro.getIsbn().equals(isbn)) {
                existe = true;
            }
        }
        return existe;
    }

    public static Libro getLibro(String isbn) {
        Libro libroDevuelto = null;
        for (Libro libro: listaLibros) {
            if(libro.getIsbn().equals(isbn)) {
                libroDevuelto = libro;
            }
        }
        return libroDevuelto;
    }

    // Comprobación de que existe al menos un ejemplar disponible
    public static boolean ejemplarDisponible(String isbn){
        boolean ejemplarDisponible = false;
        Libro libro = GestionLibro.getLibro(isbn);
        for (Ejemplar ejemplar : libro.getEjemplares()) {
            if(ejemplar.getEstado().equals("Disponible")) {
                ejemplarDisponible = true;
            }
        }
        return ejemplarDisponible;
    }

    public static List<Ejemplar> getEjemplaresDisponibles(String isbn){
        List<Ejemplar> ejemplaresDisponibles = new ArrayList<>();
        Libro libroOriginal = GestionLibro.getLibro(isbn);
        for (Ejemplar ejemplar : libroOriginal.getEjemplares()) {
            if(ejemplar.getEstado().equals("Disponible")) {
                ejemplaresDisponibles.add(ejemplar);
            }
        }
        return ejemplaresDisponibles;
    }
}
