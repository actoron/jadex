package jadex.bpmn.editor.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.mxgraph.swing.mxGraphComponent;

import jadex.bpmn.editor.BpmnEditor;
import jadex.bpmn.editor.gui.Settings.BpmnClassFilter;
import jadex.bpmn.editor.gui.Settings.FileFilter;
import jadex.bpmn.model.IModelContainer;
import jadex.bpmn.model.MBpmnModel;
import jadex.bpmn.model.MTask;
import jadex.bpmn.model.io.IdGenerator;
import jadex.bpmn.task.info.TaskMetaInfo;
import jadex.bridge.ClassInfo;

/**
 * Container for the current model.
 *
 */
public class ModelContainer implements IModelContainer
{
	/** Edit mode for selection. */
	public static final String EDIT_MODE_SELECTION = "Select";
	
	/** Edit mode for stealth selection. */
	public static final String EDIT_MODE_STEALTH_SELECTION = "StealthSelect";
	
	/** Edit mode for adding control points. */
	public static final String EDIT_MODE_ADD_CONTROL_POINT = "AddControlPoint";
	
	/** Edit mode for message edges. */
	public static final String EDIT_MODE_MESSAGING_EDGE = "MessagingEdge";
	
	/** Edit mode for adding pools. */
	public static final String EDIT_MODE_POOL = "Pool";
	
	/** Edit mode for adding lanes. */
	public static final String EDIT_MODE_LANE = "Lane";
	
	/** Edit mode for adding tasks. */
	public static final String EDIT_MODE_TASK = MTask.TASK; //MBpmnModel.TASK;
	
	/** Edit mode for adding tasks. */
	public static final String EDIT_MODE_SUBPROCESS = MBpmnModel.SUBPROCESS;
	
	/** Edit mode for adding tasks. */
	public static final String EDIT_MODE_EXTERNAL_SUBPROCESS = MBpmnModel.SUBPROCESS + "External";
	
	/** Edit mode for adding tasks. */
	public static final String EDIT_MODE_EVENT_SUBPROCESS = MBpmnModel.SUBPROCESS + "Event";
	
	/** Edit mode for adding xor-gateways. */
	public static final String EDIT_MODE_GW_XOR = MBpmnModel.GATEWAY_DATABASED_EXCLUSIVE;
	
	/** Edit mode for adding and-gateways. */
	public static final String EDIT_MODE_GW_AND = MBpmnModel.GATEWAY_PARALLEL;
	
	/** Edit mode for adding or-gateways. */
	public static final String EDIT_MODE_GW_OR = MBpmnModel.GATEWAY_DATABASED_INCLUSIVE;
	
	/** Flag for throwing events */
	public static final String THROWING_EVENT = "Throwing";
	
	/** Flag for boundary events */
	public static final String BOUNDARY_EVENT = "Boundary";
	
	/** Edit mode for adding empty start events. */
	public static final String EDIT_MODE_EVENT_START_EMPTY = MBpmnModel.EVENT_START_EMPTY;
	
	/** Edit mode for adding empty intermediate events. */
	public static final String EDIT_MODE_EVENT_INTERMEDIATE_EMPTY = MBpmnModel.EVENT_INTERMEDIATE_EMPTY;
	
	/** Edit mode for adding error intermediate events. */
	public static final String EDIT_MODE_EVENT_INTERMEDIATE_ERROR = MBpmnModel.EVENT_INTERMEDIATE_ERROR;
	
	/** Edit mode for adding empty end events. */
	public static final String EDIT_MODE_EVENT_END_EMPTY = MBpmnModel.EVENT_END_EMPTY;
	
	/** Edit mode for adding message start events. */
	public static final String EDIT_MODE_EVENT_START_MESSAGE = MBpmnModel.EVENT_START_MESSAGE;
	
	/** Edit mode for adding message intermediate events. */
	public static final String EDIT_MODE_EVENT_INTERMEDIATE_MESSAGE = MBpmnModel.EVENT_INTERMEDIATE_MESSAGE;
	
	/** Edit mode for adding throwing message intermediate events. */
	public static final String EDIT_MODE_EVENT_INTERMEDIATE_MESSAGE_THROWING = MBpmnModel.EVENT_INTERMEDIATE_MESSAGE + THROWING_EVENT;
	
	/** Edit mode for adding message end events. */
	public static final String EDIT_MODE_EVENT_END_MESSAGE = MBpmnModel.EVENT_END_MESSAGE;
	
	/** Edit mode for adding throwing message end events. */
	public static final String EDIT_MODE_EVENT_END_MESSAGE_THROWING = MBpmnModel.EVENT_END_MESSAGE + THROWING_EVENT;
	
