package jadex.extension.envsupport.environment;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.SwingUtilities;

import jadex.bridge.BasicComponentIdentifier;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.component.IMonitoringComponentFeature;
import jadex.bridge.modelinfo.SubcomponentTypeInfo;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.cms.CMSComponentDescription;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.cms.IComponentManagementService.CMSStatusEvent;
import jadex.bridge.service.types.cms.IComponentManagementService.CMSTerminatedEvent;
import jadex.bridge.service.types.monitoring.IMonitoringEvent;
import jadex.bridge.service.types.monitoring.IMonitoringService.PublishEventLevel;
import jadex.commons.IFilter;
import jadex.commons.IPropertyObject;
import jadex.commons.IValueFetcher;
import jadex.commons.collection.MultiCollection;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFutureCommandResultListener;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.meta.IPropertyMetaDataSet;
import jadex.extension.envsupport.IObjectCreator;
import jadex.extension.envsupport.MEnvSpaceInstance;
import jadex.extension.envsupport.MEnvSpaceType;
import jadex.extension.envsupport.MObjectType;
import jadex.extension.envsupport.MObjectTypeProperty;
import jadex.extension.envsupport.dataview.IDataView;
import jadex.extension.envsupport.environment.ComponentActionList.ActionEntry;
import jadex.extension.envsupport.environment.space2d.Space2D;
import jadex.extension.envsupport.environment.space3d.Space3D;
import jadex.extension.envsupport.evaluation.DefaultDataProvider;
import jadex.extension.envsupport.evaluation.IObjectSource;
import jadex.extension.envsupport.evaluation.ITableDataConsumer;
import jadex.extension.envsupport.evaluation.ITableDataProvider;
import jadex.extension.envsupport.evaluation.SpaceObjectSource;
import jadex.extension.envsupport.math.Vector2Double;
import jadex.extension.envsupport.math.Vector3Double;
import jadex.extension.envsupport.observer.gui.IObserverCenter;
import jadex.extension.envsupport.observer.gui.ObserverCenter;
import jadex.extension.envsupport.observer.perspective.IPerspective;
import jadex.javaparser.IParsedExpression;
import jadex.javaparser.SimpleValueFetcher;

/**
 *  Abstract base class for environment space. 
 */
public abstract class AbstractEnvironmentSpace	extends SynchronizedPropertyObject	implements IEnvironmentSpace
{
	//-------- attributes --------
		
	/** The context. */
	protected IExternalAccess exta;
	
	/** The space object types. */
	protected Map objecttypes;
	
	/** The space object meta data **/
	protected Map objecttypesMeta;
	
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
	protected MultiCollection<String, Object[]> perceptprocessors;
	
	/** Avatar mappings. */
	protected MultiCollection<String, AvatarMapping> avatarmappings;

	/** Initial avatar settings (cid -> [type, props]). */
	protected Map initialavatars;

	/** Data view mappings. */
	protected MultiCollection<String, Map>	dataviewmappings;
	
	/** The environment processes. */
	protected Map processes;
	
	/** Long/ObjectIDs (keys) and environment objects (values). */
	protected Map spaceobjects;
	
	/** Types of EnvironmentObjects and lists of EnvironmentObjects of that type (typed view). */
	protected Map spaceobjectsbytype;
	
	/** Space objects by owner. */
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

	/** The zombie objects. */
	protected Map zombieobjects;
	
	/** The observers. */
	protected List observercenters;
	
	/** The class loader. */
	protected ClassLoader	classloader;
	
	protected IInternalAccess ia;
	protected MEnvSpaceInstance config;
	protected IValueFetcher pfetcher;
	

	//-------- constructors --------
	
	/**
	 *  Create an environment space
	 */
	public AbstractEnvironmentSpace()
	{
		super(null, new Object());
		
		this.views = new HashMap();
		this.avatarmappings = new MultiCollection<String, AvatarMapping>();
		this.dataviewmappings = new MultiCollection<String, Map>();
		this.actions = new HashMap();
		this.processtypes = new HashMap();
		this.tasktypes = new HashMap();
		this.processes = new HashMap();
		this.percepttypes = new HashMap();
		this.perceptgenerators = new HashMap();
		this.perceptprocessors = new MultiCollection<String, Object[]>();
		this.objecttypes = new HashMap();
		this.objecttypesMeta = new HashMap();
		this.spaceobjects = new HashMap();
		this.zombieobjects = new HashMap();
		this.spaceobjectsbytype = new HashMap();
		this.spaceobjectsbyowner = new HashMap();
		
		this.objectidcounter = new AtomicCounter();
		this.taskidcounter = new AtomicCounter();
		this.actionlist	= new ComponentActionList(this);
		this.perceptlist = new PerceptList(this);
		
		this.dataproviders = new HashMap();
		this.dataconsumers = new HashMap();
		
		this.observercenters = new ArrayList();
	}
	
	public void	setInitData(IInternalAccess ia, MEnvSpaceInstance config, IValueFetcher pfetcher)
	{
		this.ia	= ia;
		this.config	= config;
		this.pfetcher	= pfetcher;
	}
	
