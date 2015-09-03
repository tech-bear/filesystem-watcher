package jordan.filesystemwatcher;

import jordan.filesystemwatcher.config.xml.Filter;
import jordan.filesystemwatcher.config.xml.Watch;
import jordan.filesystemwatcher.event.FilesystemEvent;
import jordan.filesystemwatcher.event.FilesystemEventGenerator;
import jordan.filesystemwatcher.event.FilesystemEventListener;
import jordan.filesystemwatcher.event.FilesystemEventType;

import java.io.IOException;
import java.nio.file.*;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by Jordan-admin on 8/25/2015.
 */
public class WatchInstance implements Runnable, FilesystemEventGenerator<FilesystemEvent> {
    WatchService watchSvc;
    Watch watch;
    Path path;
    Filter filter;
    boolean running = false;

    Set<FilesystemEventListener<FilesystemEvent>> eventListeners;

    static final int MAX_SLEEP_INTERVAL = 5 * 1000;

    public WatchInstance(Watch watch, Path path, Filter filter) throws IOException {
        watchSvc = FileSystems.getDefault().newWatchService();
        this.watch = watch;
        this.path = path;
        this.filter = filter;

        eventListeners = new LinkedHashSet<>();

        for (FilesystemEventType type : FilesystemEventType.fromString(filter.getType())) {
            this.path.register(watchSvc, type.getEventKind());
        }
    }

    public WatchService getWatchSvc() {
        return watchSvc;
    }

    public Watch getWatch() {
        return watch;
    }

    public Filter getFilter() {
        return filter;
    }

    public Path getPath() {
        return path;
    }

    public boolean isRunning() {
        return running;
    }

    public void cancel() {
        running = false;
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
        new Thread(() -> eventListeners.forEach(listener -> listener.handleEvent(event))).start();
    }

    @Override
    public void run() {
        running = true;

        while (running) {
            WatchKey k;
            try {
                k = watchSvc.poll();
                if (k == null) {
                    Thread.sleep(Math.min(filter.getTimerVal().longValue(), MAX_SLEEP_INTERVAL));
                    continue;
                }

                for (WatchEvent<?> event : k.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();

                    // This key is registered only
                    // for ENTRY_CREATE events,
                    // but an OVERFLOW event can
                    // occur regardless if events
                    // are lost or discarded.
                    if (kind == StandardWatchEventKinds.OVERFLOW) {
                        System.out.println("OVERFLOW event triggered!");
                        Thread.sleep(Math.min(filter.getTimerVal().longValue(), MAX_SLEEP_INTERVAL));
                        continue;
                    }

                    // The filename is the
                    // context of the event.
                    @SuppressWarnings("unchecked")
                    WatchEvent<Path> ev = (WatchEvent<Path>) event;

                    Path filename = Paths.get(getPath().toAbsolutePath().toString(), ev.context().toString());

                    // don't bother processing directories
                    if(filename.toFile().isFile()) {
                        // System.out.println("Creating FileSystemEvent.... " + FilesystemEvent.createFilesystemEvent(filename, this));
                        // todo: this is where we should filter by extension, then handle appropriately
                        System.out.println(kind.name() + "\t(" + ev.count() + ")\t: " + filename + " | filter: " + filter.getExtension());
                        System.out.println("directory? " + filename.toFile().isDirectory() + " | file? " + filename.toFile().isFile());

                        // if using the wildcard, all files / folders will be caught
                        boolean triggerEvent = "*".equalsIgnoreCase(filter.getExtension());

                        if (!triggerEvent) {
                            triggerEvent = ev.context().endsWith(filter.getExtension());
                        }

                        if (triggerEvent) {
                            broadcastEvent(FilesystemEvent.createFilesystemEvent(filename, this));
                        }
                    }
                    boolean valid = k.reset();
                    if (!valid) {
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
