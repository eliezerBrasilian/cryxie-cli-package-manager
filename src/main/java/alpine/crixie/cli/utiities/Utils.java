package alpine.crixie.cli.utiities;

import alpine.crixie.cli.utiities.requests.dtos.PackageRequestDto;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class Utils {
    public final static String BASE_URL = "http://localhost:4010/cryxie/api/v1";
    // public final static String BASE_URL =
    // "http://191.252.92.39:4010/cryxie/api/v1";
    public static final String LIB_PATH = "cryxie_libs";
    public static final String SRC_PATH = "src".concat(File.separator).concat("main").concat(File.separator)
            .concat("java");

    public static PackageRequestDto getPackageData() throws FileNotFoundException {
        var data = new PackageLuaModifier().getData();
        String userId = new LocalStorage().getData().userId();

        return new PackageRequestDto(
                data.name(),
                data.directoryWhereMainFileIs(),
                data.description(),
                userId,
                data.repositoryUrl(),
                new ArrayList<>(),
                data.version(),
                data.deps(),
                PackageRequestDto.Type.JAVA,
                data.visibility(),
                "");
    }

    public enum StatusCode {
        OK(200),
        CREATED(201),
        NO_CONTENT(204),
        BAD_REQUEST(400),
        UNAUTHORIZED(401),
        FORBIDDEN(403),
        NOT_FOUND(404),
        INTERNAL_SERVER_ERROR(500),
        EXPECTATION_FAILED(417),
        SERVICE_UNAVAILABLE(503);

        private final int code;

        StatusCode(int code) {
            this.code = code;
        }

        public static StatusCode fromCode(int code) {
            for (StatusCode status : values()) {
                if (status.code == code) {
                    return status;
                }
            }
            throw new IllegalArgumentException("Unrecognized status code: " + code);
        }

        public boolean isOK() {
            return this == OK;
        }

        public boolean isCreated() {
            return this == CREATED;
        }

        public boolean isNoContent() {
            return this == NO_CONTENT;
        }

        public boolean isBadRequest() {
            return this == BAD_REQUEST;
        }

        public boolean isUnauthorized() {
            return this == UNAUTHORIZED;
        }

        public boolean isForbidden() {
            return this == FORBIDDEN;
        }

        public boolean isNotFound() {
            return this == NOT_FOUND;
        }

        public boolean isExpectationFailed() {
            return this == EXPECTATION_FAILED;
        }

        public boolean isInternalServerError() {
            return this == INTERNAL_SERVER_ERROR;
        }

        public boolean isServiceUnavailable() {
            return this == SERVICE_UNAVAILABLE;
        }
    }
}
