package jordan.filesystemwatcher.config.model;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Created by Jordan on 3/29/2015.
 */
public class FilterOptions {
    private String extention;

    public void setExtention(String ext) {
        extention = ext;
    }

    public String getExtention() {
        return extention;
    }

    private final Collection<String> commands;

    public Collection<String> getCommands() {
        return commands;
    }

    public FilterOptions() {
        commands = new LinkedList<>();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Filter:\n");
        sb.append("\textention = ");
        sb.append(extention);
        sb.append("\n");
        sb.append("\tCommands:");
        sb.append("\n");
        for(String com : commands) {
            sb.append("\t\t");
            sb.append("command = ");
            sb.append(com);
            sb.append("\n");
        }
        return sb.toString();
    }
}
