package jadex.application.space.envsupport.environment;

import jadex.application.model.MSpaceInstance;
import jadex.application.runtime.IApplication;
import jadex.application.runtime.ISpace;
import jadex.application.space.envsupport.IObjectCreator;
import jadex.application.space.envsupport.MEnvSpaceInstance;
import jadex.application.space.envsupport.MEnvSpaceType;
import jadex.application.space.envsupport.dataview.IDataView;
import jadex.application.space.envsupport.environment.space2d.Space2D;
import jadex.application.space.envsupport.evaluation.DefaultDataProvider;
import jadex.application.space.envsupport.evaluation.IObjectSource;
import jadex.application.space.envsupport.evaluation.ITableDataConsumer;
import jadex.application.space.envsupport.evaluation.ITableDataProvider;
import jadex.application.space.envsupport.evaluation.SpaceObjectSource;
import jadex.application.space.envsupport.math.Vector2Double;
import jadex.application.space.envsupport.observer.gui.ObserverCenter;
import jadex.application.space.envsupport.observer.perspective.IPerspective;
import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentListener;
import jadex.commons.IPropertyObject;
import jadex.commons.collection.MultiCollection;
import jadex.commons.concurrent.IResultListener;
import jadex.javaparser.IParsedExpression;
import jadex.javaparser.SimpleValueFetcher;
import jadex.service.library.ILibraryService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *  Abstract base class for environment space. 
 */
public abstract class AbstractEnvironmentSpace extends SynchronizedPropertyObject implements IEnvironmentSpace, ISpace
{
	//-------- attributes --------
	
	/** The space name. */
	protected String name;
	
	/** The context. */
	protected IApplication context;
	
	/** The space object types. */
	protected Map objecttypes;
	
	/** The object task types. */
	protected Map tasktypes;
	
	/** The space process types. */
	protected Map processtypes;
	
	/** The percepttypes. */
	protected Map percepttypes;
	
	/** Available component actions. */
	protected Map actions;
	
	/** The percept generators. */
	protected Map perceptgenerators;

	/** The percept processors. */
	protected MultiCollection perceptprocessors;
	
	/** Avatar mappings. */
	protected MultiCollection avatarmappings;

	/** Initial avatar settings (aid -> [type, props]). */
	protected Map initialavatars;

	/** Data view mappings. */
	protected MultiCollection	dataviewmappings;
	
	/** The environment processes. */
	protected Map processes;
	
	/** Long/ObjectIDs (keys) and environment objects (values). */
	protected Map spaceobjects;
	
	/** Types of EnvironmentObjects and lists of EnvironmentObjects of that type (typed view). */
	protected Map spaceobjectsbytype;
	
	/** Space object by owner, owner can null (owner view). */
	protected Map spaceobjectsbyowner;
	
	/** Object id counter for new ids. */
	protected AtomicCounter objectidcounter;
	
	/** Task id counter for new ids. */
	protected AtomicCounter taskidcounter;
	
	/** The list of scheduled component actions. */
	protected ComponentActionList actionlist;
	
	/** The list of scheduled percepts. */
	protected PerceptList perceptlist;
		
	/** Available views */
	protected Map views;

	/** The environment listeners. */
	protected List listeners;
	
	/** The fetcher. */
	protected SimpleValueFetcher fetcher;
	
	/** The data providers (name -> provider). */
	protected Map dataproviders;
	
	/** The data consumers. */
	protected Map dataconsumers;

	//-------- constructors --------
	
	/**
	 *  Create an environment space
	 */
	public AbstractEnvironmentSpace()
	{
		super(new Object());
		this.views = new HashMap();
		this.avatarmappings = new MultiCollection();
		this.dataviewmappings = new MultiCollection();
		this.actions = new HashMap();
		this.processtypes = new HashMap();
		this.tasktypes = new HashMap();
		this.processes = new HashMap();
		this.percepttypes = new HashMap();
		this.perceptgenerators = new HashMap();
		this.perceptprocessors = new MultiCollection();
		this.objecttypes = new HashMap();
		this.spaceobjects = new HashMap();
		this.spaceobjectsbytype = new HashMap();
		this.spaceobjectsbyowner = new HashMap();
		
		this.objectidcounter = new AtomicCounter();
		this.taskidcounter = new AtomicCounter();
		this.actionlist	= new ComponentActionList(this);
		this.perceptlist = new PerceptList(this);
		
		this.dataproviders = new HashMap();
		this.dataconsumers = new HashMap();
	}
	
