package org.randoom.setlx.utilities;

import org.randoom.setlx.types.Value;

import java.util.HashMap;
import java.util.Map;

public class MatchResult {
    // Trace all assignments. MAY ONLY BE SET BY ENVIRONMENT CLASS!
    public  static boolean              sTraceAssignments = false;

    private        boolean              mMatches;     // does the term match?
    private final  Map<String, Value>   mVarBindings; // variables to set when term matches

    public MatchResult(final boolean matches) {
        mMatches        = matches;
        mVarBindings    = new HashMap<String, Value>();
    }

    public void setMatches(final boolean matches) {
        mMatches = matches;
    }

    public boolean isMatch() {
        return mMatches;
    }

    public boolean hasBindings() {
        return mVarBindings.size() > 0;
    }

    public void addBinding(final String id, final Value value) {
        mVarBindings.put(id, value);
    }

    public void addBindings(final MatchResult otherResult) {
        if (otherResult.mMatches) {
            mVarBindings.putAll(otherResult.mVarBindings);
        }
    }

    public void setAllBindings() {
        for (final Map.Entry<String, Value> entry : mVarBindings.entrySet()) {
            VariableScope.putValue(entry.getKey(), entry.getValue().clone());

            if (sTraceAssignments) {
                Environment.outWriteLn("~< Trace (match): " + entry.getKey() + " := " + entry.getValue() + " >~");
            }
        }
    }
}

