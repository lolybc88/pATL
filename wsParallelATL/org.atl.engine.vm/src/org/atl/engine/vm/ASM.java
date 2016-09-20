package org.atl.engine.vm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.atl.engine.vm.nativelib.ASMOclAny;
import org.atl.engine.vm.nativelib.ASMOclSimpleType;
import org.atl.engine.vm.nativelib.ASMOclType;

/**
 * This class represents a transformation module, which can have fields.
 * @author Fr�d�ric Jouault
 */
public class ASM extends ASMOclAny {

	public static ASMOclType myType = new ASMOclSimpleType("ASM", getOclAnyType());

	public ASM() {
		super(myType);
	}

	public ASM(String name) {
		this();
		this.name = name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void addField(ASMField f) {
		fields.add(f);
		fieldsMap.put(f.getName(), f);
	}

	public ASMField getField(String name) {
		return (ASMField)fieldsMap.get(name);
	}

	public List getFields() {
		return fields;
	}

	public void addOperation(ASMOperation o) {
		operations.add(o);
		operationsMap.put(o.getName(), o);
	}

	public ASMOperation getOperation(String name) {
		return (ASMOperation)operationsMap.get(name);
	}

	public List getOperations() {
		return operations;
	}

	private String name;
	private List fields = new ArrayList();
	private Map fieldsMap = new HashMap();
	private List operations = new ArrayList();
	private Map operationsMap = new HashMap();
}