	/** Edit mode for adding timer start events. */
	public static final String EDIT_MODE_EVENT_START_TIMER = MBpmnModel.EVENT_START_TIMER;
	
	/** Edit mode for adding timer intermediate events. */
	public static final String EDIT_MODE_EVENT_INTERMEDIATE_TIMER = MBpmnModel.EVENT_INTERMEDIATE_TIMER;
	
	/** Edit mode for adding rule start events. */
	public static final String EDIT_MODE_EVENT_START_RULE = MBpmnModel.EVENT_START_RULE;
	
	/** Edit mode for adding rule intermediate events. */
	public static final String EDIT_MODE_EVENT_INTERMEDIATE_RULE = MBpmnModel.EVENT_INTERMEDIATE_RULE;
	
	/** Edit mode for adding signal start events. */
	public static final String EDIT_MODE_EVENT_START_SIGNAL = MBpmnModel.EVENT_START_SIGNAL;
	
	/** Edit mode for adding signal intermediate events. */
	public static final String EDIT_MODE_EVENT_INTERMEDIATE_SIGNAL = MBpmnModel.EVENT_INTERMEDIATE_SIGNAL;
	
	/** Edit mode for adding throwing signal intermediate events. */
	public static final String EDIT_MODE_EVENT_INTERMEDIATE_SIGNAL_THROWING = MBpmnModel.EVENT_INTERMEDIATE_SIGNAL + THROWING_EVENT;
	
	/** Edit mode for adding signal end events. */
	public static final String EDIT_MODE_EVENT_END_SIGNAL = MBpmnModel.EVENT_END_SIGNAL;
	
	/** Edit mode for adding throwing signal end events. */
	public static final String EDIT_MODE_EVENT_END_SIGNAL_THROWING = MBpmnModel.EVENT_END_SIGNAL + THROWING_EVENT;
	
	/** Edit mode for adding error end events. */
	public static final String EDIT_MODE_EVENT_END_ERROR_THROWING = MBpmnModel.EVENT_END_ERROR + THROWING_EVENT;
	
	/** Edit mode for adding compensation end events. */
	public static final String EDIT_MODE_EVENT_END_COMPENSATION_THROWING = MBpmnModel.EVENT_END_COMPENSATION + THROWING_EVENT;
	
	/** Edit mode for adding compensation end events. */
	public static final String EDIT_MODE_EVENT_END_CANCEL_THROWING = MBpmnModel.EVENT_END_CANCEL + THROWING_EVENT;
	
	/** Edit mode for adding terminate end events. */
	public static final String EDIT_MODE_EVENT_END_TERMINATE_THROWING = MBpmnModel.EVENT_END_TERMINATE + THROWING_EVENT;
	
	/** Edit mode for adding multiple start events. */
	public static final String EDIT_MODE_EVENT_START_MULTIPLE = MBpmnModel.EVENT_START_MULTIPLE;
	
	/** Edit mode for adding rule intermediate events. */
	public static final String EDIT_MODE_EVENT_INTERMEDIATE_MULTIPLE = MBpmnModel.EVENT_INTERMEDIATE_MULTIPLE;
	
	/** Edit mode for adding rule intermediate events. */
	public static final String EDIT_MODE_EVENT_INTERMEDIATE_MULTIPLE_THROWING = MBpmnModel.EVENT_INTERMEDIATE_MULTIPLE + THROWING_EVENT;
	
	/** Edit mode for adding error boundary events. */
	public static final String EDIT_MODE_EVENT_BOUNDARY_ERROR = MBpmnModel.EVENT_INTERMEDIATE_ERROR + BOUNDARY_EVENT;
	
	/** Edit mode for adding message boundary events. */
	public static final String EDIT_MODE_EVENT_BOUNDARY_MESSAGE = MBpmnModel.EVENT_INTERMEDIATE_MESSAGE + BOUNDARY_EVENT;
	
	/** Edit mode for adding timer boundary events. */
	public static final String EDIT_MODE_EVENT_BOUNDARY_TIMER = MBpmnModel.EVENT_INTERMEDIATE_TIMER + BOUNDARY_EVENT;
	
	/** Edit mode for adding compensation boundary events. */
	public static final String EDIT_MODE_EVENT_BOUNDARY_COMPENSATION = MBpmnModel.EVENT_INTERMEDIATE_COMPENSATION + BOUNDARY_EVENT;
	
	/** Edit mode for adding cancellation boundary events. */
	public static final String EDIT_MODE_EVENT_BOUNDARY_CANCEL = MBpmnModel.EVENT_INTERMEDIATE_CANCEL + BOUNDARY_EVENT;
	
