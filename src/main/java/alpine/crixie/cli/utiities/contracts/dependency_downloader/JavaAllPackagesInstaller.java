package alpine.crixie.cli.utiities.contracts.dependency_downloader;

import alpine.crixie.cli.utiities.requests.dtos.PackageRequestDto;

import java.io.IOException;
import java.util.Set;


public class JavaAllPackagesInstaller extends JavaPackageInstallerBase {

    public JavaAllPackagesInstaller(String packageName, String version) {
        super(packageName, version);
    }

    @Override
    protected void downloadPackageRecursively(String packageName,
                                              String version, Set<String> downloadedPackages)
            throws IOException, InterruptedException {

        var deps = performCommonOperationsAndGetDependencies(
                packageName, version, downloadedPackages);

        if (deps == null) return;

        if (!deps.isEmpty()) {
            for (PackageRequestDto.Dependency dependency : deps) {
                downloadPackageRecursively(dependency.name(), dependency.version(), downloadedPackages);
            }
        }
    }

}
