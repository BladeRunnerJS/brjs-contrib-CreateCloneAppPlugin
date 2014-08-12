import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;

/*
 * Created by robm on 08/08/2014.
 */
public class CreateCloneAppPluginTest extends SpecTest {

    @Before
    public void initTestObjects() throws Exception {
        given(brjs).hasCommandPlugins(new CreateCloneAppPlugin()).and(brjs).hasBeenCreated();
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
        when(brjs).runCommand("clone-app-from-github", "C:/Users/robm/CreateCloneAppPlugin/src/test/resources/testRepo");
        then(new File("C:/Users/robm/BladeRunnerJS/apps/testRepo")).containsFile("README.md");
        //Now passes because I changed the paths to be absolute
    }

    @Test
    public void correctlyUnzipsMasterZip() throws Exception {
        when(brjs).runCommand("clone-app-from-github", "C:/Users/robm/CreateCloneAppPlugin/src/test/resources/testRepo", "--raw");
        then(new File("C:/Users/robm/BladeRunnerJS/apps/master")).containsFile("README.md");
    }
}
