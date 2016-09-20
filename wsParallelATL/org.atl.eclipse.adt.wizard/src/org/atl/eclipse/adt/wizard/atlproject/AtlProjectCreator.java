package org.atl.eclipse.adt.wizard.atlproject;

import org.atl.eclipse.adt.builder.AtlNature;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;

public class AtlProjectCreator extends Wizard implements INewWizard, IExecutableExtension {
	
	/** The id of the wizard extension defined in the plugin.xml file of this plugin*/
//	private static final String NEW_PROJECT_WIZARD_EXTENSION_ID = "org.atl.eclipse.adt.projectCreationWizard";
	
	private AtlDescriptionProjectScreen page;
	
	private IConfigurationElement configElement;
	
	private ISelection selection;
	
	private IProject modelProject;

	/**
	 * Constructor
	 */
	public AtlProjectCreator() {
		super();
		setNeedsProgressMonitor(true);		
	}
	
	/**
	 * Adding the page to the wizard.
	 */

	public void addPages() {
		page = new AtlDescriptionProjectScreen(selection);
		addPage(page);
	}
	
	/**
	 * This method is called when 'Finish' button is pressed in
	 * the wizard. We will create an operation and run it
	 * using wizard as execution context.
	 */
	public boolean performFinish() {
		String projectName = page.getProjectName();

		try{
			// Get the worskspace container (IWorkspaceRoot)
			IWorkspace wks = ResourcesPlugin.getWorkspace();
			IWorkspaceRoot wksroot = wks.getRoot();

			// Create a project model instance
			modelProject = wksroot.getProject(projectName);
			if (!modelProject.exists())
				modelProject.create(null);

			// open project if necessary
			if (!modelProject.isOpen())
				modelProject.open(null);
			
			modelProject.setLocal(true, IResource.DEPTH_ZERO, null);

			addNature(modelProject, AtlNature.ATL_NATURE_ID);
			BasicNewProjectResourceWizard.updatePerspective(configElement);
		}
		catch(CoreException ce){
			System.out.println("core exception caught..." + ce.getMessage());
		}

		return true;
	}
	
//	/**
//	 * This method transforms string into inputstream
//	 * @param contents content of the file to cast in InputStream
//	 * @return the InputStream content
//	 */
//	private InputStream openContentStream(String contents) {
//		return new ByteArrayInputStream(contents.getBytes());
//	}
	
	/**
	 * We will accept the selection in the workbench to see if
	 * we can initialize from it.
	 * @see IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
	}

	/**
	 * Adds a nature to a project
	 * @param project the project to add a nature to
	 * @param natureId the natureId of the nature to be added to the project
	 */
	public void addNature(IProject project, String natureId) {
	try {
	      IProjectDescription description = project.getDescription();
	      String[] natures = description.getNatureIds();
	      String[] newNatures = new String[natures.length + 1];
	      System.arraycopy(natures, 0, newNatures, 0, natures.length);
	      newNatures[natures.length] = natureId;
	      description.setNatureIds(newNatures);
	      project.setDescription(description, null);
	   } catch (CoreException e) {
	   		System.err.println(e);
	   }
	}

	/**
	 * @see org.eclipse.core.runtime.IExecutableExtension#setInitializationData(org.eclipse.core.runtime.IConfigurationElement, java.lang.String, java.lang.Object)
	 */
	public void setInitializationData(IConfigurationElement config, String propertyName, Object data) throws CoreException {
		this.configElement = config;
	}
}