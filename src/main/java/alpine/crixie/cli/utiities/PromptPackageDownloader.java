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

        var data = performCommonOperationsAndGetDependencies(
                packageName, version, downloadedPackages);

        var childDeps = data.childDeps();
        var correctVersion = data.correctVersion();

        if (childDeps == null) return;

        addBaseDependencyInPackageLua(packageName, correctVersion, childDeps);

        if (!childDeps.isEmpty()) {
            for (PackageRequestDto.Dependency dependency : childDeps) {
                downloadPackageRecursively(dependency.name(), dependency.version(), downloadedPackages);
            }
        }
    }

    private void addBaseDependencyInPackageLua(String currentPackageName, String correctVersion, List<PackageRequestDto.Dependency> childDeps) {

        //se possui n filhos, não adiciona eles, adiciona só o pai
        if (childDeps.size() > 1) {
            addToPackageLuaFile(this.name, correctVersion);
        } else {
            addToPackageLuaFile(currentPackageName, correctVersion);
        }
    }

    private void addToPackageLuaFile(String packageName, String correctVersion) {
        try {
            PackageLuaModifier modifier = new PackageLuaModifier();
            modifier.addDependency(packageName, correctVersion);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("was not possible to add dependency into package.lua, because file does not exist: " + e.getMessage());
        }
    }

}