	/**
	 *  Create a space.
	 */
	public void	initSpace(IApplication context, MSpaceInstance config) throws Exception
	{
		MEnvSpaceInstance	si	= (MEnvSpaceInstance)config;
		MEnvSpaceType	mspacetype	= (MEnvSpaceType)config.getType();
		
		SimpleValueFetcher fetcher = new SimpleValueFetcher();
		fetcher.setValue("$space", this);
		fetcher.setValue("$platform", context.getServiceContainer());
		fetcher.setValue("$args", context.getArguments());
		fetcher.setValue("$results", context.getResults());
		this.setFetcher(fetcher);
		
		List mspaceprops = mspacetype.getPropertyList("properties");
		MEnvSpaceInstance.setProperties(this, mspaceprops, fetcher);
		List spaceprops = si.getPropertyList("properties");
		MEnvSpaceInstance.setProperties(this, spaceprops, fetcher);
		
		this.context	= context;
		
		if(this instanceof Space2D) // Hack?
		{
			Double width = si.getProperty("width")!=null? (Double)si.getProperty("width"): (Double)mspacetype.getProperty("width");
			Double height = si.getProperty("height")!=null? (Double)si.getProperty("height"): (Double)mspacetype.getProperty("height");
			((Space2D)this).setAreaSize(Vector2Double.getVector2(width, height));
//			System.out.println("areasize: "+width+" "+height);
		}
		
		// Create space object types.
		List objecttypes = mspacetype.getPropertyList("objecttypes");
		if(objecttypes!=null)
		{
			for(int i=0; i<objecttypes.size(); i++)
			{
				Map mobjecttype = (Map)objecttypes.get(i);
				List props = (List)mobjecttype.get("properties");
				Map	properties	= null;
				if(props!=null)
				{
					properties	= new LinkedHashMap();
					for(int j=0; j<props.size(); j++)
					{
						Map	prop	= (Map)props.get(j);
						properties.put(prop.get("name"), prop);
					}
				}
//				Map properties = convertProperties(props, fetcher);
//				System.out.println("Adding environment object type: "+(String)getProperty(mobjecttype, "name")+" "+props);
				this.addSpaceObjectType((String)MEnvSpaceInstance.getProperty(mobjecttype, "name"), properties);
			}
		}
		
		// Add avatar mappings.
		List avmappings = mspacetype.getPropertyList("avatarmappings");
		if(avmappings!=null)
		{
			for(int i=0; i<avmappings.size(); i++)
			{
				AvatarMapping mapping = (AvatarMapping)avmappings.get(i);
//				String componenttype = (String)MEnvSpaceInstance.getProperty(mmapping, "componenttype");
//				String avatartype = (String)(String)MEnvSpaceInstance.getProperty(mmapping, "objecttype");
//				Boolean createavatar = (Boolean)MEnvSpaceInstance.getProperty(mmapping, "createavatar");
//				Boolean createcomponent = (Boolean)MEnvSpaceInstance.getProperty(mmapping, "createcomponent");
//				Boolean killavatar = (Boolean)MEnvSpaceInstance.getProperty(mmapping, "killavatar");
//				Boolean killcomponent = (Boolean)MEnvSpaceInstance.getProperty(mmapping, "killcomponent");
//				
//				AvatarMapping mapping = new AvatarMapping(componenttype, avatartype);
//				if(createavatar!=null)
//					mapping.setCreateAvatar(createavatar.booleanValue());
//				if(createcomponent!=null)
//					mapping.setCreateComponent(createcomponent.booleanValue());
//				if(killavatar!=null)
//					mapping.setKillAvatar(killavatar.booleanValue());
//				if(killcomponent!=null)
//					mapping.setKillComponent(killcomponent.booleanValue());
//				
				this.addAvatarMappings(mapping);
			}
		}
		// Create space percept types.
		List percepttypes = mspacetype.getPropertyList("percepttypes");
		if(percepttypes!=null)
		{
			for(int i=0; i<percepttypes.size(); i++)
			{
				Map mpercepttype = (Map)percepttypes.get(i);

				PerceptType pt = new PerceptType();
				
				pt.setName((String)MEnvSpaceInstance.getProperty(mpercepttype, "name"));
				
				List atypes = (List)mpercepttype.get("componenttypes");
				pt.setComponentTypes(atypes==null? null: new HashSet(atypes));
				
				List otypes = (List)mpercepttype.get("objecttypes");
				pt.setObjectTypes(otypes==null? null: new HashSet(otypes));
				
//				System.out.println("Adding environment percept type: "+pt);
				this.addPerceptType(pt);
			}
		}
		
		// Create space actions.
		List spaceactions = mspacetype.getPropertyList("actiontypes");
		if(spaceactions!=null)
		{
			for(int i=0; i<spaceactions.size(); i++)
			{
				Map maction = (Map)spaceactions.get(i);
				ISpaceAction action = (ISpaceAction)((Class)MEnvSpaceInstance.getProperty(maction, "clazz")).newInstance();
				List props = (List)maction.get("properties");
				MEnvSpaceInstance.setProperties(action, props, fetcher);
				
//				System.out.println("Adding environment action: "+MEnvSpaceInstance.getProperty(maction, "name"));
				this.addSpaceAction((String)MEnvSpaceInstance.getProperty(maction, "name"), action);
			}
		}
		
		// Create process types.
		List processes = mspacetype.getPropertyList("processtypes");
		if(processes!=null)
		{
			for(int i=0; i<processes.size(); i++)
			{
				Map mprocess = (Map)processes.get(i);
//				ISpaceProcess process = (ISpaceProcess)((Class)MEnvSpaceInstance.getProperty(mprocess, "clazz")).newInstance();
				List props = (List)mprocess.get("properties");
				String name = (String)MEnvSpaceInstance.getProperty(mprocess, "name");
				Class clazz = (Class)MEnvSpaceInstance.getProperty(mprocess, "clazz");
				
//				System.out.println("Adding environment process: "+MEnvSpaceInstance.getProperty(mprocess, "name"));
				this.addSpaceProcessType(name, clazz, props);
			}
		}
		

		// Create task types.
		List tasks = mspacetype.getPropertyList("tasktypes");
		if(tasks!=null)
		{
			for(int i=0; i<tasks.size(); i++)
			{
				Map mtask = (Map)tasks.get(i);
				List props = (List)mtask.get("properties");
				String name = (String)MEnvSpaceInstance.getProperty(mtask, "name");
				Class clazz = (Class)MEnvSpaceInstance.getProperty(mtask, "clazz");
				
//				System.out.println("Adding object task: "+MEnvSpaceInstance.getProperty(mtask, "name"));
				this.addObjectTaskType(name, clazz, props);
			}
		}
		
		// Create percept generators.
		List gens = mspacetype.getPropertyList("perceptgenerators");
		if(gens!=null)
		{
			for(int i=0; i<gens.size(); i++)
			{
				Map mgen = (Map)gens.get(i);
				IPerceptGenerator gen = (IPerceptGenerator)((Class)MEnvSpaceInstance.getProperty(mgen, "clazz")).newInstance();
				List props = (List)mgen.get("properties");
				MEnvSpaceInstance.setProperties(gen, props, fetcher);
				
//				System.out.println("Adding environment percept generator: "+MEnvSpaceInstance.getProperty(mgen, "name"));
				this.addPerceptGenerator(MEnvSpaceInstance.getProperty(mgen, "name"), gen);
			}
		}
		
		// Create percept processors.
		List pmaps = mspacetype.getPropertyList("perceptprocessors");
		if(pmaps!=null)
		{
			for(int i=0; i<pmaps.size(); i++)
			{
				Map mproc = (Map)pmaps.get(i);
				IPerceptProcessor proc = (IPerceptProcessor)((Class)MEnvSpaceInstance.getProperty(mproc, "clazz")).newInstance();
				List props = (List)mproc.get("properties");
				MEnvSpaceInstance.setProperties(proc, props, fetcher);
				
				String componenttype = (String)MEnvSpaceInstance.getProperty(mproc, "componenttype");
				List ptypes = (List)mproc.get("percepttypes");
				this.addPerceptProcessor(componenttype, ptypes==null? null: new HashSet(ptypes), proc);
			}
		}
		
		// Create initial objects.
		List objects = (List)si.getPropertyList("objects");
		if(objects!=null)
		{
			for(int i=0; i<objects.size(); i++)
			{
				Map mobj = (Map)objects.get(i);
				List mprops = (List)mobj.get("properties");
				int num	= 1;
				if(mobj.containsKey("number"))
				{
					num	= ((Number)MEnvSpaceInstance.getProperty(mobj, "number")).intValue();
				}
				
				for(int j=0; j<num; j++)
				{
					fetcher.setValue("$number", new Integer(j));
					Map props = MEnvSpaceInstance.convertProperties(mprops, fetcher);
					this.createSpaceObject((String)MEnvSpaceInstance.getProperty(mobj, "type"), props, null);
				}
			}
		}
		
		// Register initial avatars
		List avatars = (List)si.getPropertyList("avatars");
		if(avatars!=null)
		{
			for(int i=0; i<avatars.size(); i++)
			{
				Map mobj = (Map)avatars.get(i);
			
				List mprops = (List)mobj.get("properties");
				String	owner	= (String)MEnvSpaceInstance.getProperty(mobj, "owner");
				if(owner==null)
					throw new RuntimeException("Attribute 'owner' required for avatar: "+mobj);
				IComponentIdentifier	ownerid	= null;
				IComponentManagementService ces = ((IComponentManagementService)context.getServiceContainer().getService(IComponentManagementService.class));
				if(owner.indexOf("@")!=-1)
					ownerid	= ces.createComponentIdentifier((String)owner, false, null);
				else
					ownerid	= ces.createComponentIdentifier((String)owner, true, null);
				
				Map props = MEnvSpaceInstance.convertProperties(mprops, fetcher);
				this.addInitialAvatar(ownerid, (String)MEnvSpaceInstance.getProperty(mobj, "type"), props);
			}
		}
		
		// Create initial processes.
		List procs = (List)si.getPropertyList("processes");
		if(procs!=null)
		{
			for(int i=0; i<procs.size(); i++)
			{
				Map mproc = (Map)procs.get(i);
				List mprops = (List)mproc.get("properties");
				Map props = MEnvSpaceInstance.convertProperties(mprops, fetcher);
				this.createSpaceProcess((String)MEnvSpaceInstance.getProperty(mproc, "type"), props);
//				System.out.println("Create space process: "+MEnvSpaceInstance.getProperty(mproc, "type"));
			}
		}
		
		// Create initial space actions.
		List actions = (List)si.getPropertyList("spaceactions");
		if(actions!=null)
		{
			for(int i=0; i<actions.size(); i++)
			{
				Map action = (Map)actions.get(i);
				List ps = (List)action.get("parameters");
				Map params = null;
				if(ps!=null)
				{
					params = new HashMap();
					for(int j=0; j<ps.size(); j++)
					{
						Map param = (Map)ps.get(j);
						IParsedExpression exp = (IParsedExpression)param.get("value");
						params.put(param.get("name"), exp.getValue(fetcher));
					}
				}
				
//				System.out.println("Performing initial space action: "+getProperty(action, "type"));
				this.performSpaceAction((String)MEnvSpaceInstance.getProperty(action, "type"), params);
			}
		}
		
//		Map themes = new HashMap();
		List sourceviews = mspacetype.getPropertyList("views");
		if(sourceviews!=null)
		{
			for(int i=0; i<sourceviews.size(); i++)
			{				
				Map sourceview = (Map)sourceviews.get(i);
				if(MEnvSpaceInstance.getProperty(sourceview, "objecttype")==null)
				{
					Map viewargs = new HashMap();
					viewargs.put("sourceview", sourceview);
					viewargs.put("space", this);
					
					IDataView	view	= (IDataView)((IObjectCreator)MEnvSpaceInstance.getProperty(sourceview, "creator")).createObject(viewargs);
					this.addDataView((String)MEnvSpaceInstance.getProperty(sourceview, "name"), view);
				}
				else
				{
					this.addDataViewMapping((String)MEnvSpaceInstance.getProperty(sourceview, "objecttype"), sourceview);
				}
			}
		}
		
		// Create the data providers.
		List providers = mspacetype.getPropertyList("dataproviders");
		List tmp = si.getPropertyList("dataproviders");
		
		if(providers==null && tmp!=null)
			providers = tmp;
		else if(providers!=null && tmp!=null)
			providers.addAll(tmp);
		
//		System.out.println("data providers: "+providers);
		if(providers!=null)
		{
			for(int i=0; i<providers.size(); i++)
			{
				Map dcol = (Map)providers.get(i);

				List sources = (List)dcol.get("source");
				IObjectSource[] provs = new IObjectSource[sources.size()];
				for(int j=0; j<sources.size(); j++)
				{
					Map source = (Map)sources.get(j);
					String varname = source.get("name")!=null? (String)source.get("name"): "$object";
					String objecttype = (String)source.get("objecttype");
					boolean aggregate = source.get("aggregate")!=null? ((Boolean)source.get("aggregate")).booleanValue(): false;
					IParsedExpression exp = (IParsedExpression)source.get("content");
					provs[j] = new SpaceObjectSource(varname, this, objecttype, aggregate, exp);
				}
				
				String tablename = (String)MEnvSpaceInstance.getProperty(dcol, "name");
				List subdatas = (List)dcol.get("data");
				String[] columnnames = new String[subdatas.size()];
				IParsedExpression[] exps = new IParsedExpression[subdatas.size()];
				for(int j=0; j<subdatas.size(); j++)
				{
					Map subdata = (Map)subdatas.get(j);
					columnnames[j] = (String)MEnvSpaceInstance.getProperty(subdata, "name");
					exps[j] = (IParsedExpression)MEnvSpaceInstance.getProperty(subdata, "content");
				}
				
				ITableDataProvider tprov = new DefaultDataProvider(this, provs, tablename, columnnames, exps);
				this.addDataProvider(tablename, tprov);
			}
		}
		
		// Create the data consumers.
		List consumers = mspacetype.getPropertyList("dataconsumers");
		tmp = si.getPropertyList("dataconsumers");
		
		if(consumers==null && tmp!=null)
			consumers = tmp;
		else if(consumers!=null && tmp!=null)
			consumers.addAll(tmp);
		
//		System.out.println("data consumers: "+consumers);
		if(consumers!=null)
		{
			for(int i=0; i<consumers.size(); i++)
			{
				Map dcon = (Map)consumers.get(i);
				String name = (String)MEnvSpaceInstance.getProperty(dcon, "name");
				Class clazz = (Class)MEnvSpaceInstance.getProperty(dcon, "class");
				ITableDataConsumer con = (ITableDataConsumer)clazz.newInstance();
				MEnvSpaceInstance.setProperties(con, (List)dcon.get("properties"), fetcher);
				con.setProperty("envspace", this);
				this.addDataConsumer(name, con);
			}
		}
		
		List observers = si.getPropertyList("observers");
		if(observers!=null)
		{
			for(int i=0; i<observers.size(); i++)
			{				
				Map observer = (Map)observers.get(i);
				
				String title = MEnvSpaceInstance.getProperty(observer, "name")!=null? (String)MEnvSpaceInstance.getProperty(observer, "name"): "Default Observer";
				Boolean	killonexit	= (Boolean)MEnvSpaceInstance.getProperty(observer, "killonexit");
				
				List plugs = (List)observer.get("plugins");
				List plugins = null;
				if(plugs!=null)
				{
					plugins = new ArrayList();
					for(int j=0; j<plugs.size(); j++)
					{
						Map plug = (Map)plugs.get(j);
						Class clazz = (Class)MEnvSpaceInstance.getProperty(plug, "clazz");
						IPropertyObject po = (IPropertyObject)clazz.newInstance();
						MEnvSpaceInstance.setProperties(po, (List)plug.get("properties"), fetcher);
						plugins.add(po);
					}
				}
				
				final ObserverCenter oc = new ObserverCenter(title, this, (ILibraryService)context.getServiceContainer().getService(ILibraryService.class), plugins,
					killonexit!=null ? killonexit.booleanValue() : true);
							
				IComponentManagementService cs = (IComponentManagementService)context.getServiceContainer().getService(IComponentManagementService.class);
				cs.addComponentListener(context.getComponentIdentifier(), new IComponentListener()
				{
					public void componentRemoved(IComponentDescription desc, Map results)
					{
						oc.dispose();
					}
					
					public void componentChanged(IComponentDescription desc)
					{
					}
					
					public void componentAdded(IComponentDescription desc)
					{
					}
				});
				
				List perspectives = mspacetype.getPropertyList("perspectives");
				for(int j=0; j<perspectives.size(); j++)
				{
					Map sourcepers = (Map)perspectives.get(j);
					Map args = new HashMap();
					args.put("object", sourcepers);
					args.put("fetcher", fetcher);
					IPerspective persp	= (IPerspective)((IObjectCreator)MEnvSpaceInstance.getProperty(sourcepers, "creator")).createObject(args);
					
					List props = (List)sourcepers.get("properties");
					MEnvSpaceInstance.setProperties(persp, props, fetcher);
					
					oc.addPerspective((String)MEnvSpaceInstance.getProperty(sourcepers, "name"), persp);
				}
			}
		}
		
		// Create the environment executor.
		Map mse = (Map)MEnvSpaceInstance.getProperty(mspacetype.getProperties(), "spaceexecutor");
		IParsedExpression exp = (IParsedExpression)MEnvSpaceInstance.getProperty(mse, "expression");
		ISpaceExecutor exe = null;
		if(exp!=null)
		{
			exe = (ISpaceExecutor)exp.getValue(fetcher);	// Executor starts itself
		}
		else
		{
			exe = (ISpaceExecutor)((Class)MEnvSpaceInstance.getProperty(mse, "clazz")).newInstance();
			List props = (List)mse.get("properties");
			MEnvSpaceInstance.setProperties(exe, props, fetcher);
		}
		if(exe!=null)
			exe.start();			
	}
	
