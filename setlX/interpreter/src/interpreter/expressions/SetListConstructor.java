package interpreter.expressions;

import interpreter.exceptions.SetlException;
import interpreter.exceptions.UndefinedOperationException;
import interpreter.types.SetlList;
import interpreter.types.SetlOm;
import interpreter.types.SetlSet;
import interpreter.types.Value;
import interpreter.utilities.Constructor;
import interpreter.utilities.Environment;
import interpreter.utilities.ExplicitList;

import java.util.List;

public class SetListConstructor extends Expr {
    public final static int LIST  = 23;
    public final static int SET   = 42;

    private int         mType;
    private Constructor mConstructor;

    public SetListConstructor(int type, Constructor constructor) {
        mType        = type;
        mConstructor = constructor;
    }

    public Value evaluate() throws SetlException {
        Value result = null;
        if (mType == SET) {
            SetlSet set = new SetlSet();
            if (mConstructor != null) {
                mConstructor.fillCollection(set);
            }
            result = set;
        } else if (mType == LIST) {
            SetlList list = new SetlList();
            if (mConstructor != null) {
                mConstructor.fillCollection(list);
            }
            list.compress();
            result = list;
        } else {
            result = SetlOm.OM;
        }
        return result;
    }

    // sets the variables used to form this list to the variables from the list given as a parameter
    public boolean setIds(SetlList list) throws UndefinedOperationException {
        if (mType == LIST) {
            return mConstructor.setIds(list);
        } else {
            throw new UndefinedOperationException("Error in '" + this + "':\n"
                                            +     "Only explicit lists of variables can be used as targets for list assignments.");
        }
    }

    public String toString(int tabs) {
        String r;
        if (mType == SET) {
            r = "{";
        } else if (mType == LIST) {
            r = "[";
        } else {
            r = "";
        }
        if (mConstructor != null) {
            r += mConstructor.toString(tabs);
        }
        if (mType == SET) {
            r += "}";
        } else if (mType == LIST) {
            r += "]";
        }
        return r;
    }
}

