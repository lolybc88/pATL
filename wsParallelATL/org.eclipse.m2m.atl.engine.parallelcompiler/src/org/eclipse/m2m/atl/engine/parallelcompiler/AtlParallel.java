package org.eclipse.m2m.atl.engine.parallelcompiler;

import java.net.URL;

import org.eclipse.m2m.atl.engine.compiler.AtlDefaultCompiler;

public class AtlParallel extends AtlDefaultCompiler{

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.m2m.atl.engine.compiler.AtlDefaultCompiler#getCodegeneratorURL()
	 */
	protected URL getCodegeneratorURL() {
		return AtlParallel.class.getResource("resources/ATLToASMCompiler.asm");
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.m2m.atl.engine.compiler.AtlDefaultCompiler#getSemanticAnalyzerURL()
	 */
	protected URL getSemanticAnalyzerURL() {
		return AtlParallel.class.getResource("resources/ATL-WFR.asm");
	}

}