	/**
	 *  Create a space.
	 */
	public IFuture<Void>	initSpace()
	{
		final Future<Void>	ret	= new Future<Void>();
		
//		if(ia.getModel().getFullName().equals("jadex.bdibpmn.examples.marsworld.MarsWorld"))
//			System.out.println("Initing space: "+this);

		
		try
		{
			this.classloader	= ia.getClassLoader();
			final MEnvSpaceType	mspacetype	= (MEnvSpaceType)config.getType();
			
			final SimpleValueFetcher fetcher = new SimpleValueFetcher(pfetcher);
			fetcher.setValue("$space", this);
			this.setFetcher(fetcher);
			
			List mspaceprops = mspacetype.getPropertyList("properties");
			MEnvSpaceType.setProperties(this, mspaceprops, fetcher);
			List spaceprops = config.getPropertyList("properties");
			MEnvSpaceType.setProperties(this, spaceprops, fetcher);
			
			this.exta = ia.getExternalAccess();
			if(this instanceof Space2D) // Hack?
			{
				Double width = config.getProperty("width")!=null? (Double)config.getProperty("width"): (Double)mspacetype.getProperty("width");
				Double height = config.getProperty("height")!=null? (Double)config.getProperty("height"): (Double)mspacetype.getProperty("height");
				((Space2D)this).setAreaSize(Vector2Double.getVector2(width, height));
			}
			if(this instanceof Space3D) // Hack?
			{
				Double width = config.getProperty("width")!=null? (Double)config.getProperty("width"): (Double)mspacetype.getProperty("width");
				Double height = config.getProperty("height")!=null? (Double)config.getProperty("height"): (Double)mspacetype.getProperty("height");
				Double depth = config.getProperty("depth")!=null? (Double)config.getProperty("depth"): (Double)mspacetype.getProperty("depth");
				((Space3D)this).setAreaSize(Vector3Double.getVector3(width,depth, height));
			}
			// Create space object types.
			List objecttypes = mspacetype.getPropertyList("objecttypes");
			if(objecttypes!=null)
			{
				for(int i=0; i<objecttypes.size(); i++)
				{
					MObjectType mobjecttype = (MObjectType)objecttypes.get(i);
	//				List props = (List)mobjecttype.get("properties");
	//				Map	properties = null;
	//				if(props!=null)
	//				{
	//					properties	= new LinkedHashMap();
	//					Map propertiesMetaData = new HashMap();
	//					for(int j=0; j<props.size(); j++)
	//					{
	//						Map	prop	= (Map)props.get(j);
	//						properties.put(prop.get("name"), prop);
	//					}
	//				}
	//				Map properties = convertProperties(props, fetcher);
	//				System.out.println("Adding environment object type: "+(String)getProperty(mobjecttype, "name")+" "+props);
					
	//				for(Iterator it = mobjecttype.iterator(); it.hasNext();)
	//					System.out.println(((MObjectTypeProperty)it.next()).getType());
					
					this.addSpaceObjectType(mobjecttype.getName(), mobjecttype);
				}
			}
			// Add avatar mappings.
			List avmappings = mspacetype.getPropertyList("avatarmappings");
			if(avmappings!=null)
			{
				for(int i=0; i<avmappings.size(); i++)
				{
					AvatarMapping mapping = (AvatarMapping)avmappings.get(i);
	//				String componenttype = (String)getProperty(mmapping, "componenttype");
	//				String avatartype = (String)(String)getProperty(mmapping, "objecttype");
	//				Boolean createavatar = (Boolean)getProperty(mmapping, "createavatar");
	//				Boolean createcomponent = (Boolean)getProperty(mmapping, "createcomponent");
	//				Boolean killavatar = (Boolean)getProperty(mmapping, "killavatar");
	//				Boolean killcomponent = (Boolean)getProperty(mmapping, "killcomponent");
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
					
					pt.setName((String)MEnvSpaceType.getProperty(mpercepttype, "name"));
					
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
					ISpaceAction action = (ISpaceAction)((Class)MEnvSpaceType.getProperty(maction, "clazz")).newInstance();
					List props = (List)maction.get("properties");
					MEnvSpaceType.setProperties(action, props, fetcher);
					
	//				System.out.println("Adding environment action: "+getProperty(maction, "name"));
					this.addSpaceAction((String)MEnvSpaceType.getProperty(maction, "name"), action);
				}
			}
			// Create process types.
			List processtypes = mspacetype.getPropertyList("processtypes");
			if(processtypes!=null)
			{
				for(int i=0; i<processtypes.size(); i++)
				{
					Map mprocess = (Map)processtypes.get(i);
	//				ISpaceProcess process = (ISpaceProcess)((Class)getProperty(mprocess, "clazz")).newInstance();
					List props = (List)mprocess.get("properties");
					String name = (String)MEnvSpaceType.getProperty(mprocess, "name");
					Class clazz = (Class)MEnvSpaceType.getProperty(mprocess, "clazz");
					
	//				System.out.println("Adding environment process: "+getProperty(mprocess, "name"));
					this.addSpaceProcessType(name, clazz, props);
				}
			}
			// Create task types.
			List tasktypes = mspacetype.getPropertyList("tasktypes");
			if(tasktypes!=null)
			{
				for(int i=0; i<tasktypes.size(); i++)
				{
					Map mtask = (Map)tasktypes.get(i);
					List props = (List)mtask.get("properties");
					String name = (String)MEnvSpaceType.getProperty(mtask, "name");
					Class clazz = (Class)MEnvSpaceType.getProperty(mtask, "clazz");
					
	//				System.out.println("Adding object task: "+getProperty(mtask, "name"));
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
					IPerceptGenerator gen = (IPerceptGenerator)((Class)MEnvSpaceType.getProperty(mgen, "clazz")).newInstance();
					List props = (List)mgen.get("properties");
					MEnvSpaceType.setProperties(gen, props, fetcher);
					
	//				System.out.println("Adding environment percept generator: "+getProperty(mgen, "name"));
					this.addPerceptGenerator(MEnvSpaceType.getProperty(mgen, "name"), gen);
				}
			}
			// Create percept processors.
			List pmaps = mspacetype.getPropertyList("perceptprocessors");
			if(pmaps!=null)
			{
				for(int i=0; i<pmaps.size(); i++)
				{
					Map mproc = (Map)pmaps.get(i);
					IPerceptProcessor proc = (IPerceptProcessor)((Class)MEnvSpaceType.getProperty(mproc, "clazz")).newInstance();
					List props = (List)mproc.get("properties");
					MEnvSpaceType.setProperties(proc, props, fetcher);
					
					String componenttype = (String)MEnvSpaceType.getProperty(mproc, "componenttype");
					List ptypes = (List)mproc.get("percepttypes");
					this.addPerceptProcessor(componenttype, ptypes==null? null: new HashSet(ptypes), proc);
				}
			}
			// Create initial objects.
			List objects = (List)config.getPropertyList("objects");
			if(objects!=null)
			{
				for(int i=0; i<objects.size(); i++)
				{
					Map mobj = (Map)objects.get(i);
					List mprops = (List)mobj.get("properties");
					int num	= 1;
					if(mobj.containsKey("number"))
					{
						num	= ((Number)MEnvSpaceType.getProperty(mobj, "number")).intValue();
					}
					
					List tasks = (List)mobj.get("tasks");
					
					for(int j=0; j<num; j++)
					{
						fetcher.setValue("$number", Integer.valueOf(j));
						fetcher.setValue("$n", Integer.valueOf(j));
						Map props = MEnvSpaceType.convertProperties(mprops, fetcher);
						
						ISpaceObject so = this.createSpaceObject((String)MEnvSpaceType.getProperty(mobj, "type"), props, null);
					
						if(tasks!=null)
						{
							for(int k=0; k<tasks.size(); k++)
							{
								Map mtask = (Map)tasks.get(k);
								List mtprops = (List)mtask.get("properties");
								Map tprops = MEnvSpaceType.convertProperties(mtprops, fetcher);
								String type = (String)MEnvSpaceType.getProperty(mtask, "type");
								
								this.createObjectTask(type, tprops, so.getId());
				//				System.out.println("Create space process: "+getProperty(mproc, "type"));
							}
						}
					}
				}
			}
			// Register initial avatars
			List avatars = (List)config.getPropertyList("avatars");
			if(avatars!=null)
			{
				for(int i=0; i<avatars.size(); i++)
				{
					Map mobj = (Map)avatars.get(i);
				
					List mprops = (List)mobj.get("properties");
					String	owner	= (String)MEnvSpaceType.getProperty(mobj, "owner");
					if(owner==null)
						throw new RuntimeException("Attribute 'owner' required for avatar: "+mobj);
					IComponentIdentifier	ownerid	= null;
					
					// HACK!!! Do not use ThreadSuspendable
//					IComponentManagementService ces = ((IComponentManagementService)SServiceProvider.getServiceUpwards
//						(ia.getServiceContainer(), IComponentManagementService.class).get(new ThreadSuspendable()));
					if(owner.indexOf("@")!=-1)
//						ownerid	= ces.createComponentIdentifier((String)owner, false);
						ownerid	= new BasicComponentIdentifier((String)owner);
					else
//						ownerid	= ces.createComponentIdentifier((String)owner, true);
						ownerid	= new BasicComponentIdentifier((String)owner, ia.getIdentifier());
					
					Map props = MEnvSpaceType.convertProperties(mprops, fetcher);
					this.addInitialAvatar(ownerid, (String)MEnvSpaceType.getProperty(mobj, "type"), props);
				}
			}
			// Create initial processes.
			List procs = (List)config.getPropertyList("processes");
			if(procs!=null)
			{
				for(int i=0; i<procs.size(); i++)
				{
					Map mproc = (Map)procs.get(i);
					List mprops = (List)mproc.get("properties");
					Map props = MEnvSpaceType.convertProperties(mprops, fetcher);
					this.createSpaceProcess((String)MEnvSpaceType.getProperty(mproc, "type"), props);
	//				System.out.println("Create space process: "+getProperty(mproc, "type"));
				}
			}
			// Create initial space actions.
			List actions = (List)config.getPropertyList("spaceactions");
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
					this.performSpaceAction((String)MEnvSpaceType.getProperty(action, "type"), params);
				}
			}
	//		Map themes = new HashMap();
			List sourceviews = mspacetype.getPropertyList("dataviews");
			if(sourceviews!=null)
			{
				for(int i=0; i<sourceviews.size(); i++)
				{				
					Map sourceview = (Map)sourceviews.get(i);
					if(MEnvSpaceType.getProperty(sourceview, "objecttype")==null)
					{
						Map viewargs = new HashMap();
						viewargs.put("sourceview", sourceview);
						viewargs.put("space", this);
						
						IDataView	view	= (IDataView)((IObjectCreator)MEnvSpaceType.getProperty(sourceview, "creator")).createObject(viewargs);
						this.addDataView((String)MEnvSpaceType.getProperty(sourceview, "name"), view);
					}
					else
					{
						this.addDataViewMapping((String)MEnvSpaceType.getProperty(sourceview, "objecttype"), sourceview);
					}
				}
			}
			// Create the data providers.
			List providers = mspacetype.getPropertyList("dataproviders");
			List tmp = config.getPropertyList("dataproviders");
			
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
						IParsedExpression dataexp = (IParsedExpression)source.get("content");
						IParsedExpression includeexp = (IParsedExpression)source.get("includecondition");
						provs[j] = new SpaceObjectSource(varname, this, objecttype, aggregate, dataexp, includeexp);
					}
					
					String tablename = (String)MEnvSpaceType.getProperty(dcol, "name");
					List subdatas = (List)dcol.get("data");
					String[] columnnames = new String[subdatas.size()];
					IParsedExpression[] exps = new IParsedExpression[subdatas.size()];
					for(int j=0; j<subdatas.size(); j++)
					{
						Map subdata = (Map)subdatas.get(j);
						columnnames[j] = (String)MEnvSpaceType.getProperty(subdata, "name");
						exps[j] = (IParsedExpression)MEnvSpaceType.getProperty(subdata, "content");
					}
					
					ITableDataProvider tprov = new DefaultDataProvider(this, provs, tablename, columnnames, exps);
					this.addDataProvider(tablename, tprov);
				}
			}
			// Create the data consumers.
			List consumers = mspacetype.getPropertyList("dataconsumers");
			tmp = config.getPropertyList("dataconsumers");
			
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
					String name = (String)MEnvSpaceType.getProperty(dcon, "name");
					Class clazz = (Class)MEnvSpaceType.getProperty(dcon, "class");
					ITableDataConsumer con = (ITableDataConsumer)clazz.newInstance();
					MEnvSpaceType.setProperties(con, (List)dcon.get("properties"), fetcher);
					con.setProperty("envspace", this);
					this.addDataConsumer(name, con);
				}
			}
			
