package jadex.bpmn.editor.gui.propertypanels;

import jadex.bpmn.editor.gui.ModelContainer;
import jadex.bpmn.editor.model.visual.VActivity;
import jadex.bpmn.editor.model.visual.VDataEdge;
import jadex.bpmn.editor.model.visual.VElement;
import jadex.bpmn.editor.model.visual.VExternalSubProcess;
import jadex.bpmn.editor.model.visual.VInParameter;
import jadex.bpmn.editor.model.visual.VLane;
import jadex.bpmn.editor.model.visual.VOutParameter;
import jadex.bpmn.editor.model.visual.VPool;
import jadex.bpmn.editor.model.visual.VSequenceEdge;
import jadex.bpmn.editor.model.visual.VSubProcess;
import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MBpmnModel;
import jadex.bpmn.model.MSubProcess;
import jadex.bpmn.model.MTask;
import jadex.commons.IFilter;
import jadex.commons.Tuple2;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.swing.JComponent;

import com.mxgraph.model.mxICell;

/**
 *  Factory for generating appropriate property panels.
 *
 */
public class PropertyPanelFactory
{
	/** Filter keyword for default panels (no selection). */
	public static final String DEFAULT = "Default";
	
	/** Filter keyword for pools. */
	public static final String POOL = "Pool";
	
	/** Filter keyword for lanes. */
	public static final String LANE = "Lane";
	
	/** Filter keyword for tasks. */
	public static final String TASK = "Task";
	
	/** Filter keyword for internal subprocesses */
	public static final String INTERNAL_SUBPROCESS = "InternalSubProcess";
	
	/** Filter keyword for external subprocesses. */
	public static final String EXTERNAL_SUBPROCESS = "ExternalSubProcess";
	
	/** Filter keyword for event subprocesses */
	public static final String EVENT_SUBPROCESS = "EventSubProcess";
	
	/** Filter keyword for error events. */
	public static final String ERROR_EVENT = "ErrorEvent";
	
	/** Filter keyword for timer events. */
	public static final String TIMER_EVENT = "TimerEvent";
	
	/** Filter keyword for message events. */
	public static final String MESSAGE_EVENT = "MessageEvent";
	
	/** Filter keyword for rule events. */
	public static final String RULE_EVENT = "RuleEvent";
	
	/** Filter keyword for signal events. */
	public static final String SIGNAL_EVENT = "SignalEvent";
	
	/** Filter keyword for sequence edges. */
	public static final String SEQUENCE_EDGE = "SequenceEdge";
	
