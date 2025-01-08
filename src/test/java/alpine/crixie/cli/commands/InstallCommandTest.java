package alpine.crixie.cli.commands;

import alpine.crixie.cli.utiities.CryxieLibsDirectory;
import alpine.crixie.cli.utiities.FileDownloader;
import alpine.crixie.cli.utiities.PackageLuaModifier;
import alpine.crixie.cli.utiities.PomXmlModifier;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;

class InstallCommandTest {

    //@Test
    void requisicao() {
        //new FileDownloader("package1").download1();
    }

    //@Test
    void url() {
        String name = "cebola";
        String version = "1.0.1";
        System.out.println("http://localhost:4010/cryxie/api/v1/package/download?name=" + name + "?version=" + version);
    }

    //@Test
    void splitNameVersion() {
        final String pesquisa = "machine @1.0.1";

        if (pesquisa.contains("@")) {
            var name = pesquisa.split("@")[0].trim();
            var version = pesquisa.split("@")[1];

            int p = 0;
        }
    }

    //@Test
    void download() throws FileNotFoundException {
        final String name = "machine";
        new FileDownloader(name).download1(null);

        new PomXmlModifier(name, "latest").
                add();

        PackageLuaModifier modifier = PackageLuaModifier.getInstance();
        modifier.addDependency(name, "1.0.1");
    }

    // @Test
    void excluiPacoteDeCryxieLibsDirectory() throws FileNotFoundException {
        new CryxieLibsDirectory().
                jarFileName("alpine_central_email_file_manager.jar")
                .remove();
    }

    //@Test
    void removerPacoteDoPomXml() {
        new PomXmlModifier("alpine_central_email_file_manager.jar",
                "latest")
                .remove();
    }

    //@Test
    void adicionaPacoteAoPomXml() {
//        new PomXmlModifier().
//                name("alpine_central_email_file_manager.jar")
//                .add();

        new PomXmlModifier("arquivo.jar", "version").
                add();
    }

    //@Test
    void adicionaDependenciaAoPackageLua() throws FileNotFoundException {
        PackageLuaModifier modifier = PackageLuaModifier.getInstance();

        modifier.addDependency("picaru", "1.0.1");
    }

    //@Test
    void removeDependenciaDoPackageLua() throws FileNotFoundException {
        PackageLuaModifier modifier = PackageLuaModifier.getInstance();

        // Adicionando dependência
        modifier.removeDependency("cebola");
    }

    //@Test
    void run() {
        String pomFilePath = "pom.xml"; // Caminho do POM
        String jarName = "alpine_central_email_file_manager";
        String jarFilePath = "cryxie_libs/" + jarName + ".jar";

        try {
            // Lê o POM existente
            File pomFile = new File(pomFilePath);
            SAXBuilder saxBuilder = new SAXBuilder();
            Document document = saxBuilder.build(pomFile);

            // Obtém o elemento raiz
            Element projectElement = document.getRootElement();

            // Localiza ou cria a tag <dependencies>
            Element dependenciesElement = projectElement.getChild("dependencies", projectElement.getNamespace());
            if (dependenciesElement == null) {
                dependenciesElement = new Element("dependencies", projectElement.getNamespace());
                projectElement.addContent(dependenciesElement);
            }

            // Cria a nova dependência
            Element dependency = new Element("dependency", projectElement.getNamespace());

            Element groupId = new Element("groupId", projectElement.getNamespace()).setText(jarName);
            Element artifactId = new Element("artifactId", projectElement.getNamespace()).setText(jarName);
            Element version = new Element("version", projectElement.getNamespace()).setText("not-provided");
            Element scope = new Element("scope", projectElement.getNamespace()).setText("system");
            Element systemPath = new Element("systemPath", projectElement.getNamespace())
                    .setText("${project.basedir}/" + jarFilePath);

            // Adiciona os elementos à dependência
            dependency.addContent(groupId);
            dependency.addContent(artifactId);
            dependency.addContent(version);
            dependency.addContent(scope);
            dependency.addContent(systemPath);

            // Adiciona uma quebra de linha antes, se necessário
            dependenciesElement.addContent("\n    ");

            // Adiciona a dependência à lista de dependências
            dependenciesElement.addContent(dependency);

            // Adiciona uma quebra de linha após a dependência
            dependenciesElement.addContent("\n");

            // Salva o POM atualizado com formatação
            Format format = Format.getPrettyFormat();
            format.setIndent("  "); // Define a indentação como dois espaços
            format.setLineSeparator(System.lineSeparator()); // Adiciona separação de linha nativa do sistema

            XMLOutputter outputter = new XMLOutputter(format);
            try (FileWriter writer = new FileWriter(pomFilePath)) {
                outputter.output(document, writer);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}