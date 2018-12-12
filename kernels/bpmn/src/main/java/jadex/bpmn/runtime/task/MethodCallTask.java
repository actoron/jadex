package jadex.bpmn.runtime.task;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

import jadex.bpmn.model.IModelContainer;
import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MParameter;
import jadex.bpmn.model.MProperty;
import jadex.bpmn.model.task.ITask;
import jadex.bpmn.model.task.ITaskContext;
import jadex.bpmn.model.task.ITaskPropertyGui;
import jadex.bpmn.model.task.annotation.Task;
import jadex.bpmn.model.task.annotation.TaskProperty;
import jadex.bpmn.model.task.annotation.TaskPropertyGui;
import jadex.bpmn.runtime.task.MethodCallTask.ServiceCallTaskGui;
import jadex.bpmn.task.info.ParameterMetaInfo;
import jadex.bridge.ClassInfo;
import jadex.bridge.IInternalAccess;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.modelinfo.UnparsedExpression;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.IInternalRequiredServicesFeature;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.commons.SReflect;
import jadex.commons.collection.IndexMap;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.gui.PropertiesPanel;
import jadex.javaparser.SJavaParser;

/**
 *  Call a method.
 *  Class and method name may be specified as parameters.
 *  Rebind parameter is also supported.
 *  All other in and inout parameters are interpreted as method arguments.
 *  One out or inout parameter may be specifed to receive the call result.
 *  Service name may alternatively supplied as name of lane and
 *  method name as name of activity. 
 */
@Task(description="The print task can be used for calling a component service.", properties={
	@TaskProperty(name="service", clazz=String.class, description="The required service name."),
	@TaskProperty(name="method", clazz=String.class, description="The required method name.")},
	gui=@TaskPropertyGui(ServiceCallTaskGui.class)
//	@TaskProperty(name="rebind", clazz=boolean.class, description="The rebind flag (forces a frsh search).")
)
public class MethodCallTask implements ITask
{
	//-------- constants --------
	
	/** Property for service name. */
	public static final String PROPERTY_SERVICE	= "service"; 
	
	/** Property for method name. */
	public static final String PROPERTY_METHOD	= "method"; 
	
	//-------- ITask interface --------
	

