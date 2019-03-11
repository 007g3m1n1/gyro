package gyro.lang.ast.query;

import com.google.common.collect.ImmutableList;
import gyro.parser.antlr4.BeamParser.QueryExpressionContext;

import java.util.List;

public abstract class AbstractCompoundQuery extends Query {

    private final List<Query> children;

    public AbstractCompoundQuery(QueryExpressionContext context) {
        ImmutableList.Builder<Query> list = ImmutableList.builder();
        addChildren(list, Query.create(context.getChild(0)));
        addChildren(list, Query.create(context.getChild(2)));
        children = list.build();
    }

    public List<Query> getChildren() {
        return children;
    }

    private void addChildren(ImmutableList.Builder<Query> list, Query child) {
        if (getClass().isInstance(child)) {
            list.addAll(((AbstractCompoundQuery) child).getChildren());
        } else {
            list.add(child);
        }
    }
}
