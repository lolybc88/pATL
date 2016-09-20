package org.eclipse.m2m.atl.scalability.tests;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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
//import org.eclipse.m2m.atl.engine.emfvm.launch.EMFVMLauncher;
//import org.eclipse.m2m.atl.engine.emfvm.launch.EMFPVMLauncher;
import org.eclipse.m2m.atl.engine.emfvm.parallelvm.launch.EMFPVMLauncher;
//import org.eclipse.m2m.atl.engine.emfvm.launch.EMFVMLauncher;
import org.eclipse.m2m.atl.scalability.util.*;

public class IMDb {

	private ILauncher transformationLauncher;
	private ModelFactory modelFactory;
	private IInjector injector;
	private IExtractor extractor;
	private IReferenceModel inMetamodel;
	private IReferenceModel outMetamodel;
	private TransformationLogger logger;

	public static void main(String[] args) throws IOException {

		String[] cc = { "imdb-0010400-100024.movies",
				"imdb-0027800-200319.movies-fixed",
				"imdb-0084400-500716.movies-fixed",
				"imdb-0200000-1013510.movies-fixed",
				"imdb-0335000-1511287.movies-fixed",
				"imdb-0492000-2019707.movies-fixed",
				"imdb-0650000-2509987.movies-fixed",
				"imdb-0820000-3017435.movies-fixed",
				"imdb-all-3531618.movies-fixed" };
		for (String c : cc){
			System.out.println("-- " + c + " --");
		String modelPath = "/home/loli/IST_2014/imdb/xmi/"+c;
		String inMMPath = "data/IMDb/movies.ecore";
		String referenceInModelName = "MM";
		String outMMPath = "data/IMDb/movies.ecore";
		String referenceOutModelName = "MM1";
		String asmPath = "data/IMDb/pFindCouplesIMDb.asm";
		boolean parallel = true;
		int numCores = 16;

		try {
		for (int i = 0; i < 20; i++) {
		IMDb rtj;
		System.gc();
		rtj = new IMDb();
		rtj.load(inMMPath, outMMPath, parallel);

		IModel inModel;
		
			inModel = rtj.modelFactory.newModel(rtj.inMetamodel);
			rtj.injector.inject(inModel, modelPath);

			
				System.gc();

				IModel outModel = rtj.modelFactory.newModel(rtj.outMetamodel);

				double time = rtj.run("imbd", outModel, inModel,
						referenceInModelName, referenceOutModelName, parallel,
						numCores, asmPath);
				System.out.println(time);

				// String path = "data/IMDb/transformation.log";
				// rtj.logger.save(path);
			}

		} catch (ATLCoreException e) {
			e.printStackTrace();
		}
		}
	}

	private void load(String inMMPath, String outMMPath, boolean parallel) {
		/*
		 * Initializations
		 */
		if (parallel) {
			transformationLauncher = new EMFPVMLauncher();
		} else {
			transformationLauncher = new EMFVMLauncher();
		}
		modelFactory = new EMFModelFactory();
		injector = new EMFInjector();
		extractor = new EMFExtractor();

		/*
		 * Load metamodels
		 */
		try {
			inMetamodel = modelFactory.newReferenceModel();
			injector.inject(inMetamodel, inMMPath);

			outMetamodel = modelFactory.newReferenceModel();
			injector.inject(outMetamodel, outMMPath);
		} catch (ATLCoreException e) {
			e.printStackTrace();
		}

	}

	// @Override
	public double run(String fileName, IModel outModel, IModel inModel,
			String inRef, String outRef, boolean parallel, int numCore,
			String asmPath) {

		double time = -1;

		try {

			this.logger = new TransformationLogger();

			transformationLauncher.initialize(new HashMap<String, Object>());
			transformationLauncher.addInModel(inModel, "IN", inRef);
			transformationLauncher.addOutModel(outModel, "OUT", outRef);
			Map<String, Object> options = new HashMap<String, Object>();
			options.put("allowInterModelReferences", true);
			this.logger.start();

			if (parallel) {
				options.put("numCores", numCore);
			}

			double time0 = System.currentTimeMillis();
			transformationLauncher.launch(ILauncher.RUN_MODE,
					new NullProgressMonitor(), options, new FileInputStream(
							asmPath));
			time = (System.currentTimeMillis() - time0) / 1000;

			this.logger.addEvent("run the transformation rule by ATL");

			// /*
			// * Unload all models and metamodels (EMF-specific)
			// */
			EMFModelFactory emfModelFactory = (EMFModelFactory) modelFactory;
			emfModelFactory.unload((EMFModel) outModel);
			emfModelFactory.unload((EMFModel) inModel);
			emfModelFactory.unload((EMFReferenceModel) inMetamodel);
			emfModelFactory.unload((EMFReferenceModel) outMetamodel);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return time;
	}
}
