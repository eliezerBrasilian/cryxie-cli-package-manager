package alpine.crixie.cli.utiities;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.Iterator;
import java.util.List;

public class PomXmlModifier {
    private final String name;
    private String version;

    public PomXmlModifier(String name, String version) {
        if (name.trim().isEmpty()) {
            throw new InvalidParameterException("jar file name can not be null or empty");
        }

        this.name = name;
        this.version = version;
    }

    public PomXmlModifier(String name) {
        this.name = name;
    }

    public void add(String jarFilePath) {

        try {
            // Lê o POM existente
            File pomFile = new File("pom.xml");
            SAXBuilder saxBuilder = new SAXBuilder();
            Document document = saxBuilder.build(pomFile);

            // Obtém o elemento raiz
            Element projectElement = document.getRootElement();

            Element dependenciesElement = getDependenciesTag(projectElement);
            // Adiciona uma quebra de linha antes, se necessário
            dependenciesElement.addContent("\n    ");

            // Adiciona a dependência à lista de dependências
            dependenciesElement.addContent(addNewDependency(projectElement, name, jarFilePath));

            // Adiciona uma quebra de linha após a dependência
            dependenciesElement.addContent("\n");

            saveModifications(document);

        } catch (Exception e) {
            throw new RuntimeException("Error on add package to pom.xml");
        }
    }

    private Element addNewDependency(Element projectElement, String name, String jarFilePath) {
        // Cria a nova dependência
        Element dependency = new Element("dependency", projectElement.getNamespace());

        Element groupId = new Element("groupId", projectElement.getNamespace()).setText(name);
        Element artifactId = new Element("artifactId", projectElement.getNamespace()).setText(name);
        Element version = new Element("version", projectElement.getNamespace()).setText(this.version);
        Element scope = new Element("scope", projectElement.getNamespace()).setText("system");
        Element systemPath = new Element("systemPath", projectElement.getNamespace())
                .setText("${project.basedir}/" + jarFilePath);

        // Adiciona os elementos à dependência
        dependency.addContent(groupId);
        dependency.addContent(artifactId);
        dependency.addContent(version);
        dependency.addContent(scope);
        dependency.addContent(systemPath);
        return dependency;
    }

    private static Element getDependenciesTag(Element projectElement) {
        // Localiza ou cria a tag <dependencies>
        Element dependenciesElement = projectElement.getChild("dependencies", projectElement.getNamespace());
        if (dependenciesElement == null) {
            dependenciesElement = new Element("dependencies", projectElement.getNamespace());
            projectElement.addContent(dependenciesElement);
        }
        return dependenciesElement;
    }

    private static void saveModifications(Document document) throws IOException {
        // Salva o POM atualizado com formatação
        Format format = Format.getPrettyFormat();
        format.setIndent("  "); // Define a indentação como dois espaços
        format.setLineSeparator(System.lineSeparator()); // Adiciona separação de linha nativa do sistema

        XMLOutputter outputter = new XMLOutputter(format);
        try (FileWriter writer = new FileWriter("pom.xml")) {
            outputter.output(document, writer);
        }
    }

    public void remove() {
        try {
            // Lê o POM existente
            File pomFile = new File("pom.xml");
            SAXBuilder saxBuilder = new SAXBuilder();
            Document document = saxBuilder.build(pomFile);

            // Obtém o elemento raiz
            Element projectElement = document.getRootElement();
            Element dependenciesElement = projectElement.getChild("dependencies", projectElement.getNamespace());

            if (dependenciesElement != null) {
                // Localiza a dependência pelo systemPath
                List<Element> dependencies = dependenciesElement.getChildren("dependency", projectElement.getNamespace());
                for (Iterator<Element> iterator = dependencies.iterator(); iterator.hasNext(); ) {
                    Element dependency = iterator.next();
                    Element systemPath = dependency.getChild("systemPath", projectElement.getNamespace());

                    // Verifica se o systemPath contém o termo de pesquisa (sem o .jar)
                    if (systemPath != null && systemPath.getText().contains(name)) {
                        iterator.remove(); // Remove a dependência encontrada
                        break;
                    }
                }
            }

            // Salva as modificações no POM
            saveModifications(document);

        } catch (Exception e) {
            throw new RuntimeException("Error on remove package from pom.xml", e);
        }
    }

}