	/** Filter keyword for data edges. */
	public static final String DATA_EDGE = "DataEdge";
	
	
	protected static final Map<String, IFilter<Object>> FILTERS = new HashMap<String, IFilter<Object>>();
	static
	{
		FILTERS.put(DEFAULT, new IFilter<Object>()
		{
			public boolean filter(Object obj)
			{
				return obj == null;
			}
		});
		
		FILTERS.put(POOL, new IFilter<Object>()
		{
			public boolean filter(Object obj)
			{
				return obj instanceof VPool;
			}
		});
		
		FILTERS.put(LANE, new IFilter<Object>()
		{
			public boolean filter(Object obj)
			{
				return obj instanceof VLane;
			}
		});
		
		FILTERS.put(TASK, new IFilter<Object>()
		{
			public boolean filter(Object obj)
			{
				return (obj instanceof VActivity && ((VActivity) obj).getMActivity() instanceof MTask);
			}
		});
		
		FILTERS.put(INTERNAL_SUBPROCESS, new IFilter<Object>()
		{
			public boolean filter(Object obj)
			{
				boolean ret = false;
				if (obj instanceof VSubProcess)
				{
					VSubProcess sp = (VSubProcess) obj;
					if (((MSubProcess) sp.getMActivity()).getSubprocessType() != MSubProcess.SUBPROCESSTYPE_EVENT)
					{
						ret = true;
					}
				}
				return ret;
			}
		});
		
		FILTERS.put(EXTERNAL_SUBPROCESS, new IFilter<Object>()
		{
			public boolean filter(Object obj)
			{
				return obj instanceof VExternalSubProcess;
			}
		});
		
		FILTERS.put(EVENT_SUBPROCESS, new IFilter<Object>()
		{
			public boolean filter(Object obj)
			{
				boolean ret = false;
				if (obj instanceof VSubProcess)
				{
					VSubProcess sp = (VSubProcess) obj;
					if (MSubProcess.SUBPROCESSTYPE_EVENT.equals(((MSubProcess) sp.getMActivity()).getSubprocessType()))
					{
						ret = true;
					}
				}
				return ret;
			}
		});
		
		FILTERS.put(ERROR_EVENT, new IFilter<Object>()
		{
			public boolean filter(Object obj)
			{
				boolean ret = false;
				if (obj instanceof VActivity)
				{
					VElement velement = (VElement) obj;
					if (((MActivity) velement.getBpmnElement()).getActivityType() != null &&
						((MActivity) velement.getBpmnElement()).getActivityType().matches("Event.*Error"))
					{
						ret = true;
					}
				}
				return ret;
			}
		});
		
		FILTERS.put(TIMER_EVENT, new IFilter<Object>()
		{
			public boolean filter(Object obj)
			{
				boolean ret = false;
				if (obj instanceof VActivity)
				{
					VElement velement = (VElement) obj;
					if (((MActivity) velement.getBpmnElement()).getActivityType() != null &&
						((MActivity) velement.getBpmnElement()).getActivityType().contains("Timer"))
					{
						ret = true;
					}
				}
				return ret;
			}
		});
		
		FILTERS.put(MESSAGE_EVENT, new IFilter<Object>()
		{
			public boolean filter(Object obj)
			{
				boolean ret = false;
				if (obj instanceof VActivity)
				{
					VElement velement = (VElement) obj;
					if (MBpmnModel.EVENT_INTERMEDIATE_MESSAGE.equals(((MActivity) velement.getBpmnElement()).getActivityType()) ||
						MBpmnModel.EVENT_START_MESSAGE.equals(((MActivity) velement.getBpmnElement()).getActivityType()) ||
						MBpmnModel.EVENT_END_MESSAGE.equals(((MActivity) velement.getBpmnElement()).getActivityType()))
					{
						ret = true;
					}
				}
				return ret;
			}
		});
		
		FILTERS.put(RULE_EVENT, new IFilter<Object>()
		{
			public boolean filter(Object obj)
			{
				boolean ret = false;
				if (obj instanceof VActivity)
				{
					VElement velement = (VElement) obj;
					if (MBpmnModel.EVENT_INTERMEDIATE_RULE.equals(((MActivity) velement.getBpmnElement()).getActivityType()) || 
						MBpmnModel.EVENT_START_RULE.equals(((MActivity) velement.getBpmnElement()).getActivityType()))
					{
						ret = true;
					}
				}
				return ret;
			}
		});
		
		FILTERS.put(SIGNAL_EVENT, new IFilter<Object>()
		{
			public boolean filter(Object obj)
			{
				boolean ret = false;
				if (obj instanceof VActivity)
				{
					VElement velement = (VElement) obj;
					if (((MActivity)velement.getBpmnElement()).getActivityType().contains("Signal"))
					{
						ret = true;
					}
				}
				return ret;
			}
		});
		
		FILTERS.put(SEQUENCE_EDGE, new IFilter<Object>()
		{
			public boolean filter(Object obj)
			{
				return obj instanceof VSequenceEdge;
			}
		});
		
		FILTERS.put(DATA_EDGE, new IFilter<Object>()
		{
			public boolean filter(Object obj)
			{
				return obj instanceof VDataEdge;
			}
		});
	}
	
	/** An empty panel. */
	public BasePropertyPanel emptypanel = new BasePropertyPanel(null, null);
	
	/** List of panel constructors. */
	protected List<Tuple2<IFilter<Object>, Constructor<?>>> panelconstructors;
	
	/**
	 *  Creates the factory.
	 *  
	 *  @param config Panel configuration.
	 */
	public PropertyPanelFactory(Map<Object, Object> config)
	{
		setConfiguration(config);
	}
	