	/**
	 *  Execute the task.
	 *  @param context	The accessible values.
	 *  @param process	The process instance executing the task.
	 *  @return	To be notified, when the task has completed.
	 */
	public IFuture<Void> execute(final ITaskContext context, final IInternalAccess process)
	{
		final Future<Void>	ret	= new Future<Void>();
		String	service	= (String)context.getPropertyValue(PROPERTY_SERVICE);
		String	method	= (String)context.getPropertyValue(PROPERTY_METHOD);
		String	resultparam	= null;
		
		// Collect arguments and settings.
		final List<Object>	args = new ArrayList<Object>();
		final List<Class<?>> argtypes = new ArrayList<Class<?>>();
		IndexMap<String, MParameter>	mparams	= context.getActivity().getParameters();
		if(mparams!=null)
		{
			for(Iterator<MParameter> it=mparams.values().iterator(); it.hasNext(); )
			{
				MParameter	param	= (MParameter)it.next();
				if(PROPERTY_SERVICE.equals(param.getName()))
				{
					service	= (String)context.getParameterValue(param.getName());
				}
				else if(PROPERTY_METHOD.equals(param.getName()))
				{
					method	= (String)context.getParameterValue(param.getName());		
				}
				else if(MParameter.DIRECTION_IN.equals(param.getDirection()))
				{
					args.add(context.getParameterValue(param.getName()));
					argtypes.add(param.getClazz().getType(process.getClassLoader(), process.getModel().getAllImports()));
				}
				else if(MParameter.DIRECTION_INOUT.equals(param.getDirection()))
				{
					if(resultparam!=null)
						throw new RuntimeException("Only one 'out' parameter allowed for ServiceCallTask: "+context);
					
					resultparam	= param.getName();
					args.add(context.getParameterValue(param.getName()));
					argtypes.add(param.getClazz().getType(process.getClassLoader(), process.getModel().getAllImports()));
				}
				else if(MParameter.DIRECTION_OUT.equals(param.getDirection()))
				{
					if(resultparam!=null)
						throw new RuntimeException("Only one 'out' parameter allowed for ServiceCallTask: "+context);
					
					resultparam	= param.getName();
				}
			}
		}
		
		// Apply shortcuts, if necessary.
		if(service==null && context.getActivity().getLane()!=null)
		{
			service	= context.getActivity().getLane().getName();
		}
		if(method==null)
		{
			method	= context.getActivity().getName();
		}
		
		if(service==null)
		{
			throw new RuntimeException("No 'service' specified for ServiceCallTask: "+context);
		}
		if(method==null)
		{
			throw new RuntimeException("No 'method' specified for ServiceCallTask: "+context);
		}
		
		// Fetch service and call method.
		final String	fservice	= service;
		final String	fmethod	= method;
		final String	fresultparam	= resultparam;
		process.getFeature(IRequiredServicesFeature.class).getService(service)
			.addResultListener(new ExceptionDelegationResultListener<Object, Void>(ret)
		{
			public void customResultAvailable(Object result)
			{
				Class<?> servicetype = ((IInternalRequiredServicesFeature)process.getFeature(IRequiredServicesFeature.class)).getServiceInfo(fservice).getType().getType(process.getClassLoader(), process.getModel().getAllImports());
				//Method	m	= SReflect.getMethod(result.getClass(), fmethod, (Class[])argtypes.toArray(new Class[argtypes.size()]));
				
				Method[] methods = servicetype.getMethods();
				Method m = null;
				for (Method method : methods)
				{
					if (method.toString().equals(fmethod))
					{
						m = method;
						break;
					}
				}
				
				if(m==null)
				{
					throw new RuntimeException("MCT: "+ String.valueOf(process.getModel().getFilename()) + " Method "+fmethod+argtypes+" not found for service "+fservice+ " "  + fservice + ": "+context);
				}
				try
				{
					Object	val	= m.invoke(result, args.toArray());
					if(val instanceof IFuture)
					{
						((IFuture<Object>)val).addResultListener(new ExceptionDelegationResultListener<Object, Void>(ret)
						{
							public void customResultAvailable(Object result)
							{
								if(fresultparam!=null)
									context.setParameterValue(fresultparam, result);
								ret.setResult(null);
							}
						});
					}
					else
					{
						if(fresultparam!=null)
							context.setParameterValue(fresultparam, val);
						ret.setResult(null);
					}
				}
				catch(InvocationTargetException ite)
				{
					ret.setException((Exception)ite.getTargetException());
				}
				catch(Exception e)
				{
					ret.setException(e);					
				}
			}
		});
		
		return ret;
	}
	
//	/**
//	 *  Execute the task.
//	 *  @param context	The accessible values.
//	 *  @param process	The process instance executing the task.
//	 *  @return	To be notified, when the task has completed.
//	 */
//	public IFuture execute(final ITaskContext context, final IInternalAccess process)
//	{
//		final Future	ret	= new Future();
//		String	service	= null;
//		String	method	= null;
//		String	resultparam	= null;
//		boolean	rebind	= false;
//		
//		// Collect arguments and settings.
//		final List	args	= new ArrayList();
//		final List	argtypes	= new ArrayList();
//		IndexMap<String, MParameter>	mparams	= context.getActivity().getParameters();
//		for(Iterator it=mparams.values().iterator(); it.hasNext(); )
//		{
//			MParameter	param	= (MParameter)it.next();
//			if(PARAMETER_SERVICE.equals(param.getName()))
//			{
//				service	= (String)context.getParameterValue(param.getName());
//			}
//			else if(PARAMETER_METHOD.equals(param.getName()))
//			{
//				method	= (String)context.getParameterValue(param.getName());		
//			}
//			else if(PARAMETER_REBIND.equals(param.getName()))
//			{
//				Object	val	= context.getParameterValue(param.getName());
//				rebind	= val!=null ? ((Boolean)val).booleanValue() : false;
//			}
//			else if(MParameter.DIRECTION_IN.equals(param.getDirection()))
//			{
//				args.add(context.getParameterValue(param.getName()));
//				argtypes.add(param.getClazz().getType(process.getClassLoader(), process.getModel().getAllImports()));
//			}
//			else if(MParameter.DIRECTION_INOUT.equals(param.getDirection()))
//			{
//				if(resultparam!=null)
//					throw new RuntimeException("Only one 'out' parameter allowed for ServiceCallTask: "+context);
//				
//				resultparam	= param.getName();
//				args.add(context.getParameterValue(param.getName()));
//				argtypes.add(param.getClazz().getType(process.getClassLoader(), process.getModel().getAllImports()));
//			}
//			else if(MParameter.DIRECTION_OUT.equals(param.getDirection()))
//			{
//				if(resultparam!=null)
//					throw new RuntimeException("Only one 'out' parameter allowed for ServiceCallTask: "+context);
//				
//				resultparam	= param.getName();
//			}
//		}
//		
//		// Apply shortcuts, if necessary.
//		if(service==null && context.getActivity().getLane()!=null)
//		{
//			service	= context.getActivity().getLane().getName();
//		}
//		if(method==null)
//		{
//			method	= context.getActivity().getName();
//		}
//		
//		if(service==null)
//		{
//			throw new RuntimeException("No 'service' specified for ServiceCallTask: "+context);
//		}
//		if(method==null)
//		{
//			throw new RuntimeException("No 'method' specified for ServiceCallTask: "+context);
//		}
//		
//		// Fetch service and call method.
//		final String	fservice	= service;
//		final String	fmethod	= method;
//		final String	fresultparam	= resultparam;
//		process.getServiceContainer().getService(service, rebind)
//			.addResultListener(new DelegationResultListener(ret)
//		{
//			public void customResultAvailable(Object result)
//			{
//				Method	m	= SReflect.getMethod(result.getClass(), fmethod, (Class[])argtypes.toArray(new Class[argtypes.size()]));
//				if(m==null)
//				{
//					throw new RuntimeException("Method "+fmethod+argtypes+" not found for service "+fservice+": "+context);
//				}
//				try
//				{
//					Object	val	= m.invoke(result, args.toArray());
//					if(val instanceof IFuture)
//					{
//						((IFuture)val).addResultListener(new DelegationResultListener(ret)
//						{
//							public void customResultAvailable(Object result)
//							{
//								if(fresultparam!=null)
//									context.setParameterValue(fresultparam, result);
//								ret.setResult(null);
//							}
//						});
//					}
//					else
//					{
//						if(fresultparam!=null)
//							context.setParameterValue(fresultparam, val);
//						ret.setResult(null);
//					}
//				}
//				catch(InvocationTargetException ite)
//				{
//					ret.setException((Exception)ite.getTargetException());
//				}
//				catch(Exception e)
//				{
//					ret.setException(e);					
//				}
//			}
//		});
//		
//		return ret;
//	}
	
