package org.eclipse.m2m.atl.engine.emfvm.parallelvm;

import org.eclipse.m2m.atl.engine.emfvm.ASMOperation;
import org.eclipse.m2m.atl.engine.emfvm.lib.ASMModule;
import org.eclipse.m2m.atl.engine.emfvm.lib.AbstractStackFrame;
import org.eclipse.m2m.atl.engine.emfvm.lib.ExecEnv;
import org.eclipse.m2m.atl.engine.emfvm.lib.Operation;
import org.eclipse.m2m.atl.engine.emfvm.parallelvm.lib.TExecEnv;

public class TStackFrame extends AbstractStackFrame{
	
	/** The execution environment. */
	protected TExecEnv texecEnv;

	/** only initialized when there is a VMException to avoid performance impact. */
	private int pc = -1;
	
	@Override
	public TExecEnv getExecEnv() {
		return this.texecEnv;
	}

	/**
	 * Creates a new {@link TStackFrame} with the given parameters.
	 * 
	 * @param execEnv
	 *            the execution environment
	 * @param asmModule
	 *            the transformation module
	 * @param operation
	 *            the main operation
	 */
	public TStackFrame(TExecEnv execEnv, ASMModule asmModule, Operation operation) {
		super(execEnv, asmModule, operation);
		this.texecEnv = execEnv;
	}

	/**
	 * Creates a new TStackFrame.
	 * 
	 * @param caller
	 *            the parent stack frame
	 * @param operation
	 *            the operation
	 */
	protected TStackFrame(TStackFrame caller, Operation operation) {
		super(caller, operation);
		if (caller != null) {
			this.texecEnv = (TExecEnv) caller.execEnv;
		}
	}

	/**
	 * Creates an empty TStackFrame which refers to its {@link ExecEnv}.
	 * 
	 * @param execEnv
	 *            the {@link ExecEnv}
	 */
	public TStackFrame(TExecEnv execEnv) {
		super(execEnv);
		this.texecEnv = execEnv;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.m2m.atl.engine.emfvm.lib.AbstractStackFrame#newFrame(org.eclipse.m2m.atl.engine.emfvm.lib.Operation)
	 */
	@Override
	public AbstractStackFrame newFrame(Operation operation) {
		return new TStackFrame(this, operation);
	}

	/**
	 * Returns the current location.
	 * 
	 * @return the current location
	 */
	protected String getStringLocation() {
		String ret = ((ASMOperation)operation).resolveLineNumber(pc);

		if (ret == null) {
			ret = ""; //$NON-NLS-1$
		}
		ret += "#" + pc; //$NON-NLS-1$
		return ret;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.m2m.atl.engine.emfvm.lib.AbstractStackFrame#getLocation()
	 */
	@Override
	public int getLocation() {
		return pc;
	}

	/**
	 * Returns the variable name at the given slot.
	 * 
	 * @param slot
	 *            the slot
	 * @return the variable name at the given slot
	 */
	public String resolveVariableName(int slot) {
		return getOperation().resolveVariableName(slot, pc);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuffer ret = new StringBuffer();
		if (operation instanceof ASMOperation) {
			ret.append("\tat "); //$NON-NLS-1$
			ret.append(((ASMOperation)operation).getName());
			ret.append("#" + getLocation()); //$NON-NLS-1$
			ret.append('(');
			ret.append(((ASMOperation)operation).getASM().getName() + ".atl"); //$NON-NLS-1$

			String location = getStringLocation();
			if (location != null) {
				if (location.matches("[0-9]*:[0-9]*-[0-9]*:[0-9]*#[0-9]*")) { //$NON-NLS-1$
					ret.append('[' + location.split("#")[0] + ']'); //$NON-NLS-1$
				}
			}
			ret.append(')');
			ret.append("\n\t\tlocal variables: "); //$NON-NLS-1$
			boolean first = true;
			ASMOperation ao = (ASMOperation)operation;
			for (int i = 0; i < ao.getMaxLocals(); i++) {
				String varName = ao.resolveVariableName(i, pc);
				if (varName != null) {
					if (!first) {
						ret.append(", "); //$NON-NLS-1$
					}
					first = false;
					ret.append(varName);
					ret.append('=');
					ret.append(getExecEnv().toPrettyPrintedString(localVars[i]));
				}
			}
		} else {
			ret.append("<native>"); //$NON-NLS-1$
		}
		if (caller != null) {
			ret.append('\n');
			ret.append(caller.toString());
		}
		return ret.toString();
	}

	public int getPc() {
		return pc;
	}

	public void setPc(int pc) {
		this.pc = pc;
	}

}
