package alpine.crixie.cli.utiities;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.atomic.AtomicReference;

public class ProjectStructure {
    public String get() {
        try {
            // Obter o diretório atual do projeto
            Path projectDir = Paths.get("").toAbsolutePath();

            // Variáveis para armazenar o primeiro e o último diretório encontrados
            String firstDirectory = projectDir.getFileName().toString();
            AtomicReference<String> lastDirectory = new AtomicReference<>(null);

            // Listar toda a estrutura de pastas recursivamente
            Files.walkFileTree(projectDir, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                    // Ignorar o diretório raiz (já foi capturado)
                    if (!dir.equals(projectDir)) {
                        lastDirectory.set(projectDir.relativize(dir).toString().replace("\\", "/"));
                    }
                    return FileVisitResult.CONTINUE;
                }
            });

            // Concatenar o primeiro e o último diretório
            if (lastDirectory.get() != null) {
                String concatenatedPath = firstDirectory + "." + lastDirectory.get();

                return concatenatedPath;
            } else {
                System.out.println("Nenhuma subpasta encontrada.");
                return "";
            }

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }
}
