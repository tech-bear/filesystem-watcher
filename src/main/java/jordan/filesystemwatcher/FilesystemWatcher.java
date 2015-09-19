package jordan.filesystemwatcher;

import jordan.filesystemwatcher.config.ConfigParser;
import jordan.filesystemwatcher.config.xml.Filter;
import jordan.filesystemwatcher.config.xml.Watch;
import jordan.filesystemwatcher.event.FilesystemEvent;
import jordan.filesystemwatcher.event.FilesystemEventGenerator;
import jordan.filesystemwatcher.event.FilesystemEventListener;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.io.File;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by Jordan-admin on 8/24/2015.
 */
public class FilesystemWatcher extends Thread implements FilesystemEventGenerator<FilesystemEvent> {
    private ExecutorService threadGroup;
    private Collection<WatchInstance> watchInstances;
    private Collection<FilesystemEventListener<FilesystemEvent>> eventListeners;

    public FilesystemWatcher() {
        BasicThreadFactory threadFactory = new BasicThreadFactory.Builder()
                .namingPattern("WatchInstance-%d")
                .daemon(false)
                .build();
        threadGroup = Executors.newCachedThreadPool(threadFactory);

        watchInstances = new LinkedHashSet<>();
        eventListeners = new LinkedHashSet<>();
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
            watchInstances.add(new WatchInstance(watch, path, filter));

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

    @Override
    public void run() {
        System.out.println("FilesystemWatcher - Starting event loop with " + countEventListeners() + " listeners");

        while (!Thread.interrupted()) {
            try {
                threadGroup.invokeAll(watchInstances).forEach(events -> {
                    try {
                        if(events.get().size() > 0) {
                            events.get().forEach(this::broadcastEvent);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        // this is akin to WatchInvalidatedException being thrown
                        System.err.println(e.getMessage());
                        e.printStackTrace();
                    }
                });
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void waitForWatchers() {
        try {
            threadGroup.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
        } catch (Exception ignore) {
        }
    }

    @Override
    public void addEventListener(FilesystemEventListener<FilesystemEvent> listener) {
        eventListeners.add(listener);
    }

    @Override
    public void removeEventListener(FilesystemEventListener<FilesystemEvent> listener) {
        eventListeners.remove(listener);
    }

    @Override
    public int countEventListeners() {
        return eventListeners.size();
    }

    @Override
    public void broadcastEvent(FilesystemEvent event) {
        eventListeners.forEach(listener -> threadGroup.execute(() -> listener.handleEvent(event)));
    }
}
