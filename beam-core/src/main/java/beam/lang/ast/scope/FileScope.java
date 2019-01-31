package beam.lang.ast.scope;

import beam.core.LocalStateBackend;
import beam.lang.StateBackend;

import java.util.ArrayList;
import java.util.List;

public class FileScope extends Scope {

    private final String path;
    private FileScope state;
    private final List<FileScope> imports = new ArrayList<>();
    private StateBackend stateBackend = new LocalStateBackend();

    public FileScope(Scope parent, String path) {
        super(parent);

        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public FileScope getState() {
        if (state == null) {
            state = new FileScope(getGlobalScope(), getPath());
        }

        return state;
    }

    public void setState(FileScope state) {
        this.state = state;
    }

    public List<FileScope> getImports() {
        return imports;
    }

    public StateBackend getStateBackend() {
        return stateBackend;
    }

    public void setStateBackend(StateBackend stateBackend) {
        this.stateBackend = stateBackend;
    }

}
