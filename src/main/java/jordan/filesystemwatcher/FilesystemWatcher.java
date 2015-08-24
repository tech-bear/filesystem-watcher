package jordan.filesystemwatcher;

import jordan.filesystemwatcher.config.ConfigParser;
import jordan.filesystemwatcher.config.xml.Watch;

import java.io.File;
import java.nio.file.*;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by Jordan-admin on 8/24/2015.
 */
public class FilesystemWatcher {
    private ExecutorService threadGroup;
    private Collection<Future<Boolean>> listenerInstances;

    public FilesystemWatcher() {
        threadGroup = Executors.newCachedThreadPool();
    }

    public void configure(final ConfigParser config) {
        config.getWatchers().forEach(watch -> addWatch(watch));
    }

    private boolean addWatch(Watch watch) {
        Path url = Paths.get(new File(watch.getDirectory()).getAbsolutePath());
        System.out.println("path = " + url);
        System.out.println("root = " + url.getRoot());
        System.out.println("parent = " + url.getParent());
        System.out.println("filename: " + url.getFileName());
        System.out.println("filesystem: " + url.getFileSystem());
        try(DirectoryStream<Path> stream = Files.newDirectoryStream(url)) {
            Iterator<Path> iter = stream.iterator();
            while(iter.hasNext()) {
                final Path p = iter.next();
                if(Files.isDirectory(p)) {
                    System.out.println(p + " would be added!");
                }
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        System.out.println("url name count: " + url.getNameCount());
        url.forEach(item -> System.out.println("url name = " + item));
        return true;
    }
}
