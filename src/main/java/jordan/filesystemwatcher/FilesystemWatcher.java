package jordan.filesystemwatcher;

import jordan.filesystemwatcher.config.ConfigParser;
import jordan.filesystemwatcher.config.xml.Filter;
import jordan.filesystemwatcher.config.xml.Watch;
import jordan.filesystemwatcher.event.FilesystemEvent;
import jordan.filesystemwatcher.event.FilesystemEventListener;

import java.io.File;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by Jordan-admin on 8/24/2015.
 */
public class FilesystemWatcher implements FilesystemEventListener<FilesystemEvent> {
    private ExecutorService threadGroup;

    public FilesystemWatcher() {
        threadGroup = Executors.newCachedThreadPool();
    }

    public void configure(final ConfigParser config) {
        config.getWatchers().forEach(watch -> System.out.println("added " + addWatch(watch) + " watches"));
    }

    public int addWatch(Watch watch) {
        Path url = Paths.get(new File(watch.getDirectory()).getAbsolutePath());
        return addWatch(watch, url);
    }

    private int addWatch(Watch watch, Path path) {
        int sum = 0;
        for (Filter filter : watch.getFilter()) {
            sum += addFilter(watch, path, filter);
        }
        return sum;
    }

    private int addFilter(Watch watch, Path path, Filter filter) {
        int sum = 0;
        try {
            final WatchInstance wi = new WatchInstance(watch, path, filter);
            wi.addEventListener(this);

            threadGroup.execute(wi);
            System.out.println(path + " : adding filter " + filter.getExtension());
            sum++;
        } catch (Exception e) {
            System.err.println(path + " : adding filter " + filter.getExtension() + " failed! " + e.getMessage());
        }

        if (watch.isRecursive()) {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
                Iterator<Path> iter = stream.iterator();
                while (iter.hasNext()) {
                    final Path p = iter.next();
                    if (Files.isDirectory(p)) {
                        sum += addFilter(watch, p, filter);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
        }

        return sum;
    }

    public void waitForWatchers() {
        try {
            threadGroup.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
        } catch (Exception ignore) {
        }
    }

    @Override
    public void handleEvent(FilesystemEvent event) {
        System.out.println("received FilesystemEvent! " + event);
    }
}
