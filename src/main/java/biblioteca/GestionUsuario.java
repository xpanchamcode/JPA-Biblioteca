package biblioteca;

import java.sql.SQLException;
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
}

