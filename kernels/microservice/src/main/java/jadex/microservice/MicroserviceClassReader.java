package jadex.microservice;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import jadex.bridge.ClassInfo;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.LocalResourceIdentifier;
import jadex.bridge.ResourceIdentifier;
import jadex.bridge.component.IComponentFeatureFactory;
import jadex.bridge.modelinfo.ModelInfo;
import jadex.bridge.service.ProvidedServiceImplementation;
import jadex.bridge.service.ProvidedServiceInfo;
import jadex.bridge.service.annotation.Service;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.micro.MicroClassReader;
import jadex.micro.MicroModel;
import jadex.micro.annotation.Implementation;

/**
 *  Reads microservice classes and generates a model from metainfo and annotations.
 */
public class MicroserviceClassReader
{
	/**
	 *  Load a  model.
	 *  @param model The model (e.g. file name).
	 *  @param The imports (if any).
	 *  @return The loaded model.
	 */
	public MicroModel read(String model, String[] imports, ClassLoader classloader, IResourceIdentifier rid, IComponentIdentifier root,
		List<IComponentFeatureFactory> features)
	{
//		System.out.println("loading micro: "+model);
		String clname = model;
		
		// Note: it is ok if it is an absolute path with dots even it looks strange.
		// getMicroAgentClass will strip away parts until the model name is clear. 
		
		// Hack! for extracting clear classname
		if(clname.endsWith(".class"))
			clname = model.substring(0, model.indexOf(".class"));
		clname = clname.replace('\\', '.');
		clname = clname.replace('/', '.');
		
		Class<?> cma = getMicroserviceClass(clname, imports, classloader);
			
		return read(model, cma, classloader, rid, root, features);
	}
	
	/**
	 *  Load the model.
	 */
	protected MicroModel read(String model, Class<?> cma, ClassLoader classloader, IResourceIdentifier rid, IComponentIdentifier root,
		List<IComponentFeatureFactory> features)
	{
		ModelInfo modelinfo = new ModelInfo();
		MicroModel ret = new MicroModel(modelinfo);
		ret.setPojoClass(new ClassInfo(cma.getName()));
		modelinfo.internalSetRawModel(ret);
		
//		System.out.println("read micro: "+cma);
		
		String name = SReflect.getUnqualifiedClassName(cma);
//		if(name.endsWith("Microservice"))
//			name = name.substring(0, name.lastIndexOf("Microservice"));
//		String packagename = cma.getPackage()!=null? cma.getPackage().getName(): null;
		
		modelinfo.setName("Minimal");
		modelinfo.setPackage("jadex.micro");
		modelinfo.setNameHint(name);
		
		modelinfo.setFilename(SUtil.getClassFileLocation(cma));
//		System.out.println("mircor: "+src+File.separatorChar+model);
		modelinfo.setType(MicroserviceFactory.FILETYPE_MICROSERVICE);
		modelinfo.setStartable(true);
		
		if(features!=null)
			modelinfo.setFeatures((IComponentFeatureFactory[])features.toArray(new IComponentFeatureFactory[features.size()]));
		
		if(rid==null)
		{
			URL url	= null;
			try
			{
				// in robolectric testcases, location is null
				URL srcloc = cma.getProtectionDomain()==null ? null 
					: cma.getProtectionDomain().getCodeSource().getLocation();

				url	= (srcloc != null) 
					? srcloc 
					: new URL("file://" + cma.getPackage().getName().replace('.', '/') + '/');
			}
			catch(MalformedURLException e)
			{
				e.printStackTrace();
			}
			rid = new ResourceIdentifier(new LocalResourceIdentifier(root, url), null);
		}
		modelinfo.setResourceIdentifier(rid);
		modelinfo.setClassloader(classloader);
		ret.setClassloader(classloader);
		
		fillMicroModelFromAnnotations(ret, model, cma, classloader);
		
		return ret;
	}
	
	/**
	 *  Fill the model details using annotation.
	 */
	protected void fillMicroModelFromAnnotations(MicroModel micromodel, String model, final Class<?> clazz, ClassLoader cl)
	{
		ModelInfo modelinfo = (ModelInfo)micromodel.getModelInfo();
		
		List<Class<?>> sifs = new ArrayList<Class<?>>();
		for(Class<?> iface: clazz.getInterfaces())
		{
			if(MicroClassReader.isAnnotationPresent(iface, Service.class, cl))
			{
				sifs.add(iface);
			}
		}
		if(sifs.size()==0 && MicroClassReader.isAnnotationPresent(clazz, Service.class, cl))
		{
			sifs.add(clazz);
		}
		
		
		if(sifs.size()>0)
		{
			String firstname = sifs.get(0).getName()+"ms";
			String exp = "$component.getFeature(jadex.bridge.service.component.IProvidedServicesFeature.class).getProvidedServiceRawImpl(\""+firstname+"\")";
			boolean first = true;
			for(Class<?> sif: sifs)
			{
				ProvidedServiceImplementation impl = new ProvidedServiceImplementation(first? clazz: null, first? null: exp, Implementation.PROXYTYPE_DECOUPLED, null, null);
				ProvidedServiceInfo psi = new ProvidedServiceInfo(sif.getName()+"ms", sif, impl, null, null, null);
				modelinfo.addProvidedService(psi);
				first = false;
			}
		}
		else
		{
			throw new RuntimeException("No service interface/class found.");
		}
	}
	
	/**
	 * Get the mirco agent class.
	 */
	// todo: make use of cache
	protected Class<?> getMicroserviceClass(String clname, String[] imports, ClassLoader classloader)
	{
		String	oclname	= clname;
		Class<?> ret = SReflect.findClass0(clname, imports, classloader);
//		System.out.println(clname+" "+ret+" "+classloader);
		int idx;
		while(ret == null && (idx = clname.indexOf('.')) != -1)
		{
			clname = clname.substring(idx + 1);
			try
			{
				ret = SReflect.findClass0(clname, imports, classloader);
			}
			catch(IllegalArgumentException iae)
			{
				// Hack!!! Sun URL class loader doesn't like if classnames start
				// with (e.g.) 'C:'.
			}
			// System.out.println(clname+" "+cma+" "+ret);
		}
		if(ret == null)
		{
			throw new RuntimeException("Microservice class not found: " + oclname + ", " + SUtil.arrayToString(imports) + ", " + classloader);
		}
		return ret;
	}
}
