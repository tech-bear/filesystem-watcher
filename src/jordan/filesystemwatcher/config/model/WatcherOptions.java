package jordan.filesystemwatcher.config.model;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Created by Jordan on 3/29/2015.
 */
public class WatcherOptions {
    private String  directory;

    public void setDirectory(String d) {
        directory = d;
    }

    public String getDirectory() {
        return directory;
    }

    private boolean recursive;

    public void setRecursive(boolean v) {
        recursive = v;
    }

    public boolean isRecursive() {
        return recursive;
    }

    private final Collection<FilterOptions> filters;

    public Collection<FilterOptions> getFilters() {
        return filters;
    }

    public WatcherOptions() {
        filters = new LinkedList<>();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Watcher:\n");
        sb.append("\tdirectory = ");
        sb.append(directory);
        sb.append("\n");
        sb.append("\trecursive = ");
        sb.append(recursive);
        sb.append("\n");
        sb.append("\tfilters:\n");
        for(FilterOptions filter : filters) {
            sb.append("\t\t");
            sb.append(filter.toString());
            sb.append("\n");
        }
        return sb.toString();
    }
}
