package alpine.crixie.cli.utiities;

import java.io.File;
import java.io.FileNotFoundException;

public class CryxieLibsDirectory {
    private String jarName;

    public CryxieLibsDirectory jarFileName(String jarName){
        this.jarName = jarName;
        return this;
    }

    public void remove() throws FileNotFoundException {
        String jarFilePath = "cryxie_libs/" + jarName;
        File jarFile = new File(jarFilePath);

        if (jarFile.exists()) {
            if (jarFile.delete()) {
                System.out.println("Arquivo " + jarName + " foi excluído com sucesso.");
            } else {
                throw new RuntimeException("Não foi possível excluir o arquivo: " + jarName);
            }
        } else {
            throw new FileNotFoundException("Arquivo " + jarName + " não encontrado em cryxie_libs.");
        }
    }

}
