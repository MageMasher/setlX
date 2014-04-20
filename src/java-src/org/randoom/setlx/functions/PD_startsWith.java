package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.utilities.State;

import java.util.List;

/**
 * startsWith(string, prefix) : Return true if string starts with prefix.
 */
public class PD_startsWith extends PreDefinedProcedure {
    /** Definition of the PreDefinedProcedure `startsWith'. */
    public final static PreDefinedProcedure DEFINITION = new PD_startsWith();

    private PD_startsWith() {
        super();
        addParameter("string");
        addParameter("prefix");
    }

    @Override
    public Value execute(final State state, final List<Value> args, final List<Value> writeBackVars) throws IncompatibleTypeException {
        final Value string = args.get(0);
        final Value prefix = args.get(1);
        if ( ! (string instanceof SetlString)) {
            throw new IncompatibleTypeException(
                "String-argument '" + string.toString(state) + "' is not a string."
            );
        }

        if ( ! (prefix instanceof SetlString)) {
            throw new IncompatibleTypeException(
                "Prefix-argument '" + prefix.toString(state) + "' is not a string."
            );
        }


        if (string.getUnquotedString(state).startsWith(prefix.getUnquotedString(state))) {
            return SetlBoolean.TRUE;
        } else {
            return SetlBoolean.FALSE;
        }
    }

}