	//-------- methods --------
	
	/**
	 *  Add a space type.
	 *  @param typename The type name.
	 *  @param properties The properties.
	 */
	public void addSpaceObjectType(String typename, Map properties)
	{
		synchronized(monitor)
		{
			objecttypes.put(typename, properties);
		}
	}
	
	/**
	 *  Remove a space object type.
	 *  @param typename The type name.
	 */
	public void removeSpaceObjectType(String typename)
	{
		synchronized(monitor)
		{
			objecttypes.remove(typename);
			// Kill running process instances also?
		}
	}
	
	/**
	 *  Add a space process type.
	 *  @param typename The type name.
	 *  @param properties The properties.
	 */
	public void addSpaceProcessType(String typename, Class clazz, List properties)
	{
		synchronized(monitor)
		{
			processtypes.put(typename, new Object[]{clazz, properties});
		}
	}
	
	/**
	 *  Remove a space process type.
	 *  @param typename The type name.
	 */
	public void removeSpaceProcessType(String typename)
	{
		synchronized(monitor)
		{
			processtypes.remove(typename);
		}
	}
	
	/**
	 *  Creates a space process.
	 *  @param typename The type name.
	 *  @param properties The properties.
	 */
	public void createSpaceProcess(String typename, Map properties)
	{
		synchronized(monitor)
		{
			Object id;
			do
			{
				id = objectidcounter.getNext();
			}
			while(spaceobjects.containsKey(id));
			
			// Prepare properties (runtime props override type props).
			Object[] procinfo = (Object[])processtypes.get(typename);
			if(procinfo==null)
				throw new RuntimeException("Unknown space process: "+typename);
			
			try
			{
				ISpaceProcess process = (ISpaceProcess)((Class)procinfo[0]).newInstance();
				properties	= mergeProperties((List)procinfo[1], properties);
				if(properties!=null)
				{
					for(Iterator it = properties.keySet().iterator(); it.hasNext(); )
					{
						String propname = (String)it.next();
						process.setProperty(propname, properties.get(propname)); 
					}
				}
				
				processes.put(id, process);
			}
			catch(Exception e)
			{
				e.printStackTrace();
				throw new RuntimeException("Could not create space process: "+typename, e);
			}
//			process.start(this);	// Done by executor.
		}
	}
	
