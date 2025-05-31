package org.example;
import java.util.*;
import org.neo4j.driver.*;

public class Main {
    public static void main(String[] args) {
        String uri = "bolt://localhost:7687";
        String user = "neo4j";
        String password = "einstein123";

        Neo4jConnector connector = new Neo4jConnector(uri, user, password);
        UsuarioService servicio = new UsuarioService(connector.getDriver());

        SampleImporter importer = new SampleImporter(connector.getDriver());
        importer.importarSamplesDesdeCSV("C:\\Users\\ajmac\\Documents\\Universidad\\Séptimo semestre\\Algoritmos y estructuras de datos\\Proyecto 1\\proyectos_AED\\project2\\untitled\\src\\main\\java\\org\\example\\todos_los_samples.csv");

        Scanner scanner = new Scanner(System.in);
        System.out.println("1. Registrarse");
        System.out.println("2. Iniciar sesión");
        System.out.print("Seleccione una opción: ");
        int opcionInicio = scanner.nextInt();
        scanner.nextLine();

        String nombreUsuario;
        String clave;
        boolean esMusico = false;

        if (opcionInicio == 1) {
            System.out.print("Nombre de usuario: ");
            nombreUsuario = scanner.nextLine();
            System.out.print("Contraseña: ");
            clave = scanner.nextLine();
            System.out.print("¿Es músico? (s/n): ");
            esMusico = scanner.nextLine().trim().equalsIgnoreCase("s");
            servicio.crearUsuario(nombreUsuario, clave, esMusico);
        } else if (opcionInicio == 2) {
            System.out.print("Nombre de usuario: ");
            nombreUsuario = scanner.nextLine();
            System.out.print("Contraseña: ");
            clave = scanner.nextLine();
            boolean ok = servicio.autenticarUsuario(nombreUsuario, clave);
            if (!ok) {
                System.out.println("Autenticación fallida.");
                connector.close();
                scanner.close();
                return;
            }
            // Recuperar si el usuario es músico desde la base de datos
            esMusico = servicio.obtenerEsMusico(nombreUsuario);
            System.out.println("→ Usuario autenticado como músico: " + esMusico);
        } else {
            System.out.println("Opción inválida");
            return;
        }

        // Flujo principal
        System.out.println("¿Desea solicitar samples? (s/n): ");
        if (scanner.nextLine().trim().equalsIgnoreCase("s")) {
            System.out.print("Ingrese características deseadas (separadas por coma): ");
            String[] prefs = scanner.nextLine().split(",");
            List<String> preferencias = new ArrayList<>();
            for (String p : prefs) preferencias.add(p.trim());
            servicio.registrarPreferencias(nombreUsuario, preferencias);

            List<String> recomendaciones = esMusico
                    ? servicio.recomendarSamplesComoMusico(nombreUsuario, preferencias)
                    : servicio.recomendarSamplesComoNoMusico(nombreUsuario, preferencias);

            if (recomendaciones.isEmpty()) {
                System.out.println("No se encontraron recomendaciones.");
            } else {
                System.out.println("Samples recomendados:");
                for (String id : recomendaciones) {
                    System.out.println("Sample: " + id);
                    if (esMusico) {
                        System.out.print("¿Desea darle like a este sample? (s/n): ");
                        if (scanner.nextLine().trim().equalsIgnoreCase("s")) {
                            servicio.registrarLike(nombreUsuario, id);
                        }
                    }
                }
            }
        }

        connector.close();
        scanner.close();
    }
}


