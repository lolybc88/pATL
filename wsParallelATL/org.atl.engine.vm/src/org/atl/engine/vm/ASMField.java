package org.atl.engine.vm;

/**
 * @author Fr�d�ric Jouault
 */
public class ASMField {

	public ASMField(String name, String type) {
		this.name = name;
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	private String name;
	private String type;
}

