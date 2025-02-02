package alpine.crixie.cli.utiities.requests.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record PackageRequestDto(
        String name,
        @JsonProperty("project_structure") String projectStructure,
        String description,
        @JsonProperty("user_id") String userId,
        @JsonProperty("repository_url") String repositoryUrl,
        List<String> keywords,
        String version,
        List<Dependency> dependencies,
        Type type,
        Visibility visibility
) {
    public record Dependency(String name, String version) {
    }

    public enum Type {
        JAVA,
        LUA
    }

    public enum Visibility {
        PUBLIC,
        PRIVATE
    }

}
