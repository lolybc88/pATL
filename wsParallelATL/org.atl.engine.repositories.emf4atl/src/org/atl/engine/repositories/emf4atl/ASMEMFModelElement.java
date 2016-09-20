package org.atl.engine.repositories.emf4atl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.atl.engine.vm.ClassNativeOperation;
import org.atl.engine.vm.StackFrame;
import org.atl.engine.vm.nativelib.ASMBag;
import org.atl.engine.vm.nativelib.ASMBoolean;
import org.atl.engine.vm.nativelib.ASMCollection;
import org.atl.engine.vm.nativelib.ASMEnumLiteral;
import org.atl.engine.vm.nativelib.ASMInteger;
import org.atl.engine.vm.nativelib.ASMModel;
import org.atl.engine.vm.nativelib.ASMModelElement;
import org.atl.engine.vm.nativelib.ASMOclAny;
import org.atl.engine.vm.nativelib.ASMOclType;
import org.atl.engine.vm.nativelib.ASMOclUndefined;
import org.atl.engine.vm.nativelib.ASMReal;
import org.atl.engine.vm.nativelib.ASMSequence;
import org.atl.engine.vm.nativelib.ASMSet;
import org.atl.engine.vm.nativelib.ASMString;
import org.atl.engine.vm.nativelib.ASMTuple;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.Enumerator;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.ecore.util.FeatureMapUtil;
import org.eclipse.emf.ecore.xmi.XMIResource;

/**
 * @author Fr�d�ric Jouault
 */
public class ASMEMFModelElement extends ASMModelElement {
	
	// only for metamodels...?
	public ASMBoolean conformsTo(ASMOclType other) {
		boolean ret = false;

		if(other instanceof ASMEMFModelElement) {
			EObject o = ((ASMEMFModelElement)other).object;
			EObject t = object;

			if((o instanceof EClass) && (t instanceof EClass)) {
				try {
					ret = o.equals(t) || ((EClass)o).isSuperTypeOf((EClass)t);
				} catch(Exception e) {
					e.printStackTrace(System.out);
				}
			}
		}

		return new ASMBoolean(ret);
	}

	// only for metamodels...?
	public ASMModelElement getPropertyType(String name) {
		ASMModelElement ret = null;

		ASMModelElement p = getProperty(name);
		if(p != null) {
			ret = (ASMModelElement)p.get(null, "eType");
		}
		
		return ret;
	}
	
	// only for metamodels...?
	public ASMModelElement getProperty(String name) {
		ASMModelElement ret = null;

		EObject t = object;

		if(t instanceof EClass) {
			try {
				ret = ((ASMEMFModel)getModel()).getASMModelElement(((EClass)t).getEStructuralFeature(name));
			} catch(Exception e) {
				e.printStackTrace(System.out);
			}
		}

		return ret;
	}

	public ASMOclAny refImmediateComposite() {
		ASMOclAny ret = null;
		
		EObject ic = object.eContainer();
		if(ic == null) {
			ret = super.refImmediateComposite();
		} else {
			ret = ((ASMEMFModel)getModel()).getASMModelElement(ic);
		}
		
		return ret;
	}

	public ASMOclAny get(StackFrame frame, String name) {
		ASMOclAny ret = null;

		if((frame != null) && isHelper(frame, name)) {
			ret = getHelper(frame, name);
		} else if("__xmiID__".equals(name)) {
			String id = ((XMIResource)((ASMEMFModel)getModel()).getExtent()).getURIFragment(object);
			ret = emf2ASM(frame, id);
		} else {
			EStructuralFeature sf = object.eClass().getEStructuralFeature(name);
			if(sf == null) {
				frame.printStackTrace("feature " + name + " does not exist on " + getType());
			}
/*
			if(sf.isDerived()) {
				frame.printStackTrace("feature " + name + " is derived");			
			}
*/
			ret = emf2ASM(frame, object.eGet(sf));
		}
		return ret;
	}
	
