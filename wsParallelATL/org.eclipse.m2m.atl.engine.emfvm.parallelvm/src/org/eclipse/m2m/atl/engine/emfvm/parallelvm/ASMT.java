package org.eclipse.m2m.atl.engine.emfvm.parallelvm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.m2m.atl.common.ATLLogger;
import org.eclipse.m2m.atl.core.IModel;
import org.eclipse.m2m.atl.engine.emfvm.ASM;
import org.eclipse.m2m.atl.engine.emfvm.AtlSuperimposeModule;
import org.eclipse.m2m.atl.engine.emfvm.AtlSuperimposeModule.AtlSuperimposeModuleException;
import org.eclipse.m2m.atl.engine.emfvm.Messages;
import org.eclipse.m2m.atl.engine.emfvm.VMException;
import org.eclipse.m2m.atl.engine.emfvm.adapter.IModelAdapter;
import org.eclipse.m2m.atl.engine.emfvm.launch.ITool;
import org.eclipse.m2m.atl.engine.emfvm.lib.ASMModule;
import org.eclipse.m2m.atl.engine.emfvm.lib.AbstractStackFrame;
import org.eclipse.m2m.atl.engine.emfvm.lib.LibExtension;
import org.eclipse.m2m.atl.engine.emfvm.lib.Operation;
import org.eclipse.m2m.atl.engine.emfvm.parallelvm.lib.TExecEnv;

public class ASMT extends ASM{
	
	private ASMTOperation mainOperation;
	
	// TODO analyze:
		// - implements other options
		// - define options somewhere (currently, best definition is in regular VM)
		/**
		 * Launches the ASM.
		 * 
		 * @param tools
		 *            the execution tools
		 * @param models
		 *            the model map
		 * @param libraries
		 *            the library map
		 * @param superimpose
		 *            the superimpose list
		 * @param options
		 *            the option map
		 * @param monitor
		 *            the progress monitor
		 * @param modelAdapter
		 *            the {@link IModelAdapter} to use
		 * @return the execution result
		 */
		public Object run(ITool[] tools, Map<String, IModel> models, Map<String, ASM> libraries,
				List<ASM> superimpose, Map<String, Object> options, IProgressMonitor monitor,
				IModelAdapter modelAdapter) {
			Object ret = null;

			boolean printExecutionTime = "true".equals(options.get("printExecutionTime")); //$NON-NLS-1$ //$NON-NLS-2$
			long startTime = System.currentTimeMillis();
			
			TExecEnv execEnv = new TExecEnv(models, tools);
			
			if (options.get("numCores") != null){
				execEnv.init(modelAdapter, (Integer)options.get("numCores"));
			}else{
				execEnv.init(modelAdapter, execEnv.Default_NTHREADS);
			}

			if ("true".equals(options.get("step"))) { //$NON-NLS-1$ //$NON-NLS-2$
				execEnv.setStep(true);
			}
			
			

			for (LibExtension extension : getAllExtensions(options)) {
				extension.apply(execEnv, options);
			}

			addAllTypesExtensions(options);

			ASMModule asmModule = new ASMModule(getName());

			List<Object> localVars = null;
			if (!mainOperation.getParameters().isEmpty()) {
				localVars = new ArrayList<Object>();
				localVars.add(asmModule);
				for (Iterator<String> i = mainOperation.getParameters().iterator(); i.hasNext();) {
					String pname = i.next();
					pname = mainOperation.resolveVariableName(Integer.parseInt(pname), 0);
					localVars.add(options.get(pname));
				}
			}

			TStackFrame frame = new TStackFrame(execEnv, asmModule, mainOperation);

			if (localVars != null) {
				frame.setLocalVars(localVars.toArray());
			}

			for (Iterator<ASM> i = libraries.values().iterator(); i.hasNext();) {
				ASM library = i.next();
				registerOperations(execEnv, library.getOperations());
				if (library.getMainOperation() != null) {
					AbstractStackFrame rootFrame = new TStackFrame(execEnv, asmModule, library.getMainOperation());
					library.getMainOperation().exec(rootFrame.enter());
					rootFrame.leave();
				}
			}

			// register module operations after libraries to avoid overriding
			// "main" in execEnv (avoid superimposition problems)
			registerOperations(execEnv, operations.iterator());

			for (Iterator<ASM> i = superimpose.iterator(); i.hasNext();) {
				ASM module = i.next();
				AtlSuperimposeModule ami = new AtlSuperimposeModule(execEnv, module);
				try {
					ami.adaptModuleOperations();
				} catch (AtlSuperimposeModuleException e) {
					throw new VMException(frame, e.getLocalizedMessage(), e);
				}
				registerOperations(execEnv, module.getOperations());
			}
			
			//Here, get call the compiler operation to count the rules
			Operation op = execEnv.getOperation(asmModule.getClass(), "countRules");
			TStackFrame opFrame = new TStackFrame(execEnv, asmModule, op);
			op.exec(opFrame);
			Map<Object, Object> fields = asmModule.asMap();
			execEnv.setNumRules((Integer) fields.get(new String("numRules")));
			execEnv.initEndSemaphore();
			
			
			
			ret = mainOperation.exec(frame.enter(), monitor, null);
			frame.leave();
			execEnv.terminated();
			long endTime = System.currentTimeMillis();
			if (printExecutionTime) {
				ATLLogger.info(Messages.getString(
						"ASM.EXECUTIONTIME", name, new Double((endTime - startTime) / 1000.))); //$NON-NLS-1$
			}
			if ("true".equals(options.get("showSummary"))) { //$NON-NLS-1$ //$NON-NLS-2$
				ATLLogger.info(Messages.getString(
						"ASM.INSTRUCTIONSCOUNT", new Double(execEnv.getNbExecutedBytecodes()))); //$NON-NLS-1$
			}
			return ret;
		}
		
		/**
		 * Adds an operation.
		 * 
		 * @param operation
		 *            the operation to add
		 */
		public void addOperation(ASMTOperation operation) {
			operations.add(operation);
			if (operation.getName().equals("main") && operation.getContext().equals("A")) { //$NON-NLS-1$ //$NON-NLS-2$
				mainOperation = operation;
			}
		}

}
