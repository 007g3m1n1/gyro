package gyro.core.command;

import gyro.core.GyroCore;
import gyro.lang.ast.block.DirectiveNode;
import io.airlift.airline.Command;
import io.airlift.airline.Help;
import io.airlift.airline.model.MetadataLoader;

import java.util.List;
import java.util.stream.Collectors;

@Command(name = "remove", description = "Remove one or more plugins.")
public class PluginRemoveCommand extends PluginCommand {

    @Override
    protected void executeSubCommand() throws Exception {
        if (getPlugins().isEmpty()) {
            Help.help(MetadataLoader.loadCommand(PluginRemoveCommand.class));
            return;
        }

        List<DirectiveNode> removeNodes = getPluginNodes()
            .stream()
            .filter(this::pluginNodeExist)
            .collect(Collectors.toList());

        List<DirectiveNode> invalidNodes = getPluginNodes()
            .stream()
            .filter(f -> !pluginNodeExist(f))
            .collect(Collectors.toList());

        StringBuilder sb = new StringBuilder();
        int lineNumber = 0;
        for (String line : load()) {
            boolean skipLine = false;
            for (DirectiveNode pluginNode : removeNodes) {
                if (lineNumber >= pluginNode.getStartLine() && lineNumber <= pluginNode.getStopLine()) {
                    skipLine = true;
                }
            }

            if (!skipLine) {
                sb.append(line);
                sb.append("\n");
            }

            lineNumber++;
        }

        save(sb.toString());

        GyroCore.ui().write("\n");

        invalidNodes.stream()
            .map(this::toPluginString)
            .map(p -> String.format("@|bold %s|@ was not installed.%n", p))
            .forEach(GyroCore.ui()::write);

        removeNodes.stream()
            .map(this::toPluginString)
            .map(p -> String.format("@|bold %s|@ has been removed.%n", p))
            .forEach(GyroCore.ui()::write);
    }

}