package jordan.filesystemwatcher;

import jordan.filesystemwatcher.config.ConfigParser;

class Main {

    public static void main(String[] args) {

        ConfigParser parser = new ConfigParser("src/main/resources/watcher-properties.xml");
        System.out.println("Config: " + parser.toString());

        FilesystemWatcher w = new FilesystemWatcher();
        w.configure(parser);
        w.start();
        w.waitForWatchers();
    }
}
