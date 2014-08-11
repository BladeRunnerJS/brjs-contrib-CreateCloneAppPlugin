import com.martiansoftware.jsap.*;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.model.exception.command.CommandOperationException;
import org.bladerunnerjs.plugin.utility.command.ArgsParsingCommandPlugin;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.api.errors.RefNotFoundException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.TextProgressMonitor;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

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

    public static void rm(File f) {
        if (f.isDirectory())
            for (File c : f.listFiles())
                rm(c);
        f.delete();
    }

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
        downloadAndUnpackageZip(jsapResult.getString("url-of-repo-to-be-cloned"));
    }

    private void cloneRepository(JSAPResult jsapResult) {
        if (jsapResult.getBoolean("take-branch")) {
            cloneRepositoryBranch(jsapResult);
        } else {
            cloneRepositoryMaster(jsapResult);
        }
    }

    private void cloneRepositoryMaster(JSAPResult jsapResult) {
        try {
            cloneRepositoryFromGithub(jsapResult.getString("url-of-repo-to-be-cloned"));
        } catch (IOException | GitAPIException e) {
            e.printStackTrace();
        }
    }

    private void cloneRepositoryBranch(JSAPResult jsapResult) {
        try {
            makeBranchOfRepository(jsapResult.getString("url-of-repo-to-be-cloned"), jsapResult.getString("branch"));
        } catch (GitAPIException | IOException e) {
            e.printStackTrace();
        }
    }

    private void cloneRepositoryFromGithub(String url) throws IOException, GitAPIException {
        File localPath = File.createTempFile(url.split("/")[url.split("/").length].replace(".git", ""), "");
        Git.cloneRepository().setURI(url).setDirectory(localPath).call();
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        Repository repository = builder.setGitDir(localPath).readEnvironment().build();
        repository.close();
    }

    private void makeBranchOfRepository(String url, String branchName) throws GitAPIException, JGitInternalException, IOException {
        File tmpDir = new File(System.getProperty("java.io.tmpdir"), "tmp" + System.currentTimeMillis());
        tmpDir.mkdirs();
        Git git = Git.cloneRepository().setDirectory(tmpDir).setURI(url).setProgressMonitor(new TextProgressMonitor()).call();
        git.checkout().setName(branchName).call();
        try {
            git.checkout().setName("test").call();
        } catch (RefNotFoundException e) {
            System.err.println("couldn't checkout 'test'. Got exception: " + e.toString() + ". HEAD: " + git.getRepository().getRef("HEAD"));
        } finally {
            rm(tmpDir);
        }
    }

    public void downloadAndUnpackageZip(String url) {
        downloadZip(url);
        unpackageZip(url);
    }

    private void unpackageZip(String url) {
        String source = "C\\Users\\robm\\Downloads\\" + url.split("/")[url.split("/").length - 1].replace(".git", "-master.zip");
        String destination = "C\\Users\\robm\\" + url.split("/")[url.split("/").length - 1].replace(".git", "");

        try {
            ZipFile zipFile = new ZipFile(source);
            zipFile.extractAll(destination);
        } catch (ZipException e) {
            e.printStackTrace();
        }
    }

    private void downloadZip(String url) {
        String zipURLFromGithubURL = url.replace(".git", "archive/master.zip");

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