	/**
	 *  Sets the factory configuration.
	 *  
	 *  @param config Panel configuration.
	 */
	public void setConfiguration(Map<Object, Object> config)
	{
		panelconstructors = new ArrayList<Tuple2<IFilter<Object>,Constructor<?>>>();
		for (Map.Entry<Object, Object> entry : config.entrySet())
		{
			String filterkey = (String) entry.getKey();
			IFilter<Object> filter = FILTERS.get(filterkey);
			
			if (filter == null && filterkey != null && filterkey.endsWith("Handler"))
			{
				String rootkey = filterkey.substring(0, filterkey.length() - 7);
				final IFilter<Object> basefilter = FILTERS.get(rootkey);
				if (basefilter != null)
				{
					filter = new IFilter<Object>()
					{
						public boolean filter(Object obj)
						{
							boolean ret = false;
							if (obj instanceof VActivity)
							{
								ret = ((VActivity) obj).getMActivity().isEventHandler() && basefilter.filter(obj);
							}
							return ret;
						}
					};
				}
			}
			
			if (filter == null)
			{
				filter = new ActivityFilter(filterkey);
			}
			
			String classstring = (String) entry.getValue();
			
			Constructor<?> con = null;
			try
			{
				Class<?> panelclass = PropertyPanelFactory.class.getClassLoader().loadClass(classstring);
				con = panelclass.getConstructor(ModelContainer.class, Object.class);
				
			}
			catch (ClassNotFoundException e)
			{
//				throw new RuntimeException(e);
			}
			catch (NoSuchMethodException e)
			{
//				throw new RuntimeException(e);
			}
			
			if (con != null)
			{
				Tuple2<IFilter<Object>, Constructor<?>> tup = new Tuple2<IFilter<Object>, Constructor<?>>(filter, con);
				panelconstructors.add(tup);
				
				if (TASK.equals(filterkey)  || INTERNAL_SUBPROCESS.equals(filterkey) || EXTERNAL_SUBPROCESS.equals(filterkey))
				{
					final IFilter<Object> basefilter = filter;
					filter = new IFilter<Object>()
					{
						public boolean filter(Object obj)
						{
							boolean ret = false;
							if (obj instanceof VInParameter || obj instanceof VOutParameter)
							{
								ret = basefilter.filter(((mxICell) obj).getParent());
							}
							return ret;
						}
					};
					tup = new Tuple2<IFilter<Object>, Constructor<?>>(filter, con);
					panelconstructors.add(tup);
				}
			}
			else
			{
				Logger.getAnonymousLogger().warning("Property panel class not found, skipping: " + classstring);
			}
		}
	}
	
	/**
	 *  Creates a new property panel for the selected item.
	 *  
	 *  @param container The model container.
	 *  @return Property panel.
	 */
	public JComponent createPanel(ModelContainer container, Object selection)
	{
		JComponent ret = emptypanel;
		
		for (Tuple2<IFilter<Object>, Constructor<?>> tup : panelconstructors)
		{
			if (tup.getFirstEntity().filter(selection))
			{
				try
				{
					ret = (JComponent) tup.getSecondEntity().newInstance(container, selection);
					break;
				}
				catch (InvocationTargetException e)
				{
					Logger.getAnonymousLogger().warning("Constructor " + tup.getSecondEntity().toString() + " did not work, trying alternatives.");
				}
				catch (IllegalAccessException e)
				{
					Logger.getAnonymousLogger().warning("Constructor " + tup.getSecondEntity().toString() + " did not work, trying alternatives.");
				}
				catch (InstantiationException e)
				{
					Logger.getAnonymousLogger().warning("Constructor " + tup.getSecondEntity().toString() + " did not work, trying alternatives.");
				}
			}
		}
		
		return ret;
	}
	
	public static class ActivityFilter implements IFilter<Object>
	{
		protected String activitytype;
		
		public ActivityFilter(String activitytype)
		{
			this.activitytype = activitytype;
		}
		
		public boolean filter(Object obj)
		{
			boolean ret = false;
			if (obj instanceof VActivity)
			{
				VActivity vactivity = (VActivity) obj;
				MActivity mactivity = (MActivity) vactivity.getBpmnElement();
				if (activitytype.endsWith("Handler"))
				{
					String shortat = activitytype.substring(0, activitytype.length() - 7);
					ret = mactivity.getActivityType() != null &&
						  mactivity.getActivityType().equals(shortat) &&
						  mactivity.isEventHandler();
				}
				else
				{
					ret = mactivity.getActivityType() != null &&
						  mactivity.getActivityType().equals(activitytype) &&
						  !mactivity.isEventHandler();
				}
			}
			return ret;
		}
	}
}
