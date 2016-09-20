package org.atl.engine.vm.nativelib;

import java.util.HashMap;
import java.util.Map;

import org.atl.engine.vm.ASM;
import org.atl.engine.vm.StackFrame;

/**
 * @author Fr�d�ric Jouault
 */
public class ASMModule extends ASMOclAny {

	public static ASMOclType myType = new ASMOclSimpleType("Module", getOclAnyType());
	public ASMModule(ASM asm) {
		super(myType);
		this.asm = asm;
	}

	public ASMOclAny get(StackFrame frame, String name) {
		ASMOclAny ret = (ASMOclAny)fields.get(name);
		
		if(ret == null) {
			frame.printStackTrace("transformation module \"" + asm.getName() +
					"\" does not have an initialized field named \"" + name + "\"");
		}
		
		return ret;
	}

	public void set(StackFrame frame, String name, ASMOclAny value) {
		fields.put(name, value);
	}

	public String toString() {
		return asm.getName() + " : ASMModule";
	}

	public String getName() {
		return asm.getName();
	}

	private Map fields = new HashMap();
	private ASM asm;
}

