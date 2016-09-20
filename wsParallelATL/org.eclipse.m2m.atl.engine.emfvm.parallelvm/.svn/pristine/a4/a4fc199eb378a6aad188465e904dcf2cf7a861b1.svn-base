package org.eclipse.m2m.atl.engine.emfvm.parallelvm.lib;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.m2m.atl.engine.emfvm.parallelvm.ASMTOperation;
import org.eclipse.m2m.atl.engine.emfvm.parallelvm.TStackFrame;

/**
 * The Runnable implementation for the execution of Rule Operations
 * 
 * @author salvador
 *
 */
public class RuleRunnable implements Runnable{

	ASMTOperation operation;
	IProgressMonitor monitor;
	TStackFrame frame;
	private boolean verbose=false;
	
	public RuleRunnable(ASMTOperation operation, IProgressMonitor monitor,
			TStackFrame frame) {
		super();
		this.operation = operation;
		this.monitor = monitor;
		this.frame = frame;
	}
	
	@Override
	public void run() {
		if (verbose) System.out.println("Executing thread:" + operation.getName());
		operation.exec(frame.enter(), monitor, this);
		frame.leave();
		if (verbose) System.out.println("Leaving thread:" + operation.getName());
		frame.getExecEnv().sRelease();
	}

}