	/** Edit mode for adding rule boundary events. */
	public static final String EDIT_MODE_EVENT_BOUNDARY_RULE = MBpmnModel.EVENT_INTERMEDIATE_RULE + BOUNDARY_EVENT;
	
	/** Edit mode for adding signal boundary events. */
	public static final String EDIT_MODE_EVENT_BOUNDARY_SIGNAL = MBpmnModel.EVENT_INTERMEDIATE_SIGNAL + BOUNDARY_EVENT;
	
	/** Modes for adding activities. */
	public static final Set<String> ACTIVITY_MODES = new HashSet<String>();
	static
	{
		ACTIVITY_MODES.add(EDIT_MODE_TASK);
		ACTIVITY_MODES.add(EDIT_MODE_GW_XOR);
		ACTIVITY_MODES.add(EDIT_MODE_GW_AND);
		ACTIVITY_MODES.add(EDIT_MODE_GW_OR);
		ACTIVITY_MODES.add(EDIT_MODE_SUBPROCESS);
		ACTIVITY_MODES.add(EDIT_MODE_EXTERNAL_SUBPROCESS);
	}
	
	/** Mapping activity edit modes to activity types */
	public static final Map<String, String> ACTIVITY_MODES_TO_TYPES = new HashMap<String, String>();
	static
	{
		ACTIVITY_MODES_TO_TYPES.put(EDIT_MODE_EVENT_INTERMEDIATE_MESSAGE_THROWING, MBpmnModel.EVENT_INTERMEDIATE_MESSAGE);
		ACTIVITY_MODES_TO_TYPES.put(EDIT_MODE_EVENT_END_MESSAGE_THROWING, MBpmnModel.EVENT_END_MESSAGE);
		ACTIVITY_MODES_TO_TYPES.put(EDIT_MODE_EVENT_INTERMEDIATE_SIGNAL_THROWING, MBpmnModel.EVENT_INTERMEDIATE_SIGNAL);
		ACTIVITY_MODES_TO_TYPES.put(EDIT_MODE_EVENT_END_SIGNAL_THROWING, MBpmnModel.EVENT_END_SIGNAL);
		ACTIVITY_MODES_TO_TYPES.put(EDIT_MODE_EVENT_END_ERROR_THROWING, MBpmnModel.EVENT_END_ERROR);
		ACTIVITY_MODES_TO_TYPES.put(EDIT_MODE_EVENT_END_COMPENSATION_THROWING, MBpmnModel.EVENT_END_COMPENSATION);
		ACTIVITY_MODES_TO_TYPES.put(EDIT_MODE_EVENT_END_CANCEL_THROWING, MBpmnModel.EVENT_END_CANCEL);
		ACTIVITY_MODES_TO_TYPES.put(EDIT_MODE_EVENT_END_TERMINATE_THROWING, MBpmnModel.EVENT_END_TERMINATE);
		ACTIVITY_MODES_TO_TYPES.put(EDIT_MODE_EVENT_INTERMEDIATE_MULTIPLE_THROWING, MBpmnModel.EVENT_INTERMEDIATE_MULTIPLE);
		ACTIVITY_MODES_TO_TYPES.put(EDIT_MODE_EVENT_BOUNDARY_ERROR, MBpmnModel.EVENT_INTERMEDIATE_ERROR);
		ACTIVITY_MODES_TO_TYPES.put(EDIT_MODE_EVENT_BOUNDARY_MESSAGE, MBpmnModel.EVENT_INTERMEDIATE_MESSAGE);
		ACTIVITY_MODES_TO_TYPES.put(EDIT_MODE_EVENT_BOUNDARY_TIMER, MBpmnModel.EVENT_INTERMEDIATE_TIMER);
		ACTIVITY_MODES_TO_TYPES.put(EDIT_MODE_EVENT_BOUNDARY_COMPENSATION, MBpmnModel.EVENT_INTERMEDIATE_COMPENSATION);
		ACTIVITY_MODES_TO_TYPES.put(EDIT_MODE_EVENT_BOUNDARY_CANCEL, MBpmnModel.EVENT_INTERMEDIATE_CANCEL);
		ACTIVITY_MODES_TO_TYPES.put(EDIT_MODE_EVENT_BOUNDARY_RULE, MBpmnModel.EVENT_INTERMEDIATE_RULE);
		ACTIVITY_MODES_TO_TYPES.put(EDIT_MODE_EVENT_BOUNDARY_SIGNAL, MBpmnModel.EVENT_INTERMEDIATE_SIGNAL);
		ACTIVITY_MODES_TO_TYPES.put(EDIT_MODE_EXTERNAL_SUBPROCESS, MBpmnModel.SUBPROCESS);
	}
	
	/** The global settings. */
	protected Settings settings;
	
	/** The model file. */
	protected File file;
	
