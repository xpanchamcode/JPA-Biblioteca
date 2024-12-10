package biblioteca;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class GestionUsuario {
    public static List<Usuario> listaUsuarios = new ArrayList<Usuario>();

    public GestionUsuario() throws SQLException {
        listaUsuarios = Menu.DAOUsuario.readAll();
    }

    public static List<Usuario> getListaUsuarios() {
        return listaUsuarios;
    }

    public static void setListaUsuarios(List<Usuario> listaNueva) {
        listaUsuarios = listaNueva;
    }

    public static boolean usuarioExists(String dni) {
        boolean existe = false;
        for (Usuario usuario: listaUsuarios) {
            if(usuario.getDni().equals(dni)) {
                existe = true;
            }
        }
        return existe;
    }

    public static Usuario getUsuario(String dni) {
        Usuario usuarioDevuelto = null;
        for (Usuario usuario: listaUsuarios) {
            if(usuario.getDni().equals(dni)) {
                usuarioDevuelto = usuario;
            }
        }
        return usuarioDevuelto;
    }

    //Comprobación de que existe un usuario administrador
    public static boolean adminExiste(ArrayList<Usuario> listaUsuarios){
        boolean adminExiste = false;
        for (Usuario usuario : listaUsuarios) {
            if(usuario.getTipo().equals("administrador")){
                adminExiste = true;
            }
        }
        return adminExiste;
    }

    public static List<Prestamo> getPrestamosActivos(Usuario usuario){
        List<Prestamo> listaPrestamosActivos = new ArrayList<>();
        for (Prestamo prestamo : usuario.getPrestamos()) {
            if(LocalDate.now().isBefore(prestamo.getFechaDevolucion()) || LocalDate.now().isEqual(prestamo.getFechaDevolucion())) {
                listaPrestamosActivos.add(prestamo);
            }
        }
        return listaPrestamosActivos;
    }

    public static boolean usuarioPenalizado(Usuario usuario){
        if(usuario.getPenalizacionHasta()!=null && LocalDate.now().isBefore(usuario.getPenalizacionHasta()) || LocalDate.now().isEqual(usuario.getPenalizacionHasta())) {
            return true;
        }
        return false;
    }

    public static boolean prestamoDisponible(Usuario usuario){
        if(!usuarioPenalizado(usuario)){
            if(GestionUsuario.getPrestamosActivos(usuario).size()<=3){
                return true;
            }
        }
        return false;
    }
}

