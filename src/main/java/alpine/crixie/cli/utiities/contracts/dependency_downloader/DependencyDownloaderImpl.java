package alpine.crixie.cli.utiities.contracts.dependency_downloader;

import alpine.crixie.cli.utiities.FileDownloader;
import alpine.crixie.cli.utiities.PackageLuaModifier;
import alpine.crixie.cli.utiities.PomXmlModifier;

import java.io.FileNotFoundException;

public class DependencyDownloaderImpl implements DependencyDownloader {
    String pesquisa;

    String packageName;
    String version;

    public DependencyDownloaderImpl(String pesquisa){
        this.pesquisa = pesquisa;
        splitNameVersion();
    }

    private void splitNameVersion(){
        packageName = pesquisa.trim();

        if(pesquisa.contains("@")){
            packageName = pesquisa.split("@")[0].trim();
            version = pesquisa.split("@")[1];
        }
    }

    @Override
    public void download() {
        if(version == null || version.isEmpty()){
            new FileDownloader(packageName)
                    .download1();
        }else{
            new FileDownloader(packageName,version)
                    .download1();
        }
    }

    @Override
    public void addToPomXmlFile() {
        new PomXmlModifier().
                name(packageName)
                .add();
    }

    @Override
    public void addToPackageLuaFile() {
        try {
            PackageLuaModifier modifier = PackageLuaModifier.getInstance();
            modifier.addDependency(packageName, version);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("was not possible to add dependency into package.lua");

        }
    }
}
