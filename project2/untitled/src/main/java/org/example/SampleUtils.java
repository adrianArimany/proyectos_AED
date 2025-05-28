package org.example;
import java.util.HashSet;
import java.util.Set;

public class SampleUtils {

    public static Set<String> applyMappingRules(Sample sample) {
        Set<String> tags = new HashSet<>();

        // Regla 2: Instrumento
        String instrumento = sample.getInstrument().toLowerCase();
        if (instrumento.contains("piano")) {
            tags.add("clasico");
            tags.add("relajante");
        } else if (instrumento.contains("guitarra") || instrumento.contains("bateria")) {
            tags.add("rock");
            tags.add("energetico");
        } else if (instrumento.contains("sintetizador") || instrumento.contains("bajo")) {
            tags.add("electronico");
            tags.add("bailable");
        }

        // Regla 3: Velocity → Intensidad emocional
        if (sample.getVelocity() >= 0.8) tags.add("intenso");
        else if (sample.getVelocity() <= 0.3) tags.add("suave");
        else tags.add("moderado");

        // Regla 7: Pitch → Naturaleza emocional
        if (sample.getPitch() > 1000) tags.add("alegre");
        else if (sample.getPitch() < 300) tags.add("sombrio");

        // Regla 5: Combinaciones
        if (tags.contains("energetico") && tags.contains("bailable")) {
            tags.add("fiesta");
        }
        if (tags.contains("relajante") && tags.contains("acogedor")) {
            tags.add("chill");
        }

        return tags;
    }
}
