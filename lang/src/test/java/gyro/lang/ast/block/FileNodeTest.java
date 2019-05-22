package gyro.lang.ast.block;

import java.util.List;

import gyro.lang.ast.AbstractNodeTest;
import gyro.lang.ast.Node;
import gyro.lang.ast.PairNode;
import gyro.lang.ast.control.ForNode;
import gyro.lang.ast.control.IfNode;
import gyro.parser.antlr4.GyroParser;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class FileNodeTest extends AbstractNodeTest<FileNode> {

    @Test
    void constructorContext() {
        FileNode node = new FileNode(parse("foo::bar qux\nend\nfor foo in ['bar']\nend\nif 'foo'\nend\nfoo: 'bar'", GyroParser::file));
        List<Node> body = node.getBody();

        assertThat(body).hasSize(4);
        assertThat(body.get(0)).isInstanceOf(ResourceNode.class);
        assertThat(body.get(1)).isInstanceOf(ForNode.class);
        assertThat(body.get(2)).isInstanceOf(IfNode.class);
        assertThat(body.get(3)).isInstanceOf(PairNode.class);
    }

}