	/** The graph component. */
	protected mxGraphComponent graphcomponent;
	
	/** The graph (visual model) */
	protected BpmnGraph graph;
	
	/** The current model. */
	protected MBpmnModel model;
	
	/** The dirty flag. */
	protected boolean dirty;
	
	/** The project root. */
	protected File projectroot;
	
	/** The class loader root. */
	protected File classloaderroot;
	
	/** The infos of tasks in the project. */
	protected Map<String, TaskMetaInfo> projecttaskmetainfos;
	
	/** The edit mode tool bar. */
	protected AbstractEditingToolbar editingtoolbar;
	
	/** The ID generator */
	protected IdGenerator idgen;
	
	/** The property panel container. */
	protected JPanel propertypanelcontainer;
	
	/** The change listeners. */
	protected List<ChangeListener> changelisteners;
	
	/** The task classes. */
	protected volatile List<ClassInfo> taskclasses;

	/** The interface classes. */
	protected volatile List<ClassInfo> interclasses;
	
	/** The exception classes. */
	protected volatile List<ClassInfo> exceptionclasses;
	
	/** The all classes. */
	protected volatile List<ClassInfo> allclasses;

	
	/**
	 *  Creates a new container.
	 */
	public ModelContainer(Settings settings)
	{
		this.settings = settings;
		this.idgen = new IdGenerator();
		//this.imageprovider = new ImageProvider();
		this.projecttaskmetainfos = new HashMap<String, TaskMetaInfo>();
		this.changelisteners = new ArrayList<ChangeListener>();
		
		// Scan additional model container dependent classes
		setupClassInfos();
	}
	
//	/**
//	 *  Scan for task classes.
//	 */
//	public List<ClassInfo> scanForTaskClassInfos()
//	{
//		List<ClassInfo> globs = new ArrayList<ClassInfo>(globalcache.getGlobalTaskClasses());
//		
//		if(classloaderroot!=null)
//		{
//			try
//			{
//				ClassLoader cl = getProjectClassLoader();
//				Class<?>[] locals = SReflect.scanForClasses(new URL[]{classloaderroot.toURI().toURL()}, cl, new IFilter<Object>()
//				{
//					public boolean filter(Object obj)
//					{
//						String	fn	= "";
//						if(obj instanceof File)
//						{
//							File	f	= (File)obj;
//							fn	= f.getName();
//						}
//						else if(obj instanceof JarEntry)
//						{
//							JarEntry	je	= (JarEntry)obj;
//							fn	= je.getName();
//						}
//						
//						return fn.indexOf("Task")!=-1;
//					}
//				}, new IFilter<Class<?>>()
//				{
//					public boolean filter(Class<?> obj)
//					{
//						boolean ret = false;
//						try
//						{
//							if(!obj.isInterface() && !Modifier.isAbstract(obj.getModifiers()))
//							{
//								ClassLoader cl = obj.getClassLoader();
//								Class<?> taskcl = Class.forName(ITask.class.getName(), true, cl);
//								ret = SReflect.isSupertype(taskcl, obj);
//							}
//						}
//						catch(Exception e)
//						{
//						}
//						return ret;
//					}
//				});
//				
//				for(Class<?> cla: locals)
//				{
//					globs.add(new ClassInfo(cla));
//				}				
//			}
//			catch(Exception e)
//			{
//				// nop
//			}
//		}
//		return globs;
//	}
	
