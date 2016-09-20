package org.eclipse.m2m.atl.engine.emfvm.parallelvm.lib;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import org.eclipse.m2m.atl.common.ATLLogger;
import org.eclipse.m2m.atl.core.IModel;
import org.eclipse.m2m.atl.core.IReferenceModel;
import org.eclipse.m2m.atl.engine.emfvm.Messages;
import org.eclipse.m2m.atl.engine.emfvm.VMException;
import org.eclipse.m2m.atl.engine.emfvm.adapter.IModelAdapter;
import org.eclipse.m2m.atl.engine.emfvm.launch.ITool;
import org.eclipse.m2m.atl.engine.emfvm.lib.ASMModule;
import org.eclipse.m2m.atl.engine.emfvm.lib.AbstractStackFrame;
import org.eclipse.m2m.atl.engine.emfvm.lib.ExecEnv;
import org.eclipse.m2m.atl.engine.emfvm.lib.OclUndefined;
import org.eclipse.m2m.atl.engine.emfvm.lib.Operation;
import org.eclipse.m2m.atl.engine.emfvm.lib.TransientLink;
import org.eclipse.m2m.atl.engine.emfvm.lib.TransientLinkSet;
import org.eclipse.m2m.atl.engine.emfvm.parallelvm.TOperation;

public class TExecEnv extends ExecEnv{
	
	//We create here the thread manager for ATL rules
	private ExecutorService atlExecutor;
	public final int Default_NTHREADS = 8;
	public int numRules;
	private int numThreads = 0;
	private Semaphore endSemaphore;
	private boolean verbose = false;
	private int counter = 0;
	
	private ArrayList<Runnable> sleepingThreads = new ArrayList<Runnable>();
	private ArrayList<Runnable> pendingThreads = new ArrayList<Runnable>();

