/*******************************************************************************
 * Copyright (c) 2008, 2012 Obeo.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Obeo - initial API and implementation
 *******************************************************************************/
package org.eclipse.m2m.atl.core.launch;

import java.io.InputStream;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.m2m.atl.core.IModel;

/**
 * The ILauncher interface defines a transformation launcher, and a set of associated options. To use all
 * launchers in a generic way, you can use the {@link LauncherService} class.
 * 
 * @author <a href="mailto:william.piers@obeo.fr">William Piers</a>
 */
public interface ILauncher {

	/**
	 * A launch in a normal, non-debug mode(value <code>"run"</code>).
	 */
	String RUN_MODE = "run"; //$NON-NLS-1$

	/**
	 * A launch in a special debug mode (value <code>"debug"</code>).
	 */
	String DEBUG_MODE = "debug"; //$NON-NLS-1$

	/**
	 * Returns the {@link ILauncher} name.
	 * 
	 * @return the {@link ILauncher} name
	 */
	String getName();

	/**
	 * Adds an input model to the transformation context. This method is also used to load the metamodels used
	 * in this transformation.
	 * 
	 * @param model
	 *            the loaded model
	 * @param name
	 *            the name of the model as described in the main module
	 * @param referenceModelName
	 *            the name of the metamodel as described in the main module
	 */
	void addInModel(IModel model, String name, String referenceModelName);

	/**
	 * Adds an output model to the transformation context.
	 * 
	 * @param model
	 *            the loaded model
	 * @param name
	 *            the name of the model as described in the main module
	 * @param referenceModelName
	 *            the name of the metamodel as described in the main module
	 */
	void addOutModel(IModel model, String name, String referenceModelName);

	/**
	 * Adds an input/output model to the transformation context.
	 * 
	 * @param model
	 *            the loaded model
	 * @param name
	 *            the name of the model as described in the main module
	 * @param referenceModelName
	 *            the name of the metamodel as described in the main module
	 */
	void addInOutModel(IModel model, String name, String referenceModelName);

	/**
	 * Adds a preloaded library module to the transformation, or an {@link InputStream}.
	 * 
	 * @param library
	 *            the loaded library
	 * @param name
	 *            the name of the library as described in the main module
	 */
	void addLibrary(String name, Object library);

	/**
	 * Initialize the launcher.
	 * 
	 * @param options
	 *            initialization options
	 */
	void initialize(Map<String, Object> options);

	/**
	 * Launches the transformation using the given parameters and the given set of preloaded modules, or
	 * {@link InputStream}.
	 * 
	 * @param mode
	 *            the launching mode
	 * @param monitor
	 *            the progress monitor
	 * @param options
	 *            vm options
	 * @param modules
	 *            single module/ordered module set. A module set is used for superimposition, where the first
	 *            module of the set is override by the next ones. A module can be passed as an InputStream or
	 *            directly a module loaded by the loadModule method.
	 * @return the transformation return result
	 */
	Object launch(String mode, IProgressMonitor monitor, Map<String, Object> options, Object... modules);

	/**
	 * Loads a transformation module from an {@link InputStream}.
	 * 
	 * @param inputStream
	 *            the input stream to load
	 * @return the loaded module
	 */
	Object loadModule(InputStream inputStream);

	/**
	 * Returns a previously added model with the given name.
	 * 
	 * @param modelName
	 *            the model name
	 * @return a previously added model with the given name
	 */
	IModel getModel(String modelName);

	/**
	 * Returns a previously added library with the given name.
	 * 
	 * @param libraryName
	 *            the library name
	 * @return a previously added library with the given name
	 */
	Object getLibrary(String libraryName);

	/**
	 * Returns the default ModelFactory name.
	 * 
	 * @return the default ModelFactory name
	 */
	String getDefaultModelFactoryName();

	/**
	 * Returns the supported modes.
	 * 
	 * @return the supported modes
	 */
	String[] getModes();

}