	/**
	 *  Scan for task classes.
	 */
//	public List<ClassInfo>[] scanForClassInfos()
//	{
//		final List<ClassInfo> gtasks = new ArrayList<ClassInfo>(globalcache.getGlobalTaskClasses());
//		final List<ClassInfo> ginter = new ArrayList<ClassInfo>(globalcache.getGlobalInterfaces());
//		final List<ClassInfo> gex = new ArrayList<ClassInfo>(globalcache.getGlobalExceptions());
//		final List<ClassInfo> gall = new ArrayList<ClassInfo>(globalcache.getGlobalAllClasses());
//		
//		if(classloaderroot!=null)
//		{
//			try
//			{
//				ClassLoader cl = getProjectClassLoader();
//				Class<?>[] locals = SReflect.scanForClasses(new URL[]{classloaderroot.toURI().toURL()}, cl, new IFilter<Object>()
//				{
//					public boolean filter(Object obj)
//					{
//						String	fn	= "";
//						if(obj instanceof File)
//						{
//							File	f	= (File)obj;
//							fn	= f.getName();
//						}
//						else if(obj instanceof JarEntry)
//						{
//							JarEntry	je	= (JarEntry)obj;
//							fn	= je.getName();
//						}
////						
////						return fn.indexOf("Task")!=-1;
////						return fn.endsWith(".class");
//						return true;
//					}
//				}, new IFilter<Class<?>>()
//				{
//					public boolean filter(Class<?> obj)
//					{
//						boolean ret = false;
//						try
//						{
//							if(!obj.isInterface())
//							{
//								if(!Modifier.isAbstract(obj.getModifiers()) && Modifier.isPublic(obj.getModifiers()))
//								{
//									ClassInfo ci = new ClassInfo(obj.getName());
//									if(!gall.contains(ci))
//										gall.add(ci);
//									if(!gtasks.contains(ci))
//									{
//										ClassLoader cl = obj.getClassLoader();
//										Class<?> taskcl = Class.forName(ITask.class.getName(), true, cl);
//										ret = SReflect.isSupertype(taskcl, obj);
//										if(ret)
//										{
//											gtasks.add(new ClassInfo(obj.getName()));
//										}
//									}
//								}
//								
//								if (SReflect.isSupertype(Exception.class, obj))
//								{
//									ClassInfo ci = new ClassInfo(obj.getName());
//									gex.add(ci);
//								}
//							}
//							else 
//							{
//								// collect interfaces
//								ClassInfo ci = new ClassInfo(obj.getName());
//								if(!gall.contains(ci))
//									gall.add(new ClassInfo(obj.getName()));
//								if(!ginter.contains(ci))
//									ginter.add(new ClassInfo(obj.getName()));
//							}
//						}
//						catch(Exception e)
//						{
//						}
//						return ret;
//					}
//				});
//				
////				for(Class<?> cla: locals)
////				{
////					globs.add(new ClassInfo(cla));
////				}				
//			}
//			catch(Exception e)
//			{
//				// nop
//			}
//		}
//		return new List[]{gtasks, ginter, gex, gall};
//	}
	
	/**
	 *  Get the taskclasses.
	 *  @return The taskclasses.
	 */
	public List<ClassInfo> getTaskClasses()
	{
		return taskclasses;
	}
	
	/**
	 *  Get the interfaces.
	 *  @return The interfaces.
	 */
	public List<ClassInfo> getInterfaces()
	{
		return interclasses;
	}
	
	/**
	 *  Gets the exceptions.
	 *
	 *  @return The exceptions.
	 */
	public List<ClassInfo> getExceptions()
	{
		return exceptionclasses;
	}

	/**
	 *  Get the allclasses.
	 *  @return The allclasses.
	 */
	public List<ClassInfo> getAllClasses()
	{
		return allclasses;
	}

	/**
	 *  Returns the ID generator.
	 *  
	 *  @return The id generator.
	 */
	public IdGenerator getIdGenerator()
	{
		return idgen;
	}

	/**
	 *  Returns the current visual graph component.
	 *  @return The graph.
	 */
	public mxGraphComponent getGraphComponent()
	{
		return graphcomponent;
	}
	
	/**
	 *  Returns the current visual graph.
	 *  @return The graph.
	 */
	public BpmnGraph getGraph()
	{
		return graph;
	}
	
	/**
	 *  Returns the BPMN model.
	 *  @return BPMN model.
	 */
	public MBpmnModel getBpmnModel()
	{
		return model;
	}
	
	/**
	 *  Sets the current visual graph.
	 *  @param graph The graph.
	 */
	public void setGraph(BpmnGraph graph)
	{
		this.graph = graph;
	}
	
	/**
	 *  Sets the visual graph component.
	 *  @param component The component.
	 */
	public void setGraphComponent(mxGraphComponent component)
	{
		graphcomponent = component;
	}
	
	/**
	 *  Sets the GPMN model.
	 *  @param model The model.
	 */
	public void setBpmnModel(MBpmnModel model)
	{
		this.model = model;
//		generateClassLoader();
	}

	/** 
	 *  Sets the dirty model state.
	 *  
	 *  @param dirty The dirty state.
	 */
	public void setDirty(boolean dirty)
	{
		this.dirty = dirty;
		fireChangeEvent(new ChangeEvent(this));
	}
	
	/**
	 *  Tests if the state is dirty.
	 *  
	 *  @return True, if dirty.
	 */
	public boolean isDirty()
	{
		return dirty;
	}
	
	/**
	 *  Gets the project root.
	 *  
	 *  @return The project root.
	 */
	public File getProjectRoot()
	{
		return projectroot;
	}
	
