package alpine.crixie.cli.utiities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

public class JsonMapper<T> {
    private Object o = null;
    Map.Entry<String, Object>[] objects = null;

    public JsonMapper() {
    }

    public JsonMapper(Object o) {
        this.o = o;
    }

    public String toJson() throws JsonProcessingException {
        if (o == null) {
            return fieldsToJson();
        }

        var objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(o);
    }

    @SafeVarargs
    public final JsonMapper fromFields(Map.Entry<String, Object>... args) {
        this.objects = args;
        return this;
    }

    public String fieldsToJson() throws JsonProcessingException {
        var mapper = new ObjectMapper();
        Map<String, Object> jsonMap = new HashMap<>();

        // Adicionando todos os pares chave-valor ao mapa
        for (Map.Entry<String, Object> entry : objects) {
            jsonMap.put(entry.getKey(), entry.getValue());
        }

        return mapper.writeValueAsString(jsonMap);
    }

    public T fromJsonToTarget(String json, Class<T> klass) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        //não lança excessão se vier campos a mais que eu não existe no meu dto
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return (T) objectMapper.readValue(json, klass);

    }
}
