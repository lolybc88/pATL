package org.atl.engine.vm;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.atl.engine.vm.adwp.ADWPCommand;
import org.atl.engine.vm.adwp.ADWPDebuggee;
import org.atl.engine.vm.adwp.IntegerValue;
import org.atl.engine.vm.adwp.LocalObjectReference;
import org.atl.engine.vm.adwp.StringValue;
import org.atl.engine.vm.adwp.Value;
import org.atl.engine.vm.nativelib.ASMModel;
import org.atl.engine.vm.nativelib.ASMModule;
import org.atl.engine.vm.nativelib.ASMOclAny;

/**
 * @author Fr�d�ric Jouault
 */
public class NetworkDebugger implements Debugger {

	public NetworkDebugger(final int port, boolean suspend) {
		this.port = port;
		this.suspend = suspend;
		if(suspend)
			step = true;


		Thread init = new Thread() {
			public void run() {
				try {
					ServerSocket server = new ServerSocket(port);
					socket = server.accept();
					server.close();
					debuggee = new ADWPDebuggee(socket.getInputStream(), socket.getOutputStream());
				} catch(IOException ioe) {
					ioe.printStackTrace();
				}
			}
		};

		if(suspend)
			init.run();
		else
			init.start();
	}

	public void enter(StackFrame frame) {
		if(stepOver || finish)
			depth++;
	}

	public void leave(StackFrame frame) {
		if((depth == 0) && finish) {
			step = true;
			finished = true;
		}
		if(stepOver || finish) {
			if(depth > 0)
				depth--;
		}
	}

	public void step(ASMStackFrame frame) {
		if(execEnv == null)
			execEnv = (ASMExecEnv)frame.getExecEnv();
		if((stepOver == true) && (depth == 0)) {
			stepOver = false;
			step = true;
		}
		if(step) {
			if(finished) {
				dialog(frame, "after finishing");
			} else {
				dialog(frame, "for stepping");
			}
		} else {
			int location = frame.getLocation();
			ASMOperation operation = (ASMOperation)frame.getOperation();
			String sourceLocation = operation.resolveLineNumber(location);
			if(breakpoints.contains(sourceLocation)) {
				dialog(frame, "for breakpoint");
			}
		}
	}

