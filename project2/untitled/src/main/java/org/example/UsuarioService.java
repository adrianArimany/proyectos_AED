package org.example;
import org.neo4j.driver.*;

public class UsuarioService {
    private final Driver driver;

    public UsuarioService(Driver driver) {
        this.driver = driver;
    }

    public boolean crearUsuario(String nombreUsuario, String contraseña, boolean esMusico) {
        if (usuarioExiste(nombreUsuario)) {
            System.out.println("El usuario ya existe.");
            return false;
        }

        try (Session session = driver.session()) {
            session.writeTransaction(tx -> {
                tx.run("CREATE (u:Usuario {nombreUsuario: $nombreUsuario, contraseña: $contraseña, esMusico: $esMusico})",
                        Values.parameters("nombreUsuario", nombreUsuario,
                                "contraseña", contraseña,
                                "esMusico", esMusico)).consume();
                return null;
            });
            System.out.println("Usuario creado correctamente.");
            return true;
        }
    }


    public boolean autenticarUsuario(String nombreUsuario, String contraseña) {
        try (Session session = driver.session()) {
            return session.readTransaction(tx -> {
                Result result = tx.run(
                        "MATCH (u:Usuario {nombreUsuario: $nombreUsuario, contraseña: $contraseña}) RETURN u",
                        Values.parameters("nombreUsuario", nombreUsuario, "contraseña", contraseña)
                );
                return result.hasNext();
            });
        }
    }

    private boolean usuarioExiste(String nombreUsuario) {
        try (Session session = driver.session()) {
            return session.readTransaction(tx -> {
                Result result = tx.run(
                        "MATCH (u:Usuario {nombreUsuario: $nombreUsuario}) RETURN u",
                        Values.parameters("nombreUsuario", nombreUsuario)
                );
                return result.hasNext();
            });
        }
    }
}

