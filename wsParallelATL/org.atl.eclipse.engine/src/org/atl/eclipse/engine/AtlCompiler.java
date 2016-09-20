/*
 * Created on 1 juin 2004
 *
 */
package org.atl.eclipse.engine;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.ecore.EObject;

/**
 * @author JOUAULT (original version)
 * @author Matthias Bohlen (refactorings for ease of use and elimination of duplicate code)
 *
 */
public class AtlCompiler {
	
	private static AtlCompiler defaultCompiler = null;
	
	public static AtlCompiler getDefault() {
		if(defaultCompiler == null) {
			defaultCompiler = new AtlCompiler();
		}
		return defaultCompiler;
	}

	private static final int MAX_LINE_LENGTH = 1000;
	/**
	 * 
	 * @param in The InputStream to get atl source from.
	 * @param out The IFile to which the ATL compiled program will be saved.
	 * @return the problems which occured during compilation 
	 */
	public EObject[] compile(InputStream in, IFile out) {
        EObject ret[] = null;
		String atlcompiler = null;
		
		// The BufferedInputStream is required to reset the stream before actually compiling
		in = new BufferedInputStream(in, MAX_LINE_LENGTH);
		in.mark(MAX_LINE_LENGTH);
		byte buffer[] = new byte[MAX_LINE_LENGTH];
		try {
			int length = in.read(buffer);
			// The BufferedReader is used to read the first line
			// (it cannot be used to reset the underlying InputStream required by the compiler below)
			BufferedReader brin = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(buffer, 0, length)));

			String firstLine = brin.readLine();
			atlcompiler = firstLine.replaceFirst("^\\p{Space}*--\\p{Space}*@atlcompiler\\p{Space}+([^\\p{Space}]*)\\p{Space}*$", "$1");
			// if firstLine does not match the pattern then nothing was replaced and atlcompiler = firstLine
			if(atlcompiler.equals(firstLine)) {
				atlcompiler = "atl2004";
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
        try {
			in.reset();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
        ret = getCompiler(atlcompiler).compileWithProblemModel(in, out.getLocation().toString());

        try {
            out.refreshLocal(0, null);
        } catch (CoreException e) {
            e.printStackTrace();
        }
		
        return ret;
	}
	
	private static Map compilers = new HashMap();

	/**
     * Searches for the correct implementation of {@link AtlStandaloneCompiler}
     * which is independent of Eclipse platform stuff like IFile, IResource, etc.
     * 
	 * @param compilerName name of the compiler to search for
	 * @return the compiler which was found
	 */
	private static AtlStandaloneCompiler getCompiler(String compilerName) {
		AtlStandaloneCompiler ret = (AtlStandaloneCompiler)compilers.get(compilerName);
		if(ret == null) {
			if("atl2004".equals(compilerName)) {
				ret = new Atl2004Compiler();
				compilers.put(compilerName, ret);				
			} else {
				IExtensionRegistry registry = Platform.getExtensionRegistry();
                if (registry == null) {
                    throw new RuntimeException("Eclipse platform extension registry not found. Dynamic repository lookup does not work outside Eclipse.");
                }
                
				IExtensionPoint point = registry.getExtensionPoint("org.atl.eclipse.engine.atlcompiler");

				IExtension[] extensions = point.getExtensions();		
				extensions: for(int i = 0 ; i < extensions.length ; i++){		
					IConfigurationElement[] elements = extensions[i].getConfigurationElements();
					for(int j = 0 ; j < elements.length ; j++){
						try {					
							if(elements[j].getAttribute("name").equals(compilerName)) {
								ret = (AtlStandaloneCompiler)elements[j].createExecutableExtension("class");
								compilers.put(compilerName, ret);
								break extensions;
							}
						} catch (CoreException e){
							e.printStackTrace();
						}				
					}
				 }
			}

			if(ret == null) {
				throw new CompilerNotFoundException("ATL compiler " + compilerName + " not found. You may need to install an ATL compiler plugin.");
			}
		}
		
		return ret;
	}
}