	public void terminated() {
		try {
			debuggee.sendMessage(ADWPDebuggee.MSG_TERMINATED, 0, Collections.EMPTY_LIST);
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void dialog(StackFrame frame_, String msg) {
		final boolean debug = false;
		while(frame_ instanceof NativeStackFrame) {
			frame_ = frame_.getParent();
		}
		ASMStackFrame frame = (ASMStackFrame)frame_;
		int location = frame.getLocation();
		ASMOperation operation = (ASMOperation)frame.getOperation();
		String opName = operation.getName();
		String sourceLocation = operation.resolveLineNumber(location);

		//if(sourceLocation == null) return;
		debuggee.sendMessage(ADWPDebuggee.MSG_STOPPED, 0, Arrays.asList(new Object[] {
				StringValue.valueOf(msg),
				LocalObjectReference.valueOf(frame, this),
				StringValue.valueOf(opName),
				IntegerValue.valueOf(location),
				StringValue.valueOf(sourceLocation)
		}));

		boolean resume = false;
		do {
			ADWPCommand acmd = debuggee.readCommand();
			if(debug) System.out.println(acmd);

			resume = false;
			step = false;
			stepOver = false;
			finish = false;
			finished = false;

			Command cmd = (Command)commands.get(new Integer(acmd.getCode()));
			if(cmd == null) {
				System.out.println("Warning, unsupported command: " + acmd.getCode());
			} else {
				resume = cmd.doIt(acmd, frame);
			}

		} while(!resume);
	}

	public void error(StackFrame frame, String msg, Exception e) {
		System.out.println("********************************* ERROR *********************************");
new Exception().printStackTrace();
		dialog(frame, "ERROR: " + msg);
		if(msg != null)
			System.out.println("Message: " + msg);
		if(e != null)
			e.printStackTrace(System.out);
		frame.getExecEnv().printStackTrace();
		System.out.println("*************************************************************************");
	}

	private Map commands = new HashMap();

	{

// BEGIN Data inspection commands
		new Command(ADWPDebuggee.CMD_GET, "get a property from an object") {
			public boolean doIt(ADWPCommand cmd, StackFrame frame) {
				List args = cmd.getArgs();
				LocalObjectReference o = (LocalObjectReference)args.get(0);
				String propName = ((StringValue)args.get(1)).getValue();
				Value ret = o.get(propName);
				debuggee.sendMessage(ADWPDebuggee.MSG_ANS, cmd.getAck(), Arrays.asList(new Object[] {ret}));
				return false;
			}
		};
		new Command(ADWPDebuggee.CMD_SET, "set a property to an object") {
			public boolean doIt(ADWPCommand cmd, StackFrame frame) {
				List args = cmd.getArgs();
				LocalObjectReference o = (LocalObjectReference)args.get(0);
				String propName = ((StringValue)args.get(1)).getValue();
				Value value = (Value)args.get(2);
				o.set(propName, value);
				return false;
			}
		};
		new Command(ADWPDebuggee.CMD_CALL, "call an operation on an object") {
			public boolean doIt(ADWPCommand cmd, StackFrame frame) {
				List args = cmd.getArgs();
				LocalObjectReference o = (LocalObjectReference)args.get(0);
				String opName = ((StringValue)args.get(1)).getValue();
				int nbArgs = ((IntegerValue)args.get(2)).getValue();
				List realArgs = (nbArgs == 0) ? new ArrayList() : args.subList(3, args.size());
				Value ret = o.call(opName, realArgs);
				debuggee.sendMessage(ADWPDebuggee.MSG_ANS, cmd.getAck(), Arrays.asList(new Object[] {ret}));
				return false;
			}
		};
		// CMD_QUERY does not work yet
		new Command(ADWPDebuggee.CMD_QUERY, "executes a query in the current context") {
			public boolean doIt(ADWPCommand cmd, StackFrame frame) {
				List args = cmd.getArgs();
				StackFrame frame_ = (StackFrame)((LocalObjectReference)args.get(0)).getObject();
				String query = ((StringValue)args.get(1)).getValue();

				ASMOclAny asmRet = null;
				try {
					ASM asm = new ASMXMLReader().read(new ByteArrayInputStream(query.getBytes()));
					Debugger debugger = new SimpleDebugger(
							/* step = */ false,
							/* stepops = */ new ArrayList(),
							/* deepstepops = */ new ArrayList(),
							/* nostepops = */ new ArrayList(),
							/* deepnostepops = */ new ArrayList(),
							/* showStackTrace = */ true
					);
					ASMOperation op = asm.getOperation("test");
					ASMModule asmModule = new ASMModule(asm);
					List arguments = new ArrayList();
					arguments.add(0, asmModule);
					ASMExecEnv env = new ASMExecEnv(asmModule, debugger);
					Map models = execEnv.getModels();
					for(Iterator i = models.keySet().iterator() ; i.hasNext() ; ) {
						String mname = (String)i.next();
						env.addModel(mname, (ASMModel)models.get(mname));
					}
					env.registerOperations(asm);
					Map pvalues = new HashMap();
					ASMStackFrame asmFrame = ((ASMStackFrame)frame);
					for(Iterator i = asmFrame.getLocalVariables().keySet().iterator() ; i.hasNext() ; ) {
						String slot = (String)i.next();
						String pname;
						if(slot.equals("_stack")) {
							pname = slot;
						} else {
							pname = asmFrame.resolveVariableName(Integer.parseInt(slot));
						}
						pvalues.put(pname, asmFrame.getVariable(slot));						
					}
					for(Iterator i = op.getParameters().iterator() ; i.hasNext() ; ) {
						ASMParameter p = (ASMParameter)i.next();
						String pname = op.resolveVariableName(Integer.parseInt(p.getName()), 0);
						ASMOclAny value = (ASMOclAny)pvalues.get(pname);
						arguments.add(value);
					}
					StackFrame qframe = ASMStackFrame.rootFrame(env, op, arguments);
					asmRet = op.exec(qframe);
				} catch(Exception e) {
					e.printStackTrace(System.out);
				}
				Value ret = LocalObjectReference.asm2value(asmRet, thisDebugger);

				debuggee.sendMessage(ADWPDebuggee.MSG_ANS, cmd.getAck(), Arrays.asList(new Object[] {ret}));
				return false;
			}
		};
// END Data inspection commands


// BEGIN Execution control commands
		new Command(ADWPDebuggee.CMD_CONTINUE, "resume program execution") {
			public boolean doIt(ADWPCommand cmd, StackFrame frame) {
				return true;
			}
		};
		new Command(ADWPDebuggee.CMD_STEP, "execute a single instruction; stepping into method calls") {
			public boolean doIt(ADWPCommand cmd, StackFrame frame) {
				step = true;
				return true;
			}
		};
		new Command(ADWPDebuggee.CMD_STEP_OVER, "execute a single instruction; stepping over method calls") {
			public boolean doIt(ADWPCommand cmd, StackFrame frame) {
				stepOver = true;
				depth = 0;
				return true;
			}
		};
		new Command(ADWPDebuggee.CMD_FINISH, "run until after the execution of the current operation") {
			public boolean doIt(ADWPCommand cmd, StackFrame frame) {
				finish = true;
				depth = 0;
				return true;
			}
		};
		new Command(ADWPDebuggee.CMD_SET_BP, "set a breakpoint") {
			public boolean doIt(ADWPCommand cmd, StackFrame frame) {
				List args = cmd.getArgs();
				String location = ((StringValue)args.get(0)).getValue();
				breakpoints.add(location);
				return false;
			}
		};
		new Command(ADWPDebuggee.CMD_UNSET_BP, "unset a breakpoint") {
			public boolean doIt(ADWPCommand cmd, StackFrame frame) {
				List args = cmd.getArgs();
				String location = ((StringValue)args.get(0)).getValue();
				breakpoints.remove(location);
				return false;
			}
		};
// END Execution control commands


// BEGIN Code commands
		new Command(ADWPDebuggee.CMD_DISASSEMBLE, "disassemble current operation") {
			public boolean doIt(ADWPCommand cmd, StackFrame frame) {
				ASMOperation op = (ASMOperation)((StackFrame)((LocalObjectReference)cmd.getArgs().get(0)).getObject()).getOperation();
				List instr = op.getInstructions();
				List msgArgs = new ArrayList();

				int k = 0;
				for(Iterator i = instr.iterator() ; i.hasNext() ; ) {
					String inst = i.next().toString();
					if(inst.startsWith("load ")) {
						inst = "load " + op.resolveVariableName(Integer.parseInt(inst.substring(5)), k);
					} else if(inst.startsWith("store ")) {
						inst = "store " + op.resolveVariableName(Integer.parseInt(inst.substring(6)), k);
					}
					msgArgs.add(StringValue.valueOf(inst));
					k++;
				}
				debuggee.sendMessage(ADWPDebuggee.MSG_DISAS_CODE, cmd.getAck(), msgArgs);
				return false;
			}
		};
/*
		new Command("source", "display source location") {
			public boolean doIt(String args[], StackFrame frame) {
				String id = ((ASMOperation)frame.getOperation()).resolveLineNumber(((ASMStackFrame)frame).getLocation());
				out.println(id + "\r");
				return false;
			}
		};
*/
// END Code commands
	}

	private abstract class Command {

		public Command(int cmd, String description) {
			this.cmd = cmd;
			this.description = description;
			commands.put(new Integer(cmd), this);
		}

		/**
		 * Performs the command's action and returns true if the program should be resumed.
		 */
		public abstract boolean doIt(ADWPCommand cmd, StackFrame frame);

		public String getDescription() {
			return description;
		}

		private int cmd;
		private String description;
	}

	private boolean step = false;

	private boolean stepOver = false;

	private boolean finish = false;

	private boolean finished = false;

	private int depth = 0;

	private int port;

	private boolean suspend;

	private Socket socket;

	private ADWPDebuggee debuggee;

	private List breakpoints = new ArrayList();

	public ASMExecEnv getExecEnv() {
		return execEnv;
	}
	
	private ASMExecEnv execEnv;
	
	private NetworkDebugger thisDebugger = this;
}

