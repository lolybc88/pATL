package org.eclipse.m2m.atl.engine.emfvm.parallelvm;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;
import java.util.Stack;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.m2m.atl.common.ATLLogger;
import org.eclipse.m2m.atl.engine.emfvm.ASM;
import org.eclipse.m2m.atl.engine.emfvm.ASMOperation;
import org.eclipse.m2m.atl.engine.emfvm.Bytecode;
import org.eclipse.m2m.atl.engine.emfvm.Messages;
import org.eclipse.m2m.atl.engine.emfvm.VMException;
import org.eclipse.m2m.atl.engine.emfvm.lib.AbstractStackFrame;
import org.eclipse.m2m.atl.engine.emfvm.lib.ExecEnv;
import org.eclipse.m2m.atl.engine.emfvm.lib.HasFields;
import org.eclipse.m2m.atl.engine.emfvm.lib.OclSimpleType;
import org.eclipse.m2m.atl.engine.emfvm.lib.OclType;
import org.eclipse.m2m.atl.engine.emfvm.lib.OclUndefined;
import org.eclipse.m2m.atl.engine.emfvm.lib.Operation;
import org.eclipse.m2m.atl.engine.emfvm.parallelvm.lib.RuleRunnable;
import org.eclipse.m2m.atl.engine.emfvm.parallelvm.lib.TExecEnv;

public class ASMTOperation extends ASMOperation{
	
	private TBytecode[] bytecodes;

	private int nbBytecodes;

	public ASMTOperation(ASM asm, String name) {
		super(asm, name);
	}
	
