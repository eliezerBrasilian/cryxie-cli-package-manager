package alpine.crixie.cli.utiities.requests.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@AllArgsConstructor
public class PackageRequestDto {
    String name;
    @JsonProperty("project_structure")
    String projectStructure;
    String description;
    @JsonProperty("user_id")
    String userId;
    @JsonProperty("repository_url")
    String repositoryUrl;
    List<String> keywords;
    String version;
    List<Dependency> dependencies;
    Type type;
    Visibility visibility;
    @Setter
    @JsonProperty("access_token")
    String accessToken;

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