	//Here we override the operations related with TransientLinks and TransientLinkSets
	{
		// TransientLink
		ConcurrentMap<String, Operation> operationsByName;
		operationsByName = new ConcurrentHashMap<String, Operation>();
		vmTypeOperations.put(TransientLink.class, operationsByName);
		registerOperation(operationsByName, new Operation(2, "setRule") { //$NON-NLS-1$ 
			@Override
			public Object exec(AbstractStackFrame frame) {
				Object[] localVars = frame.getLocalVars();
				((TransientLink)localVars[0]).setRule((String)localVars[1]);
				return null;
			}
		});
		registerOperation(operationsByName, new Operation(3, "addSourceElement") { //$NON-NLS-1$ 
			@Override
			public Object exec(AbstractStackFrame frame) {
				Object[] localVars = frame.getLocalVars();
				((TransientLink)localVars[0]).getSourceElements().put(localVars[1], localVars[2]);
				return null;
			}
		});
		registerOperation(operationsByName, new Operation(3, "addTargetElement") { //$NON-NLS-1$ 
			@Override
			public Object exec(AbstractStackFrame frame) {
				Object[] localVars = frame.getLocalVars();
				TransientLink tl = (TransientLink)localVars[0];
				tl.getTargetElements().put(localVars[1], localVars[2]);
				tl.getTargetElementsList().add(localVars[2]);
				return null;
			}
		});
		registerOperation(operationsByName, new Operation(2, "getSourceElement") { //$NON-NLS-1$ 
			@Override
			public Object exec(AbstractStackFrame frame) {
				Object[] localVars = frame.getLocalVars();
				return ((TransientLink)localVars[0]).getSourceElements().get(localVars[1]);
			}
		});
		registerOperation(operationsByName, new Operation(2, "getTargetElement") { //$NON-NLS-1$ 
			@Override
			public Object exec(AbstractStackFrame frame) {
				Object[] localVars = frame.getLocalVars();
				Object ret = ((TransientLink)localVars[0]).getTargetElements().get(localVars[1]);
				if (ret == null) {
					ret = OclUndefined.SINGLETON;
				}
				return ret;
			}
		});
		registerOperation(operationsByName, new Operation(2, "getTargetFromSource") { //$NON-NLS-1$ 
			@Override
			public Object exec(AbstractStackFrame frame) {
				Object[] localVars = frame.getLocalVars();
				Object ret = ((TransientLink)localVars[0]).getTargetElementsList().iterator().next();
				if (ret == null) {
					ret = OclUndefined.SINGLETON;
				}
				return ret;
			}
		});
		registerOperation(operationsByName, new Operation(3, "getNamedTargetFromSource") { //$NON-NLS-1$ 
			@Override
			public Object exec(AbstractStackFrame frame) {
				Object[] localVars = frame.getLocalVars();
				Object ret = ((TransientLink)localVars[0]).getTargetElements().get(localVars[2]);
				if (ret == null) {
					ret = OclUndefined.SINGLETON;
				}
				return ret;
			}
		});
		registerOperation(operationsByName, new Operation(3, "addVariable") { //$NON-NLS-1$ 
			@Override
			public Object exec(AbstractStackFrame frame) {
				Object[] localVars = frame.getLocalVars();
				((TransientLink)localVars[0]).getVariables().put(localVars[1], localVars[2]);
				return null;
			}
		});
		registerOperation(operationsByName, new Operation(2, "getVariable") { //$NON-NLS-1$ 
			@Override
			public Object exec(AbstractStackFrame frame) {
				Object[] localVars = frame.getLocalVars();
				return ((TransientLink)localVars[0]).getVariables().get(localVars[1]);
			}
		});
		// TransientLinkSet
		operationsByName = new ConcurrentHashMap<String, Operation>();
		vmTypeOperations.put(TransientLinkSet.class, operationsByName);
		registerOperation(operationsByName, new Operation(2, "addLink") { //$NON-NLS-1$ 
			@Override
			public Object exec(AbstractStackFrame frame) {
				Object[] localVars = frame.getLocalVars();
				TransientLink tl = (TransientLink)localVars[1];
				TransientLinkSet tls = (TransientLinkSet)localVars[0];
				tls.addLink(tl);
				return null;
			}
		});
		registerOperation(operationsByName, new Operation(3, "addLink2") { //$NON-NLS-1$ 
			@Override
			/**
			 * This method needs to be synchronized as several method could trying to add
			 * trace links to the TransientLinkSet what modifies all the hash maps
			 */
			public Object exec(AbstractStackFrame frame) {
				Object[] localVars = frame.getLocalVars();
				TransientLink tl = (TransientLink)localVars[1];
				TransientLinkSet tls = (TransientLinkSet)localVars[0];
				boolean isDefault = ((Boolean)localVars[2]).booleanValue();
				synchronized (this) {
					tls.addLink2(tl, isDefault);
				}
				return null;
			}
		});
		registerOperation(operationsByName, new Operation(2, "getLinksByRule") { //$NON-NLS-1$ 
			@Override
			public Object exec(AbstractStackFrame frame) {
				Object[] localVars = frame.getLocalVars();
				return ((TransientLinkSet)localVars[0]).getLinksByRule(localVars[1]);
			}
		});
		registerOperation(operationsByName, new Operation(2, "getLinkBySourceElement") { //$NON-NLS-1$ 
			@Override
			public Object exec(AbstractStackFrame frame) {
				Object[] localVars = frame.getLocalVars();
				TransientLink ret = ((TransientLinkSet)localVars[0])
						.getLinkBySourceElement(localVars[1]);
				if (ret == null) {
					return OclUndefined.SINGLETON;
				} else {
					return ret;
				}
			}
		});
		operationsByName.put(
				"getLinkByRuleAndSourceElement", new Operation(3, "getLinkByRuleAndSourceElement") { //$NON-NLS-1$ //$NON-NLS-2$
					@Override
					public Object exec(AbstractStackFrame frame) {
						Object[] localVars = frame.getLocalVars();
						TransientLink ret = ((TransientLinkSet)localVars[0]).getLinkByRuleAndSourceElement(
								localVars[1], localVars[2]);
						if (ret == null) {
							return OclUndefined.SINGLETON;
						} else {
							return ret;
						}
					}
		});
		//We add this operation here but it should be added to the main ExecEnv so that we can use isEmpty everywhere
		operationsByName = new ConcurrentHashMap<String, Operation>();
		vmTypeOperations.put(AbstractList.class, operationsByName);
		registerOperation(operationsByName, new Operation(1, "isEmpty") { //$NON-NLS-1$ 
			@Override
			public Object exec(AbstractStackFrame frame) {
				Object[] localVars = frame.getLocalVars();
				return Boolean.valueOf(((Collection<?>)localVars[0]).isEmpty());
			}
		});
		//This operations enables the possibility to stop the current thread
		operationsByName = new ConcurrentHashMap<String, Operation>();
		vmTypeOperations.put(ASMModule.class, operationsByName);
		registerTOperation(operationsByName, new TOperation(1, "stopThread") { //$NON-NLS-1$ 
			@Override
			public Object exec(AbstractStackFrame frame, Runnable currentThread) {
				if (((TExecEnv)frame.getExecEnv()).numRules != 0){
				try {
					synchronized (currentThread) {
						sleepingThreads.add(currentThread);
						currentThread.wait();
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				}
				return null;
			}
		});
		registerTOperation(operationsByName, new TOperation(1, "decreaseCounter") { //$NON-NLS-1$ 
			@Override
			public Object exec(AbstractStackFrame frame, Runnable currentThread) {
				descreaseCounter();
				return null;
			}
		});
	}

	public TExecEnv(Map<String, IModel> models, ITool[] tools) {
		super(models, tools);
	}
	
	private void registerOperation(Map<String, Operation> map, Operation oper) {
		map.put(oper.getName(), oper);
	}
	
	private void registerTOperation(Map<String, Operation> map, TOperation oper) {
		map.put(oper.getName(), oper);
	}
	
	/**
	 * Initializes the execenv.
	 * It adds the initialization of the thread pool.
	 * 
	 * @param modelAdapterParam
	 *            the model adapter
	 */
	@Override
	public void init(IModelAdapter modelAdapterParam) {
		super.init(modelAdapterParam);
		//atlExecutor = Executors.newFixedThreadPool(NTHREADS);
	}
	
	public void init(IModelAdapter modelAdapterParam, int nThreads) {
		init(modelAdapterParam);
		atlExecutor = Executors.newFixedThreadPool(nThreads);
	}
	
	/**
	 * Creates a new element in the given frame.
	 * 
	 * @param frame
	 *            the frame context
	 * @param ec
	 *            the element type
	 * @param metamodelName
	 *            the metamodel name
	 * @return the new element
	 */
	public Object newElement(AbstractStackFrame frame, Object ec, String metamodelName) {
		Object s = null;
		IReferenceModel metamodel = (IReferenceModel)getModel(metamodelName);
		if (metamodel != null) {
			for (Iterator<IModel> i = getModels(); i.hasNext();) {
				IModel model = i.next();
				if (model.getReferenceModel().equals(metamodel) && model.isTarget()
						&& model.getReferenceModel().isModelOf(ec)) {
					synchronized (this){
						s = model.newElement(ec);
					}
					break;
				}
			}
		}
		if (s == null) {
			throw new VMException(frame,
					Messages.getString("ExecEnv.CANNOTCREATE", toPrettyPrintedString(ec))); //$NON-NLS-1$
		}
		return s;
	}
	
	/**
	 * Find an operation by its context type and name.
	 * 
	 * @param type
	 *            operation context type
	 * @param name
	 *            operation name
	 * @return the operation
	 */
	public Operation getOperation(Object type, Object name) {
		// note: debug is final, therefore there is no runtime penalty if it is false
		final boolean debug = false;
		Operation ret = null;
		
		Map<String, Operation> map = operationsByType.get(type);
		if (map == null || map.get(name) == null) {
			synchronized(operationsByType) {
				map = operationsByType.get(type);
				if (map == null) {
					Map<String, Operation> vmops = getVMOperations(type);
					if (((vmops != null) && !vmops.isEmpty())) {
						map = new HashMap<String, Operation>();
						operationsByType.put(type, map);
						if (vmops != null) {
							map.putAll(vmops);
						}	
					}
				}
			}
		}
		
		if (map != null)
			ret = map.get(name);
		
		if (debug) {
			ATLLogger.info(this + "@" + this.hashCode() + ".getOperation(" + type + ", " + name + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		}
		if (ret == null) {
			if (debug) {
				ATLLogger.info(Messages.getString("ExecEnv.LOOKINGSUPERTYPES", name)); //$NON-NLS-1$
			}
			for (Iterator<Object> i = modelAdapter.getSupertypes(type).iterator(); i.hasNext()
					&& (ret == null);) {
				Object st = i.next();
				ret = getOperation(st, name);
			}
			// let us remember this operation (remark: we could also precompute this for all types)
			if (map != null) {
				map.put(name.toString(), ret);
			} else {
				map = new HashMap<String, Operation>();
				map.put(name.toString(), ret);
				synchronized(operationsByType) {
					operationsByType.put(type, map);
				}
			}
		}
		return ret;
	}

	private Map<String, Operation> getVMOperations(Object type) {
		return vmTypeOperations.get(type);
	}
	
	/**
	 * Ends the execution.
	 */
	public void terminated() {
		
		//try {
			//endSemaphore.acquire((numRules*2)+1);
			try {
				if (verbose) System.out.println("permits: " + endSemaphore.availablePermits());
				endSemaphore.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (verbose) System.out.println("Semaphoro verde");
			atlExecutor.shutdown();
		
		
		terminateTools();
		// saving persistent weaving helpers
		if (verbose) System.out.println("TerminatingModels");
		for (Iterator<Entry<Object, Map<String, String>>> i = weavingHelperToPersistToByType.entrySet()
				.iterator(); i.hasNext();) {
			Entry<Object, Map<String, String>> entry = i.next();
			Map<String, String> weavingHelperToPersistTo = entry.getValue();
			if (weavingHelperToPersistTo != null) {
				Object type = entry.getKey();
				if (modelAdapter.isModelElement(type)) {
					persistWeavingHelpers(type, weavingHelperToPersistTo);
				} else {
					// can only persist for model elements
				}
			}
		}
		for (IModel model : modelsByName.values()) {
				modelAdapter.finalizeModel(model);	
		}
	}

	public ExecutorService getAtlExecutor() {
		return atlExecutor;
	}

	public void setAtlExecutor(ExecutorService atlExecutor) {
		this.atlExecutor = atlExecutor;
	}
	
	public int getNumRules() {
		return numRules;
	}

	public void setNumRules(int numRules) {
		this.numRules = numRules;
	}
	
	public void descreaseCounter(){
		synchronized(this){
			numRules--;
			if (verbose) System.out.println("counter = " + numRules);
			if (numRules == 0 && sleepingThreads.size() > 0){
				numRules--;
				notifySleepers();
			}
		}
	}
	
	public void notifySleepers(){
		if (verbose) System.out.println("GoingToNotifyApplySleeper");
		for(Runnable r : sleepingThreads){
			synchronized (r) {
				if (verbose) System.out.println("TryToNotify");
				r.notify();
			}
		}
	}
	
	public void addPendingThread(Runnable r){
		pendingThreads.add(r);
	}
	
	public void removePendingThread(Runnable r){
		pendingThreads.remove(r);
	}
	
	public int getNumThreads() {
		return numThreads;
	}

	public void setNumThreads(int numThreads) {
		this.numThreads = numThreads;
	}
	
	public Semaphore getEndSemaphore() {
		return endSemaphore;
	}

	public void initEndSemaphore() {
		if (verbose) System.out.println("NumRules = " + numRules);
		this.endSemaphore = new Semaphore(-1*((numRules*2)));
	}
	
	public void sRelease(){
		if (verbose) System.out.println("releasingPermits current" + this.endSemaphore.availablePermits());
		this.endSemaphore.release();
		if (verbose) System.out.println("releasingPermits after" + this.endSemaphore.availablePermits());
	}
}
