import com.martiansoftware.jsap.*;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.lang3.StringUtils;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.model.exception.command.CommandOperationException;
import org.bladerunnerjs.plugin.utility.command.ArgsParsingCommandPlugin;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.lib.TextProgressMonitor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

/*
 * Created by robm on 07/08/2014.
 */

public class CreateCloneAppPlugin extends ArgsParsingCommandPlugin {
    private BRJS brjs;

    @Override
    protected void configureArgsParser(JSAP jsap) throws JSAPException {
        jsap.registerParameter(new UnflaggedOption("url-of-repo-to-be-cloned").setRequired(true).setHelp("The url of the repository to be cloned"));
        jsap.registerParameter(new Switch("download-zip").setLongFlag("raw").setHelp("Downloads the zip for the repository"));
        jsap.registerParameter(new FlaggedOption("branch").setShortFlag('b').setRequired(false).setHelp("Specify a branch to be taken"));
    }

    @Override
    protected int doCommand(JSAPResult jsapResult) throws CommandArgumentsException, CommandOperationException {
        if (jsapResult.getBoolean("download-zip")) {
            getRawRepository(jsapResult);
        } else {
            cloneRepository(jsapResult);
        }
        return 0;
    }

    private void getRawRepository(JSAPResult jsapResult) {
        String url = jsapResult.getString("url-of-repo-to-be-cloned");
        downloadZip(url);
        ExtractFromZipAtDestination(url);
    }

    private void cloneRepository(JSAPResult jsapResult) {
        try {
            String branchName = jsapResult.getString("branch") == null ? "master" : jsapResult.getString("branch");
            makeBranchOfRepository(jsapResult.getString("url-of-repo-to-be-cloned"), branchName);
        } catch (GitAPIException | IOException e) {
            e.printStackTrace();
        }
    }

    private void makeBranchOfRepository(String url, String branchName) throws GitAPIException, JGitInternalException, IOException {
        String appName = StringUtils.substringAfterLast(url.replace("\\", "/"), "/");
        appName = appName.endsWith(".git") ? StringUtils.substringBeforeLast(appName, ".git") : appName;
        App app = brjs.app(appName);
        app.dir().mkdir();
        Git.cloneRepository().setDirectory(app.dir()).setBranch(branchName).setURI(url).setProgressMonitor(new TextProgressMonitor()).call();
    }

    private void ExtractFromZipAtDestination(String url) {
        String source = new File("src/test/resources/testRepo/archive/testRepo-master.zip").getAbsolutePath();
        String destination = "C:/Users/robm/BladeRunnerJS/apps";
        unzipFromSourceToDestination(source, destination);
    }

    private void unzipFromSourceToDestination(String source, String destination) {
        try {
            ZipFile zipFile = new ZipFile(source);
            zipFile.extractAll(destination);
        } catch (ZipException e) {
            e.printStackTrace();
        }
    }

    private void downloadZip(String url) {
        String zipURLFromGithubURL = url.replace(".git", "") + "/archive/master.zip";

        try {
            URL website = new URL(zipURLFromGithubURL);
            ReadableByteChannel readableByteChannel = Channels.newChannel(website.openStream());
            FileOutputStream fileOutputStream = new FileOutputStream(url.split("/")[url.split("/").length - 1].replace(".git", "-master.zip"));
            fileOutputStream.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getCommandName() {
        return "clone-app-from-github";
    }

    @Override
    public String getCommandDescription() {
        return "clones an app from a github repository";
    }

    @Override
    public void setBRJS(BRJS brjs) {
        this.brjs = brjs;
    }
}
