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

        var data = performCommonOperationsAndGetDependencies(
                packageName, version, downloadedPackages);

        var childDeps = data.childDeps();

        if (childDeps == null) return;

        if (!childDeps.isEmpty()) {
            for (PackageRequestDto.Dependency dependency : childDeps) {
                downloadPackageRecursively(dependency.name(), dependency.version(), downloadedPackages);
            }
        }
    }

}
