package org.atl.eclipse.engine;

import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.atl.engine.repositories.emf4atl.ASMEMFModelElement;
import org.atl.engine.vm.nativelib.ASMEnumLiteral;
import org.atl.engine.vm.nativelib.ASMModel;
import org.eclipse.emf.ecore.EObject;

/**
 * Default implementation of methods necessary for all ATL compilers.
 * Attention: This class MUST NOT reference any types of the platform
 * (e.g. IFile), because it must be usable stand-alone, without Eclipse, too.
 * 
 * @author JOUAULT
 * @author Matthias Bohlen (refactoring to eliminate duplicate code)
 *
 */
public abstract class AtlDefaultCompiler implements AtlStandaloneCompiler
{
    /* (non-Javadoc)
     * @see org.atl.eclipse.engine.AtlStandaloneCompiler#compile(java.io.InputStream, java.lang.String)
     */
    public final CompileTimeError[] compile(InputStream in, String outputFileName)
    {
        EObject eObjects[] = internalCompile (in, outputFileName);
        
        // convert the EObjects into an easily readable form (instances of CompileTimeError).
        CompileTimeError[] result = new CompileTimeError[eObjects.length];
        for (int i = 0; i < eObjects.length; i++) {
            result[i] = ProblemConverter.convertProblem(eObjects[i]);
        }

        // return them to caller
        return result;
    }

    /* (non-Javadoc)
     * @see org.atl.eclipse.engine.AtlStandaloneCompiler#compileWithProblemModel(java.io.InputStream, java.lang.String)
     */
    public EObject[] compileWithProblemModel(InputStream in, String outputFileName)
    {
        return internalCompile (in, outputFileName);
    }
    
    /**
     * Returns the ATL WFR URL (whatever that may be); to be implemented by
     * concrete subclass.
     * @return the URL
     */
    protected abstract URL getSemanticAnalyzerURL();
    /**
     * Returns the URL of the ATL compiler transformation; to be implemented by
     * concrete subclass.
     * @return the URL of the compiler itself
     */
    protected abstract URL getCodegeneratorURL();
    
    private AtlModelHandler amh;
    private ASMModel pbmm;

    public AtlDefaultCompiler() {
        amh = AtlModelHandler.getDefault(AtlModelHandler.AMH_EMF);      
        pbmm = amh.getBuiltInMetaModel("Problem");
    }
    
    private Object[] getProblems(ASMModel problems, EObject prev[]) {
        Object ret[] = new Object[2];
        EObject pbsa[] = null;
        Collection pbs = problems.getElementsByType("Problem");
        
        int nbErrors = 0;
        if(pbs != null) {
            pbsa = new EObject[pbs.size() + prev.length];
            System.arraycopy(prev, 0, pbsa, 0, prev.length);
            int k = prev.length;
            for(Iterator i = pbs.iterator() ; i.hasNext() ; ) {
                ASMEMFModelElement ame = ((ASMEMFModelElement)i.next());
                pbsa[k++] = ame.getObject();
                if("error".equals(((ASMEnumLiteral)ame.get(null, "severity")).getName())) {
                    nbErrors++;
                }
            }
        }
        
        ret[0] = new Integer(nbErrors);
        ret[1] = pbsa;
        
        return ret;
    }
    
    /**
     * 
     * @param in The InputStream to get atl source from.
     * @param outputFileName The name of the file to which the ATL compiled program will be saved.
     * @return A List of EObject instance of Problem. 
     */
    private EObject[] internalCompile(InputStream in, String outputFileName) {
        EObject ret[] = null;
        // Parsing + Semantic Analysis
        ASMModel parsed[] = AtlParser.getDefault().parseToModelWithProblems(in);
        ASMModel atlmodel = parsed[0];
        ASMModel problems = parsed[1];
        
        Object a[] = getProblems(problems, new EObject[0]);
        int nbErrors = ((Integer)a[0]).intValue();
        ret = (EObject[])a[1];
    
        if(nbErrors == 0) {
            Map models = new HashMap();
            models.put("MOF", amh.getMof());
            models.put("ATL", atlmodel.getMetamodel());
            models.put("IN", atlmodel);
            models.put("Problem", pbmm);
            models.put("OUT", problems);

            Map params = Collections.EMPTY_MAP;
            
            Map libs = Collections.EMPTY_MAP;

            AtlLauncher.getDefault().launch(getSemanticAnalyzerURL(), libs, models, params);           

            a = getProblems(problems, ret);
            nbErrors = ((Integer)a[0]).intValue();
            ret = (EObject[])a[1];
        }

        if(nbErrors == 0) {
            // Generating code
            AtlModelHandler amh = AtlModelHandler.getDefault(AtlModelHandler.AMH_EMF);
            Map models = new HashMap();
            models.put("MOF", amh.getMof());
            models.put("ATL", amh.getAtl());
            models.put("IN", atlmodel);
    
            Map params = new HashMap();
            params.put("debug", "false");
            params.put("WriteTo", outputFileName);
            
            Map libs = new HashMap();
            libs.put("typeencoding", AtlParser.class.getResource("resources/typeencoding.asm"));
            libs.put("strings", AtlParser.class.getResource("resources/strings.asm"));
    
            AtlLauncher.getDefault().launch(getCodegeneratorURL(), libs, models, params);
        }
        
        return ret;
    }

}
