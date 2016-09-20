package org.eclipse.m2m.atl.engine.emfvm.parallelvm;

import org.eclipse.m2m.atl.engine.emfvm.lib.AbstractStackFrame;
import org.eclipse.m2m.atl.engine.emfvm.lib.Operation;

public abstract class TOperation extends Operation{

	public TOperation(int maxLocals, String name) {
		super(maxLocals, name);
	}

	public abstract Object exec(AbstractStackFrame frame, Runnable currentThread);
	
	//We implement the normal exec, should never be called
	public Object exec(AbstractStackFrame frame){
		return null;
	}

}