//			if(ia.getModel().getFullName().equals("jadex.bdibpmn.examples.marsworld.MarsWorld"))
//				System.out.println("Initing space observers: "+this);
			Future<Void>	ocsdone	= new Future<Void>();
			List observers = config.getPropertyList("observers");
			if(observers!=null)
			{
				CounterResultListener<Void>	crl1	= new CounterResultListener<Void>(observers.size(), new DelegationResultListener<Void>(ocsdone));
				
				for(int i=0; i<observers.size(); i++)
				{				
					Map observer = (Map)observers.get(i);
					
					final String title = MEnvSpaceType.getProperty(observer, "name")!=null? (String)MEnvSpaceType.getProperty(observer, "name"): "Default Observer";
					final Boolean	killonexit	= (Boolean)MEnvSpaceType.getProperty(observer, "killonexit");
					
					List plugs = (List)observer.get("plugins");
					final List plugins = plugs!=null ? new ArrayList() : null;
					if(plugs!=null)
					{
						for(int j=0; j<plugs.size(); j++)
						{
							Map plug = (Map)plugs.get(j);
							Class clazz = (Class)MEnvSpaceType.getProperty(plug, "clazz");
							IPropertyObject po = (IPropertyObject)clazz.newInstance();
							MEnvSpaceType.setProperties(po, (List)plug.get("properties"), fetcher);
							plugins.add(po);
						}
					}
					
					String classname = (String)MEnvSpaceType.getProperty(observer, "class");
					IObserverCenter tmpoc = null;
					if (classname == null)
					{
						tmpoc = new ObserverCenter();
					}
					else
					{
						tmpoc = (IObserverCenter)classloader.loadClass(classname).newInstance();
					}
					
//					if(ia.getModel().getFullName().equals("jadex.bdibpmn.examples.marsworld.MarsWorld"))
//						System.out.println("starting observer: "+this+", "+tmpoc);
					final IObserverCenter oc = tmpoc;
					oc.startObserver(title, AbstractEnvironmentSpace.this,
						ia.getClassLoader(), killonexit!=null ? killonexit.booleanValue() : true);
					observercenters.add(oc);
					
					getExternalAccess().searchService( new ServiceQuery<>( IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM))
						.addResultListener(new DefaultResultListener()
					{
						public void resultAvailable(final Object result)
						{
							((IComponentManagementService)result).listenToComponent(getExternalAccess().getComponentIdentifier())
								.addIntermediateResultListener(new IIntermediateResultListener<IComponentManagementService.CMSStatusEvent>()
							{
								@Override
								public void exceptionOccurred(Exception exception)
								{
								}
								
								@Override
								public void resultAvailable(Collection<CMSStatusEvent> result)
								{
								}
								
								@Override
								public void intermediateResultAvailable(CMSStatusEvent result)
								{
									if(result instanceof CMSTerminatedEvent)
									{
										SwingUtilities.invokeLater(new Runnable()
										{
											public void run()
											{
												oc.dispose();
											}
										});
									}
								}
								
								@Override
								public void finished()
								{
								}
							});
						}
					});
					
					Future<Void>	ocdone	= new Future<Void>();
					ocdone.addResultListener(crl1);
	
					List perspectives = mspacetype.getPropertyList("perspectives");
					CounterResultListener<Void>	crl2	= new CounterResultListener<Void>(perspectives.size(), new DelegationResultListener<Void>(ocdone));
					for(int j=0; j<perspectives.size(); j++)
					{
						Map sourcepers = (Map)perspectives.get(j);
						Map args = new HashMap();
						args.put("object", sourcepers);
						args.put("fetcher", fetcher);
						try
						{
							IPerspective persp	= (IPerspective)((IObjectCreator)MEnvSpaceType.getProperty(sourcepers, "creator")).createObject(args);
							
							List props = (List)sourcepers.get("properties");
							MEnvSpaceType.setProperties(persp, props, fetcher);
							
							oc.addPerspective((String)MEnvSpaceType.getProperty(sourcepers, "name"), persp)
								.addResultListener(crl2);
						}
						catch(Exception e)
						{
							crl2.exceptionOccurred(e);
						}
					}
					
					oc.loadPlugins(plugins);
//					oc.startObserver(title, AbstractEnvironmentSpace.this,
//						ia.getClassLoader(), plugins, killonexit!=null ? killonexit.booleanValue() : true);
				}
			}
			else
			{
				ocsdone.setResult(null);
			}
			
			ocsdone.addResultListener(new DelegationResultListener<Void>(ret)
			{
				public void customResultAvailable(Void result)
				{
//					if(ia.getModel().getFullName().equals("jadex.bdibpmn.examples.marsworld.MarsWorld"))
//						System.out.println("Inited observers: "+this);
					
					Map mse = (Map)MEnvSpaceType.getProperty(mspacetype.getProperties(), "spaceexecutor");
					if(mse!=null)
					{
						IParsedExpression exp = (IParsedExpression)MEnvSpaceType.getProperty(mse, "expression");
						ISpaceExecutor exe = null;
						if(exp!=null)
						{
							exe = (ISpaceExecutor)exp.getValue(fetcher);	// Executor starts itself
						}
						else
						{
							try
							{
								exe = (ISpaceExecutor)((Class)MEnvSpaceType.getProperty(mse, "clazz")).newInstance();
								List props = (List)mse.get("properties");
								MEnvSpaceType.setProperties(exe, props, fetcher);
							}
							catch(Exception e)
							{
								ret.setException(e);
							}
						}
						if(exe!=null)
							exe.start();
					}
					ret.setResultIfUndone(result);
				}
			});
		}
		catch(Exception e)
		{
			ret.setException(e);
		}

		// In case of errors dispose observer centers if any.
		ret.addResultListener(new IResultListener<Void>()
		{
			public void resultAvailable(Void result)
			{
			}

			public void exceptionOccurred(Exception exception)
			{
				exception.printStackTrace();
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						for(int i=0; observercenters!=null && i<observercenters.size(); i++)
						{
							((ObserverCenter)observercenters.get(i)).dispose();
						}
					}
				});
			}
		});
		
		return ret;
	}
	
