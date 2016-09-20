package org.atl.engine.vm;

/**
 * @author Fr�d�ric Jouault
 */
public class ASMInstructionWithOperand extends ASMInstruction {

	public ASMInstructionWithOperand(String mnemonic, String operand) {
		super(mnemonic);
		this.operand = operand;
	}

	public String getOperand() {
		return operand;
	}

	public void setOperand(String operand) {
		this.operand = operand;
	}

	public String toString() {
		return mnemonic + " " + operand;
	}

	private String operand;
}

