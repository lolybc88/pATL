/**
 * 
 */
package org.eclipse.m2m.atl.scalability.tests;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.m2m.atl.core.ATLCoreException;
import org.eclipse.m2m.atl.core.IExtractor;
import org.eclipse.m2m.atl.core.IInjector;
import org.eclipse.m2m.atl.core.IModel;
import org.eclipse.m2m.atl.core.IReferenceModel;
import org.eclipse.m2m.atl.core.ModelFactory;
import org.eclipse.m2m.atl.core.emf.EMFExtractor;
import org.eclipse.m2m.atl.core.emf.EMFInjector;
import org.eclipse.m2m.atl.core.emf.EMFModel;
import org.eclipse.m2m.atl.core.emf.EMFModelFactory;
import org.eclipse.m2m.atl.core.emf.EMFReferenceModel;
import org.eclipse.m2m.atl.core.launch.ILauncher;
import org.eclipse.m2m.atl.engine.emfvm.launch.EMFVMLauncher;
import org.eclipse.m2m.atl.scalability.util.*;

public class Class2RelationalATL {

	private ILauncher transformationLauncher;
	private ModelFactory modelFactory;
	private IInjector injector;
	private IExtractor extractor;
	private IReferenceModel classDiagramMetamodel;
	private IReferenceModel relationalMetamodel;
	private TransformationLogger logger;
	static String folderOfResult ="Class2RelationalATL";

	public static void main(String[] args) throws IOException {

		Class2RelationalATL rtj = new Class2RelationalATL();
		rtj.load();
		String[] fileName = { "1.000","10.000","100.000","1000.000" };
		int[] numberOfIteration = {  10, 10, 10, 1 };
		
		for (int j = 0; j < fileName.length; j++) {

			String pathname = "data/Class2RelationnalJavaATLParallelATL/Models/Source/model"
					+ fileName[j];
			File fl = new File(pathname);
			File[] fls = fl.listFiles();
			Arrays.sort(fls);
			for (File string : fls) {
				if (string.getName().charAt(0) != '.') {
					System.out.println(string.getName() + string);

					IModel classDiagramModel;
					try {
						classDiagramModel = rtj.modelFactory
								.newModel(rtj.classDiagramMetamodel);
						rtj.injector.inject(classDiagramModel,
								"data/Class2RelationnalJavaATLParallelATL/Models/Source/model"
										+ fileName[j] + "/" + string.getName());
						IModel relationalModel = rtj.modelFactory
								.newModel(rtj.relationalMetamodel);
						String path = "data/"+folderOfResult+"/Results/transformation-"
								+ string.getName() + ".log";
						for (int i = 0; i < numberOfIteration[j]; i++) {
							rtj.run(string.getName(), relationalModel,
									classDiagramModel);
							rtj.logger.save(path);
						}
						System.out.println("end");
					} catch (ATLCoreException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	private void load() {
		/*
		 * Initializations
		 */
		transformationLauncher = new EMFVMLauncher();
		modelFactory = new EMFModelFactory();
		injector = new EMFInjector();
		extractor = new EMFExtractor();

		/*
		 * Load metamodels
		 */
		try {
			classDiagramMetamodel = modelFactory.newReferenceModel();
			injector.inject(classDiagramMetamodel,
					"data/"+folderOfResult+"/Metamodels/ClassDiagram.ecore");

			relationalMetamodel = modelFactory.newReferenceModel();
			injector.inject(relationalMetamodel,
					"data/"+folderOfResult+"/Metamodels/Relational.ecore");
		} catch (ATLCoreException e) {
			e.printStackTrace();
		}

	}

	// @Override
	public void run(String fileName, IModel relationalModel,
			IModel classDiagramModel) {
		try {

			this.logger = new TransformationLogger();
			

			transformationLauncher.initialize(new HashMap<String, Object>());
			transformationLauncher.addInModel(classDiagramModel, "IN",
					"ClassDiagram");
			transformationLauncher.addOutModel(relationalModel, "OUT",
					"Relational");
			Map<String, Object> options = new HashMap<String, Object>();
			options.put("allowInterModelReferences", true);
			this.logger.start();
			transformationLauncher
					.launch(ILauncher.RUN_MODE,
							new NullProgressMonitor(),
							options,
							new FileInputStream(
									"data/"+folderOfResult+"/Transformation/ClassDiagram2Relational.asm"));
			this.logger.addEvent("run the transformation rule by ATL");

			// /*
			// * Unload all models and metamodels (EMF-specific)
			// */
			 EMFModelFactory emfModelFactory = (EMFModelFactory) modelFactory;
			 emfModelFactory.unload((EMFModel) relationalModel);
			 emfModelFactory.unload((EMFModel) classDiagramModel);
			 emfModelFactory.unload((EMFReferenceModel)
			 classDiagramMetamodel);
			 emfModelFactory.unload((EMFReferenceModel) relationalMetamodel);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