	/**
	 * Executes an operation.
	 * 
	 * @param frame
	 *            the frame for execution
	 * @param monitor
	 *            the progress monitor
	 * @return the result
	 */
	public Object exec(AbstractStackFrame frame, IProgressMonitor monitor, Runnable currentThread) {
		if (monitor != null) {
			if (monitor.isCanceled()) {
				throw new VMException(null, Messages.getString("ASMOperation.EXECUTION_CANCELED")); //$NON-NLS-1$
			}
		}
		final TExecEnv execEnv = (TExecEnv) ((TStackFrame)frame).getExecEnv();

		// Note: debug is not initialized from a constant, and therefore has a performance impact
		// TODO: measure this impact, and possibly remove the debug code
		final boolean debug = execEnv.isStep();

		final Object[] localVars = frame.getLocalVars();
		int pc = 0;
		int fp = 0;
		final Object[] stack = new Object[MAX_STACK];
		final Iterator<?>[] nestedIterate = new Iterator[nbNestedIterates];
		Iterator<?> it;
		Object s;
		StringBuffer log = new StringBuffer();
		try {
			while (pc < nbBytecodes) {
				TBytecode bytecode = bytecodes[pc++];
				((TStackFrame)frame).setPc(pc - 1);
				execEnv.stepTools(frame);
				execEnv.incNbExecutedBytecodes();
				if (debug) {
					ATLLogger.info(name + ":" + (pc - 1) + "\t" + bytecode); //$NON-NLS-1$ //$NON-NLS-2$
				}
				switch (bytecode.getOpcode()) {
					case TBytecode.PUSHD:
					case TBytecode.PUSHI:
					case TBytecode.PUSH:
						stack[fp++] = bytecode.getOperand();
						break;
					case TBytecode.PUSHT:
						stack[fp++] = Boolean.TRUE;
						break;
					case TBytecode.PUSHF:
						stack[fp++] = Boolean.FALSE;
						break;
					case TBytecode.CALL:
					case TBytecode.PCALL:
						Object self = stack[fp - bytecode.getValue() - 1];
						if (debug) {
							log.append("\tCalling "); //$NON-NLS-1$
							log.append(((TStackFrame)frame).getExecEnv().toPrettyPrintedString(self));
							log.append("."); //$NON-NLS-1$
							log.append(bytecode.getOperand());
							log.append("("); //$NON-NLS-1$
						}
						Object type = execEnv.getModelAdapter().getType(self);
						int nbCalleeArgs = bytecode.getValue();
						Operation operation = execEnv.getOperation(type, bytecode.getOperand());
						
						if (operation != null) {
							TStackFrame calleeFrame = (TStackFrame)frame.newFrame(operation);
							Object[] arguments = calleeFrame.getLocalVars();

							if (nbCalleeArgs >= 1 && arguments.length < nbCalleeArgs + 1) {
								throw new VMException(frame, Messages.getString(
										"ASMOperation.WRONGNUMBERARGS", bytecode.getOperand())); //$NON-NLS-1$
							}

							boolean first = true;
							for (int i = nbCalleeArgs; i >= 1; i--) {
								arguments[i] = stack[--fp];
								if (debug) {
									if (!first) {
										log.append(", "); //$NON-NLS-1$
									}
									first = false;
									log.append(execEnv.toPrettyPrintedString(arguments[i]));
								}
							}
							if (debug) {
								log.append(")"); //$NON-NLS-1$
								ATLLogger.info(log.toString());
							}
							--fp; // pop self, that we already retrieved earlier to get the operation
							arguments[0] = self;
							if (operation instanceof ASMOperation) {
								s = ((ASMOperation)operation).exec(calleeFrame.enter(), monitor);
								calleeFrame.leave();
							//The operation is an operation that need to know about threads.
							} else if (operation instanceof TOperation) {
								s = ((TOperation)operation).exec(calleeFrame.enter(), currentThread);
								calleeFrame.leave();
							} else {
								s = operation.exec(calleeFrame.enter());
								calleeFrame.leave();
							}
						} else {
							Assert.isTrue(bytecode.getOperand() instanceof String);
							// find native method
							Object[] arguments = new Object[nbCalleeArgs];

							boolean first = true;
							for (int i = nbCalleeArgs - 1; i >= 0; i--) {
								arguments[i] = stack[--fp];
								if (debug) {
									if (!first) {
										log.append(", "); //$NON-NLS-1$
									}
									first = false;
									log.append(execEnv.toPrettyPrintedString(arguments[i]));
								}
							}
							if (debug) {
								log.append(")"); //$NON-NLS-1$
								ATLLogger.info(log.toString());
							}
							--fp; // pop self, that we already retrieved earlier to get the operation

							Method m = findMethod(self.getClass(), (String)bytecode.getOperand(),
									getTypesOf(arguments));
							if (m == null) {
								throw new VMException(
										frame,
										Messages
												.getString(
														"ASMOperation.OPERATIONNOTFOUND", execEnv.toPrettyPrintedString(self), getMethodSignature(bytecode.getOperand().toString(), getTypesOf(arguments)))); //$NON-NLS-1$)
							}
							s = execEnv.getModelAdapter().invoke(m, self, arguments);
						}

						switch (bytecode.getOpcode()) {
							case TBytecode.CALL:
								if (s == null) {
									// TODO: throw new VMException(frame, "Operation " + bytecode.getOperand()
									// + "did not return a value.");
									// Throwing an exception here leverages the distinction between call/pcall
									// to report better error messages.
									// However, this would break backward compatibility. In the future, an
									// option, or versionning of .asm files
									// could make it safe to enable this exception.
									// Moreover, it would then be possible to compute the exact operand stack
									// size that must be allocated, which
									// would likely increase performance of the virtual machine.
								} else {
									stack[fp++] = s;
								}
								break;
							case TBytecode.PCALL:
								// ignore returned value if any
								break;
							default:
								break;
						
						}
						break;
					case TBytecode.TCALL:
						self = stack[fp - bytecode.getValue() - 1];
						if (debug) {
							log.append("\tCalling "); //$NON-NLS-1$
							log.append(((TStackFrame)frame).getExecEnv().toPrettyPrintedString(self));
							log.append("."); //$NON-NLS-1$
							log.append(bytecode.getOperand());
							log.append("("); //$NON-NLS-1$
						}
						//type = execEnv.getModelAdapter().getType(self);
						//nbCalleeArgs = bytecode.getValue();
						//operation = execEnv.getOperation(type, bytecode.getOperand());
						type = execEnv.getModelAdapter().getType(self);
						nbCalleeArgs = bytecode.getValue();
						operation = execEnv.getOperation(type, bytecode.getOperand());

						TStackFrame calleeFrame = (TStackFrame)frame.newFrame(operation);
						Object[] arguments = calleeFrame.getLocalVars();

						if (nbCalleeArgs >= 1 && arguments.length < nbCalleeArgs + 1) {
							throw new VMException(frame, Messages.getString(
									"ASMOperation.WRONGNUMBERARGS", bytecode.getOperand())); //$NON-NLS-1$
						}

						boolean first = true;
						for (int i = nbCalleeArgs; i >= 1; i--) {
							arguments[i] = stack[--fp];
							if (debug) {
								if (!first) {
									log.append(", "); //$NON-NLS-1$
								}
								first = false;
								log.append(execEnv.toPrettyPrintedString(arguments[i]));
							}
						}
						if (debug) {
							log.append(")"); //$NON-NLS-1$
							ATLLogger.info(log.toString());
						}
						--fp; // pop self, that we already retrieved earlier to get the operation
						arguments[0] = self;
						
						//Here we get the ExecEnv and we assign an anonymous runnable to the executor
						RuleRunnable r = new RuleRunnable((ASMTOperation)operation, monitor, calleeFrame);
						execEnv.getAtlExecutor().execute(r);
						//s = ((ASMOperation)operation).exec(calleeFrame.enter(), monitor);
						calleeFrame.leave();
						break;
					case TBytecode.LOAD:
						stack[fp++] = localVars[bytecode.getValue()];
						break;
					case TBytecode.STORE:
						localVars[bytecode.getValue()] = stack[--fp];
						break;
					case TBytecode.SET:
						Object value = stack[--fp];
						s = stack[--fp];
						if (s instanceof HasFields) {
							((HasFields)s).set(frame, bytecode.getOperand(), value);
						} else {
							if (value instanceof Collection<?>) {
								Collection<?> c = (Collection<?>)value;
								// TODO collections of collections have to be managed
								boolean temp = true;
								while (temp) {
									temp = c.remove(OclUndefined.SINGLETON);
								}
							} else if (value instanceof OclUndefined) { // other values are *not* wrapped
								value = null;
							}
							synchronized(this){
								execEnv.getModelAdapter().set(frame, s, (String)bytecode.getOperand(), value);
							}
						}
						break;
					case TBytecode.GET:
						s = stack[--fp];
						type = execEnv.getModelAdapter().getType(s);
						String propName = (String)bytecode.getOperand();
						Operation ai = execEnv.getAttributeInitializer(type, propName);
						if (ai != null) {
							stack[fp++] = execEnv.getHelperValue(frame, type, s, propName);
						} else if (s instanceof HasFields) {
							stack[fp++] = ((HasFields)s).get(frame, propName);
						} else if (s instanceof OclSimpleType && propName.equals("name")) { //$NON-NLS-1$
							stack[fp++] = ((OclSimpleType)s).getName();
						} else {
							
								stack[fp++] = execEnv.getModelAdapter().get(frame, s, propName);
							
							
						}
						break;
					case TBytecode.DUP:
						s = stack[fp - 1];
						stack[fp++] = s;
						break;
					case TBytecode.DUP_X1: // ..., value2, value1 => ..., value1, value2, value1
						s = stack[fp - 1];
						stack[fp++] = s;
						stack[fp - 2] = stack[fp - 3];
						stack[fp - 3] = stack[fp - 1];
						break;
					case TBytecode.DELETE:
						s = stack[--fp];
						execEnv.getModelAdapter().delete(frame, s);
						break;
					case TBytecode.GETASM:
						stack[fp++] = frame.getAsmModule();
						break;
					case TBytecode.NEW:
						Object mname = stack[--fp];
						Object me = stack[--fp];

						if (mname.equals("#native")) { //$NON-NLS-1$
							// TODO: makes sure the Map implementation is actually faster, then get rid of
							// if-else-if implementation
							/*
							 * if(me.equals("Sequence")) { stack[fp++] = new ArrayList(); } else
							 * if(me.equals("Set")) { stack[fp++] = new HashSet(); } else
							 * if(me.equals("OrderedSet")) { stack[fp++] = new LinkedHashSet(); } else
							 * if(me.equals("Tuple")) { stack[fp++] = new Tuple(); } else
							 * if(me.equals("OclSimpleType")) { stack[fp++] = new OclSimpleType(); } else
							 * if(me.equals("OclParametrizedType")) { stack[fp++] = new OclParametrizedType();
							 * } else if(me.equals("TransientLinkSet")) { stack[fp++] = new
							 * TransientLinkSet(); } else if(me.equals("TransientLink")) { stack[fp++] = new
							 * TransientLink(); } else if(me.equals("Map")) { stack[fp++] = new HashMap(); }
							 * else { throw new VMException(frame, "cannot create " + mname + "!" + me); } /
							 */
							Class<?> c = OclType.getNativeClassfromOclTypeName(me.toString());
							if (c != null) {
								stack[fp++] = c.newInstance();
							} else {
								throw new VMException(frame, Messages.getString(
										"ASMOperation.CANNOTCREATE", new Object[] {mname, me})); //$NON-NLS-1$ 
							}
						} else {
							Object ec = ExecEnv.findMetaElement(frame, mname, me);
							stack[fp++] = execEnv.newElement(frame, ec, mname.toString());
						}
						break;
					case TBytecode.NEWIN:
						Object modelName = stack[--fp];
						mname = stack[--fp];
						me = stack[--fp];
						Object ec = ExecEnv.findMetaElement(frame, mname, me);
						stack[fp++] = execEnv.newElementIn(frame, ec, modelName.toString());
						break;
					case TBytecode.FINDME:
						mname = stack[--fp];
						me = stack[--fp];
						if (mname.equals("#native")) { //$NON-NLS-1$
							Class<?> c = OclType.getNativeClassfromOclTypeName(me.toString());
							if (c != null) {
								stack[fp++] = c;
							} else {
								throw new VMException(frame, Messages.getString(
										"ASMOperation.CANNOTFIND", mname, me)); //$NON-NLS-1$
							}
						} else {
							ec = ExecEnv.findMetaElement(frame, mname, me);
							stack[fp++] = ec;
						}
						break;
					case TBytecode.ITERATE:
						Object o = stack[--fp];
						if (o instanceof Collection<?>) {
							Collection<?> c = (Collection<?>)o;
							it = c.iterator();
							if (it.hasNext()) {
								nestedIterate[bytecode.getValue2()] = it;
								stack[fp++] = it.next();
							} else {
								pc = bytecode.getValue();
							}
						} else {
							throw new VMException(frame, Messages.getString("ASMOperation.CANNOT_ITERATE", //$NON-NLS-1$
									execEnv.toPrettyPrintedString(o)));
						}
						break;
					case TBytecode.ENDITERATE:
						it = nestedIterate[bytecode.getValue2()];
						if (it.hasNext()) {
							stack[fp++] = it.next();
							pc = bytecode.getValue();
						}
						break;
					case TBytecode.POP:
						fp--;
						break;
					case TBytecode.SWAP:
						s = stack[fp - 1];
						stack[fp - 1] = stack[fp - 2];
						stack[fp - 2] = s;
						break;
					case TBytecode.IF:
						if (Boolean.TRUE.equals(stack[--fp])) {
							pc = bytecode.getValue();
						}
						break;
					case TBytecode.GOTO:
						pc = bytecode.getValue();
						break;
					default:
						throw new VMException(
								frame,
								Messages
										.getString(
												"ASMOperation.UNKNOWNBYTECODE", new Object[] {Integer.valueOf(bytecode.getOpcode())})); //$NON-NLS-1$
				}

				if (debug) {
					log = new StringBuffer();
					log.append("\tstack: "); //$NON-NLS-1$
					for (int i = 0; i < fp; i++) {
						if (i > 0) {
							log.append(", "); //$NON-NLS-1$
						}
						log.append(((TStackFrame)frame).getExecEnv().toPrettyPrintedString(stack[i]));
					}
					ATLLogger.info(log.toString());

					log = new StringBuffer();
					log.append("\tlocals: "); //$NON-NLS-1$
					boolean first = true;
					for (int i = 0; i < localVars.length; i++) {
						String vname = resolveVariableName(i, pc);
						if (vname != null) {
							if (!first) {
								log.append(", "); //$NON-NLS-1$
							}
							first = false;
							log.append(vname + "="); //$NON-NLS-1$
							log.append(((TStackFrame)frame).getExecEnv().toPrettyPrintedString(localVars[i]));
						}
					}
					ATLLogger.info(log.toString());
				}
			}
		} catch (VMException e) {
			((TStackFrame)frame).setPc(pc - 1);
			throw e; // do not rewrap
		} catch (Exception e) {
			((TStackFrame)frame).setPc(pc - 1);
			throw new VMException(frame, e.getLocalizedMessage(), e);
		}
		return fp > 0 ? stack[--fp] : null;
	}
	
