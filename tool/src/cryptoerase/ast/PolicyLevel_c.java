package cryptoerase.ast;

import polyglot.ast.Node_c;
import polyglot.util.Position;
import polyglot.util.SerialVersionUID;

public class PolicyLevel_c extends Node_c implements PolicyNode {
    private static final long serialVersionUID = SerialVersionUID.generate();

    private String levelName;

    public PolicyLevel_c(Position pos, String levelName) {
        super(pos);
        this.levelName = levelName;
    }

}
