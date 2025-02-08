package alpine.crixie.cli.utiities;

/**
 * Class responsible to organize the search
 */
public class SearchArrumer {
    private final String pesquisa;

    private String packageName;
    private String version = "latest";

    public SearchArrumer(String pesquisa) {
        this.pesquisa = pesquisa;
        splitNameVersion();
    }

    private void splitNameVersion() {
        packageName = pesquisa.trim();

        if (pesquisa.contains("@")) {
            packageName = pesquisa.split("@")[0].trim();
            version = pesquisa.split("@")[1];
        }
    }

    public String getPackageName() {
        return packageName;
    }

    public String getVersion() {
        return version;
    }

    public String getPesquisa() {
        return pesquisa;
    }
}
