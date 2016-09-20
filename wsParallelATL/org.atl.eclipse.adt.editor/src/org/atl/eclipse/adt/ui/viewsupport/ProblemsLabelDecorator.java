/*
 * Created on 21 juil. 2004
 * @author idrissi
 */
package org.atl.eclipse.adt.ui.viewsupport;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.atl.eclipse.adt.ui.AtlUIPlugin;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.ListenerList;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.swt.graphics.Image;

/**
 * @author idrissi
 *
 */
public class ProblemsLabelDecorator implements ILabelDecorator, ILightweightLabelDecorator {

	private ListenerList labelProviderListeners = new ListenerList();
	
	private IProblemChangedListener problemChangedListener;
	
	/** IMageDescriptor => Image */
	private Map imageCache = new HashMap();
	
	private final static String ERROR_OV = "error_co.gif";	
	
	private final static String WARNING_OV = "warning_co_gif";	
	
	/**
	 * This is a special <code>LabelProviderChangedEvent</code> carrying additional 
	 * information whether the event origins from a maker change.
	 * <p>
	 * <code>ProblemsLabelChangedEvent</code>s are only generated by <code>
	 * ProblemsLabelDecorator</code>s.
	 * </p>
	 */
	public static class ProblemsLabelChangedEvent extends LabelProviderChangedEvent {

		private boolean fMarkerChange;

		public ProblemsLabelChangedEvent(IBaseLabelProvider source, IResource[] changedResource, boolean isMarkerChange) {
			super(source, changedResource);
			fMarkerChange= isMarkerChange;
		}
		
		/**
		 * Returns whether this event origins from marker changes. If <code>false</code> an annotation 
		 * model change is the origin. In this case viewers not displaying working copies can ignore these 
		 * events.
		 * 
		 * @return if this event origins from a marker change.
		 */
		public boolean isMarkerChange() {
			return fMarkerChange;
		}

	}
	
	/**
	 * @see org.eclipse.jface.viewers.ILabelDecorator#decorateImage(org.eclipse.swt.graphics.Image, java.lang.Object)
	 */
	public Image decorateImage(Image image, Object element) {		
		System.out.println("in decorateImage");
		IResource res = (IResource)element;
		if (!res.isAccessible())
		    return null;
		int flag = computeHighestServityFlag(res);
		ImageDescriptor imgDesc = null;
		switch (flag)  {
			case IMarker.SEVERITY_ERROR :
				imgDesc = AtlUIPlugin.getImageDescriptor(ERROR_OV);
				break;
			case IMarker.SEVERITY_WARNING :
				imgDesc = AtlUIPlugin.getImageDescriptor(WARNING_OV);
		}
		if (imgDesc == null)
			return null;
		
		Image img = (Image)imageCache.get(imgDesc);
		if (img == null) {
			img = imgDesc.createImage();
			imageCache.put(imgDesc, img);
		}
		return img;		
	}

	/**
	 * computes the highest severity flag for a given <code>Resource</code>
	 * the severity flag
	 * @param res the <code>Resource</code> for which to compute the highest severity
	 * @return the most high severity flag
	 */
	private int computeHighestServityFlag(IResource res) {
		IMarker[] pbmMarkers = null; 
		try {
			pbmMarkers = res.findMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE);
		} catch (CoreException e) {
			e.printStackTrace();
		}
		int severity = -1; // none
		if (pbmMarkers != null) {
			for (int i = 0; i < pbmMarkers.length; i++) {
				IMarker curr = pbmMarkers[i];
				severity = curr.getAttribute(IMarker.SEVERITY, -1);
				if (severity == IMarker.SEVERITY_ERROR)
					return IMarker.SEVERITY_ERROR;							
			}
		}		
		return severity;
	}
	
	/**
	 * @see org.eclipse.jface.viewers.ILabelDecorator#decorateText(java.lang.String, java.lang.Object)
	 */
	public String decorateText(String text, Object element) {		
		return text;
	}

	/**
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	public void addListener(ILabelProviderListener listener) {
		labelProviderListeners.add(listener);
		if (problemChangedListener == null) {
			problemChangedListener= new IProblemChangedListener() {
				public void problemsChanged(IResource[] changedResources, boolean isMarkerChange) {
					fireProblemsChanged(changedResources, isMarkerChange);
				}
			};
			AtlUIPlugin.getDefault().getProblemMarkerManager().addListener(problemChangedListener);
		}
	}

	/**
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
	 */
	public void dispose() {
		if (problemChangedListener != null) {
			AtlUIPlugin.getDefault().getProblemMarkerManager().removeListener(problemChangedListener);
			problemChangedListener= null;
		}
		for (Iterator images = imageCache.values().iterator(); images.hasNext();) { 
	  		((Image)images.next()).dispose();
		}
	  	imageCache.clear();	
	}

	/**
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object, java.lang.String)
	 */
	public boolean isLabelProperty(Object element, String property) {		
		return true;
	}

	
	private void fireProblemsChanged(IResource[] changedResources, boolean isMarkerChange) {
		if (labelProviderListeners != null && !labelProviderListeners.isEmpty()) {
			LabelProviderChangedEvent event= new ProblemsLabelChangedEvent(this, changedResources, isMarkerChange);
			Object[] listeners = labelProviderListeners.getListeners();
			for (int i = 0; i < listeners.length; ++i) {
			   ((ILabelProviderListener) listeners[i]).labelProviderChanged(event);
			 }			
		}
	}
	
	/**
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	public void removeListener(ILabelProviderListener listener) {
		labelProviderListeners.remove(listener);
	}
	
	/**
	 * @see org.eclipse.jface.viewers.ILightweightLabelDecorator#decorate(java.lang.Object, org.eclipse.jface.viewers.IDecoration)
	 */
	public void decorate(Object element, IDecoration decoration) {
		IResource res = (IResource)element;
		if (!res.isAccessible())
		    return;
		int flag = computeHighestServityFlag(res);		
		switch (flag)  {
			case IMarker.SEVERITY_ERROR :
				decoration.addOverlay(AtlUIPlugin.getImageDescriptor(ERROR_OV));
				break;
			case IMarker.SEVERITY_WARNING :
				decoration.addOverlay(AtlUIPlugin.getImageDescriptor(WARNING_OV));
				break;
		}	
	}
	
}