	/**
	 *  Add a object task type.
	 *  @param typename The type name.
	 *  @param properties The properties.
	 */
	public void addObjectTaskType(String typename, Class clazz, List properties)
	{
		synchronized(monitor)
		{
			tasktypes.put(typename, new Object[]{clazz, properties});
		}
	}
	
	/**
	 *  Remove an object task type.
	 *  @param typename The type name.
	 */
	public void removeObjectTaskType(String typename)
	{
		synchronized(monitor)
		{
			tasktypes.remove(typename);
		}
	}
	
	/**
	 *  Creates an object task.
	 *  @param typename The type name.
	 *  @param properties The properties.
	 *  @return The task id.
	 */
	public Object createObjectTask(String typename, Map properties, Object objectid)
	{
		synchronized(monitor)
		{
//			System.out.println("add task: "+typename+" "+objectid+" "+properties);

			// Prepare properties (runtime props override type props).
			Object[] taskinfo = (Object[])tasktypes.get(typename);
			if(taskinfo==null)
				throw new RuntimeException("Unknown space task: "+typename);
			
			try
			{
				IObjectTask task = (IObjectTask)((Class)taskinfo[0]).newInstance();
				// todo: ensure uniqueness?!
				Object id = taskidcounter.getNext();
				task.setProperty(IObjectTask.PROPERTY_ID, id);
				
				properties	= mergeProperties((List) taskinfo[1], properties);
				if(properties!=null)
				{
					for(Iterator it = properties.keySet().iterator(); it.hasNext(); )
					{
						String propname = (String)it.next();
						task.setProperty(propname, properties.get(propname)); 
					}
				}
				
				SpaceObject object = (SpaceObject)getSpaceObject(objectid);
				object.addTask(task);
				return id;
			}
			catch(Exception e)
			{
				throw new RuntimeException("Could not create space task: "+typename, e);
			}
		}
	}
	
	/**
	 *  Remove an object task.
	 *  @param typename The type name.
	 *  @param properties The properties.
	 */
	public void removeObjectTask(Object taskid, Object objectid)
	{
		synchronized(monitor)
		{
//			System.out.println("remove task: "+taskid+" "+objectid);
			SpaceObject so = (SpaceObject)getSpaceObject(objectid);
			so.removeTask(taskid, null);
		}
	}
	
	/**
	 *  Add a result listener to an object task.
	 *  The listener result will be the task id.
	 *  If the task is already finished, the listener will be notified.
	 */
	public void addTaskListener(Object taskid, Object objectid, IResultListener listener)
	{
		SpaceObject so = (SpaceObject)getSpaceObject(objectid);
		so.addTaskListener(taskid, listener);
	}
	
	/**
	 *  Remove a result listener from an object task.
	 */
	public void removeTaskListener(Object taskid, Object objectid, IResultListener listener)
	{
		SpaceObject so = (SpaceObject)getSpaceObject(objectid);
		so.removeTaskListener(taskid, listener);
	}
	
	/**
	 * Returns then names of the space processes.
	 * @return the names of the space processes
	 */
	public Set getSpaceProcessNames()
	{
		synchronized(monitor)
		{
			return new HashSet(processes.keySet());
		}
	}

	/**
	 * Returns a space process.
	 * @param id ID of the space process
	 * @return the space process or null if not found
	 */
	public ISpaceProcess getSpaceProcess(Object id)
	{
		synchronized(monitor)
		{
			return (ISpaceProcess)processes.get(id);
		}
	}

	/**
	 * Removes a space process.
	 * @param id ID of the space process
	 */
	public void removeSpaceProcess(Object id)
	{
		synchronized(monitor)
		{
			ISpaceProcess process = (ISpaceProcess)processes.remove(id);
			if(process!=null)
				process.shutdown(this);
		}
	}
	