	/**
	 *  Compensate in case the task is canceled.
	 *  @return	To be notified, when the compensation has completed.
	 */
	public IFuture<Void> cancel(IInternalAccess instance)
	{
		// Todo: how to compensate service call!?
		return IFuture.DONE;
	}
	
	/**
	 *  Get the extra parameters that depend on the property settings of the task.
	 */
	public static List<ParameterMetaInfo> getExtraParameters(Map<String, MProperty> params, IModelContainer modelcontainer, ClassLoader cl)
	{
		List<ParameterMetaInfo> ret = new ArrayList<ParameterMetaInfo>();
		
		IModelInfo mi = modelcontainer.getBpmnModel().getModelInfo();
		try
		{
			MProperty msparam = params.get(PROPERTY_SERVICE);
			MProperty mmparam = params.get(PROPERTY_METHOD);
			
			if(msparam!=null && mmparam!=null)
			{
				String reqname = (String)SJavaParser.evaluateExpression(msparam.getInitialValue().getValue(), mi.getAllImports(), null, cl);
				String methodname = (String)SJavaParser.evaluateExpression(mmparam.getInitialValue().getValue(), mi.getAllImports(), null, cl);
				
				if(reqname!=null && methodname!=null)
				{
					RequiredServiceInfo reqser = mi.getService(reqname);
					if(reqser!=null)
					{
						Class<?> type = reqser.getType().getType(cl==null? MethodCallTask.class.getClassLoader(): cl, mi.getAllImports());
						
						if(type!=null)
						{
							Method[] ms = type.getMethods();
							// todo check parameter types?
							for(Method m: ms)
							{
								if(m.toString().equals(methodname))
								{
									List<String> names = modelcontainer.getParameterNames(m);
									String retname = modelcontainer.getReturnValueName(m);
									Type[] ptypes = m.getGenericParameterTypes();
									Type pret = m.getGenericReturnType();
									
									for(int j=0; j<ptypes.length; j++)
									{
										ret.add(new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, new ClassInfo(ptypes[j]), names!=null && names.size()>0? names.get(j): "param"+j, null, null));
									}
									if(!pret.equals(Void.class) && !pret.equals(void.class))
									{
										ret.add(new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_OUT, new ClassInfo(pret), retname==null? "return": retname, null, null));
									}
								}
							}
						}
					}
				}
			}	
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	
		return ret;
	}
	
	/**
	 * 
	 */
	public static class ServiceCallTaskGui implements ITaskPropertyGui
	{
		/** The panel. */
		protected JPanel panel;
		
		/** The model. */
		protected IModelInfo model;
		
		/** The task. */
		protected MActivity task;
		
		/** The classloader. */
		protected ClassLoader cl;
		
		/** The combo box for the service name. */
		protected JComboBox cbsername;
		
		/** The combo box for the method name. */
		protected JComboBox cbmethodname;
		
		/**
		 *  Once called to init the component.
		 */
		public void init(final IModelContainer container, final MActivity task, final ClassLoader cl)
		{
			this.model = container.getBpmnModel().getModelInfo();
			this.task = task;
			this.cl = cl;
			PropertiesPanel pp = new PropertiesPanel();
			
			cbsername = pp.createComboBox("Required service name:", null);
			cbmethodname = pp.createComboBox("Method name", null);
			cbmethodname.setRenderer(new BasicComboBoxRenderer()
			{
				public Component getListCellRendererComponent(JList list, Object value,
					int index, boolean isSelected, boolean cellHasFocus)
				{
					Method method = (Method)value;
					String txt = null;
					if(method!=null)
						txt = SReflect.getMethodSignature(method);
					return super.getListCellRendererComponent(list, txt, index, isSelected, cellHasFocus);
				}
			});
			
			cbsername.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					String reqname = (String)cbsername.getSelectedItem();
					
					MProperty mprop = task.getProperties().get(PROPERTY_SERVICE);
					UnparsedExpression uexp = new UnparsedExpression(null, 
						String.class, "\""+reqname+"\"", null);
					mprop.setInitialValue(uexp);
					
					if(reqname!=null && model.getService(reqname)!=null)
					{
						RequiredServiceInfo reqser = model.getService(reqname);
						Class<?> type = reqser.getType().getType(cl==null? MethodCallTask.class.getClassLoader(): cl, model.getAllImports());
						
						if(type!=null)
						{
							ActionListener[] als = cbmethodname.getActionListeners();
							for(ActionListener al: als)
								cbmethodname.removeActionListener(al);
							
							DefaultComboBoxModel mo = ((DefaultComboBoxModel)cbmethodname.getModel());
							mo.removeAllElements();
							Method[] ms = type.getMethods();
							for(Method m: ms)
							{
								mo.addElement(m);
							}
							
							for(ActionListener al: als)
								cbmethodname.addActionListener(al);
						}
					}
				}
			});
			
			cbmethodname.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					Method method = (Method)cbmethodname.getSelectedItem();
					