	public ASMOclAny emf2ASM(StackFrame frame, Object value) {
		ASMOclAny ret = null;
		
		if(value instanceof String) {
			ret = new ASMString((String)value);
		} else if(value instanceof Boolean) {
			ret = new ASMBoolean(((Boolean)value).booleanValue());
		} else if(value instanceof Double) {
			ret = new ASMReal(((Double)value).doubleValue());
		} else if(value instanceof Float) {
			ret = new ASMReal(((Float)value).doubleValue());
		} else if(value instanceof Integer) {
			ret = new ASMInteger(((Integer)value).intValue());
		} else if(value instanceof Long) {
			ret = new ASMInteger(((Long)value).intValue());
		} else if(value instanceof Byte) {
			ret = new ASMInteger(((Byte)value).intValue());
		} else if(value instanceof Short) {
			ret = new ASMInteger(((Short)value).intValue());
		} else if(value instanceof Character) {
			ret = new ASMInteger(((Character)value).charValue());
		} else if(value instanceof Enumerator) {
			ret = new ASMEnumLiteral(((Enumerator)value).getName());
        } else if(value instanceof FeatureMap.Entry) {
            ret = new ASMTuple();
            ret.set(frame, "eStructuralFeature", 
                    emf2ASM(frame, ((FeatureMap.Entry)value).getEStructuralFeature()));
            ret.set(frame, "value", 
                    emf2ASM(frame, ((FeatureMap.Entry)value).getValue()));
		} else if(value instanceof EObject) {
            ret = eObjectToASM(frame, (EObject)value);
		} else if(value == null) {
			ret = new ASMOclUndefined();
		} else if(value instanceof Collection) {
			ASMCollection col;
			if(value instanceof List)
				col = new ASMSequence();
			else if(value instanceof Set)
				col = new ASMSet();
			else
				col = new ASMBag();
			
			for(Iterator i = ((Collection)value).iterator() ; i.hasNext() ; ) {
				col.add(emf2ASM(frame, i.next()));
			}
			ret = col;
		} else {
			frame.printStackTrace("ERROR: cannot convert " + value + " : " + value.getClass() + " from EMF.");
		}
		
		return ret;
	}
    
    /**
     * @author Dennis Wagelaar <dennis.wagelaar@vub.ac.be>
     * @param frame The ATL VM stackframe
     * @param value The EMF value to convert
     * @return The corresponding ASMModelElement, taking into account
     * the model in which the element should reside. Uses this model as a proxy
     * if no other model contains the given value.
     */
    private ASMOclAny eObjectToASM(StackFrame frame, EObject value) {
        ASMEMFModel model = (ASMEMFModel) getModel();
        Resource valueExtent = value.eResource();
        if (model.getExtent().equals(valueExtent)) {
            return model.getASMModelElement(value);
        } else {
            Iterator models = frame.getModels().values().iterator();
            while (models.hasNext()) {
                Object m = models.next();
                if ((m instanceof ASMEMFModel) && (!model.equals(m))) {
                    if (((ASMEMFModel)m).getExtent().equals(valueExtent)) {
                        return ((ASMEMFModel)m).getASMModelElement(value);
                    }
                }
            }
        }
        //Use this model as proxy
        return model.getASMModelElement(value);
    }
    
	public void set(StackFrame frame, String name, ASMOclAny value) {
		final boolean debug = false;
//		final boolean checkSameModel = !true;
		
		if(debug) System.out.println("Setting: " + this + " : " + getType() + "." + name + " to " + value);
		super.set(frame, name, value);
		EStructuralFeature feature = object.eClass().getEStructuralFeature(name);
		if(feature == null) {
			frame.printStackTrace("feature " + name + " does not exist on " + getType());
		}
		if(!feature.isChangeable()) {
			frame.printStackTrace("feature " + name + " is not changeable");			
		}
		if(feature.isMany()) {
			EList l = (EList)object.eGet(feature);
			if(value instanceof ASMCollection) {
				for(Iterator i = ((ASMCollection)value).iterator() ; i.hasNext() ; ) {
					ASMOclAny sv = (ASMOclAny)i.next();
					if(((ASMEMFModel)getModel()).isCheckSameModel() && (sv instanceof ASMModelElement) && (((ASMModelElement)sv).getModel() != getModel())) {					
						continue;
					}
					Object val = asm2EMF(frame, sv, name, feature);
					try {
						l.add(val);
						checkContainment(feature, val);
					} catch(Exception e) {
						frame.printStackTrace("cannot set feature " + getType() + "." + name + " to value " + val);
					}
				}
			} else {
				Object val = asm2EMF(frame, value, name, feature);
				l.add(val);
				checkContainment(feature, val);
			}
		} else {
			if(((ASMEMFModel)getModel()).isCheckSameModel() && (value instanceof ASMModelElement) && (((ASMModelElement)value).getModel() != getModel())) {
				// should not happen but the ATL compiler does not add checks for this in resolveTemp yet
			} else {
				Object val = asm2EMF(frame, value, name, feature);
				if(val != null) {
					try {
						object.eSet(feature, val);
						checkContainment(feature, val);						
					} catch(Exception e) {
						frame.printStackTrace("cannot set feature " + getType() + "." + name + " to value " + val, e);
					}
				}
			}
		}
	}
	
