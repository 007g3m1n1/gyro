package gyro.lang.ast.condition;

import com.google.common.base.Preconditions;
import gyro.lang.ast.Node;
import gyro.lang.ast.NodeVisitor;
import gyro.parser.antlr4.GyroParser;

public class AndConditionNode extends CompoundConditionNode {

    public AndConditionNode(GyroParser.AndConditionContext context) {
        super(
            Node.create(Preconditions.checkNotNull(context).getChild(0)),
            Node.create(context.getChild(2)));
    }

    @Override
    public <C, R> R accept(NodeVisitor<C, R> visitor, C context) {
        return visitor.visitAndCondition(this, context);
    }

}