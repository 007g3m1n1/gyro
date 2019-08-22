package gyro.core.resource;

import java.util.List;

import gyro.core.Type;
import gyro.core.directive.DirectiveProcessor;
import gyro.core.scope.RootScope;
import gyro.lang.ast.Node;
import gyro.lang.ast.block.DirectiveNode;

@Type("type-description")
public class TypeDescriptionDirectiveProcessor extends DirectiveProcessor<RootScope> {

    @Override
    public void process(RootScope scope, DirectiveNode node) {
        List<Node> arguments = validateArguments(node, 2, 2);
        String type = (String) scope.getEvaluator().visit(arguments.get(0), scope);

        scope.getSettings(DescriptionSettings.class).getTypeDescriptions().put(type, arguments.get(1));
    }

}
