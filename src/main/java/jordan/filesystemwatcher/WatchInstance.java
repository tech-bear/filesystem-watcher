package jordan.filesystemwatcher;

import jordan.filesystemwatcher.config.xml.Filter;
import jordan.filesystemwatcher.config.xml.Watch;
import jordan.filesystemwatcher.event.FilesystemEvent;
import jordan.filesystemwatcher.event.FilesystemEventType;

import java.io.IOException;
import java.nio.file.*;

/**
 * Created by Jordan-admin on 8/25/2015.
 */
public class WatchInstance implements Runnable {
    WatchService watchSvc;
    Watch watch;
    Path path;
    Filter filter;
    boolean running = false;

    static final int MAX_SLEEP_INTERVAL = 5 * 1000;

    public WatchInstance(Watch watch, Path path, Filter filter) throws IOException {
        watchSvc = FileSystems.getDefault().newWatchService();
        this.watch = watch;
        this.path = path;
        this.filter = filter;

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
                    Path filename = ev.context();

                    System.out.println("Creating FileSystemEvent.... " + FilesystemEvent.createFilesystemEvent(filename, this));
                    // todo: this is where we should filter by extension, then handle appropriately
                    System.out.println(kind.name() + "\t(" + ev.count() + ")\t: " + filename);
/*
                        // Verify that the new
                        //  file is a text file.
                        try {
                            // Resolve the filename against the directory.
                            // If the filename is "test" and the directory is "foo",
                            // the resolved name is "test/foo".
                            Path child = dir.resolve(filename);
                            if (!Files.probeContentType(child).equals("text/plain")) {
                                System.err.format("New file '%s'" +
                                        " is not a plain text file.%n", filename);
                                continue;
                            }
                        } catch (IOException x) {
                            System.err.println(x);
                            continue;
                        }

                        // Email the file to the
                        //  specified email alias.
                        System.out.format("Emailing file %s%n", filename);
                        //Details left to reader....
                    }
*/
                    // Reset the key -- this step is critical if you want to
                    // receive further watch events.  If the key is no longer valid,
                    // the directory is inaccessible so exit the loop.
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
