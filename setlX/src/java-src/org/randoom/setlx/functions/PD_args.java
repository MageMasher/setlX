package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.Value;

import java.util.List;

// args(term)              : get arguments of term

public class PD_args extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_args();

    private PD_args() {
        super("args");
        addParameter("term");
    }

    public SetlList execute(List<Value> args, List<Value> writeBackVars) throws SetlException {
        return args.get(0).arguments();
    }
}
