package biblioteca;

import jakarta.persistence.Persistence;

import java.sql.Array;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Menu {
    public static Scanner t = new Scanner(System.in);
    public static DAOGenerico<Usuario> DAOUsuario = new DAOGenerico<>(Usuario.class, "Usuario");
    public static DAOGenerico<Libro> DAOLibro = new DAOGenerico<>(Libro.class, "Libro");
    public static DAOGenerico<Ejemplar> DAOEjemplar = new DAOGenerico<>(Ejemplar.class, "Ejemplar");
    public static DAOGenerico<Prestamo> DAOPrestamo = new DAOGenerico<>(Prestamo.class, "Prestamo");


    public Menu() throws SQLException {
        //Si no existe un administrador, se introduce uno por defecto:
        if(!GestionUsuario.adminExiste(DAOUsuario.readAll())){
            Usuario admin = new Usuario("87654321Z", "admin", "admin@example.com", "clave$1", "administrador");
            DAOUsuario.addObjeto(admin);
            GestionUsuario.listaUsuarios.add(admin);
        }
        menuGlobal();
    }

    public void menuGlobal() throws SQLException{
        Usuario usuarioActual = null;
        System.out.println("""
                    Inicia sesión:
                    DNI: 
                    """);
        String dni = t.nextLine();
        if(GestionUsuario.usuarioExists(dni)) {
            usuarioActual = GestionUsuario.getUsuario(dni);
            boolean acierto = false;
            int fallos = 0;
            do {
                System.out.println("Contraseña: ");
                String contrasenya = t.nextLine();
                if (usuarioActual.getPassword().equals(contrasenya)) {
                    acierto = true;
                } else {
                    System.out.println("Contraseña incorrecta.");
                    fallos++;
                    if(fallos==3) throw new IllegalArgumentException("Demasiados intentos fallidos.");
                }
            } while (!acierto || fallos<3);
            if(usuarioActual.getTipo().equals("administrador"))
                menuAdmin();
            else
                menuUsuario(usuarioActual);
        }
        else{
            System.out.println("No existen usuarios con ese DNI.");
        }
    }

    public void menuAdmin() throws SQLException {
        int opcion = 0;
        do {
            System.out.println("""
                    Elige una opción:
                    1. Registrar libro
                    2. Listar libros
                    3. Registrar ejemplar
                    4. Ver ejemplares de un libro
                    5. Consultar stock disponible de un libro
                    6. Registrar usuario
                    7. Listar usuarios
                    8. Listar préstamos
                    9. Volver atrás
                    """);
            opcion = t.nextInt(); t.nextLine();
            switch (opcion){
                case 1 -> {
                    //Pido los datos del jugador por teclado y lo inserto en la BD y la lista (a la vez en el método)
                    System.out.println("Introduce los datos del jugador:");
                    System.out.println("DNI: ");
                    String DNI = t.nextLine();
                    System.out.println("Nombre: ");
                    String Nombre = t.nextLine();
                    System.out.println("Fecha nacimiento (yyyy-MM-dd): ");
                    LocalDate FechaNacimiento = LocalDate.parse(t.nextLine());
                    System.out.println("Estatura (float): ");
                    Float Estatura = t.nextFloat();
                    System.out.println("Peso (float): ");
                    Float Peso = t.nextFloat();
                    System.out.println("Número de goles: ");
                    Integer numGoles = t.nextInt(); t.nextLine();
                    System.out.println("Número de asistencias: ");
                    Integer numAsistencias = t.nextInt(); t.nextLine();
                    System.out.println("Número de partidos jugados: ");
                    Integer numPartidos = t.nextInt(); t.nextLine();
                    System.out.println("IdEquipo: ");
                    Integer idEquipo = t.nextInt(); t.nextLine();
                    if (GestionEquipos.equipoExists(idEquipo)) { //Si existe un equipo con ese ID dejará crearlo, si no, no.
                        Jugador jugadorN = new Jugador(DNI, Nombre, FechaNacimiento, Estatura, Peso, numGoles, numAsistencias, numPartidos, GestionEquipos.getEquipo(idEquipo));
                        //Añado a la BD y además compruebo si ha funcionado, si ha funcionado lo añado a la lista en memoria, si no
                        //el propio método del DAO arroja la excepción
                        if(DAOJugador.addObjeto(jugadorN)) {
                            GestionJugadores.getListaJugadores().add(jugadorN);
                            System.out.println("Jugador agregado.");
                        }
                    }
                    else {
                        System.out.println("No existe un equipo con ese ID.");
                    }
                }
                case 2 -> {
                    for (Jugador jugadorN : DAOJugador.readAll()) {
                        System.out.println(jugadorN);
                    }
                }
                case 3 -> {
                    System.out.println("DNI del jugador a leer: ");
                    String DNI = t.nextLine();
                    if (GestionJugadores.jugadorExists(DNI)) //Si se ha encontrado un jugador con ese DNI
                        //Leo por pantalla el objeto Jugador recibido por la consulta con su to String por defecto
                        System.out.println(DAOJugador.readObjeto(DNI));
                    else
                        System.out.println("No existe un jugador con ese DNI.");
                }
                case 4 -> {
                    System.out.println("DNI del jugador a modificar:");
                    String DNI = t.nextLine();
                    if (GestionJugadores.jugadorExists(DNI)){ //Si se ha encontrado un jugador con ese DNI
                        Jugador jugadorAupdatear = GestionJugadores.getJugador(DNI); //Creo un alias para el jugador
                        //pido sus nuevos datos, los modifico en el objeto original con el alias y envío al método update
                        System.out.println("Introduce los nuevos datos del jugador:");
                        System.out.println("Nombre: ");
                        String Nombre = t.nextLine();
                        if(Nombre!=null&&!Nombre.isEmpty()) jugadorAupdatear.setNombre(Nombre);
                        System.out.println("Fecha nacimiento (yyyy-MM-dd): ");
                        String FechaNacimientoString = t.nextLine();
                        if(FechaNacimientoString!=null&&!FechaNacimientoString.isEmpty()) {
                            LocalDate FechaNacimiento = LocalDate.parse(FechaNacimientoString);
                            jugadorAupdatear.setFechaNacimiento(FechaNacimiento);
                        }
                        System.out.println("Estatura (float): ");
                        Float Estatura = t.nextFloat();
                        if(Estatura!=null) jugadorAupdatear.setEstatura(Estatura);
                        System.out.println("Peso (float): ");
                        Float Peso = t.nextFloat();
                        if(Peso!=null) jugadorAupdatear.setPeso(Peso);
                        System.out.println("Número de goles: ");
                        Integer numGoles = t.nextInt(); t.nextLine();
                        if(numGoles!=null) jugadorAupdatear.setNGoles(numGoles);
                        System.out.println("Número de asistencias: ");
                        Integer numAsistencias = t.nextInt(); t.nextLine();
                        if(numAsistencias!=null) jugadorAupdatear.setAsistencias(numAsistencias);
                        System.out.println("Número de partidos jugados: ");
                        Integer numPartidos = t.nextInt(); t.nextLine();
                        if(numPartidos!=null) jugadorAupdatear.setNPartidosJugados(numPartidos);
                        System.out.println("IdEquipo: ");
                        Integer idEquipo = t.nextInt(); t.nextLine();
                        if(idEquipo!=null) {
                            if (GestionEquipos.equipoExists(idEquipo)) {
                                jugadorAupdatear.setIdEquipo(GestionEquipos.getEquipo(idEquipo));
                            }
                        }
                        //Lo modifico en la BD
                        if(DAOJugador.updateObjeto(jugadorAupdatear))
                            System.out.println("Jugador modificado.");
                    }
                    else{
                        System.out.println("No existe un jugador con ese DNI");
                    }
                }
                case 5 -> {
                    System.out.println("DNI del jugador a borrar:");
                    String DNI= t.nextLine();
                    //Si se ha encontrado un jugador con ese DNI, mando el jugador al método y se elimina de la BD
                    if (GestionJugadores.jugadorExists(DNI)) {
                        Jugador jugadorAborrar = GestionJugadores.getJugador(DNI);
                        if(DAOJugador.removeObjeto(jugadorAborrar)) {
                            GestionJugadores.getListaJugadores().remove(jugadorAborrar);
                            System.out.println("Jugador eliminado.");
                        }
                    }
                    else
                        System.out.println("No existe un jugador con ese ID");
                }
                case 6 ->{
                    GestionJugadores.ordenarLista();
                    for (Jugador jugador : GestionJugadores.getListaJugadores()) {
                        System.out.println(jugador);
                    }
                }
                case 7 -> {
                    GestionJugadores.ordenarListaNombre();
                    for (Jugador jugador : GestionJugadores.getListaJugadores()) {
                        System.out.println(jugador);
                    }
                }
                case 8 -> {
                    for (Prestamo prestamo : GestionPrestamo.getListaPrestamos()){
                        System.out.println(prestamo);
                    }
                }
                case 9 -> System.out.println("Volviendo atrás.");
                default -> System.out.println("Opción errónea, inténtalo de nuevo");
            }
        }while(opcion!=9);
    }

    public void menuUsuario(Usuario usuarioActual) throws SQLException {
        int opcion = 0;
        do {
            System.out.println("""
                    Elige una opción:
                    1. Consultar mi información
                    2. Ver mis préstamos
                    3. Pedir un préstamo
                    4. Devolver ejemplar
                    5. Volver atrás
                    """);
            opcion = t.nextInt(); t.nextLine();
            switch (opcion){
                case 1 -> System.out.println(usuarioActual);
                case 2 -> {
                    for (Prestamo prestamo : usuarioActual.getPrestamos()){
                        System.out.println(prestamo);
                    }
                }
                case 3 -> {
                    if(usuarioActual.getPenalizacionHasta()!=null && LocalDate.now().isAfter(usuarioActual.getPenalizacionHasta())){
                        System.out.println("ISBN del libro a pedir: ");
                        String isbn = t.nextLine();
                        if(GestionLibro.libroExists(isbn)){
                            if(GestionLibro.ejemplarDisponible(isbn)){
                                Ejemplar ejemplarApedir = GestionPrestamo.prestarEjemplar(isbn);
                                Prestamo prestamo = new Prestamo(usuarioActual, ejemplarApedir, LocalDate.now());
                                DAOPrestamo.addObjeto(prestamo);
                                GestionPrestamo.getListaPrestamos().add(prestamo);
                                System.out.println("Se le ha prestado el ejemplar con id: " + ejemplarApedir.getId() +" - NO LO OLVIDE");
                            }
                            else
                                System.out.println("No hay ejemplares disponibles de este libro para pedir.");
                        } else
                            System.out.println("No existe un libro con ese isbn.");
                    } else
                        System.out.println("Este usuario está penalizado y no podrá pedir préstamos.");
                }
                case 4 -> {
                    System.out.println("ID del ejemplar a devolver: ");
                    int idEjemplar = t.nextInt(); t.nextLine();
                    if(GestionEjemplar.ejemplarExists(idEjemplar)){
                        if(GestionPrestamo.prestamoExists(idEjemplar, usuarioActual)){
                            Prestamo prestamoAdevolver = GestionPrestamo.getPrestamo(idEjemplar, usuarioActual);
                            GestionPrestamo.devolverPrestamo(prestamoAdevolver, LocalDate.now());
                        }
                    } else
                        System.out.println("No existe un libro con ese isbn.");
                }
                case 5 -> System.out.println("Volviendo atrás.");
                default -> System.out.println("Opción errónea, inténtalo de nuevo");
            }
        }while(opcion!=5);
    }
}