	/** 
	 * Creates an object in this space.
	 * @param type the object's type
	 * @param properties initial properties (may be null)
	 * @param tasks initial task list (may be null)
	 * @param listeners initial listeners (may be null)
	 * @return the object's ID
	 */
	public ISpaceObject createSpaceObject(String typename, Map properties, List tasks)
	{
		if(!objecttypes.containsKey(typename))
			throw new RuntimeException("Unknown space object type: "+typename);
			
		ISpaceObject ret;
		
		synchronized(monitor)
		{
			// Generate id.
			Object id;
			do
			{
				id = objectidcounter.getNext();
			}
			while(spaceobjects.containsKey(id));
			
			// Prepare properties (runtime props override type props).
			properties = mergeProperties(objecttypes.get(typename)!=null ? ((Map)objecttypes.get(typename)).values() : null, properties);
			
			// Create the object.
			ret = new SpaceObject(id, typename, properties, tasks, monitor, this);
			spaceobjects.put(id, ret);

			// Store in type objects.
			List typeobjects = (List)spaceobjectsbytype.get(ret.getType());
			if(typeobjects == null)
			{
				typeobjects = new ArrayList();
				spaceobjectsbytype.put(ret.getType(), typeobjects);
			}
			typeobjects.add(ret);
			
			// Store in owner objects.
			if(properties!=null && properties.get(ISpaceObject.PROPERTY_OWNER)!=null)
			{
				IComponentIdentifier	owner	= (IComponentIdentifier)properties.get(ISpaceObject.PROPERTY_OWNER);
				List ownerobjects = (List)spaceobjectsbyowner.get(owner);
				if(ownerobjects == null)
				{
					ownerobjects = new ArrayList();
					spaceobjectsbyowner.put(owner, ownerobjects);
				}
				ownerobjects.add(ret);
			}
			
			// Create view(s) for the object if any.
			if(dataviewmappings!=null && dataviewmappings.getCollection(typename)!=null)
			{
				for(Iterator it=dataviewmappings.getCollection(typename).iterator(); it.hasNext(); )
				{
					try
					{
						Map	sourceview	= (Map)it.next();
						Map viewargs = new HashMap();
						viewargs.put("sourceview", sourceview);
						viewargs.put("space", this);
						viewargs.put("object", ret);
						
						IDataView	view	= (IDataView)((IObjectCreator)MEnvSpaceInstance.getProperty(sourceview, "creator")).createObject(viewargs);
						addDataView((String)MEnvSpaceInstance.getProperty(sourceview, "name")+"_"+id, view);
					}
					catch(Exception e)
					{
						if(e instanceof RuntimeException)
							throw (RuntimeException)e;
						throw new RuntimeException(e);
					}
				}
			}
			
			// Possibly create component.
			for(Iterator it=avatarmappings.keySet().iterator(); it.hasNext(); )
			{
				String componenttype = (String)it.next();
				AvatarMapping mapping = getAvatarMapping(componenttype, typename);
				if(mapping!=null && mapping.isCreateComponent())
				{
//					final Object	fid	= id;
					
//					String	name	= null;
					if(mapping.getComponentName()!=null)
					{
						SimpleValueFetcher	fetch	= new SimpleValueFetcher();
						fetch.setValue("$space", this);
						fetch.setValue("$object", ret);
						name	= (String) mapping.getComponentName().getValue(fetch);
					}
					
					throw new UnsupportedOperationException();
					
//					// todo: what about arguments etc.?
//					((ApplicationContext)getContext()).createAgent(name, componenttype, null, null, false, false, new IResultListener() {
//						
//						public void resultAvailable(Object source, Object result)
//						{
//							IComponentIdentifier	component	= (IComponentIdentifier)result;
//							
//							setOwner(fid, component);
//							
//							((IComponentManagementService)((ApplicationContext)getContext()).getServiceContainer().getService(IComponentManagementService.class)).resumeComponent(component, null);
////							SComponentManagementService.startComponent(((ApplicationContext)getContext()).getPlatform(), component, null);
//						}
//						
//						public void exceptionOccurred(Object source, Exception exception)
//						{
//							exception.printStackTrace();
//						}
//					}, null);
				}
			}
		}
		
		if(listeners!=null)
		{
			EnvironmentEvent event = new EnvironmentEvent(EnvironmentEvent.OBJECT_CREATED, this, ret, null, null);
			for(int i=0; i<listeners.size(); i++)
			{
				IEnvironmentListener lis = (IEnvironmentListener)listeners.get(i);
				lis.dispatchEnvironmentEvent(event);
			}
		}
		
		return ret;
	}

	/**
	 *  Add type properties to runtime properties.
	 *  Runtime properties have precedence if specified.
	 *  @param typeprops	The type properties (if any).
	 *  @param properties	The runtime properties or null.
	 *  @return	The merged runtime properties.
	 */
	protected Map mergeProperties(Collection typeprops, Map properties)
	{
		if(typeprops!=null)
		{
			if(properties==null)
				properties = new HashMap();
			for(Iterator it=typeprops.iterator(); it.hasNext(); )
			{
				Map	prop	= (Map)it.next();
				String propname = (String)prop.get("name");
				if(!properties.containsKey(propname))
				{
					IParsedExpression exp = (IParsedExpression)prop.get("value");
					if(exp!=null)
					{
						boolean dyn = ((Boolean)prop.get("dynamic")).booleanValue();
						if(dyn)
							properties.put(propname, exp);
						else
							properties.put(propname, exp.getValue(fetcher));
					}
				}
			}
		}
		return properties;
	}
	
	/** 
	 * Destroys an object in this space.
	 * @param id the object's ID
	 */
	public void destroySpaceObject(final Object id)
	{
		SpaceObject obj;
		synchronized(monitor)
		{
			obj = (SpaceObject)spaceobjects.get(id);
			if(obj==null)
				throw new RuntimeException("No object found for id: "+id);
			String	objecttype	= obj.getType();
			
			// Possibly kill component.
			IComponentIdentifier component = (IComponentIdentifier)obj.getProperty(ISpaceObject.PROPERTY_OWNER);
			if(component!=null)
			{
				String	componenttype = getContext().getComponentType(component);
				AvatarMapping mapping = getAvatarMapping(componenttype, objecttype);
				if(mapping.isKillComponent())
				{
					IComponentManagementService ces = (IComponentManagementService)getContext().getServiceContainer().getService(IComponentManagementService.class);
					ces.destroyComponent(component, null);
				}
			}
			
			// shutdown and jettison tasks
			obj.clearTasks();

			// remove object
			spaceobjects.remove(id);
			List typeobjs = (List)spaceobjectsbytype.get(objecttype);
			typeobjs.remove(obj);
			if(typeobjs.size()==0)
				spaceobjectsbytype.remove(obj.getType());
			
			if(obj.getProperty(ISpaceObject.PROPERTY_OWNER)!=null)
			{
				List ownedobjs = (List)spaceobjectsbyowner.get(obj.getProperty(ISpaceObject.PROPERTY_OWNER));
				ownedobjs.remove(obj);
				if(ownedobjs.size()==0)
					spaceobjectsbyowner.remove(obj.getProperty(ISpaceObject.PROPERTY_OWNER));
			}

			// Remove view(s) for the object if any.
			if(dataviewmappings!=null && dataviewmappings.getCollection(objecttype)!=null)
			{
				for(Iterator it=dataviewmappings.getCollection(objecttype).iterator(); it.hasNext(); )
				{
					Map	sourceview	= (Map)it.next();
					removeDataView((String)MEnvSpaceInstance.getProperty(sourceview, "name")+"_"+id);
				}
			}
		}
		
		// signal removal
//		// hmm? what about calling destroy on object? could it do sth. else than throwing event?
//		ObjectEvent event = new ObjectEvent(ObjectEvent.OBJECT_REMOVED);
//		event.setParameter("space_name", getName());
//		obj.fireObjectEvent(event);
		
		if(listeners!=null)
		{
			EnvironmentEvent ev = new EnvironmentEvent(EnvironmentEvent.OBJECT_DESTROYED, this, obj, null, null);
			for(int i=0; i<listeners.size(); i++)
			{
				IEnvironmentListener lis = (IEnvironmentListener)listeners.get(i);
				lis.dispatchEnvironmentEvent(ev);
			}
		}
	}
	
