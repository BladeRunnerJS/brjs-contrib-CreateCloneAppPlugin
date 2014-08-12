import org.bladerunnerjs.model.App;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;

/*
 * Created by robm on 08/08/2014.
 */

public class CreateCloneAppPluginTest extends SpecTest {

    private App app;

    @Before
    public void initTestObjects() throws Exception {
        given(brjs).hasCommandPlugins(new CreateCloneAppPlugin()).and(brjs).hasBeenCreated();
        app = brjs.app("testRepo");
    }

//This test is broken
//    @Test
//    @Ignore
//    public void commandIsAutomaticallyLoaded() throws Exception {
//        given(brjs).hasBeenAuthenticallyCreated();
//        when(brjs).runCommand("help", "clone-app-from-github");
//        then(exceptions).verifyNoOutstandingExceptions();
//    }

    @Test
    public void correctlyClonesGithubRepo() throws Exception {
        when(brjs).runCommand("clone-app-from-github", new File("src/test/resources/testRepo.git").getAbsolutePath());
        then(app).hasFile("README.md");
    }

    @Test
    public void correctlyUnzipsMasterZip() throws Exception {
        when(brjs).runCommand("clone-app-from-github", new File("src/test/resources/testRepo/archive/testRepo-master.zip").getAbsolutePath(), "--raw");
        then(new File("C:/Users/robm/BladeRunnerJS/apps/master")).containsFile("README.md");
    }
}
