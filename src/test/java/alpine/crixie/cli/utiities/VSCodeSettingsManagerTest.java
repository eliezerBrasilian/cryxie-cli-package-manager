package alpine.crixie.cli.utiities;

import org.junit.Test;

public class VSCodeSettingsManagerTest {

    public VSCodeSettingsManagerTest() {
        // Default constructor, no test annotation
    }

    @Test
    public void testVSCodeSettingsManagerInitialization() {
        new VSCodeSettingsManager();

    }

    @Test
    public void stringEqualsEnum() {
        enum Visibility {
            PUBLIC,
            PRIVATE
        }

        String visibility = "PUBLIC";
        String visibility_lower = "public".toUpperCase();

        System.out.println(Visibility.valueOf(visibility).ordinal());
        System.out.println(Visibility.valueOf(visibility_lower));
    }


}