	/**
	 *  Sets the project root.
	 *  
	 *  @param root The project root.
	 */
	public void setProjectRoot(File root)
	{
		projectroot = root;

		String rootpath = root.getAbsolutePath();
		if(rootpath.contains("src" + File.separator + "main" + File.separator + "java"))
		{
			String clpath = rootpath.replace("src" + File.separator + "main" + File.separator + "java",
											 "target" + File.separator + "classes");
			classloaderroot = new File(clpath);
			if (!classloaderroot.exists())
			{
				classloaderroot = null;
			}
		}
		if(classloaderroot == null && (rootpath.endsWith("src") || rootpath.endsWith("src" + File.separator)))
		{
			String clpath = rootpath.replace("src", "bin");
			classloaderroot = new File(clpath);
			if (!classloaderroot.exists())
			{
				classloaderroot = null;
			}
		}
		
		generateClassLoader();
	}
	
	/**
	 *  Returns the root for the project class loader.
	 *  @return The root of the project class loader.
	 */
	public File getProjectClassLoaderRoot()
	{
		return classloaderroot;
	}
	
	/**
	 *  Gets the global settings.
	 *  @return The settings
	 */
	public Settings getSettings()
	{
		return settings;
	}
	
	/**
	 *  Gets the model file.
	 *  
	 *  @return The model file.
	 */
	public File getFile()
	{
		return file;
	}
	
	/**
	 *  Sets the model file.
	 *  
	 *  @param file The model file.
	 */
	public void setFile(File file)
	{
		this.file = file;
		try
		{
			projecttaskmetainfos.clear();
			classloaderroot = null;
			projectroot = null;
			findProjectRoot();
		}
		catch (Exception e)
		{
		}
	}
	
	/**
	 *  Gets the project class loader.
	 *  
	 *  @return The project class loader.
	 */
	public ClassLoader getProjectClassLoader()
	{
		return model != null? model.getClassLoader() : settings!= null? settings.getLibraryClassLoader() != null? settings.getLibraryClassLoader() : Settings.class.getClassLoader() : Settings.class.getClassLoader();
	}
	
	/**
	 *  Get the project task meta infos.
	 *  @return The meta infos.
	 */
	public Map<String, TaskMetaInfo> getProjectTaskMetaInfos()
	{
		return projecttaskmetainfos;
	}
	
//	/**
//	 *  Returns all available Java classes in the project.
//	 *  
//	 *  @return Array of class names, null if unknown.
//	 */
//	public String[] getProjectClasses()
//	{
//		String[] ret = null;
//		if (projectroot != null)
//		{
//			List<String> list = searchForClasses(projectroot);
//			ret = list.toArray(new String[list.size()]);
//		}
//		return ret;
//	}
//	
//	/**
//	 *  Recursive search for class files.
//	 */
//	public List<String> searchForClasses(File dir)
//	{
//		List<String> ret = new ArrayList<String>();
//		
//		File[] files = dir.listFiles();
//		
//		for (int i = 0; i < files.length; ++i)
//		{
//			File file = files[i];
//			if (file.isDirectory() && !file.getName().equals(".") && !file.getName().equals(".."))
//			{
//				ret.addAll(searchForClasses(file));
//			}
//			else if (file.getAbsolutePath().endsWith(".java") ||
//					 file.getAbsolutePath().endsWith(".class"))
//			{
//				String classname = file.getAbsolutePath().substring(projectroot.getAbsolutePath().length());
//				while (classname.startsWith(File.separator))
//				{
//					classname = classname.substring(1);
//				}
//				classname = classname.substring(0, classname.lastIndexOf('.'));
//				classname = classname.replaceAll(File.separator, ".");
//				ret.add(classname);
//			}
//		}
//		
//		return ret;
//	}
	
	/**
	 *  Gets the edit mode.
	 *
	 *  @return The edit mode.
	 */
	public String getEditMode()
	{
		return editingtoolbar==null? "" : editingtoolbar.getEditMode();
	}

	/**
	 *  Sets the edit mode.
	 *
	 *  @param editmode The edit mode.
	 */
	public void setEditMode(String editmode)
	{
		if(editingtoolbar!=null)
			editingtoolbar.setEditMode(editmode);
	}
	
	/**
	 *  Gets the editing tool bar.
	 *
	 *  @return The editing tool bar.
	 */
	public AbstractEditingToolbar getEditingToolbar()
	{
		return editingtoolbar;
	}
	
	/**
	 *  Sets the editing tool bar.
	 *
	 *  @param toolbar The editing tool bar.
	 */
	public void setEditingToolbar(AbstractEditingToolbar toolbar)
	{
		this.editingtoolbar = toolbar;
	}
	
	/**
	 *  Sets the current property panel.
	 *  
	 *  @param panel The panel.
	 */
	public void setPropertyPanel(JComponent panel)
	{
		propertypanelcontainer.removeAll();
		propertypanelcontainer.add(panel, BorderLayout.CENTER);
	}
	
