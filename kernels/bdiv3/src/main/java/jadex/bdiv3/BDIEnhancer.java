package jadex.bdiv3;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import jadex.bdiv3.model.BDIModel;
import jadex.bridge.ResourceIdentifier;
import jadex.commons.FileFilter;
import jadex.commons.IFilter;
import jadex.commons.SClassReader;
import jadex.commons.SClassReader.AnnotationInfo;
import jadex.commons.SClassReader.ClassFileInfo;
import jadex.commons.SClassReader.ClassInfo;
import jadex.commons.SClassReader.MethodInfo;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.micro.annotation.Agent;

/**
 * 
 */
public class BDIEnhancer
{
	/**
	 * 
	 */
	public static void enhanceBDIClasses(String indir, String outdir)
	{
		if(outdir==null)
			outdir = indir;
			
		URL inurl = null;
		try
		{
			inurl = Paths.get(indir).toUri().toURL();
		}
		catch(IOException e)
		{
			SUtil.throwUnchecked(e);
		}
		
		ClassLoader origcl = BDIEnhancer.class.getClassLoader();
		URLClassLoader cl = new URLClassLoader(new URL[]{inurl}, origcl);
		
		BDIModelLoader loader = new BDIModelLoader();
		ByteKeepingASMBDIClassGenerator gen = new ByteKeepingASMBDIClassGenerator();
		loader.setGenerator(gen);
		
		Set<ClassFileInfo> cis = new HashSet<>();
		FileFilter ff = new FileFilter(null, false, ".class");
		Set<ClassFileInfo> allcis = SReflect.scanForClassFileInfos(new URL[]{inurl}, ff, new IFilter<ClassFileInfo>()
		{
			public boolean filter(ClassFileInfo ci)
			{
				AnnotationInfo ai = ci.getClassInfo().getAnnotation(Agent.class.getName());
				if (ai != null)
					cis.add(ci);
				return true;
				//return ai!=null;
			}
		});
		
		Map<String, String> classfiles = new HashMap<>();
		allcis.stream().forEach(ci -> classfiles.put(ci.getClassInfo().getClassName(), ci.getFilename()));
		
		for(ClassFileInfo ci : cis)
		{
			if(AbstractAsmBdiClassGenerator.isEnhanced(ci))
			{
				System.out.println("Already enhanced: "+ci.getFilename());
				
				// just copy file
                if(!indir.equals(outdir))
                {
                	Path p = Paths.get(indir);
                	Path p2 = Paths.get(ci.getFilename());
                	Path relp = p.relativize(p2);
                	
                    File f = new File(outdir, relp.toString());
                    if(!f.exists())
                    {
                    	f.getParentFile().mkdirs();
                    	try
                    	{
                    		SUtil.copyFile(new File(ci.getFilename()), f);
                    	}
                    	catch(IOException e)
                    	{
                    		SUtil.throwUnchecked(e);
                    	}
                    }
                }
			}
			else
			{
				System.out.println("Processing: "+ci.getFilename());
				
				gen.clearRecentClassBytes();
				
                try
                {
                	BDIModel model = loader.loadComponentModel(ci.getFilename(), null, null, cl, new Object[]{new ResourceIdentifier(), null, null});
                }
                catch(Exception e)
                {
                	SUtil.throwUnchecked(e);
                }

                for(Map.Entry<String, byte[]> entry: gen.getRecentClassBytes().entrySet())
                {
                	System.out.println("writing: "+entry.getKey());
                	
                    byte[] bytes = entry.getValue();
                    
                    Path p = Paths.get(indir);
                	Path p2 = Paths.get(classfiles.get(entry.getKey()));
                	Path relp = p.relativize(p2);
                	
                    // write enhanced class
                    File enhfile = new File(outdir, relp.toString());
                    enhfile.getParentFile().mkdirs();
                    
                    try(FileOutputStream fos = new FileOutputStream(enhfile))
                    {
                        fos.write(bytes);
                    }
                    catch(IOException e)
                    {
                    	SUtil.throwUnchecked(e);
                        // URLClassLoader.close() not in JDK 1.6
                        try{ ((URLClassLoader)cl).close(); } catch(IOException e2) {}
                    }
                }
			}
		}
	}
	
	/**
	 *  Main for testing.
	 */
	public static void main(String[] args) throws Exception
	{
		/*Map<String, MethodInfo> m = new HashMap<>();
		ClassInfo ci = SClassReader.getClassInfo(new FileInputStream("C:/tmp/bin/jadex/bdiv3/testcases/componentplans/ComponentPlanBDI.class"), true, true); 
		ClassInfo ci = SClassReader.getClassInfo(new FileInputStream("/home/jander/git/jadex/applications/bdiv3/bintest/main/jadex/bdiv3/testcases/componentplans/ComponentPlanBDI.class"), true, true); 
		for(MethodInfo mi: ci.getMethodInfos())
		{
//			System.out.println(mi.getMethodName()+" "+mi.getMethodDescriptor());
			if(m.containsKey(mi.getMethodName()+" " + mi.getMethodDescriptor()))
				System.out.println("Dup method: "+mi.getMethodName()+" "+mi.getMethodDescriptor());
			else
				m.put(mi.getMethodName()+" " + mi.getMethodDescriptor(), mi);
		}*/
		
		//String indir = "/home/jander/git/jadex/applications/bdiv3/bintest/main";
		String indir = "C:/tmp/bin";
		String outdir = null;//"C:/tmp/bdi";
		BDIEnhancer.enhanceBDIClasses(indir, outdir);
	}
}
