package org.atl.engine.vm.nativelib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.atl.engine.vm.ASM;
import org.atl.engine.vm.ASMEmitter;
import org.atl.engine.vm.ASMExecEnv;
import org.atl.engine.vm.ASMStackFrame;
import org.atl.engine.vm.NativeOperation;
import org.atl.engine.vm.NativeStackFrame;
import org.atl.engine.vm.Operation;
import org.atl.engine.vm.StackFrame;

/**
 * @author Fr�d�ric Jouault
 */
public abstract class ASMOclType extends ASMOclAny {

	public static ASMOclType myType = new ASMOclSimpleType("OclType");

	private static Map typeOperations;
	
	public static Map getVMOperations() {
		if(typeOperations == null) {
			typeOperations = new HashMap();
			NativeOperation.registerOperations(ASMOclType.myType, ASMOclType.class);
			myType.setType(myType);
			NativeOperation.registerOperations(ASMOclType.myType, ASMOclSimpleType.class);

			NativeOperation.registerOperations(ASMTupleType.myType, ASMTupleType.class);
			NativeOperation.registerOperations(ASMTuple.myType, ASMTuple.class);
			NativeOperation.registerOperations(ASMTransientLinkSet.myType, ASMTransientLinkSet.class);
			NativeOperation.registerOperations(ASMTransientLink.myType, ASMTransientLink.class);
			NativeOperation.registerOperations(ASMString.myType, ASMString.class);
			NativeOperation.registerOperations(ASMSet.myType, ASMSet.class);
			NativeOperation.registerOperations(ASMSequence.myType, ASMSequence.class);
			NativeOperation.registerOperations(ASMNumber.myType, ASMReal.class);
			NativeOperation.registerOperations(ASMOclUndefined.myType, ASMOclUndefined.class);
			NativeOperation.registerOperations(ASMOclParametrizedType.myType, ASMOclParametrizedType.class);
			NativeOperation.registerOperations(getOclAnyType(), ASMOclAny.class);
			NativeOperation.registerOperations(ASMMap.myType, ASMMap.class);
			NativeOperation.registerOperations(ASMInteger.myType, ASMInteger.class);
			NativeOperation.registerOperations(ASMEnumLiteral.myType, ASMEnumLiteral.class);
			NativeOperation.registerOperations(ASMCollection.myType, ASMCollection.class);
			NativeOperation.registerOperations(ASMBoolean.myType, ASMBoolean.class);
			NativeOperation.registerOperations(ASMBag.myType, ASMBag.class);
			NativeOperation.registerOperations(ASMOrderedSet.myType, ASMOrderedSet.class);
			
			NativeOperation.registerOperations(ASMEmitter.myType, ASMEmitter.class, false, true, true, true);
			NativeOperation.registerOperations(ASM.myType, ASMEmitter.class, false, true, true, true);
			NativeOperation.registerOperations(ASMStackFrame.myType, ASMStackFrame.class, false, true, true, true);
			NativeOperation.registerOperations(NativeStackFrame.myType, NativeStackFrame.class, false, true, true, true);
			NativeOperation.registerOperations(StackFrame.myType, StackFrame.class, false, true, true, true);
		}
		return typeOperations;
	}

	public ASMOclType(ASMOclType type) {
		super(type);
		supertypes = new ArrayList();
	}

	private static Map getVMOperations(ASMOclType type) {
		Map ret = (Map)getVMOperations().get(type);

		if(ret == null) {
			ret = new HashMap();
			getVMOperations().put(type, ret);
		}

		return ret;
	}

	// Only for VM operations
	public void registerVMOperation(Operation op) {
		getVMOperations(this).put(op.getName(), op);
	}

	public void addSupertype(ASMOclType supertype) {
		if(supertype != null) {
			supertypes.add(0, supertype);
		}
	}

	public List getSupertypes() {
		return supertypes;
	}
	
	public abstract ASMBoolean conformsTo(ASMOclType other);

	public abstract String getName();
	
	public ASMOclAny get(StackFrame frame, String name) {
		ASMOclAny ret = null;
		
		if(name.equals("name")) {
			ret = getName(frame, this);
		} else if(name.equals("operations")) {
			ret = new ASMSet(((ASMExecEnv)frame.getExecEnv()).getOperations(this));
		} else if(name.equals("supertypes")) {
			ret = new ASMSet(supertypes);
		} else {
			ret = super.get(frame, name);
		}
		
		return ret;
	}

	// Native Operations below

	public static ASMString getName(StackFrame frame, ASMOclType self) {
		return new ASMString(self.getName());
	}
	
	public static ASMBoolean conformsTo(StackFrame frame, ASMOclType self, ASMOclType other) {
		return self.conformsTo(other);
	}
	
	public static void registerHelperAttribute(StackFrame frame, ASMOclType self, ASMString name, ASMString initOperationName) {
		ASMExecEnv aee = ((ASMExecEnv)frame.getExecEnv());
		aee.registerAttributeHelper(self, name.getSymbol(), aee.getOperation(self, initOperationName.getSymbol()));
	}

	private List supertypes;
}