	private void checkContainment(EStructuralFeature feature, Object val) {
		if((val != null) && (feature instanceof EReference)) {
			EReference ref = (EReference)feature;
			if(ref.isContainment()) {
				ASMEMFModel model = (ASMEMFModel)getModel();
				/* TODO
				 * add a plugin dependency to an assertion API 
				 * (e.g., org.eclipse.jface.text.Assert in 3.1.2 or 
				 * org.eclipse.core.runtime.Assert in 3.2)
				 * Assert.isNotNull(model);
				 */
				EList toplevelElements = model.getExtent().getContents();
				// Check if 'val' is a toplevel element 
				// in the content list of the model resource extent.
				if (toplevelElements.contains(val)) {
					// 'val' is about to become a contained element.
					// therefore, we need to remove it from the list of toplevel elements
					toplevelElements.remove(val);
				}
			} else if(ref.isContainer()) {
				ASMEMFModel model = (ASMEMFModel)getModel();
				/* TODO
				 * add a plugin dependency to an assertion API 
				 * (e.g., org.eclipse.jface.text.Assert in 3.1.2 or 
				 * org.eclipse.core.runtime.Assert in 3.2)
				 * Assert.isNotNull(model);
				 */
				EList toplevelElements = model.getExtent().getContents();
				// Check if 'val' is a toplevel element 
				// in the content list of the model resource extent.
				if (toplevelElements.contains(object)) {
					// 'val' is about to become a contained element.
					// therefore, we need to remove it from the list of toplevel elements
					toplevelElements.remove(object);
				}
			}
		}
	}
	
	public Object asm2EMF(StackFrame frame, ASMOclAny value, String propName, EStructuralFeature feature) {
		Object ret = null;
        
        if(value instanceof ASMString) {
			ret = ((ASMString)value).getSymbol();
		} else if(value instanceof ASMBoolean) {
			ret = new Boolean(((ASMBoolean)value).getSymbol());
		} else if(value instanceof ASMReal) {
			ret = new Double(((ASMReal)value).getSymbol());
		} else if(value instanceof ASMInteger) {
			int val = ((ASMInteger)value).getSymbol();
			if(feature != null) {
				String targetType = feature.getEType().getInstanceClassName();
				if(targetType.equals("java.lang.Double") || targetType.equals("java.lang.Float")) {
					ret = new Double(val);
				} else {
					ret = new Integer(val);
				}
			} else {
				ret = new Integer(val);
			}
		} else if(value instanceof ASMEMFModelElement) {
			ret = ((ASMEMFModelElement)value).object;
		} else if(value instanceof ASMOclUndefined) {
			ret = null;
		} else if (value instanceof ASMEnumLiteral) {
			String name = ((ASMEnumLiteral)value).getName();
			EClassifier type = ((EClass)((ASMEMFModelElement)getMetaobject()).object).getEStructuralFeature(propName).getEType();
			ret = ((EEnum)type).getEEnumLiteral(name).getInstance();
        } else if (value instanceof ASMTuple) {
            Object f = asm2EMF(frame, 
                    ((ASMTuple)value).get(frame, "eStructuralFeature"),
                    propName, feature);
            if (f instanceof EStructuralFeature) {
                Object v = asm2EMF(frame, 
                        ((ASMTuple)value).get(frame, "value"),
                        propName, feature);
                ret = FeatureMapUtil.createEntry((EStructuralFeature)f, v);
            } else {
                frame.printStackTrace("ERROR: cannot convert " + value + " : " + value.getClass() + " to EMF.");
            }
		} else if(value instanceof ASMCollection) {
			ret = new ArrayList();
			for(Iterator i = ((ASMCollection)value).iterator() ; i.hasNext() ; ) {
				Object v = asm2EMF(frame, (ASMOclAny)i.next(), propName, feature);
				if(v != null)
					((List)ret).add(v);
			}
		} else {
			frame.printStackTrace("ERROR: cannot convert " + value + " : " + value.getClass() + " to EMF.");
		}
		
		return ret;
	}
	
	private static ASMModelElement getMetaobject(ASMModel model, EObject object) {
		ASMModelElement ret = null;
		
		EObject metaobject = object.eClass();
		if(metaobject != object) {
			ret= ((ASMEMFModel)model.getMetamodel()).getASMModelElement(metaobject);
		}
		
		return ret;
	}
	
