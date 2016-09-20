/**
 * this code aim to generate the target model from the source model
 * using Java instead of at (equivalent to the ATL code Class2Relational).
 * the code can do the transformation with a set of other option 
 * as creating element in the source Model and propagate this modification
 * to the target model.
 * the same for modification and deleting (apple a propagation in the 
 * target model by modifying or deleting elements).
 * in general, in the context of this application, this application
 * can can use model which is generated automatically by the project
 * of randomness.which can take a MM to generate an automatic model 
 * by specifying the number to generate in this model.  
 */
package org.eclipse.m2m.atl.scalability.tests;

import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.impl.DynamicEObjectImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreEList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.m2m.atl.scalability.util.*;


public class Class2RelationalJava {
	
		/**
		 * @param args
		 * @throws IOException
		 */
		EFactory targetFactoryInstance;
		EFactory sourceFactoryInstance;
		static Map<EObject, EObject> traceMap = new HashMap<EObject, EObject>();
		static Map<EClass, EClass> transformationMap = new HashMap<EClass, EClass>();
		static ArrayList<EObject> sourceModelElements = new ArrayList<EObject>();
		
		static EPackage smmroot;
		static EPackage tmmroot;
		EObject elementToPropagate;

		/**
		 * @param smname
		 * @param smm
		 * @param tmm
		 * @return
		 */
		/**
		 * @param smname
		 * @param smm
		 * @param tmm
		 * @return
		 */
		@SuppressWarnings("unchecked")
		private EObject run(String smname, Resource smm, Resource tmm) {

			smmroot = (EPackage) smm.getContents().get(0);// EPackage
			tmmroot = (EPackage) tmm.getContents().get(0);// EPackage

			ResourceSet resourceSet = new ResourceSetImpl();
		
			resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap()
					.put("*", new XMIResourceFactoryImpl());
			
			resourceSet.getPackageRegistry().put("ClassDiagram", smmroot);
			resourceSet.getPackageRegistry().put("Relational", tmmroot);
		
			Resource sm = resourceSet.getResource(URI.createURI(smname), true);

			targetFactoryInstance = tmmroot.getEFactoryInstance();
			sourceFactoryInstance = smmroot.getEFactoryInstance();

			EClass systemClass = (EClass) tmmroot.getEClassifier("System");
			EClass schemaClass = (EClass) tmmroot.getEClassifier("Schema");
			EClass tableClass = (EClass) tmmroot.getEClassifier("Table");
			EClass columnClass = (EClass) tmmroot.getEClassifier("Column");
			EClass systemTargetClass = (EClass) smmroot.getEClassifier("System");

			EObject systemSourceClass = null;

			
			EObject systemEObject = null;
			EObject schemaEObject = null;
			EObject tableEObject = null;
			EObject columnEObject = null;
			EObject classEObject = null;

			systemEObject = createElementOfTargetModel(systemClass);
			for (Iterator<EObject> iterator = sm.getAllContents(); iterator
					.hasNext();) {
				EObject eobject = iterator.next();
				if (eobject.eClass().getName().equals("System")) {
					setAttribute(systemEObject, "name",
							eobject.eGet(systemTargetClass
									.getEStructuralFeature("name")));
					systemSourceClass = eobject;
					traceMap.put(eobject, systemEObject);
					transformationMap.put(eobject.eClass(), systemEObject.eClass());
					sourceModelElements.add(eobject);
					System.out.println("sytem: " + systemEObject + "   " + eobject);
					for (int i = 0; i < eobject.eContents().size(); i++) {
						EObject packageEObject = eobject.eContents().get(i);
						schemaEObject = createElementOfTargetModel(schemaClass);
						System.out.println("schema:" + schemaEObject);
						traceMap.put(packageEObject, schemaEObject);
						transformationMap.put(packageEObject.eClass(),
								schemaEObject.eClass());
						sourceModelElements.add(packageEObject);
						System.out.println("schema  " + packageEObject + "  "
								+ schemaEObject);						
						setAttribute(schemaEObject, "name",
								packageEObject.eGet(packageEObject.eClass()
										.getEStructuralFeature("name")));		
						System.out.println(systemTargetClass
								.getEStructuralFeature("name"));
						for (int l = 0; l < packageEObject.eContents().size(); l++) {
							
							if (packageEObject.eContents().get(l).eClass()
									.getName().equals("Class")) {
								classEObject = packageEObject.eContents().get(l);
								tableEObject = createElementOfTargetModel(tableClass);
								traceMap.put(classEObject, tableEObject);
								transformationMap.put(classEObject.eClass(),
										tableEObject.eClass());
								sourceModelElements.add(classEObject);
								if ((classEObject.eClass().getEStructuralFeature(0)) instanceof EAttribute) {
									if ((classEObject.eClass().getEStructuralFeature(0))
											.getName().equals("name")) {
										setAttribute(tableEObject,
												"name",
												classEObject.eGet((classEObject.eClass()
														.getEStructuralFeature(0))));
										for (int m = 0; m < classEObject
												.eContents().size(); m++) {

											columnEObject = createElementOfTargetModel(columnClass);
											EObject attributeEObject = classEObject
													.eContents().get(m);
											traceMap.put(attributeEObject, columnEObject);
											transformationMap.put(attributeEObject.eClass(),
													columnEObject.eClass());
											sourceModelElements.add(attributeEObject);
											if ((attributeEObject.eClass()
													.getEStructuralFeature(0)) instanceof EAttribute) {	
												if ((attributeEObject.eClass()
														.getEStructuralFeature(0))
														.getName().equals("name")) {

													setAttribute(columnEObject,
															// columnClass,
															"name",
															attributeEObject
																	.eGet((attributeEObject.eClass()
																			.getEStructuralFeature(0))));
												}
												// affect type
												if ((columnClass
														.getEStructuralFeature(3))
														.getName().equals("type")) {
													
												}
											}
											
											if ((tableClass.getEStructuralFeature(2)) instanceof EReference) 
											{	
												((EList) tableEObject.eGet(tableClass.getEStructuralFeature(2)))
														.add(columnEObject);											
											}
										}
									}
								}
									
								if (schemaClass.getEStructuralFeature(1) instanceof EReference) {
									((EList) schemaEObject.eGet(schemaClass
											.getEStructuralFeature(1)))
											.add(tableEObject);
								}
							}
						}
						
						if ((systemClass.getEStructuralFeature(1)) instanceof EReference) 
						{				
							((EList) systemEObject.eGet(systemClass
									.getEStructuralFeature(1))).add(schemaEObject);	
						}
					}
				} 
			}
			return systemSourceClass;
		}
		
