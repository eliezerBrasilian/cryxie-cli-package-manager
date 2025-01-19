package alpine.crixie.cli.utiities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonMapper {
    private final Object o;

    public JsonMapper(Object o) {
        this.o = o;
    }

    public String toJson() throws JsonProcessingException {
        var objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(o);
    }
}
