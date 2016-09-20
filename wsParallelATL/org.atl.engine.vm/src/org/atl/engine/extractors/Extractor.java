package org.atl.engine.extractors;

import java.io.OutputStream;
import java.util.Map;

import org.atl.engine.vm.nativelib.ASMModel;

/**
 * @author Fr�d�ric Jouault
 */
public interface Extractor {

	/*	New Extractor interface. */

	/**
	 * returns the list of parameters supported by this extractor
	 * in the form of a Map with parameter names as keys and type
	 * name as value.
	 * Known type names are:
	 * 		"String"
	 * 		"Model" 
	 */
	public Map getParameterTypes();

	/**
	 * Performs the extraction.
	 * @param source The model to extract.
	 * @param target The target OutputStream. Note that other target kinds
	 * 				can be used using params.
	 * @param params A Map of additional parameters. The key is the name of the
	 * 				parameter.
	 */
	public void extract(ASMModel source, OutputStream target, Map params);

	/* Old Extractor interface. */
	
	public String getPrefix();

	public void extract(ASMModel format, ASMModel extent, OutputStream out);
}
