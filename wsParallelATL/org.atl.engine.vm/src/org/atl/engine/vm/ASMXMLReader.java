package org.atl.engine.vm;

import java.io.InputStream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;

/**
 * This Java class reads an ATL Stack Machine (ASM) from its XML definition.
 * @author Fr�d�ric Jouault
 */
public class ASMXMLReader extends DefaultHandler {

	private Object asmNameIndex;
	private ASMEmitter asme;
	private ArrayList cp = new ArrayList();
	private boolean inCode;

	public ASM read(InputStream in) {
		asme = new ASMEmitter();
		asme.newUnnamedASM();
		SAXParserFactory factory = SAXParserFactory.newInstance();
		try {
			errors = 0;
			SAXParser saxParser = factory.newSAXParser();
			saxParser.parse(in, this);
		} catch (Throwable err) {
		       	err.printStackTrace(System.out);
		}
		if(errors > 0) System.exit(1);
		return asme.getASM();
	}

	private String resolve(Object index) {
		int idx = toInt(index);
		return (String)cp.get(idx);
	}

	private int toInt(Object s) {
		return Integer.parseInt((String)s);
	}

	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		Map attrs = new HashMap();
		for(int i = 0 ; i < attributes.getLength() ; i++) {
			attrs.put(attributes.getQName(i), attributes.getValue(i));
		}

		if(qName.equals("asm")) {
			asmNameIndex = attrs.get("name");
		} else if(qName.equals("cp")) {
			// nothing to do
		} else if(qName.equals("constant")) {
			cp.add(attrs.get("value"));
		} else if(qName.equals("field")) {
			asme.addField(resolve(attrs.get("name")), resolve(attrs.get("type")));
		} else if(qName.equals("operation")) {
			asme.addOperationWithoutLVE(resolve(attrs.get("name")));
		} else if(qName.equals("context")) {
			asme.setContext(resolve(attrs.get("type")));
		} else if(qName.equals("parameters")) {
			// nothing to do
		} else if(qName.equals("parameter")) {
			asme.addParameterWithoutLVE(resolve(attrs.get("name")), resolve(attrs.get("type")));
		} else if(qName.equals("code")) {
			inCode = true;
		} else if(qName.equals("linenumbertable")) {
			// nothing to do
		} else if(qName.equals("lne")) {
			asme.addLineNumberEntry(resolve(attrs.get("id")), toInt(attrs.get("begin")), toInt(attrs.get("end")));
		} else if(qName.equals("localvariabletable")) {
			// nothing to do
		} else if(qName.equals("lve")) {
			asme.addLocalVariableEntry(toInt(attrs.get("slot")), resolve(attrs.get("name")), toInt(attrs.get("begin")), toInt(attrs.get("end")));

		} else {
			if(inCode) {
				if(attrs.containsKey("arg")) {
//					if(qName.equals("if") || qName.equals("goto")) {
						
//					} else {
						asme.emitWithoutLabel(qName, resolve(attrs.get("arg")));
//					}
				} else {
					asme.emitSimple(qName);
				}
			}
		}
	}

	public void endElement(String uri, String localName, String qName) throws SAXException {
		if(qName.equals("cp")) {
			asme.setName(resolve(asmNameIndex));
		} else if(qName.equals("code")) {
			inCode = false;
		}
	}

	public void error(SAXParseException e) {
		System.out.println("Error: line " + e.getLineNumber() + ":" + e.getColumnNumber() + ": " + e.getMessage());
	       	errors++;
	}

	public void fatalError(SAXParseException e) {
		System.out.println("Fatal error: line " + e.getLineNumber() + ":" + e.getColumnNumber() + ": " + e.getMessage());
		System.exit(1);
	}

	private int errors;
}

