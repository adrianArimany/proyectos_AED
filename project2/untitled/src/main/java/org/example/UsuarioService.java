package org.example;
import org.neo4j.driver.*;
import org.neo4j.driver.Record;

import java.util.ArrayList;
import java.util.List;

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

    public void registrarPreferencias(String nombreUsuario, List<String> caracteristicas) {
        try (Session session = driver.session()) {
            for (String caracteristica : caracteristicas) {
                session.writeTransaction(tx -> {
                    tx.run(
                            "MERGE (c:Caracteristica {nombre: $nombre}) " +
                                    "MATCH (u:Usuario {nombreUsuario: $usuario}) " +
                                    "MERGE (u)-[:QUIERE_CARACTERISTICA]->(c)",
                            Values.parameters("nombre", caracteristica, "usuario", nombreUsuario)
                    );
                    return null;
                });
            }
            System.out.println("Preferencias registradas.");
        }
    }

    public List<String> recomendarSamplesParaUsuario(String nombreUsuario) {
        List<String> recomendaciones = new ArrayList<>();
        try (Session session = driver.session()) {
            session.readTransaction(tx -> {
                Result result = tx.run(
                        "MATCH (u:Usuario {nombreUsuario: $usuario})-[:QUIERE_CARACTERISTICA]->(c:Caracteristica)<-[:TIENE_CARACTERISTICA]-(s:Sample) " +
                                "RETURN DISTINCT s.id AS sampleId " +
                                "LIMIT 10",
                        Values.parameters("usuario", nombreUsuario)
                );
                while (result.hasNext()) {
                    Record row = result.next();
                    recomendaciones.add(row.get("sampleId").asString());
                }
                return null;
            });
        }
        return recomendaciones;
    }


}

