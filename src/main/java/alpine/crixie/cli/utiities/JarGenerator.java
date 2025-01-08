package alpine.crixie.cli.utiities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

public class JarGenerator {
    private File jarFile;

    public void generateJar() throws IOException {
        String name = PackageLuaModifier.getInstance().getName();
        String version = PackageLuaModifier.getInstance().getVersion();
        String directoryWhereMainFileIs = PackageLuaModifier.getInstance().getDirectoryWhereMainFileIs();
        String jarName = name + "@" + version + ".jar";

        // Caminho para salvar o arquivo .jar
        jarFile = new File(System.getProperty("user.dir") + "/builds/" + jarName);

        // Certifique-se de que a pasta "builds" exista
        File buildsDir = new File(System.getProperty("user.dir") + "/builds");
        if (!buildsDir.exists()) {
            buildsDir.mkdirs();
        }

        // Diretório onde o código-fonte está localizado
        File srcDir = new File(System.getProperty("user.dir") + "/src");

        // Criar o arquivo .jar
        try (FileOutputStream fos = new FileOutputStream(jarFile);
             JarOutputStream jos = new JarOutputStream(fos)) {

            // Adicionar o manifesto para especificar a classe principal
            jos.putNextEntry(new JarEntry("META-INF/"));
            jos.closeEntry();
            jos.putNextEntry(new JarEntry("META-INF/MANIFEST.MF"));
            jos.write(generateManifestContent(directoryWhereMainFileIs).getBytes());
            jos.closeEntry();

            // Adicionar os arquivos do diretório src ao arquivo .jar
            addDirectoryToJar(jos, srcDir, srcDir.getAbsolutePath() + "/");
        }
    }

    public File getJarFile() {
        return jarFile;
    }

    private void addDirectoryToJar(JarOutputStream jos, File directory, String basePath) throws IOException {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                String entryName = file.getAbsolutePath().replace(basePath, "").replace("\\", "/");
                if (file.isDirectory()) {
                    // Se for um diretório, adiciona recursivamente
                    jos.putNextEntry(new JarEntry(entryName + "/"));
                    jos.closeEntry();
                    addDirectoryToJar(jos, file, basePath);
                } else {
                    // Se for um arquivo, adiciona ao .jar
                    jos.putNextEntry(new JarEntry(entryName));
                    try (FileInputStream fis = new FileInputStream(file)) {
                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        while ((bytesRead = fis.read(buffer)) != -1) {
                            jos.write(buffer, 0, bytesRead);
                        }
                    }
                    jos.closeEntry();
                }
            }
        }
    }

    private String generateManifestContent(String mainClassName) {
        return "Manifest-Version: 1.0\n" +
                "Main-Class: " + mainClassName + "\n";
    }
}
