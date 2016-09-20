package org.eclipse.m2m.atl.engine.emfvm.parallelvm;

import org.eclipse.m2m.atl.engine.emfvm.Bytecode;
import org.eclipse.m2m.atl.engine.emfvm.Messages;
import org.eclipse.m2m.atl.engine.emfvm.VMException;

public class TBytecode extends Bytecode{
	
	/** Call a procedure (i.e., an operation with no returned value) to be executed in a thread */
	public static final int TCALL = 24;
	
	/** List of codes. */
	public static final String[] OPCODENAMES = {"push", //$NON-NLS-1$
			"pushi", //$NON-NLS-1$
			"pushd", //$NON-NLS-1$
			"pusht", //$NON-NLS-1$
			"pushf", //$NON-NLS-1$
			"call", //$NON-NLS-1$
			"load", //$NON-NLS-1$
			"store", //$NON-NLS-1$
			"new", //$NON-NLS-1$
			"iterate", //$NON-NLS-1$
			"enditerate", //$NON-NLS-1$
			"dup", //$NON-NLS-1$
			"set", //$NON-NLS-1$
			"get", //$NON-NLS-1$
			"pop", //$NON-NLS-1$
			"getasm", //$NON-NLS-1$
			"if", //$NON-NLS-1$
			"goto", //$NON-NLS-1$
			"swap", //$NON-NLS-1$
			"findme", //$NON-NLS-1$
			"dup_x1", //$NON-NLS-1$
			"delete", //$NON-NLS-1$
			"pcall", //$NON-NLS-1$
			"newin", //$NON-NLS-1$
			"tcall", //$NON-NLS-1$
	};
	
	public TBytecode(String opcode) {
		super(opcode);
	}
	
	/**
	 * Bytecode constructor, for bytecodes which needs an operand.
	 * 
	 * @param opcode
	 *            the bytecode name
	 * @param operand
	 *            the operand
	 */
	public TBytecode(String opcode, String operand) {
		super("push", operand);
		if (opcode.equals("push")) { //$NON-NLS-1$
			this.opcode = PUSH;
			this.operand = operand;
		} else if (opcode.equals("pushi")) { //$NON-NLS-1$
			this.opcode = PUSHI;
			this.operand = Integer.valueOf(operand);
		} else if (opcode.equals("pushd")) { //$NON-NLS-1$
			this.opcode = PUSHD;
			this.operand = Double.valueOf(operand);
		} else if (opcode.equals("call")) { //$NON-NLS-1$
			this.opcode = CALL;
			this.completeOperand = operand;
			this.operand = getOpName(operand);
			this.value = getNbArgs(operand);
		} else if (opcode.equals("load")) { //$NON-NLS-1$
			this.opcode = LOAD;
			this.operand = operand; // for toString
			this.value = Integer.parseInt(operand);
		} else if (opcode.equals("store")) { //$NON-NLS-1$
			this.opcode = STORE;
			this.operand = operand; // for toString
			this.value = Integer.parseInt(operand);
		} else if (opcode.equals("set")) { //$NON-NLS-1$
			this.opcode = SET;
			this.operand = operand;
		} else if (opcode.equals("get")) { //$NON-NLS-1$
			this.opcode = GET;
			this.operand = operand;
		} else if (opcode.equals("if")) { //$NON-NLS-1$
			this.opcode = IF;
			this.operand = operand; // for toString
			this.value = Integer.parseInt(operand);
		} else if (opcode.equals("goto")) { //$NON-NLS-1$
			this.opcode = GOTO;
			this.operand = operand; // for toString
			this.value = Integer.parseInt(operand);
		} else if (opcode.equals("pcall")) { //$NON-NLS-1$
			this.opcode = PCALL;
			this.completeOperand = operand;
			this.operand = getOpName(operand);
			this.value = getNbArgs(operand);
		}else if (opcode.equals("tcall")) { //$NON-NLS-1$
			this.opcode = TCALL;
			this.completeOperand = operand;
			this.operand = getOpName(operand);
			this.value = getNbArgs(operand);
		} else {
			throw new VMException(null, Messages.getString("ByteCode.UNSUPPORTEDOPCODEWARGS", opcode)); //$NON-NLS-1$
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return OPCODENAMES[opcode]
				+ ((completeOperand != null) ? " " + completeOperand : ((operand != null) ? " " + operand : "")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

}