//	/**
//	 *  Create a space.
//	 */
//	public void	initSpace(final IApplication context, MSpaceInstance config, IValueFetcher pfetcher) throws Exception
//	{
//		MEnvSpaceInstance	si	= (MEnvSpaceInstance)config;
//		final MEnvSpaceType	mspacetype	= (MEnvSpaceType)config.getType();
//		
//		final SimpleValueFetcher fetcher = new SimpleValueFetcher(pfetcher);
//		fetcher.setValue("$space", this);
//		this.setFetcher(fetcher);
//		
//		List mspaceprops = mspacetype.getPropertyList("properties");
//		setProperties(this, mspaceprops, fetcher);
//		List spaceprops = si.getPropertyList("properties");
//		setProperties(this, spaceprops, fetcher);
//		
//		this.application	= context;
//		
//		if(this instanceof Space2D) // Hack?
//		{
//			Double width = si.getProperty("width")!=null? (Double)si.getProperty("width"): (Double)mspacetype.getProperty("width");
//			Double height = si.getProperty("height")!=null? (Double)si.getProperty("height"): (Double)mspacetype.getProperty("height");
//			((Space2D)this).setAreaSize(Vector2Double.getVector2(width, height));
////			System.out.println("areasize: "+width+" "+height);
//		}
//		
//		// Create space object types.
//		List objecttypes = mspacetype.getPropertyList("objecttypes");
//		if(objecttypes!=null)
//		{
//			for(int i=0; i<objecttypes.size(); i++)
//			{
//				MObjectType mobjecttype = (MObjectType)objecttypes.get(i);
////				List props = (List)mobjecttype.get("properties");
////				Map	properties = null;
////				if(props!=null)
////				{
////					properties	= new LinkedHashMap();
////					Map propertiesMetaData = new HashMap();
////					for(int j=0; j<props.size(); j++)
////					{
////						Map	prop	= (Map)props.get(j);
////						properties.put(prop.get("name"), prop);
////					}
////				}
////				Map properties = convertProperties(props, fetcher);
////				System.out.println("Adding environment object type: "+(String)getProperty(mobjecttype, "name")+" "+props);
//				
////				for(Iterator it = mobjecttype.iterator(); it.hasNext();)
////					System.out.println(((MObjectTypeProperty)it.next()).getType());
//				
//				this.addSpaceObjectType(mobjecttype.getName(), mobjecttype);
//			}
//		}
//		
//		// Add avatar mappings.
//		List avmappings = mspacetype.getPropertyList("avatarmappings");
//		if(avmappings!=null)
//		{
//			for(int i=0; i<avmappings.size(); i++)
//			{
//				AvatarMapping mapping = (AvatarMapping)avmappings.get(i);
////				String componenttype = (String)getProperty(mmapping, "componenttype");
////				String avatartype = (String)(String)getProperty(mmapping, "objecttype");
////				Boolean createavatar = (Boolean)getProperty(mmapping, "createavatar");
////				Boolean createcomponent = (Boolean)getProperty(mmapping, "createcomponent");
////				Boolean killavatar = (Boolean)getProperty(mmapping, "killavatar");
////				Boolean killcomponent = (Boolean)getProperty(mmapping, "killcomponent");
////				
////				AvatarMapping mapping = new AvatarMapping(componenttype, avatartype);
////				if(createavatar!=null)
////					mapping.setCreateAvatar(createavatar.booleanValue());
////				if(createcomponent!=null)
////					mapping.setCreateComponent(createcomponent.booleanValue());
////				if(killavatar!=null)
////					mapping.setKillAvatar(killavatar.booleanValue());
////				if(killcomponent!=null)
////					mapping.setKillComponent(killcomponent.booleanValue());
////				
//				this.addAvatarMappings(mapping);
//			}
//		}
//		// Create space percept types.
//		List percepttypes = mspacetype.getPropertyList("percepttypes");
//		if(percepttypes!=null)
//		{
//			for(int i=0; i<percepttypes.size(); i++)
//			{
//				Map mpercepttype = (Map)percepttypes.get(i);
//
//				PerceptType pt = new PerceptType();
//				
//				pt.setName((String)getProperty(mpercepttype, "name"));
//				
//				List atypes = (List)mpercepttype.get("componenttypes");
//				pt.setComponentTypes(atypes==null? null: new HashSet(atypes));
//				
//				List otypes = (List)mpercepttype.get("objecttypes");
//				pt.setObjectTypes(otypes==null? null: new HashSet(otypes));
//				
////				System.out.println("Adding environment percept type: "+pt);
//				this.addPerceptType(pt);
//			}
//		}
//		
//		// Create space actions.
//		List spaceactions = mspacetype.getPropertyList("actiontypes");
//		if(spaceactions!=null)
//		{
//			for(int i=0; i<spaceactions.size(); i++)
//			{
//				Map maction = (Map)spaceactions.get(i);
//				ISpaceAction action = (ISpaceAction)((Class)getProperty(maction, "clazz")).newInstance();
//				List props = (List)maction.get("properties");
//				setProperties(action, props, fetcher);
//				
////				System.out.println("Adding environment action: "+getProperty(maction, "name"));
//				this.addSpaceAction((String)getProperty(maction, "name"), action);
//			}
//		}
//		
//		// Create process types.
//		List processtypes = mspacetype.getPropertyList("processtypes");
//		if(processtypes!=null)
//		{
//			for(int i=0; i<processtypes.size(); i++)
//			{
//				Map mprocess = (Map)processtypes.get(i);
////				ISpaceProcess process = (ISpaceProcess)((Class)getProperty(mprocess, "clazz")).newInstance();
//				List props = (List)mprocess.get("properties");
//				String name = (String)getProperty(mprocess, "name");
//				Class clazz = (Class)getProperty(mprocess, "clazz");
//				
////				System.out.println("Adding environment process: "+getProperty(mprocess, "name"));
//				this.addSpaceProcessType(name, clazz, props);
//			}
//		}
//		
//
//		// Create task types.
//		List tasks = mspacetype.getPropertyList("tasktypes");
//		if(tasks!=null)
//		{
//			for(int i=0; i<tasks.size(); i++)
//			{
//				Map mtask = (Map)tasks.get(i);
//				List props = (List)mtask.get("properties");
//				String name = (String)getProperty(mtask, "name");
//				Class clazz = (Class)getProperty(mtask, "clazz");
//				
////				System.out.println("Adding object task: "+getProperty(mtask, "name"));
//				this.addObjectTaskType(name, clazz, props);
//			}
//		}
//		
//		// Create percept generators.
//		List gens = mspacetype.getPropertyList("perceptgenerators");
//		if(gens!=null)
//		{
//			for(int i=0; i<gens.size(); i++)
//			{
//				Map mgen = (Map)gens.get(i);
//				IPerceptGenerator gen = (IPerceptGenerator)((Class)getProperty(mgen, "clazz")).newInstance();
//				List props = (List)mgen.get("properties");
//				setProperties(gen, props, fetcher);
//				
////				System.out.println("Adding environment percept generator: "+getProperty(mgen, "name"));
//				this.addPerceptGenerator(getProperty(mgen, "name"), gen);
//			}
//		}
//		
//		// Create percept processors.
//		List pmaps = mspacetype.getPropertyList("perceptprocessors");
//		if(pmaps!=null)
//		{
//			for(int i=0; i<pmaps.size(); i++)
//			{
//				Map mproc = (Map)pmaps.get(i);
//				IPerceptProcessor proc = (IPerceptProcessor)((Class)getProperty(mproc, "clazz")).newInstance();
//				List props = (List)mproc.get("properties");
//				setProperties(proc, props, fetcher);
//				
//				String componenttype = (String)getProperty(mproc, "componenttype");
//				List ptypes = (List)mproc.get("percepttypes");
//				this.addPerceptProcessor(componenttype, ptypes==null? null: new HashSet(ptypes), proc);
//			}
//		}
//		
//		// Create initial objects.
//		List objects = (List)si.getPropertyList("objects");
//		if(objects!=null)
//		{
//			for(int i=0; i<objects.size(); i++)
//			{
//				Map mobj = (Map)objects.get(i);
//				List mprops = (List)mobj.get("properties");
//				int num	= 1;
//				if(mobj.containsKey("number"))
//				{
//					num	= ((Number)getProperty(mobj, "number")).intValue();
//				}
//				
//				for(int j=0; j<num; j++)
//				{
//					fetcher.setValue("$number", Integer.valueOf(j));
//					Map props = convertProperties(mprops, fetcher);
//					this.createSpaceObject((String)getProperty(mobj, "type"), props, null);
//				}
//			}
//		}
//		
//		// Register initial avatars
//		List avatars = (List)si.getPropertyList("avatars");
//		if(avatars!=null)
//		{
//			for(int i=0; i<avatars.size(); i++)
//			{
//				Map mobj = (Map)avatars.get(i);
//			
//				List mprops = (List)mobj.get("properties");
//				String	owner	= (String)getProperty(mobj, "owner");
//				if(owner==null)
//					throw new RuntimeException("Attribute 'owner' required for avatar: "+mobj);
//				IComponentIdentifier	ownerid	= null;
//				
//				// HACK!!! Do not use ThreadSuspendable
//				IComponentManagementService ces = ((IComponentManagementService)SServiceProvider.getServiceUpwards
//					(context.getServiceContainer(), IComponentManagementService.class).get(new ThreadSuspendable()));
//				if(owner.indexOf("@")!=-1)
//					ownerid	= ces.createComponentIdentifier((String)owner, false);
//				else
//					ownerid	= ces.createComponentIdentifier((String)owner, true);
//				
//				Map props = convertProperties(mprops, fetcher);
//				this.addInitialAvatar(ownerid, (String)getProperty(mobj, "type"), props);
//			}
//		}
//		
//		// Create initial processes.
//		List procs = (List)si.getPropertyList("processes");
//		if(procs!=null)
//		{
//			for(int i=0; i<procs.size(); i++)
//			{
//				Map mproc = (Map)procs.get(i);
//				List mprops = (List)mproc.get("properties");
//				Map props = convertProperties(mprops, fetcher);
//				this.createSpaceProcess((String)getProperty(mproc, "type"), props);
////				System.out.println("Create space process: "+getProperty(mproc, "type"));
//			}
//		}
//		
//		// Create initial space actions.
//		List actions = (List)si.getPropertyList("spaceactions");
//		if(actions!=null)
//		{
//			for(int i=0; i<actions.size(); i++)
//			{
//				Map action = (Map)actions.get(i);
//				List ps = (List)action.get("parameters");
//				Map params = null;
//				if(ps!=null)
//				{
//					params = new HashMap();
//					for(int j=0; j<ps.size(); j++)
//					{
//						Map param = (Map)ps.get(j);
//						IParsedExpression exp = (IParsedExpression)param.get("value");
//						params.put(param.get("name"), exp.getValue(fetcher));
//					}
//				}
//				
////				System.out.println("Performing initial space action: "+getProperty(action, "type"));
//				this.performSpaceAction((String)getProperty(action, "type"), params);
//			}
//		}
//		
////		Map themes = new HashMap();
//		List sourceviews = mspacetype.getPropertyList("dataviews");
//		if(sourceviews!=null)
//		{
//			for(int i=0; i<sourceviews.size(); i++)
//			{				
//				Map sourceview = (Map)sourceviews.get(i);
//				if(getProperty(sourceview, "objecttype")==null)
//				{
//					Map viewargs = new HashMap();
//					viewargs.put("sourceview", sourceview);
//					viewargs.put("space", this);
//					
//					IDataView	view	= (IDataView)((IObjectCreator)getProperty(sourceview, "creator")).createObject(viewargs);
//					this.addDataView((String)getProperty(sourceview, "name"), view);
//				}
//				else
//				{
//					this.addDataViewMapping((String)getProperty(sourceview, "objecttype"), sourceview);
//				}
//			}
//		}
//		
//		// Create the data providers.
//		List providers = mspacetype.getPropertyList("dataproviders");
//		List tmp = si.getPropertyList("dataproviders");
//		
//		if(providers==null && tmp!=null)
//			providers = tmp;
//		else if(providers!=null && tmp!=null)
//			providers.addAll(tmp);
//		
////		System.out.println("data providers: "+providers);
//		if(providers!=null)
//		{
//			for(int i=0; i<providers.size(); i++)
//			{
//				Map dcol = (Map)providers.get(i);
//
//				List sources = (List)dcol.get("source");
//				IObjectSource[] provs = new IObjectSource[sources.size()];
//				for(int j=0; j<sources.size(); j++)
//				{
//					Map source = (Map)sources.get(j);
//					String varname = source.get("name")!=null? (String)source.get("name"): "$object";
//					String objecttype = (String)source.get("objecttype");
//					boolean aggregate = source.get("aggregate")!=null? ((Boolean)source.get("aggregate")).booleanValue(): false;
//					IParsedExpression dataexp = (IParsedExpression)source.get("content");
//					IParsedExpression includeexp = (IParsedExpression)source.get("includecondition");
//					provs[j] = new SpaceObjectSource(varname, this, objecttype, aggregate, dataexp, includeexp);
//				}
//				
//				String tablename = (String)getProperty(dcol, "name");
//				List subdatas = (List)dcol.get("data");
//				String[] columnnames = new String[subdatas.size()];
//				IParsedExpression[] exps = new IParsedExpression[subdatas.size()];
//				for(int j=0; j<subdatas.size(); j++)
//				{
//					Map subdata = (Map)subdatas.get(j);
//					columnnames[j] = (String)getProperty(subdata, "name");
//					exps[j] = (IParsedExpression)getProperty(subdata, "content");
//				}
//				
//				ITableDataProvider tprov = new DefaultDataProvider(this, provs, tablename, columnnames, exps);
//				this.addDataProvider(tablename, tprov);
//			}
//		}
//		
//		// Create the data consumers.
//		List consumers = mspacetype.getPropertyList("dataconsumers");
//		tmp = si.getPropertyList("dataconsumers");
//		
//		if(consumers==null && tmp!=null)
//			consumers = tmp;
//		else if(consumers!=null && tmp!=null)
//			consumers.addAll(tmp);
//		
////		System.out.println("data consumers: "+consumers);
//		if(consumers!=null)
//		{
//			for(int i=0; i<consumers.size(); i++)
//			{
//				Map dcon = (Map)consumers.get(i);
//				String name = (String)getProperty(dcon, "name");
//				Class clazz = (Class)getProperty(dcon, "class");
//				ITableDataConsumer con = (ITableDataConsumer)clazz.newInstance();
//				setProperties(con, (List)dcon.get("properties"), fetcher);
//				con.setProperty("envspace", this);
//				this.addDataConsumer(name, con);
//			}
//		}
//		
//		List observers = si.getPropertyList("observers");
//		if(observers!=null)
//		{
//			for(int i=0; i<observers.size(); i++)
//			{				
//				Map observer = (Map)observers.get(i);
//				
//				final String title = getProperty(observer, "name")!=null? (String)getProperty(observer, "name"): "Default Observer";
//				final Boolean	killonexit	= (Boolean)getProperty(observer, "killonexit");
//				
//				List plugs = (List)observer.get("plugins");
//				final List plugins = plugs!=null ? new ArrayList() : null;
//				if(plugs!=null)
//				{
//					for(int j=0; j<plugs.size(); j++)
//					{
//						Map plug = (Map)plugs.get(j);
//						Class clazz = (Class)getProperty(plug, "clazz");
//						IPropertyObject po = (IPropertyObject)clazz.newInstance();
//						setProperties(po, (List)plug.get("properties"), fetcher);
//						plugins.add(po);
//					}
//				}
//				
//				final ObserverCenter oc = new ObserverCenter(title, AbstractEnvironmentSpace.this,
//					getContext().getApplicationType().getModelInfo().getClassLoader(), plugins,
//					killonexit!=null ? killonexit.booleanValue() : true);
//								
//				SServiceProvider.getServiceUpwards(context.getServiceContainer(), IComponentManagementService.class).addResultListener(new DefaultResultListener()
//				{
//					public void resultAvailable(final Object result)
//					{
//						((IComponentManagementService)result).addComponentListener(context.getComponentIdentifier(), new ICMSComponentListener()
//						{
//							public IFuture componentRemoved(IComponentDescription desc, Map results)
//							{
//								((IComponentManagementService)result).removeComponentListener(context.getComponentIdentifier(), this);
//								oc.dispose();
//								return IFuture.DONE;
//							}
//							
//							public IFuture componentChanged(IComponentDescription desc)
//							{
//								return IFuture.DONE;
//							}
//							
//							public IFuture componentAdded(IComponentDescription desc)
//							{
//								return IFuture.DONE;
//							}
//						});
//					}
//				});
//
//				List perspectives = mspacetype.getPropertyList("perspectives");
//				for(int j=0; j<perspectives.size(); j++)
//				{
//					Map sourcepers = (Map)perspectives.get(j);
//					Map args = new HashMap();
//					args.put("object", sourcepers);
//					args.put("fetcher", fetcher);
//					try
//					{
//						IPerspective persp	= (IPerspective)((IObjectCreator)getProperty(sourcepers, "creator")).createObject(args);
//						
//						List props = (List)sourcepers.get("properties");
//						setProperties(persp, props, fetcher);
//						
//						oc.addPerspective((String)getProperty(sourcepers, "name"), persp);
//					}
//					catch(Exception e)
//					{
//						System.out.println("Exception while creating perspective: "+sourcepers);
//						e.printStackTrace();
//					}
//				}
//			}
//		}
//		
//		// Create the environment executor.
//		Map mse = (Map)getProperty(mspacetype.getProperties(), "spaceexecutor");
//		IParsedExpression exp = (IParsedExpression)getProperty(mse, "expression");
//		ISpaceExecutor exe = null;
//		if(exp!=null)
//		{
//			exe = (ISpaceExecutor)exp.getValue(fetcher);	// Executor starts itself
//		}
//		else
//		{
//			exe = (ISpaceExecutor)((Class)getProperty(mse, "clazz")).newInstance();
//			List props = (List)mse.get("properties");
//			setProperties(exe, props, fetcher);
//		}
//		if(exe!=null)
//			exe.start();			
//	}
	
	//-------- methods --------

	/**
	 *  Add a space type.
	 *  @param typename The type name.
	 *  @param initproperties The MobjectType.
	 */
	public void addSpaceObjectType(String typename, IPropertyMetaDataSet mobjecttype)
	{
		synchronized(monitor)
		{
			objecttypes.put(typename, mobjecttype);
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
				
				process.setProperty(ISpaceProcess.ID, id);
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
	 *  @param initproperties The properties.
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
	// todo: refactor with generic future 
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
	 *  Add a result listener to an object task.
	 *  The listener result will be the task id.
	 *  If the task is already finished, the listener will be notified.
	 */
	public IFuture<Void> waitForTask(Object taskid, Object objectid)
	{
		final Future<Void> ret = new Future<Void>(); 
		SpaceObject so = (SpaceObject)getSpaceObject(objectid);
		so.addTaskListener(taskid, new IResultListener<Object>()
		{
			public void resultAvailable(Object result)
			{
				ret.setResultIfUndone(null);
			}
			
			public void exceptionOccurred(Exception exception)
			{
				ret.setExceptionIfUndone(exception);
			}
		});
		return ret;
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
	 * @return the object's ID
	 */
	public final ISpaceObject createSpaceObject(final String typename, Map properties, List tasks)
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
			while(spaceobjects.containsKey(id) || zombieobjects.containsKey(id));
			
			// Prepare properties (runtime props override type props).
			MObjectType mObjectType = (MObjectType)objecttypes.get(typename);
			if(properties!=null)
				properties = new HashMap(properties);
			properties = mergeProperties(mObjectType, properties);
			
			// Create the object.
			ret = new SpaceObject(id, mObjectType, properties, tasks, monitor, this);
			
			spaceobjects.put(id, ret);

			// Store in owner objects.
			if(properties!=null && properties.containsKey(ISpaceObject.PROPERTY_OWNER))
			{
				Object owner = properties.get(ISpaceObject.PROPERTY_OWNER);
				List ownerobjects = (List)spaceobjectsbyowner.get(owner);
				if(ownerobjects == null)
				{
					ownerobjects = new ArrayList();
					spaceobjectsbyowner.put(owner, ownerobjects);
				}
				ownerobjects.add(ret);
			}
		}
		
		initSpaceObject(ret);
		
		return ret;
	}
	
	/** 
	 * Creates a zombie object in this space.
	 * Zombies are not (yet) visible in the space and must be inited separately.
	 * @param type the object's type
	 * @param properties initial properties (may be null)
	 * @param tasks initial task list (may be null)
	 * @return the object's ID
	 */
	public ISpaceObject createSpaceObjectZombie(final String typename, Map properties, List tasks)
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
			while(spaceobjects.containsKey(id) || zombieobjects.containsKey(id));
			
			// Prepare properties (runtime props override type props).
			MObjectType mObjectType = (MObjectType)objecttypes.get(typename);
			properties = mergeProperties(mObjectType, properties);
			
			// Create the object.
			ret = new SpaceObject(id, mObjectType, properties, tasks, monitor, this);
			
			zombieobjects.put(id, ret);

			// Store in owner objects.
			if(properties!=null && properties.containsKey(ISpaceObject.PROPERTY_OWNER))
			{
				Object owner = properties.get(ISpaceObject.PROPERTY_OWNER);
				List ownerobjects = (List)spaceobjectsbyowner.get(owner);
				if(ownerobjects == null)
				{
					ownerobjects = new ArrayList();
					spaceobjectsbyowner.put(owner, ownerobjects);
				}
				ownerobjects.add(ret);
			}
		}
		
		return ret;
	}

	/** 
	 * Creates an object in this space.
	 * @param type the object's type
	 * @param initproperties initial properties (may be null)
	 * @param tasks initial task list (may be null)
	 * @param listeners initial listeners (may be null)
	 */
	public void	initSpaceObject(final ISpaceObject ret)
	{
		synchronized(monitor)
		{
			if(zombieobjects.containsKey(ret.getId()))
			{
				zombieobjects.remove(ret.getId());
				spaceobjects.put(ret.getId(), ret);
			}
			
			// Store in type objects.
			List typeobjects = (List)spaceobjectsbytype.get(ret.getType());
			if(typeobjects == null)
			{
				typeobjects = new ArrayList();
				spaceobjectsbytype.put(ret.getType(), typeobjects);
			}
			typeobjects.add(ret);
						
			// Create view(s) for the object if any.
			if(dataviewmappings!=null && dataviewmappings.get(ret.getType())!=null)
			{
				for(Iterator it=dataviewmappings.get(ret.getType()).iterator(); it.hasNext(); )
				{
					try
					{
						Map	sourceview	= (Map)it.next();
						Map viewargs = new HashMap();
						viewargs.put("sourceview", sourceview);
						viewargs.put("space", this);
						viewargs.put("object", ret);
						
						IDataView	view	= (IDataView)((IObjectCreator)MEnvSpaceType.getProperty(sourceview, "creator")).createObject(viewargs);
						addDataView((String)MEnvSpaceType.getProperty(sourceview, "name")+"_"+ret.getId(), view);
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
				AvatarMapping mapping = getAvatarMapping(componenttype, ret.getType());
				if(mapping!=null && mapping.isCreateComponent())
				{
//					final Object	fid	= id;
					
//					String	name	= null;
//					if(mapping.getComponentName()!=null)
//					{
////						SimpleValueFetcher fetch = new SimpleValueFetcher();
////						fetch.setValue("$space", this);
////						fetch.setValue("$object", ret);
//						name = (String)mapping.getComponentName().getValue(getFetcher());
//					}
					
					final String compotype = componenttype;
					
					getExternalAccess().getFileName(compotype).addResultListener(new DefaultResultListener()
					{
						public void resultAvailable(Object result)
						{
							final String filename = (String)result;
							
							exta.searchService( new ServiceQuery<>( IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)).addResultListener(new DefaultResultListener()
							{
								public void resultAvailable(Object result)
								{
									IComponentManagementService cms = (IComponentManagementService)result;
									// cannot be dummy cid because agent calls getAvatar(cid) in init and needs its avatar
									// the cid must be the final cid of the component hence it creates unique ids
///									IComponentIdentifier cid = cms.generateComponentIdentifier(SUtil.createUniqueId(compotype, 3), getExternalAccess().getComponentIdentifier().getName().replace("@", "."));
									// SUtil.createUniqueId(compotype, 3) might lead to conflicts due to race conditions. Use object id as it is really unique.
//									IComponentIdentifier cid = cms.generateComponentIdentifier(compotype+"_"+ret.getId(), getExternalAccess().getComponentIdentifier().getName().replace("@", "."));
									// todo: can fail?
									IComponentIdentifier cid = new BasicComponentIdentifier(compotype+"_"+ret.getId(), getExternalAccess().getComponentIdentifier());
//									IComponentIdentifier cid = new ComponentIdentifier("dummy@hummy");
									// Hack!!! Should have actual description and not just name and local type!?
									CMSComponentDescription desc = new CMSComponentDescription();
									desc.setName(cid);
									desc.setLocalType(compotype);
									setOwner(ret.getId(), desc);
//									System.out.println("env create: "+cid);
									IFuture	future	= cms.createComponent(cid.getLocalName(), filename,
										new CreationInfo(null, null, getExternalAccess().getComponentIdentifier(), false, getExternalAccess().getModel().getAllImports()), null);
									future.addResultListener(new IResultListener()
									{
										public void resultAvailable(Object result)
										{
//											System.out.println("env created: "+result);
//											setOwner(ret.getId(), (IComponentIdentifier)result);
										}
										
										public void exceptionOccurred(final Exception exception)
										{
											exta.scheduleStep(new IComponentStep<Void>()
											{
												public IFuture<Void> execute(IInternalAccess ia)
												{
													// Todo: Propagate exception to kill application!
													StringWriter	sw	= new StringWriter();
													exception.printStackTrace(new PrintWriter(sw));
													ia.getLogger().severe("Could not create component: "+compotype+"\n"+exception);
													return IFuture.DONE;
												}
											});
										}
									});
								}
							});
						}
					});
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
	 *  Add type properties to runtime properties.
	 *  Runtime properties have precedence if specified.
	 *  @param typeprops	The type 
	 *  @param properties	The runtime properties or null.
	 *  @return	The merged runtime properties.
	 */
	protected Map mergeProperties(IPropertyMetaDataSet mObjectType, Map properties)
	{
		if (mObjectType != null) {
			if (properties == null)
				properties = new HashMap();
			
			for (Iterator it = mObjectType.iterator(); it.hasNext(); ) {
				MObjectTypeProperty	property = (MObjectTypeProperty)it.next();
//				System.out.println(property.getName());
				if (!properties.containsKey(property.getName())) {
					IParsedExpression exp = (IParsedExpression)property.getValue();
					
					if (exp != null) {
						if (property.isDynamic())
							properties.put(property.getName(), exp);
						else
							properties.put(property.getName(), exp.getValue(fetcher));
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
//		System.out.println("destroy so: "+id);
		
		SpaceObject obj;
		IComponentDescription tmp = null;
		String ot = null;
		synchronized(monitor)
		{
			obj = (SpaceObject)spaceobjects.get(id);
			if(obj==null)
				throw new RuntimeException("No object found for id: "+id);
			ot = obj.getType();
			
			// Possibly kill component.
			tmp = (IComponentDescription)obj.getProperty(ISpaceObject.PROPERTY_OWNER);
		}	
//		String	componenttype = getExternalAccess().getLocalType(cid);
		final String objecttype = ot;	
		final IComponentDescription desc = tmp;
		
		if(desc!=null)
		{
			synchronized(monitor)
			{
				String componenttype = desc.getLocalType();
				AvatarMapping mapping = getAvatarMapping(componenttype, objecttype);
				if(mapping.isKillComponent())
				{
					getExternalAccess().searchService( new ServiceQuery<>( IComponentManagementService.class, RequiredServiceInfo.SCOPE_COMPONENT))
						.addResultListener(new DefaultResultListener()
					{
						public void resultAvailable(Object result)
						{
							((IComponentManagementService)result).destroyComponent(desc.getName());
						}
					});
				}
			}
		}

		synchronized(monitor)
		{
			// Mark obsolete actions
			ActionEntry[] actions = actionlist.getActionEntries();
			for(int i=0; i<actions.length; i++)
			{
				Object actorid = actions[i].parameters.get(ISpaceAction.ACTOR_ID);
				if(actorid!=null)
				{
					ISpaceObject so = getAvatar((IComponentDescription)actorid);
					if(so!=null)
					{
						Object avatarid = so.getId();
						if(avatarid==id)
						{
							actions[i].setInvalid(true);
						}
					}
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
			if(dataviewmappings!=null && dataviewmappings.get(objecttype)!=null)
			{
				for(Iterator it=dataviewmappings.get(objecttype).iterator(); it.hasNext(); )
				{
					Map	sourceview	= (Map)it.next();
					removeDataView((String)MEnvSpaceType.getProperty(sourceview, "name")+"_"+id);
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
			this.avatarmappings.add(mapping.getComponentType(), mapping);			
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
			this.avatarmappings.removeObject(mapping.getComponentType(), mapping);			
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
	public int performSpaceAction(String id, Map parameters, IResultListener listener)
	{
		synchronized(monitor)
		{
			return actionlist.scheduleComponentAction(getSpaceAction(id), parameters, listener);
		}
	}
	
	/**
	 * Cancel a queued space action.
	 */
	public void cancelSpaceAction(int id)
	{
		synchronized(monitor)
		{
			actionlist.cancelComponentAction(id);
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
	 *  @param cid The component that should receive the percept.
	 */
	public void createPercept(final String typename, final Object data, final IComponentDescription comp, final ISpaceObject avatar)
	{
		String	componenttype = comp.getLocalType();
		synchronized(monitor)
		{
//			if(!percepttypes.containsKey(typename))
//				throw new RuntimeException("Unknown percept type: "+typename);
			
//			System.out.println("New percept: "+typename+", "+data+", "+comp.getName());
			
//			String	componenttype = ia.getComponentType(cid);
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
			if(proc==null)
			{
				procs	= (List)perceptprocessors.get(null);
				if(procs!=null)
				{
					for(int i=0; i<procs.size() && proc==null; i++)
					{
						Object[] tmp = (Object[])procs.get(i);
						if(tmp[0]==null || ((Collection)tmp[0]).contains(typename))
							proc = (IPerceptProcessor)tmp[1];
					}
				}
			}
			
			if(proc!=null)
				perceptlist.schedulePercept(typename, data, comp, avatar, proc);
			else
				System.out.println("Warning: No processor for percept: "+typename+", "+data+", "+comp+", "+avatar);
		}
	}
	
	/**
	 *  Get the owner of an object.
	 *  @param id The id.
	 *  @return The owner.
	 */
	public IComponentDescription	getOwner(Object id)
	{
		synchronized(monitor)
		{
			ISpaceObject obj = getSpaceObject(id); 
			if(obj==null)
				throw new RuntimeException("Space object not found: "+id);
			return (IComponentDescription)obj.getProperty(ISpaceObject.PROPERTY_OWNER);
		}
	}
	
	/**
	 *  Set the owner of an object.
	 *  @param id The object id.
	 *  @param pos The object owner.
	 */
	public void setOwner(Object id, IComponentDescription owner)
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
	public ISpaceObject[] getAvatars(IComponentDescription owner)
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
	public IComponentDescription[] getComponents()
	{
		synchronized(monitor)
		{
			return (IComponentDescription[])spaceobjectsbyowner.keySet().toArray(new IComponentDescription[spaceobjectsbyowner.keySet().size()]);
		}
	}
	
	/**
	 *  Get the avatar object.
	 *  @return The avatar object. 
	 */
	public ISpaceObject getAvatar(IComponentDescription owner)
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
	 *  Get the avatar object.
	 *  @return The avatar object. 
	 */
	public ISpaceObject getAvatar(IComponentDescription owner, String fullname)
	{
		ISpaceObject	ret	= getAvatar(owner);

		// Create avatar on the fly if componentAdded not yet called.
		if(ret==null)
		{
			ret = createAvatar(owner, fullname, true);
		}
		
		return ret;
	}

	/**
	 *  Create an avatar.
	 */
	protected ISpaceObject createAvatar(IComponentDescription owner, String fullname, boolean zombie)
	{
		ISpaceObject	ret	= null;
		// Possibly add or create avatar(s) if any.
		if(initialavatars!=null && initialavatars.containsKey(owner))
		{
			Object[]	ia	= (Object[])initialavatars.get(owner);
			String	objecttype	=	(String)ia[0];
			Map	props	=	(Map)ia[1];
			if(props==null)
				props	= new HashMap();
			props.put(ISpaceObject.PROPERTY_OWNER, owner);
			ret	= zombie ? createSpaceObjectZombie(objecttype, props, null)
				: createSpaceObject(objecttype, props, null);
		}
		else
		{
			String	componenttype	= owner.getLocalType();
			if(componenttype==null && fullname!=null)
			{
				SubcomponentTypeInfo[] atypes = exta.getModel().getSubcomponentTypes();
				for(int i=0; i<atypes.length; i++)
				{
					String tmp = atypes[i].getFilename().replace('/', '.');
					if(tmp.indexOf(fullname)!=-1)
					{
						componenttype = atypes[i].getName();
						break;
					}
				}
			}
			if(componenttype!=null && avatarmappings.get(componenttype)!=null)
			{
				for(Iterator it=avatarmappings.get(componenttype).iterator(); it.hasNext(); )
				{
					AvatarMapping mapping = (AvatarMapping)it.next();
					// Only create avatar if it has none
					if(mapping.isCreateAvatar() && !spaceobjectsbyowner.containsKey(owner))
					{							
						Map	props	= new HashMap();
						props.put(ISpaceObject.PROPERTY_OWNER, owner);
						ret	= zombie ? createSpaceObjectZombie(mapping.getObjectType(), props, null)
							: createSpaceObject(mapping.getObjectType(), props, null);
					}
				}
			}
		}
		return ret;
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
			dataviewmappings.add(objecttype, view);
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
			perceptprocessors.add(componenttype, new Object[]{percepttypes, proc});
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
					perceptprocessors.removeObject(componenttype, tmp);
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
	public void componentAdded(final IComponentDescription owner)//, String type)
	{
//		getComponentType(cid).addResultListener(new DefaultResultListener()
//		{
//			public void resultAvailable(Object result)
//			{
//				String componenttype = (String)result;
//				String componenttype = owner.getLocalType();
				synchronized(monitor)
				{
					// Possibly add or create avatar(s) if any.
					List ownedobjs = (List)spaceobjectsbyowner.get(owner);
					if(ownedobjs==null)
					{
						createAvatar(owner, null, false);
					}
					else
					{
						// Init zombie avatars.
						for(Iterator it=ownedobjs.iterator(); it.hasNext(); )
						{
							ISpaceObject	obj	= (ISpaceObject)it.next();
							if(!spaceobjects.containsKey(obj.getId()))
							{
								initSpaceObject(obj);
							}
						}
					}
					
					if(perceptgenerators!=null)
					{
						for(Iterator it=perceptgenerators.keySet().iterator(); it.hasNext(); )
						{
							IPerceptGenerator gen = (IPerceptGenerator)perceptgenerators.get(it.next());
							gen.componentAdded(owner, AbstractEnvironmentSpace.this);
						}
					}
				}
//			}
//		});
	}
	
	/**
	 *  Called when an component was removed.
	 */
	public void componentRemoved(final IComponentDescription desc)
	{
//		System.out.println("comp removed: "+desc.getName());
		
//		getComponentType(cid)
//			.addResultListener(new DefaultResultListener()
//		{
//			public void resultAvailable(Object result)
//			{
//				String componenttype = (String)result;
				String componenttype = desc.getLocalType();
		
				synchronized(monitor)
				{
//							String	componenttype = application.getComponentType(cid);
					
					// Possibly kill avatars of that component.
					if(componenttype!=null && avatarmappings.get(componenttype)!=null)
					{
						ISpaceObject[] avatars = getAvatars(desc);
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
							gen.componentRemoved(desc, AbstractEnvironmentSpace.this);
						}
					}
				}
//			}
//		});
		
		// Remove the owned object too?
	}
		
	/**
	 * 
	 */
	protected IFuture getComponentType(final IComponentIdentifier cid)
	{
		final Future ret = new Future();
		getExternalAccess().searchService( new ServiceQuery<>( IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM))
			.addResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				IComponentManagementService cms = (IComponentManagementService)result;
				cms.getExternalAccess(cid).addResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object result)
					{
						IExternalAccess exta = (IExternalAccess)result;
						String componenttype = exta.getLocalType();
						ret.setResult(componenttype);
					}
				});
			}
		});
		return ret;
	}
	
	/**
	 *  Get the context.
	 *  @return The context.
	 */
	public IExternalAccess getExternalAccess()
	{
		return exta;
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
			this.fetcher = new SimpleValueFetcher()
			{
				public Object fetchValue(String name)
				{
					Object ret = null;
					if(getPropertyNames().contains(name))
					{
						ret = getProperty(name);
					}
					else
					{
						ret = super.fetchValue(name);
					}
					return ret;
				}
			};
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
	public Collection getProcesses()
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
		boolean	fire = false;
		synchronized(monitor)
		{
			MObjectType	props = (MObjectType)objecttypes.get(object.getType());
			
			if(props != null) 
			{
				MObjectTypeProperty prop = (MObjectTypeProperty)props.getProperty(property);
				fire = prop != null && prop.isEvent();
			}
		}
		if(fire)
			fireEnvironmentEvent(new EnvironmentEvent(EnvironmentEvent.OBJECT_PROPERTY_CHANGED, this, object, property, value));
	}
	
	/**
	 *  Synchronized counter class
	 */
	protected class AtomicCounter
	{
		long count_;
		
		public AtomicCounter()
		{
			count_ = 0;
		}
		
		public synchronized Long getNext()
		{
			return Long.valueOf(count_++);
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
		for(Iterator<AvatarMapping> it=avatarmappings.getCollection(componenttype).iterator(); mapping==null && it.hasNext(); )
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
	
	/**
	 *  Initialize the extension.
	 *  Called once, when the extension is created.
	 */
	public IFuture<Void>	init()
	{
//		space = (ISpace)getClazz().newInstance();

		final Future<Void>	ret	= new Future<Void>();
		
		initSpace().addResultListener(ia.getFeature(IExecutionFeature.class).createResultListener(new DelegationResultListener<Void>(ret)
		{
			public void customResultAvailable(Void result)
			{
//				System.out.println("inited space");
				
//				ia.addComponentListener(new IComponentListener()
//				{
//					IFilter filter = new IFilter()
//					{
//						public boolean filter(Object obj)
//						{
//							IComponentChangeEvent event = (IComponentChangeEvent)obj;
//							return event.getSourceCategory().equals(StatelessAbstractInterpreter.TYPE_COMPONENT);
//						}
//					};
//					public IFilter getFilter()
//					{
//						return filter;
//					}
//					
//					public IFuture eventOccured(IComponentChangeEvent cce)
//					{
//						if(cce.getEventType().equals(IComponentChangeEvent.EVENT_TYPE_CREATION))
//						{
////									System.out.println("add: "+cce.getDetails());
//							componentAdded((IComponentDescription)cce.getDetails());
//						}
//						else if(cce.getEventType().equals(IComponentChangeEvent.EVENT_TYPE_DISPOSAL))
//						{
////									System.out.println("rem: "+cce.getComponent());
//							componentRemoved((IComponentDescription)cce.getDetails());
//						}
//						return IFuture.DONE;
//					}
//				});
				
				final ISubscriptionIntermediateFuture<IMonitoringEvent> sub = ia.getFeature(IMonitoringComponentFeature.class).subscribeToEvents(new IFilter<IMonitoringEvent>()
				{
					public boolean filter(IMonitoringEvent obj)
					{
						return obj.getType().endsWith(IMonitoringEvent.SOURCE_CATEGORY_COMPONENT)
							|| obj.getType().equals(IMonitoringEvent.TYPE_SUBSCRIPTION_START);
					}
				}, false, PublishEventLevel.COARSE);
				
//				System.out.println("sub add: "+this);
				sub.addResultListener(new IIntermediateFutureCommandResultListener<IMonitoringEvent>()
				{
					public void resultAvailable(Collection<IMonitoringEvent> result)
					{
					}
					
					public void intermediateResultAvailable(IMonitoringEvent result)
					{
//						System.out.println("rec: "+result);
						if(result.getType().equals(IMonitoringEvent.TYPE_SUBSCRIPTION_START))
						{
//							System.out.println("space subscribed");
							ret.setResult(null);
						}
						else if(result.getType().startsWith(IMonitoringEvent.EVENT_TYPE_CREATION))
						{
//							System.out.println("add: "+result);
							componentAdded((IComponentDescription)result.getProperty("details"));	
						}
						else if(result.getType().startsWith(IMonitoringEvent.EVENT_TYPE_DISPOSAL))
						{
							componentRemoved((IComponentDescription)result.getProperty("details"));
						}
					}
					
				    public void finished()
				    {
//				    	System.out.println("fini");
				    }
				    
				    public void exceptionOccurred(Exception e)
				    {
				    	e.printStackTrace();
				    }
				    
				    public void commandAvailable(Object command)
				    {
				    	// ignore timer updates
				    }
				});
			}
		}));	
		return ret;
	}
	
	/**
	 *  Terminate the extension.
	 *  Called once, when the extension is terminated.
	 */
	public IFuture<Void> terminate()
	{
//		System.err.println("terminate space: "+exta.getComponentIdentifier());
		final Future<Void>	ret	= new Future<Void>();
		final IObserverCenter[]	ocs	= (IObserverCenter[])observercenters.toArray(new IObserverCenter[observercenters.size()]);
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				try
				{
					for(int i=0; i<ocs.length; i++)
					{
						ocs[i].dispose();
					}
					ret.setResult(null);
				}
				catch(Exception e)
				{
					ret.setException(e);
				}
			}
		});
//		System.err.println("terminate space finished: "+ret.isDone());
		return ret;
	}
	
	/**
	 *  Get the class loader.
	 */
	public ClassLoader getClassLoader()
	{
		return classloader;
	}
	
	/**
	 *  Get the object type.
	 */
	public MObjectType getSpaceObjectType(String type)
	{
		return (MObjectType)objecttypes.get(type);
	}

//	/**
//	 * @return the _areaSize
//	 */
//	//TODO: good?
//	public IVector3 getAreaSize3d()
//	{
//		return _areaSize;
//	}
}
