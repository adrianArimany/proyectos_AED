package org.example;
import org.neo4j.driver.*;
import org.neo4j.driver.Record;

import java.util.*;

public class UsuarioService {
    private final Driver driver;

    public UsuarioService(Driver driver) {
        this.driver = driver;
    }

    public boolean crearUsuario(String nombreUsuario, String contrasena, boolean esMusico) {
        if (usuarioExiste(nombreUsuario)) {
            System.out.println("El usuario ya existe.");
            return false;
        }

        try (Session session = driver.session()) {
            session.writeTransaction(tx -> {
                tx.run("CREATE (u:Usuario {nombreUsuario: $nombreUsuario, contrasena: $contrasena, esMusico: $esMusico})",
                        Values.parameters("nombreUsuario", nombreUsuario, "contrasena", contrasena, "esMusico", esMusico));
                return null;
            });
            return true;
        }
    }

    public boolean autenticarUsuario(String nombreUsuario, String contraseña) {
        try (Session session = driver.session()) {
            return session.readTransaction(tx -> {
                Result result = tx.run("MATCH (u:Usuario {nombreUsuario: $nombreUsuario, contraseña: $contraseña}) RETURN u",
                        Values.parameters("nombreUsuario", nombreUsuario, "contraseña", contraseña));
                return result.hasNext();
            });
        }
    }

    private boolean usuarioExiste(String nombreUsuario) {
        try (Session session = driver.session()) {
            return session.readTransaction(tx -> {
                Result result = tx.run("MATCH (u:Usuario {nombreUsuario: $nombreUsuario}) RETURN u",
                        Values.parameters("nombreUsuario", nombreUsuario));
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
                                    "WITH c " +
                                    "MATCH (u:Usuario {nombreUsuario: $usuario}) " +
                                    "MERGE (u)-[:QUIERE_CARACTERISTICA]->(c)",
                            Values.parameters("nombre", caracteristica, "usuario", nombreUsuario)
                    );

                    return null;
                });
            }
        }
    }

    public void registrarLike(String nombreUsuario, String sampleId) {
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> {
                tx.run("MATCH (u:Usuario {nombreUsuario: $usuario}) " +
                                "MATCH (s:Sample {id: $sampleId}) " +
                                "MERGE (u)-[:LIKE]->(s)",
                        Values.parameters("usuario", nombreUsuario, "sampleId", sampleId));
                return null;
            });
        }
    }

    public List<String> recomendarSamplesComoMusico(String usuario, List<String> caracteristicas) {
        List<String> resultados = new ArrayList<>();
        try (Session session = driver.session()) {
            session.readTransaction(tx -> {
                Result result = tx.run(
                        "MATCH (s:Sample)-[:TIENE_CARACTERISTICA]->(c:Caracteristica) " +
                                "WHERE c.nombre IN $tags " +
                                "WITH s, COLLECT(DISTINCT c.nombre) AS matchedTags " +
                                "RETURN s.id AS id, size(matchedTags) AS tagCount " +
                                "ORDER BY tagCount DESC",
                        Values.parameters("tags", caracteristicas));
                while (result.hasNext()) {
                    Record row = result.next();
                    resultados.add(row.get("id").asString());
                }
                return null;
            });
        }
        return resultados;
    }


    public List<String> recomendarSamplesComoNoMusico(String usuario, List<String> caracteristicas) {
        List<String> resultados = new ArrayList<>();
        try (Session session = driver.session()) {
            session.readTransaction(tx -> {
                Result result = tx.run(
                        "MATCH (m:Usuario)-[:LIKE]->(s:Sample)-[:TIENE_CARACTERISTICA]->(c:Caracteristica) " +
                                "WHERE m.esMusico = true AND c.nombre IN $tags " +
                                "WITH s, COLLECT(DISTINCT c.nombre) AS matchedTags " +
                                "RETURN s.id AS id, size(matchedTags) AS tagCount " +
                                "ORDER BY tagCount DESC",
                        Values.parameters("tags", caracteristicas));
                while (result.hasNext()) {
                    Record row = result.next();
                    resultados.add(row.get("id").asString());
                }
                return null;
            });
        }
        return resultados;
    }

    public boolean obtenerEsMusico(String nombreUsuario) {
        try (Session session = driver.session()) {
            return session.readTransaction(tx -> {
                Result result = tx.run("MATCH (u:Usuario {nombreUsuario: $nombre}) RETURN u.esMusico AS musico",
                        Values.parameters("nombre", nombreUsuario));
                if (result.hasNext()) {
                    return result.next().get("musico").asBoolean();
                }
                return false;
            });
        }
    }

}
