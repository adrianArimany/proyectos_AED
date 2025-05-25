package org.example;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;

public class Neo4jConnector {
    private final Driver driver;

    public Neo4jConnector(String uri, String user, String password) {
        driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
    }

    public Driver getDriver() {
        return driver;
    }

    public void close() {
        driver.close();
    }
}
