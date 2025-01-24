package alpine.crixie.cli.utiities;

import alpine.crixie.cli.utiities.contracts.dependency_downloader.JavaPackageInstallerBase;
import alpine.crixie.cli.utiities.requests.dtos.PackageRequestDto;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Set;

public class PromptPackageDownloader extends JavaPackageInstallerBase {
    public PromptPackageDownloader(String packageName) {
        super(packageName);
    }

    public PromptPackageDownloader(String packageName, String version) {
        super(packageName, version);
    }

    public record PasscodeRequest(String pass_code) {
    }

    @Override
    protected void downloadPackageRecursively(String packageName, String version, Set<String> downloadedPackages)
            throws IOException, InterruptedException {

        var deps = performCommonOperationsAndGetDependencies(
                packageName, version, downloadedPackages);

        if (deps == null) return;

        addToPomXmlFile(packageName, version);

        addBaseDependencyInPackageLua(packageName, version, deps);

        if (!deps.isEmpty()) {
            for (PackageRequestDto.Dependency dependency : deps) {
                downloadPackageRecursively(dependency.name(), dependency.version(), downloadedPackages);
            }
        }
    }

    private void addBaseDependencyInPackageLua(String packageName, String version, List<PackageRequestDto.Dependency> deps) {
        if (deps.size() == 1) {
            addToPackageLuaFile(packageName, version);
        }
    }

    private void addToPomXmlFile(String packageName, String version) {
        new PomXmlModifier(packageName, version).
                add(outputFilePath);
    }

    private void addToPackageLuaFile(String packageName, String version) {
        try {
            PackageLuaModifier modifier = new PackageLuaModifier();
            modifier.addDependency(packageName, version);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("was not possible to add dependency into package.lua");
        }
    }


}
