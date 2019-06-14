package jadex.bdiv3;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;

import jadex.bdiv3.model.BDIModel;
import jadex.bridge.ResourceIdentifier;
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
		
		Set<ClassFileInfo> cis = SReflect.scanForClassFileInfos(new URL[]{inurl}, null, new IFilter<ClassFileInfo>()
		{
			public boolean filter(ClassFileInfo ci)
			{
				AnnotationInfo ai = ci.getClassInfo().getAnnotation(Agent.class.getName());
				return ai!=null;
			}
		});
		
		for(ClassFileInfo ci : cis)
		{
			if(gen.isEnhanced(ci.getClassInfo()))
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

			    //System.out.println("Generating classes for: " + ci.getFilename());

                for(Map.Entry<String, byte[]> entry: gen.getRecentClassBytes().entrySet())
                {
                    byte[] bytes = entry.getValue();
                    try
                    {
                    	Path p = Paths.get(indir);
                    	Path p2 = Paths.get(ci.getFilename());
                    	Path relp = p.relativize(p2);
                    	
                        // write enhanced class
                        File enhfile = new File(outdir, relp.toString());
                        System.out.println("writing: "+enhfile.getAbsolutePath());
                        enhfile.getParentFile().mkdirs();
                        DataOutputStream dos = new DataOutputStream(new FileOutputStream(enhfile));
                        dos.write(bytes);
                        dos.close();
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
		/*ClassInfo ci = SClassReader.getClassInfo(new FileInputStream("C:/projects/jadex-newnew/jadex4/jadex/applications/bdiv3/bin/main/jadex/bdiv3/testcases/componentplans/ComponentPlanBDI.class"), true, true); 
		for(MethodInfo mi: ci.getMethodInfos())
		{
			System.out.println(mi.getMethodName()+" "+mi.getMethodDescriptor());
		}*/
		
		//String indir = "C:/projects/jadex-newnew/jadex4/jadex/applications/bdiv3/bin/main";
		String indir = "C:/tmp/bin";
		String outdir = null;//"C:/tmp/bdi";
		BDIEnhancer.enhanceBDIClasses(indir, outdir);
	}
}
