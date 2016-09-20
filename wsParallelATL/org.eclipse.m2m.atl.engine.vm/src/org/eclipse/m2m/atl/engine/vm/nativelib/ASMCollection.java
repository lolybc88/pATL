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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.m2m.atl.engine.vm.ASMExecEnv;
import org.eclipse.m2m.atl.engine.vm.Operation;
import org.eclipse.m2m.atl.engine.vm.StackFrame;

/**
 * @author <a href="mailto:frederic.jouault@univ-nantes.fr">Frederic Jouault</a>
 */
public abstract class ASMCollection extends ASMOclAny {

	public static ASMOclType myType = ASMOclParametrizedType.getASMOclParametrizedType("Collection", getOclAnyType(), getOclAnyType());

	public ASMCollection(ASMOclType type) {
		super(type);
	}

	public abstract Iterator iterator();

	public abstract Collection collection();
	
	public ASMOclAny get(StackFrame frame, String name) {
		frame.printStackTrace("Collections do not have properties, use ->collect()");
		return null;
	}

	public int size() {
		return collection().size();
	}

	public abstract void add(ASMOclAny o);

	// Native Operations below

	public static ASMInteger size(StackFrame frame, ASMCollection self) {
		return new ASMInteger(self.collection().size());
	}

	public static ASMBoolean includes(StackFrame frame, ASMCollection self, ASMOclAny o) {
		return new ASMBoolean(self.collection().contains(o));
	}

	public static ASMBoolean excludes(StackFrame frame, ASMCollection self, ASMOclAny o) {
		return new ASMBoolean(!self.collection().contains(o));
	}
	
	public static ASMInteger count(StackFrame frame, ASMCollection self, ASMOclAny object) {
		int ret = 0;
		
		Collection c = self.collection();
		
		if(c instanceof Set) {
			ret = c.contains(object) ? 1 : 0;
		} else {
			for(Iterator i = c.iterator() ; i.hasNext() ; ) {
				ASMOclAny o = (ASMOclAny)i.next();
				if(object.equals(o)) {
					ret++;
				}
			}
		}
		
		return new ASMInteger(ret);
	}

	public static ASMBoolean includesAll(StackFrame frame, ASMCollection self, ASMCollection o) {
		return new ASMBoolean(self.collection().containsAll(o.collection()));
	}

	public static ASMBoolean excludesAll(StackFrame frame, ASMCollection self, ASMCollection o) {
		boolean ret = true;
		
		for(Iterator i = o.iterator() ; i.hasNext() ; ) {
			Object object = i.next();
			ret = ret && !self.collection().contains(object);
		}
		
		return new ASMBoolean(ret);
	}
	
	public static ASMBoolean isEmpty(StackFrame frame, ASMCollection self) {
		return new ASMBoolean(self.collection().size() == 0);
	}
	
	public static ASMBoolean notEmpty(StackFrame frame, ASMCollection self) {
		return new ASMBoolean(self.collection().size() != 0);
	}
	
	public static ASMOclAny sum(StackFrame frame, ASMCollection self) {
		ASMOclAny ret = null;
		
		for(Iterator i = self.iterator() ; i.hasNext() ; ) {
			ASMOclAny o = (ASMOclAny)i.next();
			if(ret == null) {
				ret = o;
			} else {
				Operation oper = ((ASMExecEnv)frame.getExecEnv()).getOperation(ret.getType(), "+");
				ArrayList arguments = new ArrayList();
				
				arguments.add(ret);
				arguments.add(o);

				if(oper != null) {
					ret = oper.exec(frame.enterFrame(oper, arguments));
				} else {
					frame.printStackTrace("Could not find operation + on " + ret.getType() + " having supertypes: " + ret.getType().getSupertypes());
				}

			}
		}
		
		return ret;
	}

	// TODO: product(c2: Collection(T2)) : Set( Tuple( first: T, second: T2) )
	
	public static ASMBag asBag(StackFrame frame, ASMCollection self) {
		return new ASMBag(self.collection());
	}

	public static ASMSequence asSequence(StackFrame frame, ASMCollection self) {
		return new ASMSequence(self.collection());
	}

  	public static ASMOrderedSet asOrderedSet(StackFrame frame, ASMCollection self) {
		return new ASMOrderedSet(self.collection());
	}

	public static ASMSet asSet(StackFrame frame, ASMCollection self) {
		return new ASMSet(self.collection());
	}

}

