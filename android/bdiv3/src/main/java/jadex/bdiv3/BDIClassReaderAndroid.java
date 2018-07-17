package jadex.bdiv3;

import java.net.URL;
import java.util.List;

/**
 *  Reads micro agent classes and generates a model from metainfo and annotations.
 */
public class BDIClassReaderAndroid extends BDIClassReader
{
	/**
	 *  Create a new bdi class reader.
	 */
	public BDIClassReaderAndroid(BDIModelLoader loader)
	{
		super(loader);
	}
	
	@Override
	protected DummyClassLoader createDummyClassLoader(ClassLoader original, ClassLoader parent, List<URL> urls)
	{
//		JadexDexClassLoader jadexDexClassLoader = (JadexDexClassLoader) SUtil.androidUtils().findJadexDexClassLoader(original);
//		String dexPath = jadexDexClassLoader.getDexPath();
		// dummyclassloader must have NO REFERENCE to a existing classloader which knows about users classes
//		JadexDexClassLoader androidParentClassloader = new JadexDexClassLoader(dexPath, AsmDexBdiClassGenerator.OUTPATH.getAbsolutePath(), null, getClass().getClassLoader());
//		DummyClassLoader cl = new DummyClassLoader((URL[])urls.toArray(new URL[urls.size()]), androidParentClassloader, original);
//		if (original instanceof DexDelegationClassLoader) {
//			
//		}
		DummyClassLoader cl = new DummyClassLoader((URL[])urls.toArray(new URL[urls.size()]), original, original);
		return cl;
	}
	
	/**
	 *  Load the model.
	 */
//	@Override
//	protected BDIModel read(String model, Class<?> cma, ClassLoader cl, IResourceIdentifier rid, IComponentIdentifier root,  List<IComponentFeatureFactory> features)
//	{
//		ClassLoader classloader = ((DummyClassLoader)cl).getOriginal();
//		
//		ModelInfo modelinfo = new ModelInfo();
//		BDIModel ret = new BDIModel(modelinfo, new MCapability(cma.getName()));
//		modelinfo.internalSetRawModel(ret);
//		
//		String name = SReflect.getUnqualifiedClassName(cma);
//		if(name.endsWith(BDIModelLoader.FILE_EXTENSION_BDIV3_FIRST))
//			name = name.substring(0, name.lastIndexOf(BDIModelLoader.FILE_EXTENSION_BDIV3_FIRST));
//		String packagename = cma.getPackage()!=null? cma.getPackage().getName(): null;
////		modelinfo.setName(name+"BDI");
//		modelinfo.setName(name);
//		modelinfo.setPackage(packagename);
//		modelinfo.setFilename(model);
////		String src = SUtil.convertURLToString(cma.getProtectionDomain().getCodeSource().getLocation());
////		modelinfo.setFilename(src+File.separator+SReflect.getClassName(cma)+".class");
//		modelinfo.setStartable(!Modifier.isAbstract(cma.getModifiers()));
//		modelinfo.setType(BDIAgentFactory.FILETYPE_BDIAGENT);
//		modelinfo.setResourceIdentifier(rid);
//		modelinfo.setClassloader(classloader);
//		ret.setClassloader(classloader); // use parent
//		
////		System.out.println("filename: "+modelinfo.getFilename());
//		
//		if(rid==null)
//		{
//			URL url = cma.getProtectionDomain().getCodeSource().getLocation();
//			rid = new ResourceIdentifier(new LocalResourceIdentifier(root, url), null);
//		}
//		modelinfo.setResourceIdentifier(rid);
//		
//		fillMicroModelFromAnnotations(ret, model, cma, cl);
//		
//		fillBDIModelFromAnnotations(ret, model, cma, cl, rid, root, features);
//		
//		return ret;
//	}
}