	/**
	 * Returns an object in this space.
	 * @param id the object's ID
	 * @return the object in this space
	 */
	public ISpaceObject getSpaceObject(Object id)
	{
		synchronized(monitor)
		{
			ISpaceObject ret = (ISpaceObject)spaceobjects.get(id);
			if(ret==null)
				throw new RuntimeException("Space object not found: "+id);
			return ret;
		}
	}
	
	/**
	 * Returns an object in this space.
	 * @param id the object's ID
	 * @return the object in this space
	 */
	public ISpaceObject getSpaceObject0(Object id)
	{
		synchronized(monitor)
		{
			return (ISpaceObject)spaceobjects.get(id);
		}
	}
	
	/**
	 * Get all space object of a specific type.
	 * @param type The space object type.
	 * @return The space objects of the desired type.
	 */
	public ISpaceObject[] getSpaceObjectsByType(String type)
	{
		List obs = (List)spaceobjectsbytype.get(type);
		return obs==null? new ISpaceObject[0]: (ISpaceObject[])obs.toArray(new ISpaceObject[obs.size()]); 
	}
	
	/**
	 * Adds an avatar mapping.
	 * @param componenttype The component type.
	 * @param objecttype The object type to represent the component.
	 */
	public void addAvatarMappings(AvatarMapping mapping)
	{
		synchronized(monitor)
		{
			this.avatarmappings.put(mapping.getComponentType(), mapping);			
		}
	}

	/**
	 * Remove an avatar mapping.
	 * @param componenttype The component type.
	 * @param objecttype The object type to represent the component.
	 */
	public void removeAvatarMappings(AvatarMapping mapping)
	{
		synchronized(monitor)
		{
			this.avatarmappings.remove(mapping.getComponentType(), mapping);			
		}
	}
	
	/**
	 * Adds an space action.
	 * @param actionId the action ID
	 * @param action the action
	 */
	public void addSpaceAction(String id, ISpaceAction action)
	{
		synchronized(monitor)
		{
			actions.put(id, action);
		}
	}
	
	/**
	 * Adds an space action.
	 * @param actionId the action ID
	 * @param action the action
	 */
	public ISpaceAction	getSpaceAction(String id)
	{
		ISpaceAction	ret	= (ISpaceAction)actions.get(id);
		if(ret==null)
		{
			throw new RuntimeException("No such space action: "+id);
		}
		return ret;
	}

	/**
	 * Removes an space action.
	 * @param actionId the action ID
	 */
	public void removeSpaceAction(String id)
	{
		synchronized(monitor)
		{	
			actions.remove(id);
		}
	}
	
	/**
	 * Schedules an space action.
	 * @param id Id of the action
	 * @param parameters parameters for the action (may be null)
	 * @param listener the result listener
	 */
	public void performSpaceAction(String id, Map parameters, IResultListener listener)
	{
		synchronized(monitor)
		{
			actionlist.scheduleComponentAction(getSpaceAction(id), parameters, listener);
		}
	}
	
	/**
	 * Performs a space action.
	 * @param id Id of the action
	 * @param parameters parameters for the action (may be null)
	 * @return return value of the action
	 */
	public Object performSpaceAction(String id, Map parameters)
	{
		synchronized(monitor)
		{
			ISpaceAction action = (ISpaceAction)actions.get(id);
			if(action==null)
				throw new RuntimeException("Action not found: "+id);
			return action.perform(parameters, this);
		}
	}
	
	/**
	 *  Create a percept for the given component.
	 *  @param typename The percept type.
	 *  @param data	The content of the percept (if any).
	 *  @param component The component that should receive the percept.
	 */
	public void createPercept(String typename, Object data, IComponentIdentifier component, ISpaceObject avatar)
	{
		synchronized(monitor)
		{
//			if(!percepttypes.containsKey(typename))
//				throw new RuntimeException("Unknown percept type: "+typename);
			
//			System.out.println("New percept: "+typename+", "+data+", "+component);
			
			String	componenttype = context.getComponentType(component);
			List procs	= (List)perceptprocessors.get(componenttype);
			IPerceptProcessor proc = null;
			if(procs!=null)
			{
				for(int i=0; i<procs.size() && proc==null; i++)
				{
					Object[] tmp = (Object[])procs.get(i);
					if(tmp[0]==null || ((Collection)tmp[0]).contains(typename))
						proc = (IPerceptProcessor)tmp[1];
				}
			}
			
			if(proc!=null)
				perceptlist.schedulePercept(typename, data, component, avatar, proc);
			else
				System.out.println("Warning: No processor for percept: "+typename+", "+data+", "+component+", "+avatar);
		}
	}
	
	/**
	 *  Get the owner of an object.
	 *  @param id The id.
	 *  @return The owner.
	 */
	public IComponentIdentifier	getOwner(Object id)
	{
		synchronized(monitor)
		{
			ISpaceObject obj = getSpaceObject(id); 
			if(obj==null)
				throw new RuntimeException("Space object not found: "+id);
			return (IComponentIdentifier)obj.getProperty(ISpaceObject.PROPERTY_OWNER);
		}
	}
	
	/**
	 *  Set the owner of an object.
	 *  @param id The object id.
	 *  @param pos The object owner.
	 */
	public void setOwner(Object id, IComponentIdentifier owner)
	{
		synchronized(monitor)
		{
			ISpaceObject obj = getSpaceObject(id); 
			if(obj==null)
				throw new RuntimeException("Space object not found: "+id);
			Object oldowner = obj.getProperty(ISpaceObject.PROPERTY_OWNER);
			if(oldowner!=null)
			{
				List ownedobjs = (List)spaceobjectsbyowner.get(oldowner);
				ownedobjs.remove(obj);
				if(ownedobjs.size()==0)
					spaceobjectsbyowner.remove(oldowner);
			}
			if(owner!=null)
			{
				List ownedobjs = (List)spaceobjectsbyowner.get(owner);
				if(ownedobjs==null)
				{
					ownedobjs = new ArrayList();
					spaceobjectsbyowner.put(owner, ownedobjs);
				}
				ownedobjs.add(obj);
			}
			obj.setProperty(ISpaceObject.PROPERTY_OWNER, owner);
		}
	}
	
