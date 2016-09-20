/*******************************************************************************
 * Copyright (c) 2004 INRIA.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * 	   Frederic Jouault (INRIA) - initial API and implementation
 *******************************************************************************/
package org.eclipse.m2m.atl.engine.vm.nativelib;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.m2m.atl.engine.vm.StackFrame;

/**
 * A non-OCL type. Proves useful in some transformations to establish mappings.
 * @author <a href="mailto:frederic.jouault@univ-nantes.fr">Frederic Jouault</a>
 */
public class ASMMap extends ASMOclAny {

	public static ASMOclType myType = new ASMOclSimpleType("Map", getOclAnyType());	// TODO : type parametre

	public ASMMap() {
		super(myType);
		s = new HashMap();
	}

	public ASMMap(Map init) {
		super(myType);
		s = new HashMap(init);
	}

	public String toString() {
		StringBuffer ret = new StringBuffer();

		ret.append("Map {");
		for(Iterator i = s.keySet().iterator() ; i.hasNext() ; ) {
			Object n = i.next();
			Object o = s.get(n);
			ret.append('(');
			ret.append(n);
			ret.append(" , ");
			ret.append(o);
			ret.append(')');
			if(i.hasNext()) ret.append(", ");
		}
		ret.append("}");

		return ret.toString();
	}

	public boolean equals(Object o) {
		return (o instanceof ASMMap) && (((ASMMap)o).s.equals(s));
	}

	public int hashCode() {
		return s.hashCode();
	}

	public void put(ASMOclAny key, ASMOclAny value) {
		s.put(key, value);
	}

	public ASMOclAny get(ASMOclAny key) {
		return (ASMOclAny)s.get(key);
	}

	public Iterator getKeys() {
		return s.keySet().iterator();
	}

	// Native Operations below

	public static ASMOclAny get(StackFrame frame, ASMMap self, ASMOclAny key) {
		ASMOclAny ret = (ASMOclAny)self.s.get(key);

		if(ret == null) {
			ret = new ASMOclUndefined();
		}

		return ret;
	}

	public static ASMMap including(StackFrame frame, ASMMap self, ASMOclAny key, ASMOclAny value) {
		ASMMap ret = new ASMMap(self.s);

		ret.s.put(key, value);

		return ret;
	}

	public static ASMBoolean containsKey(StackFrame frame, ASMMap self, ASMOclAny o) {
		return new ASMBoolean(self.s.containsKey(o));
	}
	
	public static ASMBoolean containsValue(StackFrame frame, ASMMap self, ASMOclAny o) {
		return new ASMBoolean(self.s.containsValue(o));
	}
	
	public static ASMMap union(StackFrame frame, ASMMap self, ASMMap other) {
		ASMMap ret = new ASMMap(self.s);

		ret.s.putAll(other.s);

		return ret;
	}

	public static ASMSet getKeys(StackFrame frame, ASMMap self) {
		ASMSet ret = new ASMSet(self.s.keySet());
		return ret;
	}

	public static ASMBag getValues(StackFrame frame, ASMMap self) {
		ASMBag ret = new ASMBag(self.s.values());
		return ret;
	}

	private Map s;
}