	/**
	 *  Gets the current property panel.
	 *  
	 *  @return The panel.
	 */
	public JComponent getPropertyPanel()
	{
		return (JComponent) getPropertypanelcontainer().getComponent(0);
	}

	/**
	 *  Gets the property panel container.
	 *
	 *  @return The property panel container.
	 */
	public JPanel getPropertypanelcontainer()
	{
		return propertypanelcontainer;
	}

	/**
	 *  Sets the property panel container.
	 *
	 *  @param propertypanelcontainer The property panel container.
	 */
	public void setPropertypanelcontainer(JPanel propertypanelcontainer)
	{
		this.propertypanelcontainer = propertypanelcontainer;
	}
	
	/**
	 *  Gets the image provider.
	 *
	 *  @return The image provider.
	 */
//	public ImageProvider getImageProvider()
//	{
//		return imageprovider;
//	}
	
	/**
	 *  Handles unsaved model deletions.
	 *  
	 *  @param parent Parent component.
	 *  @param modelcontainer The model container.
	 */
	public boolean checkUnsaved(Component parent)
	{
		boolean ret = true;
		if (isDirty())
		{
			int result = JOptionPane.showConfirmDialog(parent, "The model contains unsaved changes, proceed anyway?", BpmnEditor.APP_NAME, JOptionPane.YES_NO_OPTION);
	        switch(result)
	        {
	            case JOptionPane.NO_OPTION:
	            case JOptionPane.CLOSED_OPTION:
	                ret = false;
	            case JOptionPane.YES_OPTION:
	            default:
	        }
		}
		return ret;
	}

	/** Attempts to find the project root. */
	protected void findProjectRoot()
	{
		String pkg = model.getModelInfo().getPackage();
		if (pkg == null || pkg.length() == 0)
		{
			generateClassLoader();
			return;
		}
		String filepath = file.getAbsolutePath();
		int ind = filepath.lastIndexOf(File.separator);
		if (ind >= 0)
		{
			filepath = filepath.substring(0, ind);
			StringTokenizer tok = new StringTokenizer(pkg, ".");
			
			LinkedList<String> tokenstack = new LinkedList<String>();
			while (tok.hasMoreTokens())
			{
				tokenstack.add(tok.nextToken());
			}
			
			while (!tokenstack.isEmpty())
			{
				String pkgfrag = tokenstack.removeLast();
				ind = filepath.lastIndexOf(File.separator);
				if (ind >= 0 && filepath.length() > 1)
				{
					String pathfrag = filepath.substring(ind + 1);
					
					if (!pathfrag.equals(pkgfrag))
					{
						// Package/Directory mismatch
						generateClassLoader();
						return;
					}
					
					filepath = filepath.substring(0, ind);
				}
				else
				{
					generateClassLoader();
					return;
				}
			}
		}
		
		setProjectRoot(new File(filepath));
	}
	
	/**
	 *  Adds a change listener. Currently only reports dirty events.
	 *  
	 *  @param listener The listener.
	 */
	public void addChangeListener(ChangeListener listener)
	{
		changelisteners.add(listener);
	}
	
	public void removeChangeListener(ChangeListener listener)
	{
		changelisteners.remove(listener);
	}
	
	/**
	 *  Generates the class loader.
	 */
	public void generateClassLoader()
	{
		if(settings==null)
			return;
		
		ClassLoader parent = settings.getLibraryClassLoader();
		if(parent == null)
		{
			parent = Settings.class.getClassLoader();
		}
		
		if(classloaderroot != null)
		{
			URL[] urls;
			try
			{
				urls = new URL[]{classloaderroot.toURI().toURL()};
				model.setClassLoader(new URLClassLoader(urls, parent));
			}
			catch (MalformedURLException e)
			{
				Logger.getLogger(BpmnEditor.APP_NAME).log(Level.WARNING,
						"Identified project root, unable to generate classloader: " + e.getMessage());
				model.setClassLoader(parent);
			}
		}
		else
		{
			model.setClassLoader(parent);
		}
		setupClassInfos();
		
		if (propertypanelcontainer != null && propertypanelcontainer.getComponentCount() > 0)
		{
			if (getGraph().getSelectionCount() == 1)
			{
//				setPropertyPanel(SPropertyPanelFactory.createPanel(getGraph().getSelectionCell(), this));
				settings.getPropertyPanelFactory().createPanel(this, getGraph().getSelectionCell());
			}
			else
			{
//				setPropertyPanel(SPropertyPanelFactory.createPanel(null, this));
				settings.getPropertyPanelFactory().createPanel(this, null);
			}
		}
		
		getGraph().refresh();
	}
	