	private static void registerMOFOperation(String modelelementName, String methodName, Class args[]) throws Exception {
		List realArgs = new ArrayList(Arrays.asList(args));
		realArgs.add(0, ASMEMFModelElement.class);
		realArgs.add(0, StackFrame.class);
		ClassNativeOperation no = new ClassNativeOperation(ASMEMFModelElement.class.getMethod(methodName, (Class[])realArgs.toArray(args)));
		ASMModelElement amme = ASMEMFModel.getMOF().findModelElement(modelelementName);
//		System.out.println("Registering on " + amme + " : " + no);
		amme.registerVMOperation(no);
	}

	static {
		try {
			// Force creation of MOF!EClass before any other (otherwise MOF!Classifier gets created twice)
			ASMEMFModel.getMOF().findModelElement("EClass");

			// Operations on MOF!Classifier
			// TODO on EClassifier after having added supertypes
			registerMOFOperation("EClass", "allInstances", new Class[] {});
			registerMOFOperation("EClass", "allInstancesFrom", new Class[] {ASMString.class});
			registerMOFOperation("EClassifier", "newInstance", new Class[] {});
			registerMOFOperation("EClassifier", "getInstanceById", new Class[] {ASMString.class, ASMString.class});
//			registerMOFOperation("Classifier", "getElementBy", new Class[] {ASMString.class, ASMOclAny.class});
//			registerMOFOperation("Classifier", "getElementsBy", new Class[] {ASMString.class, ASMOclAny.class});

			// Operations on MOF!GeneralizableElement
//			registerMOFOperation("GeneralizableElement", "findElementsByTypeExtended", new Class[] {ASMMDRModelElement.class, ASMBoolean.class});
//			registerMOFOperation("GeneralizableElement", "lookupElementExtended", new Class[] {ASMString.class});

			// Operations on MOF!AssociationEnd
//			registerMOFOperation("AssociationEnd", "otherEnd", new Class[] {});
		} catch(Exception e) {
			e.printStackTrace(System.out);
		}
	}
	
	// For testing purpose
	public static ASMOclAny getInstanceById(StackFrame frame, ASMEMFModelElement self, ASMString modelName, ASMString id) {
		ASMOclAny ret = null;
		
		ASMModel model = (ASMModel)frame.getModels().get(modelName.getSymbol());
		if(model instanceof ASMEMFModel) {
																				//TODO: test new version, was: getIDToEObjectMap().get(id.getSymbol());
			EObject eo = (EObject)((XMIResource)((ASMEMFModel)model).getExtent()).getEObject(id.getSymbol());
			if(eo != null)
				ret = ((ASMEMFModel)model).getASMModelElement(eo);
		}
		
		return (ret == null) ? new ASMOclUndefined() : ret;
	}

	public static ASMSet allInstances(StackFrame frame, ASMEMFModelElement self) {
		return allInstancesFrom(frame, self, null);
	}

	public static ASMSet allInstancesFrom(StackFrame frame, ASMEMFModelElement self, ASMString sourceModelName) {
final boolean debug = false;
		Set ret = new HashSet();

if(debug) System.out.println(self + ".allInstancesFrom(" + ((sourceModelName == null) ? "null" : "\"" + sourceModelName + "\"") + ")");
		//if(self.object.eClass().equals()) {
			for(Iterator i = frame.getModels().keySet().iterator() ; i.hasNext() ; ) {
				String mname = (String)i.next();
if(debug) System.out.println("\ttrying: " + mname);
				if((sourceModelName != null) && !mname.equals(sourceModelName.getSymbol())) continue;
				ASMModel am = (ASMModel)frame.getModels().get(mname);
if(debug) System.out.println("\t\tfound: " + am.getName());
if(debug) System.out.println("\t\tam.getMetamodel() = " + am.getMetamodel().hashCode());
if(debug) System.out.println("\t\tself.getModel() = " + self.getModel().hashCode());
if(debug) System.out.println("\t\tam.getMetamodel().equals(self.getModel()) = " + am.getMetamodel().equals(self.getModel()));
				if(!am.getMetamodel().equals(self.getModel())) continue;
if(debug) System.out.println("\t\t\tsearching on: " + am.getName());
				Set elems = am.getElementsByType(self);
				ret.addAll(elems);
if(debug) System.out.println("\t\t\t\tfound: " + elems);
			}
		//}
			
		return new ASMSet(ret);
	}
	
