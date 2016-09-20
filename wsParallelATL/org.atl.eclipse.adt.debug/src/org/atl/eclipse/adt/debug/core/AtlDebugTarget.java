/*
 * Created on 10 mai 2004
 */
package org.atl.eclipse.adt.debug.core;

import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.atl.eclipse.adt.debug.AtlDebugPlugin;
import org.atl.eclipse.adt.debug.Messages;
import org.atl.eclipse.adt.launching.AtlLauncherTools;
import org.atl.eclipse.engine.AtlNbCharFile;
import org.atl.engine.vm.adwp.ADWP;
import org.atl.engine.vm.adwp.ADWPCommand;
import org.atl.engine.vm.adwp.ADWPDebugger;
import org.atl.engine.vm.adwp.IntegerValue;
import org.atl.engine.vm.adwp.ObjectReference;
import org.atl.engine.vm.adwp.StringValue;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IDebugElement;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IMemoryBlock;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IThread;


/**
 * A debug target is a debuggable execution context. It's the root of the element
 * hierarchy.
 * 
 * The AtlDebugTarget contains only one thread : the main thread.
 * The thread contains the current stackframe
 * 
 * @author Freddy Allilaire
 */
public class AtlDebugTarget extends AtlDebugElement implements IDebugTarget {

	/**
	 * current state of the debugger
	 */
	private int state;

	/**
	 * constant state possible for the debugger
	 */
	public static final int stateTerminated 	= 0;
	public static final int stateRunning 		= 1;
	public static final int stateSuspended 		= 2;
	public static final int stateDisconnected 	= 3;
	
	/**
	 * constant action possible for the debugger
	 */
	static final int TERMINATE 		= 0;
	static final int SUSPEND 		= 1;
	static final int STEP_RETURN 	= 2;
	static final int STEP_OVER 		= 3;
	static final int STEP_INTO 		= 4;
	static final int SUSPEND_STEP 	= 5;
	static final int RESUME 		= 6;
	static final int CREATE			= 7;
	
	private ADWPDebugger debugger;
	private ILaunch launch;
	
	private boolean disassemblyMode = false;

	private String prevLocation = null;
	/**
	 * Name of the process
	 */
	private String processName = AtlDebugModelConstants.DEBUGTARGETNAME;
	
	private String port = AtlDebugModelConstants.NULL;
	private String host = AtlDebugModelConstants.NULL;
	
	private Socket socket = null;
	
	private String messageFromDebuggee = "";

	/**
	 * The array of threads of the debug target
	 * In ATL, there is only one thread --> the main thread
	 */
	private AtlThread threads[];

	/**
	 * The current AtlStackFrame which contains the current current stackframe
	 * For the moment, only the current stackframe can be accessed
	 */
//	private AtlStackFrame cf;

	private AtlNbCharFile structFile;
	
	public AtlDebugTarget(ILaunch launch) {
		super(null);
 		DebugPlugin.getDefault().getBreakpointManager().addBreakpointListener(this);
//		DebugPlugin.getDefault().addDebugEventListener(this);

		try {
			disassemblyMode = launch.getLaunchConfiguration().getAttribute(AtlLauncherTools.MODEDEBUG, false);
		} catch (CoreException e) {
			e.printStackTrace();
		}
		state = stateDisconnected;
		this.launch = launch;
	}

