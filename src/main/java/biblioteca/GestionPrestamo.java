package biblioteca;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GestionPrestamo {
    public static List<Prestamo> listaPrestamos = new ArrayList<Prestamo>();

    public GestionPrestamo() throws SQLException {
        listaPrestamos = Menu.DAOPrestamo.readAll();
    }

    public static List<Prestamo> getListaPrestamos() {
        return listaPrestamos;
    }

    public static void setListaPrestamos(List<Prestamo> listaNueva) {
        listaPrestamos = listaNueva;
    }

    public static boolean prestamoExists(int idPrestamo) {
        boolean existe = false;
        for (Prestamo prestamo : listaPrestamos) {
            if (prestamo.getId().equals(idPrestamo)) {
                existe = true;
            }
        }
        return existe;
    }

    public static boolean prestamoExists(int idEjemplar, Usuario usuario) {
        boolean existe = false;
        Ejemplar ejemplar = GestionEjemplar.getEjemplar(idEjemplar);
        for (Prestamo prestamo : listaPrestamos) {
            if (prestamo.getEjemplar().equals(ejemplar) && prestamo.getUsuario().equals(usuario)){
                existe = true;
            }
        }
        return existe;
    }

    public static Prestamo getPrestamo(int idPrestamo) {
        Prestamo prestamoDevuelto = null;
        for (Prestamo prestamo : listaPrestamos) {
            if (prestamo.getId().equals(idPrestamo)) {
                prestamoDevuelto = prestamo;
            }
        }
        return prestamoDevuelto;
    }

    public static Prestamo getPrestamo(int idEjemplar, Usuario usuario) {
        Prestamo prestamoDevuelto = null;
        Ejemplar ejemplarAdevolver = GestionEjemplar.getEjemplar(idEjemplar);
        for (Prestamo prestamo : listaPrestamos) {
            if (prestamo.getEjemplar().equals(ejemplarAdevolver) && prestamo.getUsuario().equals(usuario)){
                prestamoDevuelto = prestamo;
            }
        }
        return prestamoDevuelto;
    }

    // Obtener préstamos por usuario
    public static List<Prestamo> getPrestamosUsuario(String dni) {
        List<Prestamo> prestamosUsuario = new ArrayList<>();
        Usuario usuario = GestionUsuario.getUsuario(dni);
        for (Prestamo prestamo : listaPrestamos) {
            if (prestamo.getUsuario().equals(usuario)) {
                prestamosUsuario.add(prestamo);
            }
        }
        return prestamosUsuario;
    }

    public static Ejemplar prestarEjemplar(String isbn){
        Libro libro = GestionLibro.getLibro(isbn);
        if(GestionLibro.ejemplarDisponible(isbn)) {
            for (Ejemplar ejemplar : libro.getEjemplares()) {
                if(ejemplar.getEstado().equals("Disponible")){
                    ejemplar.setEstado("Prestado");
                    return ejemplar;
                }
            }
        }
        return null;
    }

    public static void devolverPrestamo(Prestamo prestamo, LocalDate fechaDevuelto) {
        prestamo.getEjemplar().setEstado("Disponible");
        //Sincronización a BD
        Menu.DAOEjemplar.updateObjeto(prestamo.getEjemplar());
        if(fechaDevuelto.isAfter(prestamo.getFechaDevolucion())) {
            //Las penalizaciones duran 15 días por cada libro prestado fuera de
            //plazo. Es decir, si como máximo un usuario ha devuelto fuera de
            //plazo 3 libros, la penalización será de 3*15=45 días.
            LocalDate penalizacion = prestamo.getUsuario().getPenalizacionHasta();
            if(penalizacion==null){
                prestamo.getUsuario().setPenalizacionHasta(LocalDate.now().plusDays(15));
            }
            else{
                LocalDate penalizacionNueva = penalizacion.plusDays(15);
                prestamo.getUsuario().setPenalizacionHasta(penalizacionNueva);
            }
            //Sincronización a BD
            Menu.DAOUsuario.updateObjeto(prestamo.getUsuario());
        }
    }
}