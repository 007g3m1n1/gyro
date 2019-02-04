package beam.commands;

import beam.core.BeamCore;
import beam.core.diff.ChangeType;
import beam.core.diff.ResourceDiff;
import beam.lang.ast.scope.RootScope;
import beam.lang.ast.scope.State;
import io.airlift.airline.Command;
import io.airlift.airline.Option;

import java.util.List;
import java.util.Set;

@Command(name = "up", description = "Updates all resources to match the configuration.")
public class UpCommand extends AbstractConfigCommand {

    @Option(name = { "--skip-refresh" })
    public boolean skipRefresh;

    @Override
    public void doExecute(RootScope current, RootScope pending) throws Exception {
        BeamCore.ui().write("\n@|bold,white Looking for changes...\n\n|@");
        List<ResourceDiff> diffs = core().diff(current, pending, !skipRefresh);
        State state = new State(pending);

        Set<ChangeType> changeTypes = core().writeDiffs(diffs);

        boolean hasChanges = false;
        if (changeTypes.contains(ChangeType.CREATE) || changeTypes.contains(ChangeType.UPDATE)) {
            hasChanges = true;

            if (BeamCore.ui().readBoolean(Boolean.FALSE, "\nAre you sure you want to create and/or update resources?")) {
                BeamCore.ui().write("\n");
                core().createOrUpdate(state, diffs);
            }
        }

        if (changeTypes.contains(ChangeType.DELETE)) {
            hasChanges = true;

            if (BeamCore.ui().readBoolean(Boolean.FALSE, "\nAre you sure you want to delete resources?")) {
                BeamCore.ui().write("\n");
                core().delete(state, diffs);
            }
        }

        if (changeTypes.contains(ChangeType.REPLACE)) {
            BeamCore.ui().write("\n");

            hasChanges = true;
        }

        if (!hasChanges) {
            BeamCore.ui().write("\n@|bold,green No changes.|@\n\n");
        }

    }

}
