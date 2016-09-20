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
package org.eclipse.m2m.atl.common;

/**
 * This interface contains the common constants used for launch ATL transformations in the Eclipse UI.
 * 
 * @author <a href="mailto:william.piers@obeo.fr">William Piers</a>
 */
public interface ATLLaunchConstants {

	/** The ATL default compiler version. */
	String DEFAULT_COMPILER_VERSION = "1.0"; //$NON-NLS-1$
	
	/** ATL launch type. */
	String LAUNCH_CONFIGURATION_TYPE = "org.eclipse.m2m.atl.adt.launching.atlTransformation"; //$NON-NLS-1$

	/** Regular VM name, for launch compatibility adaptations. */
	String REGULAR_VM_NAME = "Regular VM"; //$NON-NLS-1$

	/** EMF VM name, for default values. */
	String EMF_VM_NAME = "EMF-specific VM"; //$NON-NLS-1$

	/** Default model handler name. */
	String DEFAULT_MODEL_HANDLER_NAME = "EMF"; //$NON-NLS-1$

	/** ModelHandler option. */
	String OPTION_MODEL_HANDLER = "modelHandlers"; //$NON-NLS-1$

	/** ATL VM name. */
	String ATL_VM = "ATL VM"; //$NON-NLS-1$

	/** ATL File Name. */
	String ATL_FILE_NAME = "ATL File Name"; //$NON-NLS-1$

	/** Output models. */
	String OUTPUT = "Output"; //$NON-NLS-1$

	/** Ordered output models list. */
	String ORDERED_OUTPUT = "OrderedOutput"; //$NON-NLS-1$

	/** Input models. */
	String INPUT = "Input"; //$NON-NLS-1$

	/** Ordered input models list. */
	String ORDERED_INPUT = "OrderedInput"; //$NON-NLS-1$

	/** Null parameter. */
	String NULL_PARAMETER = "<null>"; //$NON-NLS-1$

	/** Path map. */
	String PATH = "Path"; //$NON-NLS-1$

	/** Debug host. */
	String HOST = "Host"; //$NON-NLS-1$

	/** Debug port. */
	String PORT = "Port"; //$NON-NLS-1$

	/** Default debug port. */
	int DEFAULT_PORT = 6060;

	/** Libraries map. */
	String LIBS = "Libs"; //$NON-NLS-1$

	/** Output model. */
	String IS_REFINING = "IS_REFINING"; //$NON-NLS-1$

	/** ATL Compiler. */
	String ATL_COMPILER = "ATL_COMPILER"; //$NON-NLS-1$

	/** Output model. */
	String MODEL_OUTPUT = "MODELOUTPUT"; //$NON-NLS-1$

	/** Input model. */
	String MODEL_INPUT = "MODELINPUT"; //$NON-NLS-1$

	/** Output metamodel. */
	String METAMODEL_OUTPUT = "METAMODELOUTPUT"; //$NON-NLS-1$

	/** Input metamodel. */
	String METAMODEL_INPUT = "METAMODELINPUT"; //$NON-NLS-1$

	/** Superimposed modules. */
	String SUPERIMPOSE = "Superimpose"; //$NON-NLS-1$

	/** Stop in main option (for debug mode). */
	String STOP_IN_MAIN = "stopInMain"; //$NON-NLS-1$
	
	/** Superimposed modules. */
	String OPTIONS = "Options"; //$NON-NLS-1$

	/** Model type. */
	String MODEL_TYPE = "ModelType"; //$NON-NLS-1$

	/** Model handler. */
	String MODEL_HANDLER = "Model Handler"; //$NON-NLS-1$

	/** ATL icon path. */
	String ATL_ICON_PATH = "atl_logo.gif"; //$NON-NLS-1$

	/** Delete icon path. */
	String DELETE_ICON_PATH = "delete_obj.gif"; //$NON-NLS-1$

	/** ATL extensions. */
	String[] ATL_EXTENSIONS = new String[] {"atl", "acg"}; //$NON-NLS-1$ //$NON-NLS-2$

	/** The ATL file. */
	String OPTION_ATL_FILE_PATH = "ATL_FILE"; //$NON-NLS-1$

}