//					System.out.println("setting: "+method);
					
					MProperty mprop = task.getProperties().get(PROPERTY_METHOD);
					UnparsedExpression uexp = new UnparsedExpression(null, 
						String.class, method!=null? "\""+method.toString()+"\"": "null", null);
					mprop.setInitialValue(uexp);
				}
			});
			
			refresh();
			
			panel = pp;
		}
		
		/**
		 * 
		 */
		protected void refresh()
		{
			DefaultComboBoxModel mo = ((DefaultComboBoxModel)cbsername.getModel());
			mo.removeAllElements();
			
			RequiredServiceInfo[] reqs = model.getServices();
			
			ActionListener[] als = cbsername.getActionListeners();
			for(ActionListener al: als)
				cbsername.removeActionListener(al);
			if(reqs!=null)
			{
				for(int i=0; i<reqs.length; i++)
				{
					mo.addElement(reqs[i].getName());
				}
			}
			for(ActionListener al: als)
				cbsername.addActionListener(al);
			
			MProperty mprop = task.getProperties().get(PROPERTY_SERVICE);
			if(mprop.getInitialValue()!=null)
			{
				String sername = (String)SJavaParser.parseExpression(mprop.getInitialValue(), model.getAllImports(), cl).getValue(null);
				cbsername.setSelectedItem(sername);
//				System.out.println("sel item: "+sername);
			
				mprop = task.getProperties().get(PROPERTY_METHOD);
				if(mprop.getInitialValue()!=null)
				{
					String methodname = (String)SJavaParser.parseExpression(mprop.getInitialValue(), model.getAllImports(), cl).getValue(null);
//					System.out.println(task.getName()+" "+mprop.getInitialValueString());
					
					RequiredServiceInfo reqser = model.getService(sername);
					if(reqser!=null)
					{
						Class<?> type = reqser.getType().getType(cl==null? MethodCallTask.class.getClassLoader(): cl, model.getAllImports());
						
						if(type!=null)
						{
							Method[] ms = type.getMethods();
							for(Method m: ms)
							{
								if(m.toString().equals(methodname))
								{
									cbmethodname.setSelectedItem(m);
	//								System.out.println("sel item2: "+methodname);
								}
							}
						}
					}
				}
			}
		}
		
		/**
		 *  Informs the panel that it should stop all its computation.
		 */
		public void shutdown()
		{
		}
		
		/**
		 *  The component to be shown in the gui.
		 *  @return	The component to be displayed.
		 */
		public JComponent getComponent()
		{
			return panel;
		}
	}
}
