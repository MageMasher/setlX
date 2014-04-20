package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.FileNotWriteableException;
import org.randoom.setlx.types.CollectionValue;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.WriteFile;
import org.randoom.setlx.utilities.State;

import java.util.List;

/**
 * writeFile(fileName, content)  : Writes a list of strings into a file, each
 *                                 string representing a single line.
 */
public class PD_writeFile extends PreDefinedProcedure {
    /** Definition of the PreDefinedProcedure `writeFile'. */
    public final static PreDefinedProcedure DEFINITION = new PD_writeFile();

    /**
     * Create a new writeFile function.
     */
    protected PD_writeFile() {
        super();
        addParameter("fileName");
        addParameter("contents");
    }

    @Override
    public Value execute(final State state, final List<Value> args, final List<Value> writeBackVars) throws IncompatibleTypeException, FileNotWriteableException {
        return exec(state, args, false);
    }

    /**
     * Execute writeFile() functionality.
     *
     * @param state          Current state of the running setlX program.
     * @param args           Values of the call-parameters in the same order as defined.
     * @param append         Defines if file should be appended, instead of newly created.
     * @return               SetlBoolean.TRUE if writing was successful.
     * @throws IncompatibleTypeException Thrown in case the wrong parameters are supplied.
     * @throws FileNotWriteableException File to be written cannot be written.
     */
    protected Value exec(final State state, final List<Value> args, final boolean append) throws IncompatibleTypeException, FileNotWriteableException {
        final Value  fileArg = args.get(0);
        if ( ! (fileArg instanceof SetlString)) {
            throw new IncompatibleTypeException("FileName-argument '" + fileArg.toString(state) + "' is not a string.");
        }
        final Value contentArg = args.get(1);

        // get name of file to be written
        final String    fileName = fileArg.getUnquotedString(state);
        // get content to be written into the file
        CollectionValue content  = null;
        if (contentArg instanceof CollectionValue && ! (contentArg instanceof Term || contentArg instanceof SetlString)) {
            content = (CollectionValue) contentArg;
        } else {
            content = new SetlList(1);
            content.addMember(state, contentArg);
        }

        final boolean verbose = state.isPrintVerbose();
        state.setPrintVerbose(true);
        final String  endl    = state.getEndl();
        state.setPrintVerbose(verbose);

        // write file
        final StringBuilder sb = new StringBuilder();
        for (final Value v : content) {
            v.appendUnquotedString(state, sb, 0);
            sb.append(endl);
        }
        WriteFile.writeToFile(state, sb.toString(), fileName, append);

        return SetlBoolean.TRUE;
    }
}

