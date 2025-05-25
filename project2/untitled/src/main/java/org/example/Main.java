package org.example;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        String uri = "bolt://localhost:7687";
        String user = "neo4j";
        String password = "tu_contraseña";

        Neo4jConnector connector = new Neo4jConnector(uri, user, password);
        UsuarioService servicio = new UsuarioService(connector.getDriver());

        Scanner scanner = new Scanner(System.in);
        System.out.println("1. Crear usuario");
        System.out.println("2. Iniciar sesión");
        System.out.print("Seleccione una opción: ");
        int opcion = scanner.nextInt();
        scanner.nextLine(); // consumir salto de línea

        System.out.print("Nombre de usuario: ");
        String nombreUsuario = scanner.nextLine();

        System.out.print("Contraseña: ");
        String contraseña = scanner.nextLine();

        if (opcion == 1) {
            System.out.print("¿Es músico? (s/n): ");
            boolean esMusico = scanner.nextLine().trim().equalsIgnoreCase("s");
            servicio.crearUsuario(nombreUsuario, contraseña, esMusico);
        } else if (opcion == 2) {
            boolean autenticado = servicio.autenticarUsuario(nombreUsuario, contraseña);
            if (autenticado) {
                System.out.println("Inicio de sesión exitoso.");
            } else {
                System.out.println("Nombre de usuario o contraseña incorrectos.");
            }
        } else {
            System.out.println("Opción no válida.");
        }

        connector.close();
        scanner.close();
    }
}