		private EObject createElementOfTargetModel(EClass systemClass) {
			return targetFactoryInstance.create(systemClass);
		}

		private EObject setAttribute(EObject systemObject,
				String attribute, Object value) {
			systemObject.eSet(systemObject.eClass()
					.getEStructuralFeature(attribute), value.toString());
			return systemObject;
		}

		public static Resource loadMetaModel(String address) {

			ResourceSet resourceSet = new ResourceSetImpl();
			Map<String, Object> m = resourceSet.getResourceFactoryRegistry()
					.getExtensionToFactoryMap();
			m.put("ecore", new XMIResourceFactoryImpl());
			Resource result = null;

			try {
				URI fileURI = URI.createFileURI(address);
				result = resourceSet.getResource(fileURI, true);
			} catch (Exception e) {
				System.out.println("you need to change the path.");
				System.out
						.println("if the path is correct you need to change the header of you metamodel. it mean in this case, you shoud change some property of the ecore file.");
			}
			return result;
		}

		public void showStructure(EPackage ePackage) {
			for (Iterator<EClassifier> iter = ePackage.getEClassifiers().iterator(); iter
					.hasNext();) {
				/** getting all EClassifier */
				EClassifier classifier = iter.next();
				System.out.print("EClassifier: ");
				System.out.println(classifier.getName());

				if (classifier instanceof EClass) {
					EClass eClass = (EClass) classifier;
					System.out.print("EAttribute: ");
					for (Iterator<EAttribute> ai = eClass.getEAttributes()
							.iterator(); ai.hasNext();) {
						EAttribute attribute = ai.next();
						System.out.print(attribute.getName() + " ");
					}
					System.out.println(" ");
					System.out.print("EReference: ");
					for (Iterator<EReference> ri = eClass.getEReferences()
							.iterator(); ri.hasNext();) {
						EReference reference = ri.next();
						System.out.print(reference.getName() + " ");
						System.out.print(reference.isChangeable() + " ");
					}
					System.out.println();
				} else if (classifier instanceof EEnum) {
					EEnum eEnum = (EEnum) classifier;
					for (Iterator<EEnumLiteral> ei = eEnum.getELiterals()
							.iterator(); ei.hasNext();) {
						EEnumLiteral literal = ei.next();
						System.out.print(literal.getName() + " ");
					}
				} else if (classifier instanceof EDataType) {
					EDataType eDataType = (EDataType) classifier;
					System.out.print(eDataType.getInstanceClassName() + " ");
				}
				System.out.println();
			}
		}

