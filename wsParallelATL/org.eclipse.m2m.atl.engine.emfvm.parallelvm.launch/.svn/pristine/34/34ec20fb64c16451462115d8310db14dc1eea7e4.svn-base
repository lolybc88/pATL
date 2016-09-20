package org.eclipse.m2m.atl.engine.emfvm.parallelvm.launch;

import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.m2m.atl.common.ATLLaunchConstants;
import org.eclipse.m2m.atl.engine.emfvm.VMException;
import org.eclipse.m2m.atl.engine.emfvm.launch.ITool;
import org.eclipse.m2m.atl.engine.emfvm.launch.debug.NetworkDebugger;



public class EMFPVMUILauncher extends EMFPVMLauncher{
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.m2m.atl.engine.emfvm.launch.EMFVMLauncher#launch(java.lang.String,
	 *      org.eclipse.core.runtime.IProgressMonitor, java.util.Map, java.lang.Object[])
	 */
	@Override
	public Object launch(final String mode, final IProgressMonitor monitor,
			final Map<String, Object> options, final Object... modules) {
		try {
			if (mode.equals(ILaunchManager.DEBUG_MODE)) {
				return internalLaunch(new ITool[] {new NetworkDebugger(
						getPort((ILaunch)options.get("launch")), true),}, monitor, options, modules); //$NON-NLS-1$
			} else {
				return internalLaunch(null, monitor, options, modules);
			}
		} catch (CoreException e) {
			throw new VMException(null, e.getLocalizedMessage(), e);
		}
	}

	private int getPort(ILaunch launch) throws CoreException {
		String portOption = ""; //$NON-NLS-1$
		if (launch != null) {
			portOption = launch.getLaunchConfiguration().getAttribute(ATLLaunchConstants.PORT,
					Integer.valueOf(ATLLaunchConstants.DEFAULT_PORT).toString());
		}
		if (portOption.equals("")) { //$NON-NLS-1$
			portOption = Integer.valueOf(ATLLaunchConstants.DEFAULT_PORT).toString();
		}
		return new Integer(portOption).intValue();
	}
	
	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.m2m.atl.core.launch.ILauncher#getModes()
	 */
	@Override
	public String[] getModes() {
		return new String[]{RUN_MODE, DEBUG_MODE,};
	}
}


