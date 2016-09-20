/*
 * Created on 21 avr. 2004
 */
package org.atl.ui.perspective;

import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.console.IConsoleConstants;


/**
 * @author idrissi
 */
public class AtlPerspective implements IPerspectiveFactory {
	
	/**
	 * @see org.eclipse.ui.IPerspectiveFactory#createInitialLayout(org.eclipse.ui.IPageLayout)
	 */	
	public void createInitialLayout(IPageLayout layout) {
		defineActions(layout);
	    defineLayout(layout);
	}
	
	public void defineActions(IPageLayout layout) {
        // Add "new wizards".
        layout.addNewWizardShortcut("atlProjectWizard");
        layout.addNewWizardShortcut("atlFileWizard");

        // Add "show views".
        layout.addShowViewShortcut(IPageLayout.ID_RES_NAV);
        
        layout.addPerspectiveShortcut("org.atl.eclipse.adt.atlPerspective");
	}
	
	public void defineLayout(IPageLayout layout) {
        // editors are placed for free.
        String editorArea = layout.getEditorArea();
     
        // place navigator to the left (of editor area)
        IFolderLayout left = layout.createFolder("left", IPageLayout.LEFT, (float) 0.25, editorArea);
        left.addView(IPageLayout.ID_RES_NAV);
        
        // problem view at the bottom (of editor area)
        IFolderLayout bottom = layout.createFolder("bottom", IPageLayout.BOTTOM, (float) 0.75, editorArea);
        bottom.addView(IPageLayout.ID_PROBLEM_VIEW);
        bottom.addView(IPageLayout.ID_PROP_SHEET); 
        bottom.addView("org.eclipse.pde.runtime.LogView");
        bottom.addView(IConsoleConstants.ID_CONSOLE_VIEW);
        
        // outline view to right (of editor area)
        IFolderLayout right = layout.createFolder("right", IPageLayout.RIGHT, (float) 0.75, editorArea);
        right.addView(IPageLayout.ID_OUTLINE);
        
        // add shortcuts
        layout.addNewWizardShortcut("atlProjectWizard");
        layout.addNewWizardShortcut("atlFileWizard");
        layout.addNewWizardShortcut("org.eclipse.ui.wizards.new.folder");
		layout.addNewWizardShortcut("org.eclipse.ui.wizards.new.file");
        layout.addActionSet(IDebugUIConstants.LAUNCH_ACTION_SET);

        layout.addShowViewShortcut(IPageLayout.ID_RES_NAV);
        
        layout.addPerspectiveShortcut("org.atl.eclipse.adt.atlPerspective");
	}
	
}