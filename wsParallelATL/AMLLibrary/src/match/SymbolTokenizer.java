/*******************************************************************************

 * Copyright (c) 2009 Ecole des Mines de Nantes.

 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kelly Garces - initial API and implementation and/or initial documentation
 *******************************************************************************/ 

package match;

import java.util.StringTokenizer;

abstract public class SymbolTokenizer implements Tokenizer {
	
	private String symbol;
	
		
	SymbolTokenizer (String symbolArg) {
		symbol = symbolArg;
	}
	
	public String tokenize (String cad) {
		
		StringTokenizer s = new StringTokenizer(cad, symbol);
		String outCad = "";
		while (s.hasMoreTokens()) {
	         outCad = outCad + " " + s.nextToken() ;
	     }
		
		return outCad.trim().toLowerCase();
	}
	
	
		
}
