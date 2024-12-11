package biblioteca;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws SQLException {
        GestionLibro gestionLibro = new GestionLibro();
        GestionUsuario gestionUsuario = new GestionUsuario();
        GestionEjemplar gestionEjemplar = new GestionEjemplar();
        GestionPrestamo gestionPrestamo = new GestionPrestamo();

        Menu menu = new Menu();
    }
}