	/**
	 *  Get the avatar objects.
	 *  @return The avatar objects. 
	 */
	public ISpaceObject[] getAvatars(IComponentIdentifier owner)
	{
		synchronized(monitor)
		{
			List ownedobjs = (List)spaceobjectsbyowner.get(owner);
			return ownedobjs==null? new ISpaceObject[0]: (ISpaceObject[])ownedobjs.toArray(new ISpaceObject[ownedobjs.size()]);
		}
	}
	
	/**
	 *  Get the avatar objects.
	 *  @return The avatar objects. 
	 */
	public IComponentIdentifier[] getComponents()
	{
		synchronized(monitor)
		{
			return (IComponentIdentifier[])spaceobjectsbyowner.keySet().toArray(new IComponentIdentifier[spaceobjectsbyowner.keySet().size()]);
		}
	}
	
	/**
	 *  Get the avatar object.
	 *  @return The avatar object. 
	 */
	public ISpaceObject getAvatar(IComponentIdentifier owner)
	{
		synchronized(monitor)
		{
			ISpaceObject ret = null;
			List ownedobjs = (List)spaceobjectsbyowner.get(owner);
			if(ownedobjs!=null)
			{
				if(ownedobjs.size()>1)
					throw new RuntimeException("More than one avatar for component: "+owner);
				else if(ownedobjs.size()==1)
					ret = (ISpaceObject)ownedobjs.get(0);
			}
			return ret;
		}
	}
	
	/**
	 * Adds a view to the space.
	 * @param name name of the view
	 * @param view the view
	 */
	public void addDataView(String name, IDataView view)
	{
		synchronized (monitor)
		{
			views.put(name, view);
		}
	}
	
	/**
	 * Removes a view from the space.
	 * @param name name of the view
	 */
	public void removeDataView(String name)
	{
		synchronized (monitor)
		{
			views.remove(name);
		}
	}
	
	/**
	 * Gets a specific view.
	 * @param name name of the view
	 * @return the view
	 */
	public IDataView getDataView(String name)
	{
		synchronized (monitor)
		{
			return (IDataView) views.get(name);
		}
	}
	
	/**
	 * Get all available dataviews in this space.
	 * @return all available dataviews
	 */
	public Map getDataViews()
	{
		synchronized (monitor)
		{
			return new HashMap(views);
		}
	}
	
	/**
	 *  Add a mapping from object type to data view
	 *  @param objecttype	The object type.
	 *  @param view	Settings for view creation.
	 */
	public void addDataViewMapping(String objecttype, Map view)
	{
		synchronized(monitor)
		{
			dataviewmappings.put(objecttype, view);
		}
	}

	/**
	 *  Add an environment listener.
	 *  @param listener The environment listener. 
	 */
	public void addEnvironmentListener(IEnvironmentListener listener)
	{
		synchronized(monitor)
		{
			if(listeners==null)
				listeners = new ArrayList();
			listeners.add(listener);
		}
	}
	
	/**
	 *  Remove an environment listener.
	 *  @param listener The environment listener. 
	 */
	public void removeEnvironmentListener(IEnvironmentListener listener)
	{
		synchronized(monitor)
		{
			listeners.remove(listener);
			if(listeners.size()==0)
				listeners = null;
		}
	}
	
	/**
	 * Adds a percept generator.
	 * @param id The percept generator id.
	 * @param gen The percept generator.
	 */
	public void addPerceptGenerator(Object id, IPerceptGenerator gen)
	{
		synchronized(monitor)
		{
			addEnvironmentListener(gen);
			perceptgenerators.put(id, gen);
		}
	}
	
	/**
	 * Remove a percept generator.
	 * @param id The percept generator id.
	 */
	public void removePerceptGenerator(Object id)
	{
		synchronized(monitor)
		{
			removeEnvironmentListener((IEnvironmentListener)perceptgenerators.remove(id));
		}
	}

	/**
	 *  Add a percept processor.
	 *  @param	componenttype	The component type.
	 *  @param	proc	The percept processor.
	 */
	public void addPerceptProcessor(String componenttype, Set percepttypes, IPerceptProcessor proc)
	{
		synchronized(monitor)
		{
			perceptprocessors.put(componenttype, new Object[]{percepttypes, proc});
		}
	}
	
	/**
	 *  remove a percept processor.
	 *  @param	componenttype	The component type.
	 *  @param	proc	The percept processor.
	 */
	public void removePerceptProcessor(String componenttype, IPerceptProcessor proc)
	{
		synchronized(monitor)
		{
			List procs = (List)perceptprocessors.get(componenttype);
			for(int i=0; i<procs.size(); i++)
			{
				Object[] tmp = (Object[])procs.get(i);
				if(proc.equals(tmp[1]))
				{
					perceptprocessors.remove(componenttype, tmp);
					break;
				}
			}
		}
	}
	
	/**
	 *  Add a space percept type.
	 *  @param typename The percept name.
	 *  @param objecttypes The objecttypes.
	 *  @param componenttypes The componenttypes.
	 */
	public void addPerceptType(PerceptType percepttype)
	{
		synchronized(monitor)
		{
			percepttypes.put(percepttype.getName(), percepttype);
		}
	}
	
	/**
	 *  Remove a space process type.
	 *  @param typename The type name.
	 */
	public void removePerceptType(String typename)
	{
		synchronized(monitor)
		{
			percepttypes.remove(typename);
		}
	}
	
	/**
	 *  Get a space percept type.
	 *  @param percepttype The name of the percept type.
	 *  @return The percept type. 
	 */
	public PerceptType getPerceptType(String percepttype)
	{
		synchronized(monitor)
		{
			return (PerceptType)percepttypes.get(percepttype);
		}
	}
	
	//-------- ISpace methods --------
	
	/**
	 *  Called when an component was added. 
	 */
	public void componentAdded(IComponentIdentifier aid, String type)
	{
		synchronized(monitor)
		{
			// Possibly add or create avatar(s) if any.
			if(initialavatars!=null && initialavatars.containsKey(aid))
			{
				Object[]	ia	= (Object[])initialavatars.get(aid);
				String	objecttype	=	(String)ia[0];
				Map	props	=	(Map)ia[1];
				if(props==null)
					props	= new HashMap();
				props.put(ISpaceObject.PROPERTY_OWNER, aid);
				createSpaceObject(objecttype, props, null);
			}
			else
			{
				String	componenttype	= context.getComponentType(aid);
				if(componenttype!=null && avatarmappings.getCollection(componenttype)!=null)
				{
					for(Iterator it=avatarmappings.getCollection(componenttype).iterator(); it.hasNext(); )
					{
						AvatarMapping mapping = (AvatarMapping)it.next();
						if(mapping.isCreateAvatar())
						{
							Map	props	= new HashMap();
							props.put(ISpaceObject.PROPERTY_OWNER, aid);
							createSpaceObject(mapping.getObjectType(), props, null);
						}
					}
				}
			}
			
			if(perceptgenerators!=null)
			{
				for(Iterator it=perceptgenerators.keySet().iterator(); it.hasNext(); )
				{
					IPerceptGenerator gen = (IPerceptGenerator)perceptgenerators.get(it.next());
					gen.componentAdded(aid, this);
				}
			}
		}
	}
	
