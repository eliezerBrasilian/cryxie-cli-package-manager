package alpine.crixie.cli.utiities.contracts.dependency_downloader;

import alpine.crixie.cli.utiities.CryxieLibsDirectory;
import alpine.crixie.cli.utiities.PackageLuaModifier;
import alpine.crixie.cli.utiities.PomXmlModifier;
import alpine.crixie.cli.utiities.SearchArrumer;

import java.io.FileNotFoundException;
import java.io.IOException;

public class DependencyRemoverContractImpl implements DependencyRemoverContract {
    private final SearchArrumer searchArrumer;

    public DependencyRemoverContractImpl(SearchArrumer arrumer) {
        this.searchArrumer = arrumer;
    }

    @Override
    public void removeFromPomXmlFile() {
        new PomXmlModifier(searchArrumer.getPesquisa())
                .remove();
    }

    @Override
    public void removePackageFromLuaFile() throws FileNotFoundException {
        PackageLuaModifier modifier = new PackageLuaModifier();

        modifier.removeDependency(searchArrumer.getPackageName());
    }

    @Override
    public void removePackageFromCryxieLibsDirectory() throws IOException, InterruptedException {
        new CryxieLibsDirectory().
                jarFileName(searchArrumer.getPesquisa())
                .remove();
    }
}
