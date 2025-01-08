package alpine.crixie.cli.utiities.requests.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public record PackageRequestDto(
        String name,
        @JsonProperty("project_structure") String projectStructure,
        String description,
        @JsonProperty("is_private") boolean isPrivate,
        @JsonProperty("user_id") String userId,
        @JsonProperty("repository_url") String repositoryUrl,
        List<String> keywords,
        String version,
        List<Dependency> dependencies,
        Type type
) {
    public record Dependency(String name, String version) {
    }

    public enum Type {
        JAVA,
        LUA
    }

    public String getJson() throws JsonProcessingException {
        var objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(this);
    }
}
