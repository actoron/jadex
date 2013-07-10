package jadex.bdiv3;

import jadex.bdiv3.annotation.BDIConfiguration;
import jadex.bdiv3.annotation.BDIConfigurations;
import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Body;
import jadex.bdiv3.annotation.Capability;
import jadex.bdiv3.annotation.Deliberation;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.GoalContextCondition;
import jadex.bdiv3.annotation.GoalCreationCondition;
import jadex.bdiv3.annotation.GoalDropCondition;
import jadex.bdiv3.annotation.GoalInhibit;
import jadex.bdiv3.annotation.GoalMaintainCondition;
import jadex.bdiv3.annotation.GoalRecurCondition;
import jadex.bdiv3.annotation.GoalTargetCondition;
import jadex.bdiv3.annotation.Goals;
import jadex.bdiv3.annotation.Mapping;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Plans;
import jadex.bdiv3.annotation.ServicePlan;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.model.BDIModel;
import jadex.bdiv3.model.ConstructorInfo;
import jadex.bdiv3.model.MBelief;
import jadex.bdiv3.model.MBody;
import jadex.bdiv3.model.MCapability;
import jadex.bdiv3.model.MCondition;
import jadex.bdiv3.model.MConfiguration;
import jadex.bdiv3.model.MDeliberation;
import jadex.bdiv3.model.MGoal;
import jadex.bdiv3.model.MPlan;
import jadex.bdiv3.model.MTrigger;
import jadex.bdiv3.model.MethodInfo;
import jadex.bdiv3.runtime.ChangeEvent;
import jadex.bdiv3.runtime.impl.BDIAgentInterpreter;
import jadex.bdiv3.runtime.impl.GoalDelegationHandler;
import jadex.bdiv3.runtime.impl.IServiceParameterMapper;
import jadex.bridge.ClassInfo;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.LocalResourceIdentifier;
import jadex.bridge.ResourceIdentifier;
import jadex.bridge.modelinfo.ConfigurationInfo;
import jadex.bridge.modelinfo.ModelInfo;
import jadex.bridge.modelinfo.UnparsedExpression;
import jadex.bridge.service.ProvidedServiceImplementation;
import jadex.bridge.service.ProvidedServiceInfo;
import jadex.bridge.service.PublishInfo;
import jadex.bridge.service.RequiredServiceBinding;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.Value;
import jadex.bridge.service.component.BasicServiceInvocationHandler;
import jadex.commons.FieldInfo;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.micro.MicroAgent;
import jadex.micro.MicroClassReader;
import jadex.micro.MicroModel;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Component;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.NameValue;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.Publish;
import jadex.micro.annotation.RequiredService;
import jadex.rules.eca.EventType;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import dalvik.system.DexClassLoader;

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
	
	/**
	 *  Load a  model.
	 *  @param model The model (e.g. file name).
	 *  @param The imports (if any).
	 *  @return The loaded model.
	 */
	@Override
	public MicroModel read(String model, String[] imports, ClassLoader classloader, IResourceIdentifier rid, IComponentIdentifier root)
	{
		// use dummy classloader that will not be visisble outside
		List<URL> urls = SUtil.getClasspathURLs(classloader, false);
		
		DexClassLoader androidDummyClassloader = new DexClassLoader(AsmDexBdiClassGenerator.APP_PATH, AsmDexBdiClassGenerator.OUTPATH.getAbsolutePath(), null, classloader);
		// real parent needed here
		DummyClassLoader cl = new DummyClassLoader((URL[])urls.toArray(new URL[urls.size()]), androidDummyClassloader, classloader);
		return super.read(model, imports, cl, rid, root);
	}
	
	/**
	 *  Load the model.
	 */
	protected BDIModel read(String model, Class<?> cma, ClassLoader cl, IResourceIdentifier rid, IComponentIdentifier root)
	{
		ClassLoader classloader = ((DummyClassLoader)cl).getOriginal();
		
		ModelInfo modelinfo = new ModelInfo();
		BDIModel ret = new BDIModel(modelinfo, new MCapability(cma.getName()));
		
		String name = SReflect.getUnqualifiedClassName(cma);
		if(name.endsWith(BDIModelLoader.FILE_EXTENSION_BDIV3_FIRST))
			name = name.substring(0, name.lastIndexOf(BDIModelLoader.FILE_EXTENSION_BDIV3_FIRST));
		String packagename = cma.getPackage()!=null? cma.getPackage().getName(): null;
//		modelinfo.setName(name+"BDI");
		modelinfo.setName(name);
		modelinfo.setPackage(packagename);
		modelinfo.setFilename(model);
//		String src = SUtil.convertURLToString(cma.getProtectionDomain().getCodeSource().getLocation());
//		modelinfo.setFilename(src+File.separator+SReflect.getClassName(cma)+".class");
		modelinfo.setStartable(!Modifier.isAbstract(cma.getModifiers()));
		modelinfo.setType(BDIAgentFactory.FILETYPE_BDIAGENT);
		modelinfo.setResourceIdentifier(rid);
		ret.setClassloader(classloader); // use parent
		
//		System.out.println("filename: "+modelinfo.getFilename());
		
		if(rid==null)
		{
			URL url = cma.getProtectionDomain().getCodeSource().getLocation();
			rid = new ResourceIdentifier(new LocalResourceIdentifier(root, url), null);
		}
		modelinfo.setResourceIdentifier(rid);
		
		fillMicroModelFromAnnotations(ret, model, cma, cl);
		
		fillBDIModelFromAnnotations(ret, model, cma, cl, rid, root);
		
		return ret;
	}
	


}
