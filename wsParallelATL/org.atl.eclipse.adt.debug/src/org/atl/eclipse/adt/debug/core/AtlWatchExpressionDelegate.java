/*
 * Created on 29 juin 2004
 */
package org.atl.eclipse.adt.debug.core;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.atl.engine.vm.ASM;
import org.atl.engine.vm.ASMXMLWriter;
import org.atl.engine.vm.adwp.ADWPDebugger;
import org.atl.engine.vm.adwp.StringValue;
import org.atl.engine.vm.adwp.Value;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IDebugElement;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.debug.core.model.IWatchExpressionDelegate;
import org.eclipse.debug.core.model.IWatchExpressionListener;
import org.eclipse.debug.core.model.IWatchExpressionResult;
import org.eclipse.gmt.atl.oclquery.core.OclHelper;

/**
 * A delegate which computes the value of a watch expression
 * when provided a context. Watch delegates are provided on a
 * per debug model basis. Watch expressions query the appropriate
 * delegate based on the debug model of the context element.
 * Plug-ins that wish to contribute watch expression delegates may do so using the
 * org.eclipse.debug.core.watchExpressionDelegates
 * extension point.
 * 
 * @author allilaire
 */
public class AtlWatchExpressionDelegate implements IWatchExpressionDelegate {

	/**
	 * This inner class implements IWatchExpression
	 * 
	 * The result of an evaluation performed by an
	 * org.eclipse.debug.core.model.IWatchExpressionDelegate.
	 * A watch expression reports the value of the evaluation
	 * and any errors or exceptions that occurred.
	 * 
	 * @author allilaire
	 */
	class AtlWatchExpressionResult implements IWatchExpressionResult {

		String errorMessages[];
		IValue value;
		DebugException de;
		
		public AtlWatchExpressionResult(DebugException de, String[] errorMessages, IValue value) {
			this.errorMessages = errorMessages;
			this.value = value;
		}
		
		/**
		 * @see org.eclipse.debug.core.model.IWatchExpressionResult#getValue()
		 */
		public IValue getValue() {
			return value;
		}

		/**
		 * @see org.eclipse.debug.core.model.IWatchExpressionResult#hasErrors()
		 */
		public boolean hasErrors() {
			if ((errorMessages != null) && (errorMessages.length > 0))
				return true;
			else
				return false;
		}

		/**
		 * @see org.eclipse.debug.core.model.IWatchExpressionResult#getErrorMessages()
		 */
		public String[] getErrorMessages() {
			return errorMessages;
		}

		/**
		 * @see org.eclipse.debug.core.model.IWatchExpressionResult#getExpressionText()
		 */
		public String getExpressionText() {
			try {
				return value.getValueString();
			}
			catch (DebugException e) {
				return null;
			}
		}

		/**
		 * @see org.eclipse.debug.core.model.IWatchExpressionResult#getException()
		 */
		public DebugException getException() {
			return de;
		}
		
	}
	
	/**
	 * Evaluates the given expression in the given context asynchronously and
	 * notifies the given listener when the evaluation finishes.
	 * 
	 * @see org.eclipse.debug.core.model.IWatchExpressionDelegate#evaluateExpression(java.lang.String, org.eclipse.debug.core.model.IDebugElement, org.eclipse.debug.core.model.IWatchExpressionListener)
	 */
	public void evaluateExpression(String expression, IDebugElement context, IWatchExpressionListener listener) {

//		this.expressionText = expression;
//		this.listener = listener;

		AtlStackFrame frame = null;
		if (context instanceof AtlStackFrame) {
			frame = (AtlStackFrame)context;
		}
		else if (context instanceof IThread) {
			try {
				frame = (AtlStackFrame)((IThread)context).getTopStackFrame();
			}
			catch (DebugException e) {
			}
		}
		if (frame == null) {
			listener.watchEvaluationFinished(null);
		}
		else {
			AtlWatchExpressionResult atlwe = doEvaluation(frame, expression);
			listener.watchEvaluationFinished(atlwe);
		}
	}

	/**
	 * This method should evaluate the expression, the parsed expression should be sent
	 * to the ATL VM.
	 * @return
	 */
	private AtlWatchExpressionResult doEvaluation(AtlStackFrame frame, String expression) {
		DebugException de = null;
		String errorMessages[] = null;
		IValue value = null;
		
		try {
			List parameters = new ArrayList();
			IVariable variables[] = frame.getVariables();
			for(int i = 0 ; i < variables.length ; i++) {
				String pname = variables[i].getName();
				if(!pname.equals("self"))
					parameters.add(pname);
			}
			
			ASM asm = new OclHelper(expression, parameters).compile();
			StringWriter sw = new StringWriter();
			new ASMXMLWriter(new PrintWriter(sw), false).print(asm);
			AtlDebugTarget debugTarget = (AtlDebugTarget)frame.getDebugTarget();
			ADWPDebugger debugger = debugTarget.getDebugger();
			Value val = debugger.request(ADWPDebugger.CMD_QUERY, Arrays.asList(new Object[] {frame.getStackFrame(), StringValue.valueOf(sw.toString())}));
		
			value = new AtlValue(val, debugTarget);
		} catch(DebugException e) {
			de = e;
		} catch(Exception e) {
			de = new DebugException(new Status(Status.ERROR, "org.atl.eclipse.adt.debug", Status.OK, "error while evaluating expression", e));
		}
		return new AtlWatchExpressionResult(de, errorMessages, value);
	}
	
}
