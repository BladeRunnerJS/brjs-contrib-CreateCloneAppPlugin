import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;

/*
 * Created by robm on 08/08/2014.
 */
public class CreateCloneAppPluginTest extends SpecTest{

    @Before
    public void initTestObjects() throws Exception {
        given(brjs).hasCommandPlugins(new CreateCloneAppPlugin()).and(brjs).hasBeenCreated();
    }

    @Test
    public void commandIsAutomaticallyLoaded() throws Exception {
        given(brjs).hasBeenAuthenticallyCreated();
        when(brjs).runCommand("help", "clone-app-from-github");
        then(exceptions).verifyNoOutstandingExceptions();
    }
}
