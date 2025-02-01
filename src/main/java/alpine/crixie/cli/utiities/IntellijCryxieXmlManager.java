package alpine.crixie.cli.utiities;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class IntellijCryxieXmlManager {

    public IntellijCryxieXmlManager() throws IOException {
        createCryxieXml();
    }

    public void createCryxieXml() throws IOException {
        String dirPath = ".idea";
        String filePath = dirPath.concat(File.separator)
                .concat("libraries").concat(File.separator)
                .concat("cryxie_libs.xml");

        File dir = new File(dirPath);
        File file = new File(filePath);


        if (!dir.exists()) {
            dir.mkdirs();
            createFileCryxie_libsXml(file);
        } else if (dir.exists() && !file.exists()) {
            createFileCryxie_libsXml(file);
        } else {
            System.out.println("Nenhuma ação foi realizada.");
        }
    }

    private void createFileCryxie_libsXml(File file) throws IOException {
        try {
            file.getParentFile().mkdirs(); // Cria a pasta "libraries" se não existir
            file.createNewFile(); // Cria o arquivo cryxie_xml

            // Criando o elemento raiz
            Element component = new Element("component");
            component.setAttribute("name", "libraryTable");

            // Criando o elemento library
            Element library = new Element("library");
            library.setAttribute("name", "cryxie_libs");

            // Adicionando as seções internas
            Element classes = new Element("CLASSES");
            Element root = new Element("root");
            root.setAttribute("url", "file://$PROJECT_DIR$/cryxie_libs");
            classes.addContent(root);

            Element javadoc = new Element("JAVADOC");
            Element sources = new Element("SOURCES");

            Element jarDirectory = new Element("jarDirectory");
            jarDirectory.setAttribute("url", "file://$PROJECT_DIR$/cryxie_libs");
            jarDirectory.setAttribute("recursive", "false");

            // Construindo a estrutura
            library.addContent(classes);
            library.addContent(javadoc);
            library.addContent(sources);
            library.addContent(jarDirectory);
            component.addContent(library);

            // Criando o documento XML
            Document document = new Document(component);

            // Escrevendo no arquivo
            XMLOutputter xmlOutputter = new XMLOutputter(Format.getPrettyFormat());
            FileOutputStream outputStream = null;
            try {
                outputStream = new FileOutputStream(file);
                xmlOutputter.output(document, outputStream);
            } finally {
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                        System.err.println("Erro ao fechar o arquivo: " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            throw new IOException(e);
        }
    }
}