	@Override
	public Object exec(AbstractStackFrame frame) {
		return exec(frame, null, null);
	}
	
	@Override
	public Object exec(AbstractStackFrame frame, IProgressMonitor monitor) {
		return exec(frame, monitor, null);
	}

	
	/**
	 * Sets the bytecodes for the operation.
	 * 
	 * @param bytecodes
	 *            the bytecodes to set
	 */
	public void setBytecodes(TBytecode[] bytecodes) {
		this.bytecodes = bytecodes;
		this.nbBytecodes = bytecodes.length;

		// There are at least as many local variables (excluding self) as parameters.
		// This statement is necessary because the last parameters may be unused.
		maxLocals = parameters.size();

		// pre-computes:
		// - target and nesting levels for iterate and enditerate
		// - maxLocals
		Stack<Object> stack = new Stack<Object>();
		for (int i = 0; i < nbBytecodes; i++) {
			TBytecode bytecode = bytecodes[i];
			if (bytecode.getOpcode() == Bytecode.ITERATE) {
				bytecode.setValue2(stack.size());
				stack.push(Integer.valueOf(i));
				if (bytecode.getValue2() > nbNestedIterates) {
					nbNestedIterates = bytecode.getValue2();
				}
			} else if (bytecode.getOpcode() == Bytecode.ENDITERATE) {
				int iterateIndex = ((Integer)stack.pop()).intValue();
				bytecode.setValue(iterateIndex + 1);
				bytecode.setValue2(stack.size());
				bytecodes[iterateIndex].setValue(i + 1);
			} else if ((bytecode.getOpcode() == Bytecode.LOAD) || (bytecode.getOpcode() == Bytecode.STORE)) {
				// With the new model-based ASM the variables are explicit even without debug information.
				// Therefore, we could use that information instead of analyzing loads and stores.
				if (bytecode.getValue() > maxLocals) {
					maxLocals = bytecode.getValue();
				}
			}
		}
		maxLocals++; // because the highest encountered index is maxLocals - 1
		nbNestedIterates++; // because the highest encountered nesting level is nbNestedIterates - 1
	}
}