		public void saveModel(EObject rootElement, String filename) {
			ResourceSet resourceSet1 = new ResourceSetImpl();
			resourceSet1.getResourceFactoryRegistry().getExtensionToFactoryMap()
					.put("xmi", new XMIResourceFactoryImpl());
			URI uri = URI.createFileURI(filename);
			Resource resource = resourceSet1.createResource(uri);
			System.out.println(rootElement);
			resource.getContents().add(rootElement);
			try {
				resource.save(null);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void createReferences(EObject Object, EClass eClass,
				String nameOfTheReference, List<EObject> LCO) {
			Object.eSet(eClass.getEStructuralFeature(nameOfTheReference), LCO);
		}


		public static void printAttributeValues(EObject object) {
			EClass eClass = object.eClass();
			System.out.println();
			System.out.println(eClass.getName());
			for (java.util.Iterator<EAttribute> iter = eClass.getEAllAttributes()
					.iterator(); iter.hasNext();) {
				EAttribute attribute = (EAttribute) iter.next();
				Object value = object.eGet(attribute);
				System.out.print(" " + attribute.getName() + ":" + value);
				if (object.eIsSet(attribute))
					System.out.println();
				else
					System.out.println(" (default)");
			}

			for (java.util.Iterator<EReference> iter = eClass.getEAllReferences()
					.iterator(); iter.hasNext();) {
				EReference reference = (EReference) iter.next();
				Object value = object.eGet(reference);
				System.out.print(" " + reference.getName() + ":" + value);
				if (object.eIsSet(reference))
					System.out.println();
				else
					System.out.println(" (default)");
			}
		}
		
		
		Resource smm;
		Resource tmm;
		static String sm;
		static String tm;
		private void load() {
			 smm = Class2RelationalJava
			.loadMetaModel("data/Class2RelationalJava/Metamodels/ClassDiagram.ecore");
			 tmm = Class2RelationalJava.loadMetaModel("data/Class2RelationalJava/Metamodels/Relational.ecore");
		}
		
//		@Override
		public TransformationLogger run() {
			TransformationLogger tle= new TransformationLogger();
			tle.start();			
			EObject sourceRootElement = run(sm, smm, tmm);
			tle.addEvent("run the transformation");
			return tle;
			
		}
		

		public static void main(String[] args) throws IOException {
			
		Class2RelationalJava c2r = new Class2RelationalJava();
		String[] fileName = {  "1.000", "10.000", "100.000", "1000.000" };
		int[] numberOfIteration = {  10, 10, 10, 1 };// the nbr is the number of iteration
		c2r.load();
		for (int j = 0; j < fileName.length; j++) {

			String pathname = "data/Class2RelationnalJavaATLParallelATL/Models/Source/model"
					+ fileName[j];
			File fl = new File(pathname);
			File[] fls = fl.listFiles();
			Arrays.sort(fls);
			for (File string : fls) {
				sm = "data/Class2RelationnalJavaATLParallelATL/Models/Source/model"
						+ fileName[j] + "/" + string.getName();
				tm = "data/Class2RelationalJava/Target/" + string.getName()
						+ ".xmi";
				String path = "data/Class2RelationalJava/Results/transformation-"
						+ string.getName() + ".log";

				for (int i = 0; i < numberOfIteration[j]; i++) {
					TransformationLogger tl = c2r.run();
					tl.save(path);
				}
			}
		}
	}
		


		
		/**
		 * this method take the object to modified and apply the modification in the
		 * source model. TO LOOK in the attribute is simple to modified element but
		 * of the reference it is more complicated because you should get the
		 * correct element to reference to it.
		 */
		@SuppressWarnings("unused")
		private ModifiedElement executeUpdateAttribute(ModifiedElement modifiedElement) {

		System.out.println(modifiedElement.property.getEType().getName());	
		String variable = modifiedElement.property.getEType().getName();
		
		if (variable.equals("Boolean")) {
			
			boolean newBoolValue = true;
		}
		else{
			String newAttrValue = "newvalue";
			setAttribute(modifiedElement.element, modifiedElement.property,
					newAttrValue);
		}
		
		//java7
		/*switch (variable) {

		case "Boolean" :
			boolean newBoolValue = true;
			System.out.println("This * part * should * be verified and uncommente the code");
			
			break;
		default: 
			String newAttrValue = "newvalue";
			setAttribute(modifiedElement.element, modifiedElement.property,
					newAttrValue);
		}*/
			return modifiedElement;
		}

		private EObject setAttribute(EObject modifiedElement,
				EStructuralFeature property, Object mod) {
			modifiedElement.eSet(
					modifiedElement.eClass().getEStructuralFeature(
							property.getName()), mod.toString());
			return modifiedElement;
		}
}

	class ModifiedElement {
		EObject element;
		EStructuralFeature property;

		public ModifiedElement(EObject modifiedElement, EStructuralFeature property) {
			super();
			this.element = modifiedElement;
			this.property = property;
		}
	}