	/**
	 * 
	 */
	protected void setupClassInfos()
	{
		if(settings==null)
			return;
		
		taskclasses = settings.getGlobalTaskClasses();
		interclasses = settings.getGlobalInterfaces();
		exceptionclasses = settings.getGlobalExceptions();
		allclasses = settings.getGlobalAllClasses();
		
//		if (model != null &&
//			model.getClassLoader() != null &&
//			model.getClassLoader() != settings.getLibraryClassLoader() &&
//			model.getClassLoader() != Settings.class.getClassLoader())
//		{
//			long ts = System.currentTimeMillis();
//			Set<ClassInfo>[] infos = Settings.scanForClasses(model.getClassLoader(), false);
//			System.out.println("WEF " +(System.currentTimeMillis() - ts));
//			infos[0].addAll(settings.getGlobalTaskClasses());
//			infos[1].addAll(settings.getGlobalInterfaces());
//			infos[2].addAll(settings.getGlobalExceptions());
//			infos[3].addAll(settings.getGlobalAllClasses());
//			taskclasses = new ArrayList(infos[0]);
//			interclasses = new ArrayList(infos[1]);
//			exceptionclasses = new ArrayList(infos[2]);
//			allclasses = new ArrayList(infos[3]);
//		}
//		else
//		{
//			taskclasses = settings.getGlobalTaskClasses();
//			interclasses = settings.getGlobalInterfaces();
//			exceptionclasses = settings.getGlobalExceptions();
//			allclasses = settings.getGlobalAllClasses();
//		}
		
		if(model != null &&
			model.getClassLoader() != null &&
			model.getClassLoader() != settings.getLibraryClassLoader() &&
			model.getClassLoader() != Settings.class.getClassLoader() &&
			model.getClassLoader() instanceof URLClassLoader)
		{
			//final ClassLoader cl = model.getClassLoader();
			
			Set<ClassInfo>[] infos = new Set[]{new HashSet<ClassInfo>(), new HashSet<ClassInfo>(), new HashSet<ClassInfo>(), new HashSet<ClassInfo>() };
			URL[] urls = ((URLClassLoader)model.getClassLoader()).getURLs();
//			for (URL url : urls)
//			{
//				URLClassLoader cl = new URLClassLoader(new URL[] { url });
//				SReflect.scanForClasses(cl.getURLs(), cl, new Settings.FileFilter("$", false), new BpmnClassFilter(infos[0], infos[1], infos[2], infos[3], false));
//			}
			
			Settings.scanForClasses(settings, urls, new FileFilter("$", false), new BpmnClassFilter(infos[0], infos[1], infos[2], infos[3], false), false);
			
			infos[0].addAll(settings.getGlobalTaskClasses());
			infos[1].addAll(settings.getGlobalInterfaces());
			infos[2].addAll(settings.getGlobalExceptions());
			infos[3].addAll(settings.getGlobalAllClasses());
			taskclasses = new ArrayList<ClassInfo>(infos[0]);
			interclasses = new ArrayList<ClassInfo>(infos[1]);
			exceptionclasses = new ArrayList<ClassInfo>(infos[2]);
			allclasses = new ArrayList<ClassInfo>(infos[3]);
			
//			(new Thread(new Runnable()
//			{
//				public void run()
//				{
//					Set<ClassInfo>[] infos = new Set[] { new HashSet<ClassInfo>(), new HashSet<ClassInfo>(), new HashSet<ClassInfo>(), new HashSet<ClassInfo>() };
//					URL[] urls = ((URLClassLoader)cl).getURLs();
//					SReflect.scanForClasses(urls, cl, new Settings.FileFilter("$", false), new BpmnClassFilter(infos[0], infos[1], infos[2], infos[3], false));
//					
//					infos[0].addAll(settings.getGlobalTaskClasses());
//					infos[1].addAll(settings.getGlobalInterfaces());
//					infos[2].addAll(settings.getGlobalExceptions());
//					infos[3].addAll(settings.getGlobalAllClasses());
//					taskclasses = new ArrayList(infos[0]);
//					interclasses = new ArrayList(infos[1]);
//					exceptionclasses = new ArrayList(infos[2]);
//					allclasses = new ArrayList(infos[3]);
//				}
//			})).start();
		}
	}
	
	/**
	 *  Fires a change event.
	 *  
	 *  @param e The event.
	 */
	protected void fireChangeEvent(ChangeEvent e)
	{
		for (ChangeListener listener : changelisteners)
		{
			listener.stateChanged(e);
		}
	}
	
	/**
	 *  Get parameter names of a method.
	 */
	public List<String> getParameterNames(Method m)
	{
		return SHelper.getParameterNames(m);
	}
	
	/**
	 *  Get return value name.
	 */
	public String getReturnValueName(Method m)
	{
		return SHelper.getReturnValueName(m);
	}

}