	public static ASMModelElement newInstance(StackFrame frame, ASMEMFModelElement self) {
		ASMModelElement ret = null;
		if(self.object.eClass().getName().equals("EClass")) {
			for (Iterator j = frame.getExecEnv().getModels().values().iterator(); j.hasNext();) {
				ASMModel model = (ASMModel)j.next();
				if (model.getMetamodel().equals(self.getModel()) && model.isTarget()) {
					ret = model.newModelElement(self);
					break;
				}
			}
		}
		
//		if(self.object.eClass().getName().equals("EClass")) {
//			for(Iterator i = self.getModel().getSubModels().values().iterator() ; i.hasNext() ; ) {
//				ASMModel am = (ASMModel)i.next();
//				if(am.isTarget()) {
//					ret = am.newModelElement(self);
//					break;
//				}
//			}
//		}

		return ret;
	}

	/**
	 * @param model
	 * @param metaobject
	 */
	protected ASMEMFModelElement(Map modelElements, ASMModel model, EObject object) {
		super(model, getMetaobject(model, object));
		this.object = object;
		
		// must be done here and not in getASMModelElement because EClass extends EClassifier whose type is EClass
		modelElements.put(object, this);
		
		EStructuralFeature sfName = object.eClass().getEStructuralFeature("name");
		if (sfName != null) {
			String name = (String)object.eGet(sfName);
			if(name == null) {
				name = "<notnamedyet>";
			}
			setName(name);
		} else { 
			setName("<unnamed>");
		}

		if(getMetaobject() == null) {
			setMetaobject(this);
		}
		setType(getMetaobject());
		
		// Supertypes
		if(object instanceof EClass) {
			addSupertype(ASMOclType.myType);
			EClass cl = (EClass)object;
			for(Iterator i = cl.getESuperTypes().iterator() ; i.hasNext() ; ) {
				EClass s = (EClass)i.next();
				addSupertype(((ASMEMFModel)model).getASMModelElement(s));
			}
		}
	}

	private Method findMethod(Class cls, String name, Class argumentTypes[]) {
		Method ret = null;

		Method methods[] = cls.getDeclaredMethods(); 
		for(int i = 0 ; i < (methods.length) && (ret == null) ; i++) {
			Method method = methods[i];
			if(method.getName().equals(name)) {
				Class pts[] = method.getParameterTypes();
				if(pts.length == argumentTypes.length) {
					boolean ok = true;
					for(int j = 0 ; (j < pts.length) && ok ; j++) {
						if(!pts[j].isAssignableFrom(argumentTypes[j])) {
                            if (!(pts[j] == boolean.class && argumentTypes[j] == Boolean.class
                            		|| pts[j] == int.class && argumentTypes[j] == Integer.class
                            		|| pts[j] == char.class && argumentTypes[j] == Character.class
                            		|| pts[j] == long.class && argumentTypes[j] == Long.class
                            		|| pts[j] == float.class && argumentTypes[j] == Float.class
                            		|| pts[j] == double.class && argumentTypes[j] == Double.class)) {
                            	ok = false;
                            }
						}
					}
					if(ok)
						ret = method;
				}
			}
		}
		
		if((ret == null) && (cls.getSuperclass() != null)) {
			ret = findMethod(cls.getSuperclass(), name, argumentTypes);
		}

		return ret;
	}
	
	public ASMOclAny invoke(StackFrame frame, String opName, List arguments) {
		ASMOclAny ret = null;

		if(findOperation(frame, opName, arguments) != null) {
			ret = super.invoke(frame, opName, arguments);
		} else {
			Object args[] = new Object[arguments.size()];
			Class argumentTypes[] = new Class[arguments.size()];
			int k = 0;
			for(Iterator i = arguments.iterator() ; i.hasNext() ; ) {
				// warning: ASMEnumLiterals will not be converted!
				args[k] = asm2EMF(frame, (ASMOclAny)i.next(), null, null);
				argumentTypes[k] = args[k].getClass();
				k++;
			}
			
			Method method = findMethod(object.getClass(), opName, argumentTypes);
			try {
				if(method != null) {
					ret = emf2ASM(frame, method.invoke(object, args));
				} else {
					frame.printStackTrace("ERROR: could not find operation " + opName + " on " + getType() + " having supertypes: " + getType().getSupertypes() + " (including Java operations)");									
				}
			} catch(IllegalAccessException e) {
				frame.printStackTrace("ERROR: could not invoke operation " + opName + " on " + getType() + " having supertypes: " + getType().getSupertypes() + " (including Java operations)");				
			} catch(InvocationTargetException e) {
				Throwable cause = e.getCause();
				Exception toReport = (cause instanceof Exception) ? (Exception)cause : e;
				frame.printStackTrace("ERROR: exception during invocation of operation " + opName + " on " + getType() + " (java method: " + method + ")", toReport);				
			}
		}
		
		return ret;
	}

	public EObject getObject() {
		return object;
	}
	
	private EObject object;
    
}
