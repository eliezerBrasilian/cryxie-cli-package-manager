package alpine.crixie.cli;

import alpine.crixie.cli.utiities.JsonMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

class MainTest {

    @Test
    void main() throws JsonProcessingException {

        String json = new JsonMapper<>()
                .fromFields(Map.entry("name", "mauro"),
                        Map.entry("idade", 10))
                .fieldsToJson();

        System.out.println(json);

    }

    void toJson(Map.Entry<String, Object>... args) {
        var mapper = new ObjectMapper();
        Map<String, Object> jsonMap = new HashMap<>();

        // Adicionando todos os pares chave-valor ao mapa
        for (Map.Entry<String, Object> entry : args) {
            jsonMap.put(entry.getKey(), entry.getValue());
        }

        try {
            // Convertendo o mapa para JSON
            String json = mapper.writeValueAsString(jsonMap);
            System.out.println(json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}