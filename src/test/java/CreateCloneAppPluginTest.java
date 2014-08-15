/*
 * Created by rob moore on 08/08/2014.
 */

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

public class CreateCloneAppPluginTest extends SpecTest {

    private App app;

    @Before
    public void initTestObjects() throws Exception {
        given(brjs).hasCommandPlugins(new CreateCloneAppPlugin()).and(brjs).hasBeenCreated();
        app = brjs.app("testRepo");
    }

    @Test
    public void commandIsAutomaticallyLoaded() throws Exception {
        when(brjs).runCommand("help", "clone-app-from-github");
        then(exceptions).verifyNoOutstandingExceptions();
    }

    @Test
    public void correctlyClonesGithubRepo() throws Exception {
        when(brjs).runCommand("clone-app-from-github", new File("src/test/resources/testRepo.git").getAbsolutePath());
        then(app).hasFile("README.md");
    }

    @Test
    public void correctlyUnzipsAndDownloadsMasterZip() throws Exception {
        when(brjs).runCommand("clone-app-from-github", "https://github.com/BladeRunnerJS/brjstodo-getting-started.git", "--raw");
        then(new File("src/test/resources/apps/brjstodo-getting-started-master/todo-bladeset/blades/input/resources/html")).containsFile("view.html");
    }
}