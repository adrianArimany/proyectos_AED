package org.example;
import com.opencsv.CSVReader;
import org.neo4j.driver.*;
import java.io.FileReader;
import java.util.List;
import java.util.Set;

public class SampleImporter {
    private final Driver driver;
    //private String rutaArchivo = "\"C:\\Users\\ajmac\\Documents\\Universidad\\Séptimo semestre\\Algoritmos y estructuras de datos\\Proyecto 1\\proyectos_AED\\project2\\untitled\\src\\main\\java\\org\\example\\todos_los_samples.csv\"";

    public SampleImporter(Driver driver) {
        this.driver = driver;
    }

    public void importarSamplesDesdeCSV(String rutaArchivo) {
        try (CSVReader reader = new CSVReader(new FileReader(rutaArchivo))) {
            String[] headers = reader.readNext(); // leer cabecera
            String[] fila;

            while ((fila = reader.readNext()) != null) {
                String id = fila[0] + "_" + fila[3];              // note + instrument_str
                String instrument = fila[3];                       // instrument_str
                double pitch = Double.parseDouble(fila[4]);       // pitch
                double velocity = Double.parseDouble(fila[5]);    // velocity

                Sample sample = new Sample(id, instrument, pitch, velocity);
                Set<String> tags = SampleUtils.applyMappingRules(sample);

                try (Session session = driver.session()) {
                    session.writeTransaction(tx -> {
                        tx.run("MERGE (s:Sample {id: $id}) " +
                                        "SET s.instrument = $instrument, s.pitch = $pitch, " +
                                        "s.velocity = $velocity",
                                Values.parameters(
                                        "id", id,
                                        "instrument", instrument,
                                        "pitch", pitch,
                                        "velocity", velocity
                                ));
                        return null;
                    });

                    for (String tag : tags) {
                        session.writeTransaction(tx -> {
                            tx.run("MERGE (c:Caracteristica {nombre: $nombre})",
                                    Values.parameters("nombre", tag));
                            tx.run("MATCH (s:Sample {id: $id}), (c:Caracteristica {nombre: $nombre}) " +
                                            "MERGE (s)-[:TIENE_CARACTERISTICA]->(c)",
                                    Values.parameters("id", id, "nombre", tag));
                            return null;
                        });
                    }
                }
            }

            System.out.println("Carga de samples completada.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
