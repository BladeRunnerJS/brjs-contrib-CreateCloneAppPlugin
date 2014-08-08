import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/*
 * Created by robm on 08/08/2014.
 */
public class CreateCloneAppPluginTest {
    @Test
    public void urlIsCorrectlyManipulatedToGetRelevantParts() {
        String url = "https://github.com/robknows/CreateCloneAppPlugin.git";
        assertEquals("CreateCloneAppPlugin", url.split("/")[url.split("/").length - 1].replace(".git", ""));
        assertEquals("https://github.com/robknows/CreateCloneAppPlugin/archive/master.zip", url.replace(".git", "/archive/master.zip"));
    }
}
