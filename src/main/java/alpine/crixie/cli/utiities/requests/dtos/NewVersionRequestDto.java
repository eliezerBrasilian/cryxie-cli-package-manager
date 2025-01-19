package alpine.crixie.cli.utiities.requests.dtos;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record NewVersionRequestDto(
        @JsonProperty("package_name") String packageName,
        @JsonProperty("version_identifier") String versionIdentifier,
        List<PackageRequestDto.Dependency> dependencies,
        @JsonProperty("user_id") String userId,
        @JsonProperty("package_type") PackageRequestDto.Type packageType
) {

}
