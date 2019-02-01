package beam.lang.ast.scope;

import beam.core.LocalFileBackend;
import beam.lang.Resource;
import beam.lang.FileBackend;
import beam.lang.plugins.PluginLoader;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FileScope extends Scope {

    private static final FileBackend DEFAULT_FILE_BACKEND = new LocalFileBackend();

    private final String file;
    private FileBackend backend;
    private final List<FileScope> imports = new ArrayList<>();
    private final List<PluginLoader> pluginLoaders = new ArrayList<>();
    private final Map<String, Resource> resources = new LinkedHashMap<>();

    public FileScope(FileScope parent, String file) {
        super(parent);

        if (!file.endsWith(".bcl") && !file.endsWith(".bcl.state")) {
            file += ".bcl";
        }

        if (parent != null) {
            file = Paths.get(parent.getFile()).getParent().resolve(file).toString();
        }

        this.file = file;
    }

    public String getFile() {
        return file;
    }

    public FileBackend getBackend() {
        if (backend == null || !file.endsWith(".bcl.state")) {
            return DEFAULT_FILE_BACKEND;

        } else {
            return backend;
        }
    }

    public void setBackend(FileBackend backend) {
        this.backend = backend;
    }

    public List<FileScope> getImports() {
        return imports;
    }

    public List<PluginLoader> getPluginLoaders() {
        return pluginLoaders;
    }

    public Map<String, Resource> getResources() {
        return resources;
    }

}
