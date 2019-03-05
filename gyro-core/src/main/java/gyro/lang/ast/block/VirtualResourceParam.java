package gyro.lang.ast.block;

import gyro.parser.antlr4.BeamParser;

public class VirtualResourceParam {

    private final String name;

    public VirtualResourceParam(BeamParser.VirtualResourceParamContext context) {
        name = context.IDENTIFIER().getText();
    }

    public String getName() {
        return name;
    }

}