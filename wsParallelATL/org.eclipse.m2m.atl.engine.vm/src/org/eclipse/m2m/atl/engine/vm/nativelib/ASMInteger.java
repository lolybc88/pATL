/*******************************************************************************
 * Copyright (c) 2004 INRIA.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * 	   Frederic Jouault (INRIA) - initial API and implementation
 *******************************************************************************/
package org.eclipse.m2m.atl.engine.vm.nativelib;

import org.eclipse.m2m.atl.engine.vm.StackFrame;

/**
 * @author <a href="mailto:frederic.jouault@univ-nantes.fr">Frederic Jouault</a>
 */
public class ASMInteger extends ASMNumber {

	public static ASMOclType myType = new ASMOclSimpleType("Integer", ASMReal.myType);

	public ASMInteger(int s) {
		super(myType);
		this.s = s;
	}

	public String toString() {
		return "" + s;
	}

	public int getSymbol() {
		return s;
	}

	public double asDouble() {
		return s;
	}

	public boolean equals(Object o) {
		return (o instanceof ASMInteger) && (((ASMInteger)o).s == s);
	}

	public int hashCode() {
		return s;
	}

	// Native Operations below

	public static ASMInteger abs(StackFrame frame, ASMInteger self) {
		return new ASMInteger((self.s < 0) ? -self.s : self.s);
	}

	public static ASMInteger mod(StackFrame frame, ASMInteger self, ASMInteger o) {
		return new ASMInteger(self.s % o.s);
	}

	public static ASMInteger div(StackFrame frame, ASMInteger self, ASMInteger o) {
		return new ASMInteger(self.s / o.s);
	}

	public static ASMInteger max(StackFrame frame, ASMInteger self, ASMInteger o) {
		return new ASMInteger(Math.max(self.s, o.s));
	}

	public static ASMInteger min(StackFrame frame, ASMInteger self, ASMInteger o) {
		return new ASMInteger(Math.min(self.s, o.s));
	}

	/*
	 * TODO: operation overloading is not supported yet. public static ASMInteger operatorMinus(StackFrame
	 * frame, ASMInteger self) { return new ASMInteger(-self.s); }
	 */
	public static ASMNumber operatorMinus(StackFrame frame, ASMInteger self, ASMNumber o) {
		if (o instanceof ASMInteger) {
			return new ASMInteger(self.s - ((ASMInteger)o).s);
		}
		return new ASMReal(self.s - o.asDouble());
	}

	public static ASMNumber operatorPlus(StackFrame frame, ASMInteger self, ASMNumber o) {
		if (o instanceof ASMInteger) {
			return new ASMInteger(self.s + ((ASMInteger)o).s);
		}
		return new ASMReal(self.s + o.asDouble());
	}

	public static ASMNumber operatorMul(StackFrame frame, ASMInteger self, ASMNumber o) {
		if (o instanceof ASMInteger) {
			return new ASMInteger(self.s * ((ASMInteger)o).s);
		}
		return new ASMReal(self.s * o.asDouble());
	}

	public static ASMReal operatorDiv(StackFrame frame, ASMInteger self, ASMNumber o) {// Second parameter
																						// should be ASMReal
		return new ASMReal(self.s / o.asDouble());
	}

	public static ASMBoolean operatorEQ(StackFrame frame, ASMInteger self, ASMOclAny o) {
		if (o instanceof ASMInteger) {
			return new ASMBoolean(self.s == ((ASMInteger)o).s);
		} else if (o instanceof ASMNumber) {
			return new ASMBoolean(self.s == ((ASMNumber)o).asDouble());
		} else {
			return new ASMBoolean(false);
		}
	}

	public static ASMBoolean operatorNE(StackFrame frame, ASMInteger self, ASMOclAny o) {
		if (o instanceof ASMInteger) {
			return new ASMBoolean(self.s != ((ASMInteger)o).s);
		} else if (o instanceof ASMNumber) {
			return new ASMBoolean(self.s != ((ASMNumber)o).asDouble());
		} else {
			return new ASMBoolean(true);
		}
	}

	public static ASMBoolean operatorLT(StackFrame frame, ASMInteger self, ASMNumber o) {
		if (o instanceof ASMInteger) {
			return new ASMBoolean(self.s < ((ASMInteger)o).s);
		}
		return new ASMBoolean(self.s < o.asDouble());
	}

	public static ASMBoolean operatorLE(StackFrame frame, ASMInteger self, ASMNumber o) {
		if (o instanceof ASMInteger) {
			return new ASMBoolean(self.s <= ((ASMInteger)o).s);
		}
		return new ASMBoolean(self.s <= o.asDouble());
	}

	public static ASMBoolean operatorGT(StackFrame frame, ASMInteger self, ASMNumber o) {
		if (o instanceof ASMInteger) {
			return new ASMBoolean(self.s > ((ASMInteger)o).s);
		}
		return new ASMBoolean(self.s > o.asDouble());
	}

	public static ASMBoolean operatorGE(StackFrame frame, ASMInteger self, ASMNumber o) {
		if (o instanceof ASMInteger) {
			return new ASMBoolean(self.s >= ((ASMInteger)o).s);
		}
		return new ASMBoolean(self.s >= o.asDouble());
	}

	public static ASMString toString(StackFrame frame, ASMInteger self) {
		return new ASMString("" + self.s);
	}

	// additional operations, not in OCL standard library

	public static ASMString toHexString(StackFrame frame, ASMInteger self) {
		return new ASMString(Integer.toHexString(self.s));
	}

	public static ASMString toBinaryString(StackFrame frame, ASMInteger self) {
		return new ASMString(Integer.toBinaryString(self.s));
	}

	private int s;
}