	/**
	 *  Called when an component was removed.
	 */
	public void componentRemoved(IComponentIdentifier aid)
	{
		synchronized(monitor)
		{
			String	componenttype	= context.getComponentType(aid);
			
			// Possibly kill avatars of that component.
			if(componenttype!=null && avatarmappings.getCollection(componenttype)!=null)
			{
				ISpaceObject[] avatars = getAvatars(aid);
				if(avatars!=null)
				{
					for(int i=0; i<avatars.length; i++)
					{
						String avatartype = avatars[i].getType();
						AvatarMapping mapping = getAvatarMapping(componenttype, avatartype);
						
						if(mapping!=null && mapping.isKillAvatar())
						{
							destroySpaceObject(avatars[i].getId());
						}
					}
				}
			}
			
			if(perceptgenerators!=null)
			{
				for(Iterator it=perceptgenerators.keySet().iterator(); it.hasNext(); )
				{
					IPerceptGenerator gen = (IPerceptGenerator)perceptgenerators.get(it.next());
					gen.componentRemoved(aid, this);
				}
			}
		}
		
		// Remove the owned object too?
	}
		
	/**
	 *  Get the context.
	 *  @return The context.
	 */
	public IApplication getContext()
	{
		return context;
	}
	
	/**
	 *  Terminate the space.
	 */
	public void terminate()
	{
		
	}

	/**
	 * Returns a property.
	 * @param name name of the property
	 * @return the property
	 */
	public Object getProperty(String name)
	{
		synchronized(monitor)
		{
			Object ret = super.getProperty(name);
			
			if(ret instanceof IParsedExpression)
			{
				ret = ((IParsedExpression) ret).getValue(getFetcher());
			}
			
			return ret;
		}
	}
	
	/**
	 *  Get the value fetcher.
	 *  @return The fetcher.
	 */
	public SimpleValueFetcher getFetcher()
	{
		if(fetcher==null)
		{
			this.fetcher = new SimpleValueFetcher();
			fetcher.setValue("$space", this);
		}
		return this.fetcher;
	}

	/**
	 *  Set the fetcher.
	 *  @param fetcher The fetcher to set.
	 */
	public void setFetcher(SimpleValueFetcher fetcher)
	{
		this.fetcher = fetcher;
	}

	/**
	 *  Get the space objects.
	 */
	// Hack!!! getSpaceObjecs() implemented in Space2D???
	public Collection	getSpaceObjectsCollection()
	{
		return spaceobjects.values();
	}
	
	/**
	 *  Get the processes.
	 */
	public Collection	getProcesses()
	{
		return processes.values();
	}
	
	/**
	 *  Get the list of scheduled component actions
	 */
	public ComponentActionList	getComponentActionList()
	{
		return actionlist;
	}
	
	/**
	 *  Get the list of scheduled percepts.
	 */
	public PerceptList	getPerceptList()
	{
		return perceptlist;
	}
	
	/**
	 *  Get the views.
	 */
	public Collection	getViews()
	{
		return views.values();
	}
	
	/**
	 *  Fire an environment event.
	 *  @param event The event.
	 */
	protected void fireEnvironmentEvent(EnvironmentEvent event)
	{
		IEnvironmentListener[]	alisteners	= null;
		synchronized(monitor)
		{
			if(listeners!=null)
				alisteners	= (IEnvironmentListener[])listeners.toArray(new IEnvironmentListener[listeners.size()]);
		}

		if(alisteners!=null)
		{
			for(int i=0; i<alisteners.length; i++)
			{
				alisteners[i].dispatchEnvironmentEvent(event);
			}
		}
	}
	
	/**
	 *  Fire an object event.
	 *  @param object The object.
	 *  @param property The changed property.
	 *  @param value The new property value.
	 */
	protected void fireObjectEvent(SpaceObject object, String property, Object value)
	{
		boolean	fire	= false;
		synchronized(monitor)
		{
			Map	props	= (Map)objecttypes.get(object.getType());
			if(props!=null)
			{
				Map	prop	= (Map)props.get(property);
				if(prop!=null)
				{
					Object	event	= prop.get("event");
					fire	= event!=null && ((Boolean)event).booleanValue();
				}
			}
		}
		if(fire)
			fireEnvironmentEvent(new EnvironmentEvent(EnvironmentEvent.OBJECT_PROPERTY_CHANGED, this, object, property, value));
	}
	
	/**
	 *  Synchronized counter class
	 */
	private class AtomicCounter
	{
		long count_;
		
		public AtomicCounter()
		{
			count_ = 0;
		}
		
		public synchronized Long getNext()
		{
			return new Long(count_++);
		}
	}

	/**
	 *  Initial settings for the avatar of a specific component.
	 *  @param ownerid	The component id.
	 *  @param type	The object type.
	 *  @param props	The properties for the object (if any).
	 */
	public void addInitialAvatar(IComponentIdentifier ownerid, String type,	Map props)
	{
		synchronized(monitor)
		{
			if(initialavatars==null)
				initialavatars	= new HashMap();

			initialavatars.put(ownerid, new Object[]{type, props});
		}
	}
	
	/**
	 *  Get the avatar mapping for an component avatar combination.
	 */
	protected AvatarMapping getAvatarMapping(String componenttype, String avatartype)
	{
		AvatarMapping mapping = null;
		for(Iterator it=avatarmappings.getCollection(componenttype).iterator(); mapping==null && it.hasNext(); )
		{
			AvatarMapping	test = (AvatarMapping)it.next();
			if(avatartype.equals(test.getObjectType()))
				mapping	= test;
		}
		return mapping;
	}
	
	/**
	 *  Add a new data provider.
	 *  @param name The name.
	 *  @param provider The provider.
	 */
	public void addDataProvider(String name, ITableDataProvider provider)
	{
		dataproviders.put(name, provider);
	}
	
	/**
	 *  Get a data provider.
	 *  @param name The name.
	 *  @return The provider.
	 */
	public ITableDataProvider getDataProvider(String name)
	{
		return (ITableDataProvider)dataproviders.get(name);
	}
	
	/**
	 *  Add a new data consumer.
	 *  @param consumer The consumer.
	 */
	public void addDataConsumer(String name, ITableDataConsumer consumer)
	{
		dataconsumers.put(name, consumer);
	}
	
	/**
	 *  Get a data consumer.
	 *  @param name The name.
	 *  @return The consumer.
	 */
	public ITableDataConsumer getDataConsumer(String name)
	{
		return (ITableDataConsumer)dataconsumers.get(name);
	}
	
	/**
	 *  Get the data consumers.
	 *  @return The data consumers.
	 */
	public Collection getDataConsumers()
	{
		return dataconsumers.values();
	}
}
