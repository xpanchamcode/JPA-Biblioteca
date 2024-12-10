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
                    if(fallos==3) throw new IllegalArgumentException("Has llegado al límite de 3 intentos fallidos.");
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
                    1. Menú libros
                    2. Menú ejemplares
                    3. Menú usuario
                    4. Menú préstamo
                    5. Volver atrás
                    """);
            opcion = t.nextInt(); t.nextLine();
            switch (opcion){
                case 1 -> menuLibro();
                case 2 -> menuEjemplar();
                case 3 -> menuUsuario();
                case 4 -> { menuPrestamo();
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
                case 5 -> System.out.println("Volviendo atrás.");
                default -> System.out.println("Opción errónea, inténtalo de nuevo");
            }
        }while(opcion!=5);
    }

    public void menuLibro()throws SQLException{
        int opcion = 0;
        do{
            System.out.println("""
                    1. Registrar libro
                    2. Listar libros
                    3. Leer libro específico
                    4. Modificar libro
                    5. Eliminar libro
                    6. Consultar stock disponible de un libro
                    7. Volver atrás
                    """);
            opcion = t.nextInt(); t.nextLine();
            switch (opcion){
                case 1 -> {
                    //Pido los datos del libro por teclado y lo inserto en la BD y la lista
                    System.out.println("Introduce los datos del libro:");
                    System.out.println("ISBN: ");
                    String ISBN = t.nextLine();
                    //Si no existe ya un libro con ese ISBN:
                    if (!GestionLibro.libroExists(ISBN)) {
                        System.out.println("Título: ");
                        String titulo = t.nextLine();
                        System.out.println("Autor: ");
                        String autor = t.nextLine();
                        Libro libro = new Libro(ISBN, titulo, autor);
                        //Añado a la BD y además compruebo si ha funcionado, si ha funcionado lo añado a la lista en memoria, si no
                        //el propio método del DAO arroja la excepción
                        if(DAOLibro.addObjeto(libro)) {
                            GestionLibro.getListaLibros().add(libro);
                            System.out.println("Libro agregado.");
                        }
                    }
                    else {
                        System.out.println("Ya existe un libro con ese isbn, prueba a modificarlo.");
                    }
                }
                case 2 -> {
                    for (Libro libro : DAOLibro.readAll()){
                        System.out.println(libro);
                    }
                }
                case 3 -> {
                    System.out.println("ISBN del libro a leer: ");
                    String ISBN = t.nextLine();
                    //Si se ha encontrado un libro con ese ISBN:
                    if (GestionLibro.libroExists(ISBN))
                        //Leo por pantalla el objeto Libro recibido por la consulta con su to String por defecto
                        System.out.println(DAOLibro.readObjeto(ISBN));
                    else
                        System.out.println("No existe un libro con ese ISBN.");
                }
                case 4 -> {

                }
                case 5 -> {

                }
                case 6 -> {

                }
                case 7 ->System.out.println("Volviendo atrás...");
                default -> System.out.println("Opción errónea, inténtalo de nuevo");
            }
        }while(opcion!=7);
    }

    public void menuEjemplar()throws SQLException{
        int opcion = 0;
        do{
            System.out.println("""
                    1. Registrar ejemplar
                    2. Ver ejemplares de un libro
                    3. Ver un ejemplar específico
                    4. Modificar ejemplar
                    5. Eliminar ejemplar
                    6. Volver atrás
                    """);
            opcion = t.nextInt(); t.nextLine();
            switch (opcion){
                case 1 -> {
                    //Pido los datos del ejemplar por teclado y lo inserto en la BD y la lista
                    System.out.println("Introduce el ISBN original del libro: ");
                    System.out.println("ISBN: ");
                    String ISBN = t.nextLine();
                    //Si el libro existe:
                    if (GestionLibro.libroExists(ISBN)) {
                        System.out.println("Introduce el estado (Disponible/Prestado/Dañado): ");
                        String estado = t.nextLine();
                        Ejemplar ejemplar = new Ejemplar(GestionLibro.getLibro(ISBN), estado);
                        //Añado a la BD y además compruebo si ha funcionado, si ha funcionado lo añado a la lista en memoria, si no
                        //el propio método del DAO arroja la excepción
                        if(DAOEjemplar.addObjeto(ejemplar)) {
                            GestionEjemplar.getListaEjemplares().add(ejemplar);
                            System.out.println("Ejemplar agregado.");
                        }
                    }
                    else {
                        System.out.println("No existe un libro con ese ISBN.");
                    }
                }
                case 2 -> {
                    for (Ejemplar ejemplar : DAOEjemplar.readAll()){
                        System.out.println(ejemplar);
                    }
                }
                case 3 -> {
                    System.out.println("ID del ejemplar a leer: ");
                    int idEjemplar = t.nextInt(); t.nextLine();
                    if (GestionEjemplar.ejemplarExists(idEjemplar))
                        System.out.println(DAOEjemplar.readObjeto(idEjemplar));
                    else
                        System.out.println("No existe un ejemplar con ese ID.");
                }
                case 4 -> {

                }
                case 5 -> {

                }
                case 6 ->System.out.println("Volviendo atrás...");
                default -> System.out.println("Opción errónea, inténtalo de nuevo");
            }
        }while(opcion!=6);
    }

    public void menuUsuario()throws SQLException{
        int opcion = 0;
        do{
            System.out.println("""
                    1. Registrar usuario
                    2. Listar usuarios
                    3. Listar un usuario específico
                    4. Modificar usuario
                    5. Eliminar usuario
                    6. Volver atrás
                    """);
            opcion = t.nextInt(); t.nextLine();
            switch (opcion){
                case 1 -> {
                    //Pido los datos del usuario por teclado y lo inserto en la BD y la lista
                    System.out.println("Introduce los datos del usuario:");
                    System.out.println("DNI: ");
                    String DNI = t.nextLine();
                    //Si no existe ya un usuario con ese DNI
                    if (!GestionUsuario.usuarioExists(DNI)) {
                        System.out.println("Nombre: ");
                        String nombre = t.nextLine();
                        System.out.println("email: ");
                        String email = t.nextLine();
                        System.out.println("Contraseña: ");
                        String contrasenya = t.nextLine();
                        System.out.println("Tipo (normal/administrador): ");
                        String tipo = t.nextLine();
                        Usuario usuario = new Usuario(DNI, nombre, email, contrasenya, tipo);
                        //Añado a la BD y además compruebo si ha funcionado, si ha funcionado lo añado a la lista en memoria, si no
                        //el propio método del DAO arroja la excepción
                        if(DAOUsuario.addObjeto(usuario)) {
                            GestionUsuario.getListaUsuarios().add(usuario);
                            System.out.println("Usuario agregado.");
                        }
                    }
                    else {
                        System.out.println("Ya existe un usuario con ese DNI, prueba a modificarlo.");
                    }
                }
                case 2 -> {
                    for (Usuario usuario : DAOUsuario.readAll()){
                        System.out.println(usuario);
                    }
                }
                case 3 -> {
                    System.out.println("DNI del usuario a leer: ");
                    String dni = t.nextLine();
                    if (GestionUsuario.usuarioExists(dni)) {
                        Usuario usuario = GestionUsuario.getUsuario(dni);
                        System.out.println(DAOUsuario.readObjeto(usuario.getId()));
                    }
                    else
                        System.out.println("No existe un usuario con ese DNI.");
                }
                case 4 -> {

                }
                case 5 -> {

                }
                case 6 ->System.out.println("Volviendo atrás...");
                default -> System.out.println("Opción errónea, inténtalo de nuevo");
            }
        }while(opcion!=6);
    }

    public void menuPrestamo()throws SQLException{
        int opcion = 0;
        do{
            System.out.println("""
                    1. Registrar préstamo
                    2. Listar préstamos
                    3. Listar préstamo específico
                    4. Modificar préstamo
                    5. Eliminar préstamo
                    6. Ver todos los préstamos de un usuario
                    7. Ver los préstamos ACTIVOS de un usuario
                    8. Volver atrás
                    """);
            opcion = t.nextInt(); t.nextLine();
            switch (opcion){
                case 1 -> {
                    //Pido los datos del préstamo por teclado y lo inserto en la BD y la lista
                    System.out.println("Introduce el DNI del usuario del que crear este préstamo: ");
                    System.out.println("DNI: ");
                    String DNI = t.nextLine();
                    //Si el usuario existe:
                    if (GestionUsuario.usuarioExists(DNI)) {
                        System.out.println("Introduce el id del ejemplar a pedir: ");
                        int idEjemplar = t.nextInt(); t.nextLine();
                        if(GestionEjemplar.ejemplarExists(idEjemplar)) {
                            Prestamo prestamo = new Prestamo(GestionUsuario.getUsuario(DNI), GestionEjemplar.getEjemplar(idEjemplar));
                            //Añado a la BD y además compruebo si ha funcionado, si ha funcionado lo añado a la lista en memoria, si no
                            //el propio método del DAO arroja la excepción
                            if (DAOPrestamo.addObjeto(prestamo)) {
                                GestionPrestamo.getListaPrestamos().add(prestamo);
                                System.out.println("Préstamo agregado.");
                            }
                        }
                        else System.out.println("No existe un ejemplar con ese ID.");
                    }
                    else System.out.println("No existe un usuario con ese DNI.");
                }
                case 2 -> {
                    for (Prestamo prestamo : DAOPrestamo.readAll()){
                        System.out.println(prestamo);
                    }
                }
                case 3 -> {
                    System.out.println("ID del préstamo a leer: ");
                    int idPrestamo = t.nextInt(); t.nextLine();
                    if (GestionPrestamo.prestamoExists(idPrestamo))
                        System.out.println(DAOPrestamo.readObjeto(idPrestamo));
                    else
                        System.out.println("No existe un préstamo con ese ID.");
                }
                case 4 -> {

                }
                case 5 -> {

                }
                case 6 -> {
                    System.out.println("Introduce el dni del usuario: ");
                    String dni = t.nextLine();
                    if(GestionUsuario.usuarioExists(dni)) {
                        Usuario usuario = GestionUsuario.getUsuario(dni);
                        System.out.println("Lista de préstamos del usuario "+ usuario.getNombre()+": ");
                        for (Prestamo prestamo : usuario.getPrestamos()) {
                            System.out.println(prestamo);
                        }
                    }
                    else System.out.println("No existe un usuario con ese dni.");
                }
                case 7 -> {
                    System.out.println("Introduce el dni del usuario: ");
                    String dni = t.nextLine();
                    if(GestionUsuario.usuarioExists(dni)) {
                        Usuario usuario = GestionUsuario.getUsuario(dni);
                        System.out.println("Lista de préstamos ACTIVOS del usuario "+ usuario.getNombre()+": ");
                        for (Prestamo prestamo : GestionUsuario.getPrestamosActivos(usuario)) {
                            System.out.println(prestamo);
                        }
                    }
                    else System.out.println("No existe un usuario con ese dni.");
                }
                case 8 ->System.out.println("Volviendo atrás...");
                default -> System.out.println("Opción errónea, inténtalo de nuevo");
            }
        }while(opcion!=8);
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
                    if(GestionUsuario.prestamoDisponible(usuarioActual)){
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
                    } else {
                        if (GestionUsuario.usuarioPenalizado(usuarioActual)) {
                            System.out.println(String.format("Este usuario no puede pedir préstamos, penalizado hasta : %s", usuarioActual.getPenalizacionHasta()));
                        }
                        else System.out.println("Este usuario no puede pedir préstamos, ya tiene 3 activos.");
                    }
                }
                case 4 -> {
                    System.out.println("ID del ejemplar a devolver: ");
                    int idEjemplar = t.nextInt(); t.nextLine();
                    if(GestionEjemplar.ejemplarExists(idEjemplar)){
                        if(GestionPrestamo.prestamoExists(idEjemplar, usuarioActual)){
                            Prestamo prestamoAdevolver = GestionPrestamo.getPrestamo(idEjemplar, usuarioActual);
                            GestionPrestamo.devolverPrestamo(prestamoAdevolver, LocalDate.now());
                            //Sincronización a BD:
                            DAOPrestamo.updateObjeto(prestamoAdevolver);
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
