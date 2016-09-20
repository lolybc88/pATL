package org.eclipse.m2m.atl.engine.emfvm.parallelvm.launch;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.m2m.atl.core.launch.ILauncher;
import org.eclipse.m2m.atl.core.service.LauncherService;
import org.eclipse.m2m.atl.engine.emfvm.ASM;
import org.eclipse.m2m.atl.engine.emfvm.adapter.EMFModelAdapter;
import org.eclipse.m2m.atl.engine.emfvm.adapter.IModelAdapter;
import org.eclipse.m2m.atl.engine.emfvm.adapter.UML2ModelAdapter;
import org.eclipse.m2m.atl.engine.emfvm.launch.EMFVMLauncher;
import org.eclipse.m2m.atl.engine.emfvm.launch.ITool;
import org.eclipse.m2m.atl.engine.emfvm.parallelvm.ASMT;
import org.eclipse.m2m.atl.engine.emfvm.parallelvm.ASMTXMLReader;

public class EMFPVMLauncher extends EMFVMLauncher{
	
	/**
	 * Launches the transformation with preloaded modules.
	 * 
	 * @param tools
	 *            the execution tools
	 * @param monitor
	 *            the progression monitor
	 * @param options
	 *            the launching options
	 * @param modules
	 *            the transformation modules
	 * @return the execution result
	 */
	protected Object internalLaunch(ITool[] tools, final IProgressMonitor monitor,
			final Map<String, Object> options, Object... modules) {
		List<ASM> superimpose = new ArrayList<ASM>();
		ASMT mainModule = getASMFromObject(modules[0]);
		for (int i = 1; i < modules.length; i++) {
			superimpose.add(getASMFromObject(modules[i]));
		}
		IModelAdapter modelAdapter;
		if (LauncherService.getBooleanOption(options.get("supportUML2Stereotypes"), false)) { //$NON-NLS-1$ 
			modelAdapter = new UML2ModelAdapter();
		} else {
			modelAdapter = new EMFModelAdapter();
		}
		modelAdapter.setAllowInterModelReferences(LauncherService.getBooleanOption(options
				.get("allowInterModelReferences"), false)); //$NON-NLS-1$ 	
		return mainModule.run(tools, models, libraries, superimpose, options, monitor, modelAdapter);
	}
	
	/**
	 * Load a module if necessary.
	 * 
	 * @param module
	 *            the given {@link ASM} or {@link InputStream}.
	 * @return the {@link ASM}
	 */
	protected ASMT getASMFromObject(Object module) {
		if (module instanceof InputStream) {
			return (ASMT)loadModule((InputStream)module);
		} else if (module instanceof ASMT) {
			return (ASMT)module;
		}
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.m2m.atl.core.launch.ILauncher#loadModule(java.io.InputStream)
	 */
	public Object loadModule(InputStream inputStream) {
		return new ASMTXMLReader().read(inputStream);
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.m2m.atl.core.launch.ILauncher#getDefaultModelFactoryName()
	 */
	public String getDefaultModelFactoryName() {
		return MODEL_FACTORY_NAME;
	}

}
