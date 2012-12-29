package org.randoom.setlx.functions;

import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

import java.util.List;

// isString(value)         : test if value-type is string

public class PD_isString extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_isString();

    private PD_isString() {
        super("isString");
        addParameter("value");
    }

    public Value execute(final State state, List<Value> args, List<Value> writeBackVars) {
        return args.get(0).isString();
    }
}
