package alpine.crixie.cli.utiities.contracts.dependency_downloader;

import java.io.FileNotFoundException;
import java.io.IOException;

public interface DependencyRemoverContract {
    void removeFromPomXmlFile();

    void removePackageFromLuaFile() throws FileNotFoundException;

    void removePackageFromCryxieLibsDirectory() throws IOException, InterruptedException;
}

