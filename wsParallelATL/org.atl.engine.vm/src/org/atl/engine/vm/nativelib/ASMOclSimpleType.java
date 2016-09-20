package org.atl.engine.vm.nativelib;

import java.util.Iterator;

import org.atl.engine.vm.StackFrame;

/**
 * An OclType for simple types.
 * @author Fr�d�ric Jouault
 */
public class ASMOclSimpleType extends ASMOclType {

	public ASMOclSimpleType() {
		super(myType);
	}

	public ASMOclSimpleType(String name) {
		this();
		setName(name);
	}

	public ASMOclSimpleType(String name, ASMOclType supertype) {
		this();
		this.name = name;
		addSupertype(supertype);
	}

	public String toString() {
		return name;
	}

	public String getName() {
		return name;
	}

	public int hashCode() {
		return ("OST" + name).hashCode();
	}
	
	public boolean equals(Object other) {
		return (other instanceof ASMOclSimpleType) && (name.equals(((ASMOclSimpleType)other).name));
	}

	public ASMBoolean conformsTo(ASMOclType other) {
		boolean ret = equals(other);
		
		if(!ret) {
			for(Iterator i = getSupertypes().iterator() ; i.hasNext() ; ) {
				ASMOclType t = (ASMOclType)i.next();
				ret = t.conformsTo(other).getSymbol();
			}
		}
		
		return new ASMBoolean(ret);	// TODO
	}
	
	public void setName(String name) {
		this.name = name;
	}

	// Native Operations below

	public static void setName(StackFrame frame, ASMOclSimpleType self, ASMString name) {
		self.setName(name.getSymbol());
	}

	private String name = "<unnamedyet>";
}

