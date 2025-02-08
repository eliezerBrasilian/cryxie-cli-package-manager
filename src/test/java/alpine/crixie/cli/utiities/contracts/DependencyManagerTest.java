package alpine.crixie.cli.utiities.contracts;

import alpine.crixie.cli.utiities.SearchArrumer;
import alpine.crixie.cli.utiities.contracts.dependency_downloader.DependencyRemoverContractImpl;
import org.junit.Test;

import java.io.IOException;

public class DependencyManagerTest {

    @Test
    public void uninstallDependency() throws IOException, InterruptedException {
        var manager = new DependencyManager(new DependencyRemoverContractImpl(
                new SearchArrumer("alfa")
        ));
        manager.uninstallDependency();
    }

    @Test
    public void installDependencyFromPrompt() throws IOException {
        var manager = new DependencyManager(new SearchArrumer("alfa"));
        manager.installDependencyFromPrompt();
    }

    @Test
    public void installDependenciesFromLuaPackageFile() throws IOException {
        var manager = new DependencyManager();
        manager.installDependenciesFromLuaPackageFile();
    }
}