	public void start() {
		
		System.out.println(Messages.getString("AtlDebugTarget.CONNECTIONDEBUGEE"));
		try {
			do {
				try {
					try {
						port = launch.getLaunchConfiguration().getAttribute(AtlLauncherTools.PORT, AtlDebugModelConstants.PORT);
						host = launch.getLaunchConfiguration().getAttribute(AtlLauncherTools.HOST, AtlDebugModelConstants.HOST);
						if (port.equals(""))
							port = AtlDebugModelConstants.PORT;
						if (host.equals(""))
							host = AtlDebugModelConstants.HOST;
						socket = new Socket(host, Integer.parseInt(port));
					} catch (CoreException e1) {
						e1.printStackTrace();
					}
				}
				catch(ConnectException ce) {
					try {
						Thread.sleep(100);
					} catch(InterruptedException ie) {

					}
				}
			}
			while(socket == null);
			
			debugger = new ADWPDebugger(socket.getInputStream(), socket.getOutputStream());
			System.out.println(Messages.getString("AtlDebugTarget.CONNECTED")); //$NON-NLS-1$
			state = stateSuspended;

			threads = new AtlThread[1];
			threads[0] = new AtlThread(AtlDebugModelConstants.THREADNAME, this);
			
			IBreakpoint bpArray[] = DebugPlugin.getDefault().getBreakpointManager().getBreakpoints(getModelIdentifier());
			for (int i=0; i<bpArray.length; i++)
				breakpointAdded(bpArray[i]);
		}
		catch (UnknownHostException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		new Thread() {
			public void run() {
//				final boolean debug = false;
				ADWPCommand msg;
				prevLocation = null;

				generateDebugEvent(AtlDebugTarget.CREATE, AtlDebugTarget.this);
				
				while (true) {
					msg = debugger.readMessage();

					if (msg.getCode() == ADWP.MSG_TERMINATED)
						break;
					
					messageFromDebuggee = ((StringValue) msg.getArgs().get(0)).getValue();
					
					ObjectReference currentFrame = (ObjectReference) msg.getArgs().get(1);
//					String opName = ((StringValue) msg.getArgs().get(2)).getValue();
//					int location = ((IntegerValue) msg.getArgs().get(3)).getValue();
					String sourceLocation = ((StringValue) msg.getArgs().get(4)).getValue();

					if (sourceLocation.equals("<null>") && !disassemblyMode) {
						debugger.sendCommand(ADWP.CMD_STEP, Arrays.asList(new Object[]{}));
					}
					else if(sourceLocation.equals(prevLocation) && !disassemblyMode) {
						debugger.sendCommand(ADWP.CMD_STEP, Arrays.asList(new Object[]{}));
					}
					else {
						ObjectReference stack = (ObjectReference)currentFrame.call("getStack", new ArrayList()); //$NON-NLS-1$
						int n = ((IntegerValue) stack.call("size", new ArrayList())).getValue(); //$NON-NLS-1$
						AtlStackFrame frames[] = new AtlStackFrame[n];
						for(int i = 1 ; i <= n ; i++) {
							// TODO To get current file being debugged
							try {
								String fileName = launch.getLaunchConfiguration().getAttribute(AtlLauncherTools.ATLFILENAME, AtlLauncherTools.NULLPARAMETER);

								IWorkspace wks = ResourcesPlugin.getWorkspace();
								IWorkspaceRoot wksroot = wks.getRoot();

								IFile file = wksroot.getFile(new Path(fileName));
								file.refreshLocal(IResource.DEPTH_ZERO, null);
								
								structFile = new AtlNbCharFile(file.getContents());
							} catch (CoreException e1) {
								e1.printStackTrace();
							}

//							frames[n - i] = cf = new AtlStackFrame(
							frames[n - i] = new AtlStackFrame(
								threads[0],
								(ObjectReference)stack.call("at", Arrays.asList(new Object[]{IntegerValue.valueOf(i)})), //$NON-NLS-1$
								structFile);
						}
						threads[0].setStackFrames(frames);
						setState(AtlDebugTarget.stateSuspended);
						generateDebugEvent(AtlDebugTarget.SUSPEND_STEP, AtlDebugTarget.this);
						
						prevLocation = sourceLocation;
					}
				}
				//TERMINATE
				setState(AtlDebugTarget.stateSuspended);
				try {
					terminate();
				} catch (DebugException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}
	
	
	/**
	 * @see org.eclipse.debug.core.IBreakpointListener#breakpointAdded(org.eclipse.debug.core.model.IBreakpoint)
	 */
	public void breakpointAdded(IBreakpoint breakpoint) {
		AtlBreakpoint ab = (AtlBreakpoint) breakpoint;
		String location = "";
		Boolean enabled = new Boolean(false);
		try {
			location = (String)ab.getMarker().getAttribute(IMarker.LOCATION);
			enabled = (Boolean)ab.getMarker().getAttribute(IBreakpoint.ENABLED);
		}
		catch (CoreException e) {
			e.printStackTrace();
			return;
		}

		if (enabled.booleanValue()) {
			List parameter = new ArrayList();
			parameter.add(StringValue.valueOf(location));
			debugger.sendCommand(ADWP.CMD_SET_BP, parameter);
		}
	}
	
	/**
	 * @see org.eclipse.debug.core.IBreakpointListener#breakpointChanged(org.eclipse.debug.core.model.IBreakpoint, org.eclipse.core.resources.IMarkerDelta)
	 */
	public void breakpointChanged(IBreakpoint breakpoint, IMarkerDelta delta) {
		AtlBreakpoint ab = (AtlBreakpoint) breakpoint;
		String location = "";
		Boolean enabled = new Boolean(false);
		try {
			location = (String)ab.getMarker().getAttribute(IMarker.LOCATION);
			enabled = (Boolean)ab.getMarker().getAttribute(IBreakpoint.ENABLED);
		}
		catch (CoreException e) {
			e.printStackTrace();
			return;
		}

		List parameter = new ArrayList();
		parameter.add(StringValue.valueOf(location));
		
		if (enabled.booleanValue()) {
			debugger.sendCommand(ADWP.CMD_SET_BP, parameter);
		}
		else {
			debugger.sendCommand(ADWP.CMD_UNSET_BP, parameter);
		}
	}
	
	/**
	 * @see org.eclipse.debug.core.IBreakpointListener#breakpointRemoved(org.eclipse.debug.core.model.IBreakpoint, org.eclipse.core.resources.IMarkerDelta)
	 */
	public void breakpointRemoved(IBreakpoint breakpoint, IMarkerDelta delta) {
	
		AtlBreakpoint ab = (AtlBreakpoint) breakpoint;
		String location = "";
		try {
			location = (String)ab.getMarker().getAttribute(IMarker.LOCATION);
		}
		catch (CoreException e) {
			e.printStackTrace();
			return;
		}

		List parameter = new ArrayList();
		parameter.add(location);
		debugger.sendCommand(ADWP.CMD_UNSET_BP, parameter);
	}
	
	/**
	 * Return true if the debugger can be disconnected
	 * @see org.eclipse.debug.core.model.IDisconnect#canDisconnect()
	 */
	public boolean canDisconnect() 
	{
		return !isDisconnected();
	}
	
	/**
	 * Return true if the debugger can be resumed
	 * @see org.eclipse.debug.core.model.ISuspendResume#canResume()
	 */
	public boolean canResume() 
	{
		return (state != stateDisconnected && state != stateRunning);
	}
	
	/**
	 * Return true if the debugger can be suspended
	 * @see org.eclipse.debug.core.model.ISuspendResume#canSuspend()
	 */
	public boolean canSuspend() 
	{
		return false;
	}
	
	/**
	 * Return true if the debugger can be terminated
	 * @see org.eclipse.debug.core.model.ITerminate#canTerminate()
	 */
	public boolean canTerminate() 
	{
		return !isTerminated();
	}
	
	/**
	 * This method disconnect the debugger
	 * @see org.eclipse.debug.core.model.IDisconnect#disconnect()
	 */
	public void disconnect() throws DebugException {
		setState(AtlDebugTarget.stateDisconnected);
	}
		
	/**
	 * Returns the debugTarget
	 * @see org.eclipse.debug.core.model.IDebugElement#getDebugTarget()
	 */
	public IDebugTarget getDebugTarget() 
	{
		return this;
	}
	
	/**
	 * Returns the launch
	 * @see org.eclipse.debug.core.model.IDebugElement#getLaunch()
	 */
	public ILaunch getLaunch() 
	{
		return launch;
	}
	
	/**
	 * Not use in ATL debugger
	 * @see org.eclipse.debug.core.model.IMemoryBlockRetrieval#getMemoryBlock(long, long)
	 */
	public IMemoryBlock getMemoryBlock(long startAddress, long length) throws DebugException 
	{
		return null;
	}
	
	/**
	 * Returns the unique identifier of the plug-in
	 * @see org.eclipse.debug.core.model.IDebugElement#getModelIdentifier()
	 */
	public String getModelIdentifier() 
	{
		return AtlDebugPlugin.getUniqueIdentifier();
	}
	
	/**
	 * Returns the name of the process
	 * @see org.eclipse.debug.core.model.IDebugTarget#getName()
	 */
	public String getName() throws DebugException {
		return processName;
	}
	
	/**
	 * Not use in ATL debugger
	 * @see org.eclipse.debug.core.model.IDebugTarget#getProcess()
	 */
	public IProcess getProcess() {
		return null;
	}
	
	/**
	 * This method returns the array of threads.
	 * In our context, this method returns an array with only the main thread
	 * @see org.eclipse.debug.core.model.IDebugTarget#getThreads()
	 */
	public IThread[] getThreads() throws DebugException	{
		return threads;
	}
	
	/**
	 * This method allows to know if there is a thread in the debugTarget
	 * In ATL, there is always one and only one thread : the main thread
	 * @see org.eclipse.debug.core.model.IDebugTarget#hasThreads()
	 */
	public boolean hasThreads() throws DebugException {
		return (getThreads() != null && getThreads().length > 0) ;
	}
	
	/**
	 * This method allows to know if the debugger is in a state "Disconnected"
	 * @see org.eclipse.debug.core.model.IDisconnect#isDisconnected()
	 */
	public boolean isDisconnected() {
		return (state == stateDisconnected);
	}
	
	/**
	 * This method allows to know if the debugger is in a state "Suspended"
	 * @see org.eclipse.debug.core.model.ISuspendResume#isSuspended()
	 */
	public boolean isSuspended() {		
		return (state == stateSuspended);
	}
	
	/**
	 * This method allows to know if the debugger is in a state "Terminated"
	 * @see org.eclipse.debug.core.model.ITerminate#isTerminated()
	 */
	public boolean isTerminated() {
		return (state == stateTerminated);
	}
	
	/**
	 * This method resume the debugger
	 * @see org.eclipse.debug.core.model.ISuspendResume#resume()
	 */
	public void resume() throws DebugException {
		if (!isSuspended())
			return;
		
		prevLocation = null;
		setState(AtlDebugTarget.stateRunning);
		generateDebugEvent(RESUME, this);
		debugger.sendCommand(ADWP.CMD_CONTINUE, Arrays.asList(new Object[]{}));
	}
	
	/**
	 * @see org.eclipse.debug.core.model.IDebugTarget#supportsBreakpoint(org.eclipse.debug.core.model.IBreakpoint)
	 */
	public boolean supportsBreakpoint(IBreakpoint breakpoint) {
		return (breakpoint instanceof AtlBreakpoint);
	}
	
	/**
	 * Not use in our context
	 *  @see org.eclipse.debug.core.model.IMemoryBlockRetrieval#supportsStorageRetrieval()
	 */
	public boolean supportsStorageRetrieval() {
		return false;
	}
	
	/**
	 * This method suspends the debugger
	 * @see org.eclipse.debug.core.model.ISuspendResume#suspend()
	 */
	public void suspend() throws DebugException	{
		if (isSuspended())
			return;

		setState(AtlDebugTarget.stateSuspended);
		generateDebugEvent(SUSPEND, this);
	}
	
	/**
	 * This method terminates the action of the debugger
	 * @see org.eclipse.debug.core.model.ITerminate#terminate()
	 */
	public void terminate() throws DebugException {
		if (!isSuspended())
			return;
		
		setState(AtlDebugTarget.stateTerminated);
		debugger.sendCommand(ADWP.CMD_FINISH, Arrays.asList(new Object[]{}));
		generateDebugEvent(TERMINATE, this);
	}
	
	/**
	 * @return Returns the debugger.
	 */
	public ADWPDebugger getDebugger() {
		return debugger;
	}

	/**
	 * @return Returns the state.
	 */
	public int getState() {
		return state;
	}

	/**
	 * state corresponding to the state of the debugger (running, disconnected ...)
	 * This method allows to update state
	 * @param state The state to set.
	 */
	public void setState(int state) {
		this.state = state;
	}
	
	/**
	 * This method generate a debug event corresponding to the command in parameter
	 * This allows for example to prevent DebugUI that there is an action execute.
	 * In this case, DebugUI update the display 
	 * @param command
	 * @param contextObject
	 */
	void generateDebugEvent(int command, IDebugElement contextObject) {
		DebugEvent event = null;
		try {
			switch (command) {
				case CREATE :
				event = new DebugEvent(getDebugTarget().getThreads()[0],
						DebugEvent.CREATE);
				break;
				case STEP_INTO :
					event = new DebugEvent(getDebugTarget().getThreads()[0],
							DebugEvent.RESUME, DebugEvent.STEP_INTO);
					break;
				case STEP_OVER :
					event = new DebugEvent(getDebugTarget().getThreads()[0],
							DebugEvent.RESUME, DebugEvent.STEP_OVER);
					break;
				case STEP_RETURN :
					event = new DebugEvent(getDebugTarget().getThreads()[0],
							DebugEvent.RESUME, DebugEvent.STEP_RETURN);
					break;
				case RESUME :
					event = new DebugEvent(getDebugTarget().getThreads()[0],
							DebugEvent.RESUME);
					break;
				case SUSPEND :
					event = new DebugEvent(getDebugTarget().getThreads()[0],
							DebugEvent.SUSPEND, DebugEvent.CLIENT_REQUEST);
					break;
				case TERMINATE :
					event = new DebugEvent(getDebugTarget(),
							DebugEvent.TERMINATE);
					break;
				case SUSPEND_STEP :	// The VM signals it has stopped
					event = new DebugEvent(getDebugTarget().getThreads()[0],
							DebugEvent.SUSPEND, DebugEvent.BREAKPOINT);
					break;
				default : return;
			}
			DebugEvent debugEvents[] = new DebugEvent[1];
			debugEvents[0] = event;
			DebugPlugin.getDefault().fireDebugEventSet(debugEvents);
		} catch (DebugException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method allows to receive DebugEvent sent
	 * @see org.eclipse.debug.core.IDebugEventSetListener#handleDebugEvents(org.eclipse.debug.core.DebugEvent[])
	 */
	public void handleDebugEvents(DebugEvent[] events) {
		for (int i = 0; i < events.length; i++)
			if (events[i].getDetail() == DebugEvent.SUSPEND)
				setState(AtlDebugTarget.stateSuspended);
	}

	public boolean isDisassemblyMode() {
		return disassemblyMode;
	}
	
	/**
	 * @param disassemblyMode The disassemblyMode to set.
	 */
	public void setDisassemblyMode(boolean disassemblyMode) {
		this.disassemblyMode = disassemblyMode;
	}
	
	/**
	 * @param prevLocation The prevLocation to set.
	 */
	public void setPrevLocation(String prevLocation) {
		this.prevLocation = prevLocation;
	}

	/**
	 * @return Returns the host.
	 */
	public String getHost() {
		return host;
	}

	/**
	 * @return Returns the port.
	 */
	public String getPort() {
		return port;
	}
	/**
	 * @return Returns the messageFromDebuggee.
	 */
	public String getMessageFromDebuggee() {
		return messageFromDebuggee;
	}

}