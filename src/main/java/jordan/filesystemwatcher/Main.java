package jordan.filesystemwatcher;

import jordan.filesystemwatcher.config.ConfigParser;
import jordan.filesystemwatcher.event.FilesystemEvent;

class Main {

    public static void main(String[] args) {

        ConfigParser parser = new ConfigParser("src/main/resources/watcher-properties.xml");
        System.out.println("Config: " + parser.toString());

        FilesystemWatcher w = new FilesystemWatcher();
        CommandQueue<FilesystemEvent> cmdQueue = new CommandQueue<>();

        w.configure(parser);
        w.addEventListener(cmdQueue);

        cmdQueue.start();
        w.start();
        w.waitForWatchers();
    }
}
