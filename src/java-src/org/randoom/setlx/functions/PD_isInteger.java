package org.randoom.setlx.functions;

import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

import java.util.List;

// isInteger(value)        : test if value-type is integer

public class PD_isInteger extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_isInteger();

    private PD_isInteger() {
        super("isInteger");
        addParameter("value");
    }

    public Value execute(final State state, List<Value> args, List<Value> writeBackVars) {
        return args.get(0).isInteger();
    }
}
