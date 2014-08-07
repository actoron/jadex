package jadex.bdiv3.model;

import jadex.bdiv3.annotation.GoalResult;
import jadex.bdiv3.runtime.impl.BDIAgentInterpreter;
import jadex.bdiv3.runtime.impl.RGoal;
import jadex.bridge.ClassInfo;
import jadex.commons.MethodInfo;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 *  Goal model.
 */
public class MGoal extends MClassBasedElement
{
	/** Goal creation condition name. */
	public static final String CONDITION_CREATION = "creation";
	
	/** Goal drop condition name. */
	public static final String CONDITION_DROP = "drop";
	
	/** Goal target condition name. */
	public static final String CONDITION_TARGET = "target";

	/** Goal maintain condition name. */
	public static final String CONDITION_MAINTAIN = "maintain";
	
	/** Goal context condition name. */
	public static final String CONDITION_CONTEXT = "context";

	/** Goal recur condition name. */
	public static final String CONDITION_RECUR = "recur";

	
	/** Never exclude plan candidates from apl. */
	public static final String EXCLUDE_NEVER = "never";

	/** Exclude tried plan candidates from apl. */ 
	public static final String EXCLUDE_WHEN_TRIED = "when_tried";
	
	/** Exclude failed plan candidates from apl. */
	public static final String EXCLUDE_WHEN_FAILED = "when_failed";

	/** Exclude succeeded plan candidates from apl. */
	public static final String EXCLUDE_WHEN_SUCCEEDED = "when_succeeded";

	
	/** The retry flag. */
	protected boolean retry;
	
	/** The recur flag. */
	protected boolean recur;
	
	/** The retry delay. */
	protected long retrydelay;
	
	/** The recur delay. */
	protected long recurdelay;
	
	/** The procedual success flag. */
	protected boolean orsuccess;
	
	/** The unique. */
	protected boolean unique;
	
	/** The deliberation. */
	protected MDeliberation deliberation;
	
	/** The trigger (other goals) if this goal is used as plan. */
	protected List<ClassInfo> triggergoals;
	protected List<MGoal> mtriggergoals;
	
	/** The pojo result access (field or method). */
	protected Object pojoresultreadaccess;
	protected Object pojoresultwriteaccess;
	
	/** The goal conditions. */
	protected Map<String, List<MCondition>> conditions;
	
	/** The parameters. */
	protected List<MParameter> parameters;
	
	/** The goal service parameter mappings. */
	protected Map<String, MethodInfo> spmappings;
	
	/** The goal service result mappings. */
	protected Map<String, MethodInfo> srmappings;
	
	/**
	 *  Create a new goal model element.
	 */
	public MGoal(String name, String target, boolean posttoall, boolean randomselection, String excludemode,
		boolean retry, boolean recur, long retrydelay, long recurdelay, 
		boolean orsuccess, boolean unique, MDeliberation deliberation, List<MParameter> parameters,
		Map<String, MethodInfo> spmappings, Map<String, MethodInfo> srmappings, List<ClassInfo> triggergoals)
	{
		super(name, target, posttoall, randomselection, excludemode);
		this.retry = retry;
		this.recur = recur;
		this.retrydelay = retrydelay;
		this.recurdelay = recurdelay;
		this.orsuccess = orsuccess;
		this.unique = unique;
		this.deliberation = deliberation;
		this.parameters = parameters;
		this.spmappings = spmappings;
		this.srmappings = srmappings;
		this.triggergoals = triggergoals;
		
//		System.out.println("create: "+target);
	}
	
	/**
	 *  Test if is retry.
	 *  @return True, if is retry.
	 */
	public boolean isRetry()
	{
		return retry;
	}
	
	/**
	 *  Get the retry delay.
	 *  @return The retry delay.
	 */
	public long getRetryDelay()
	{
		return retrydelay;
	}
	
	/**
	 *  Test if is recur.
	 *  @return True, if is recur.
	 */
	public boolean isRecur()
	{
		return recur;
	}
	
	/**
	 *  Get the retry delay.
	 *  @return The retry delay.
	 */
	public long getRecurDelay()
	{
		return recurdelay;
	}
	
	/**
	 *  Get the flag if is or success.
	 *  @return The or success flag..
	 */
	public boolean isOrSuccess()
	{
		return orsuccess;
	}

	/**
	 *  Set the or success.
	 *  @param orsuccess The or success flag..
	 */
	public void setOrSuccess(boolean orsuccess)
	{
		this.orsuccess = orsuccess;
	}

	/**
	 *  Get the unique.
	 *  @return The unique.
	 */
	public boolean isUnique()
	{
		return unique;
	}

	/**
	 *  Set the unique.
	 *  @param unique The unique to set.
	 */
	public void setUnique(boolean unique)
	{
		this.unique = unique;
	}

	/**
	 *  Get the deliberation.
	 *  @return The deliberation.
	 */
	public MDeliberation getDeliberation()
	{
		return deliberation;
	}
	
	// todo: do not write from instance level!
	
	/**
	 *  Get the declarative.
	 *  @return The declarative.
	 */
	public boolean isDeclarative()
	{
		return conditions!=null && (conditions.get(CONDITION_TARGET)!=null || conditions.get(CONDITION_TARGET)!=null);
//		return declarative;
	}

//	/**
//	 *  Set the declarative.
//	 *  @param declarative The declarative to set.
//	 */
//	public void setDeclarative(boolean declarative)
//	{
//		this.declarative = declarative;
//	}

//	/**
//	 *  Get the maintain.
//	 *  @return The maintain.
//	 */
//	public boolean isMaintain()
//	{
//		return maintain;
//	}
//
//	/**
//	 *  Set the maintain.
//	 *  @param maintain The maintain to set.
//	 */
//	public void setMaintain(boolean maintain)
//	{
//		this.maintain = maintain;
//	}
	
	/**
	 *  Get the pojo result access, i.e. the method or field
	 *  annotated with @GoalResult.
	 */
	public Object getPojoResultReadAccess(ClassLoader cl)
	{
		if(pojoresultreadaccess==null)
		{
			Class<?> pojocl = getTargetClass(cl);
			for(Method m: pojocl.getDeclaredMethods())
			{
				if(m.isAnnotationPresent(GoalResult.class))
				{
					if(void.class.equals(m.getReturnType()))
					{
						pojoresultwriteaccess = m;
					}
					else
					{
						pojoresultreadaccess = m;
						break;
					}
				}
			}
			if(pojoresultreadaccess==null)
			{
				for(Field f: pojocl.getDeclaredFields())
				{
					if(f.isAnnotationPresent(GoalResult.class))
					{
						pojoresultreadaccess = f;
						pojoresultwriteaccess = f;
						break;
					}
				}
				if(pojoresultreadaccess==null)
					pojoresultreadaccess = Boolean.FALSE;
			}
		}
		
		if(!Boolean.FALSE.equals(pojoresultreadaccess))
		{
			return pojoresultreadaccess;
		}
		else
		{
			return null;
		}
	}
	
	/**
	 *  Get the pojo result write access, i.e. the method or field
	 *  annotated with @GoalResult.
	 */
	public Object getPojoResultWriteAccess(ClassLoader cl)
	{
		if(pojoresultwriteaccess==null)
		{
			Class<?> pojocl = getTargetClass(cl);
			for(Method m: pojocl.getDeclaredMethods())
			{
				if(m.isAnnotationPresent(GoalResult.class))
				{
					if(void.class.equals(m.getReturnType()))
					{
						pojoresultwriteaccess = m;
						break;
					}
					else
					{
						pojoresultreadaccess = m;
					}
				}
			}
			if(pojoresultwriteaccess==null)
			{
				for(Field f: pojocl.getDeclaredFields())
				{
					if(f.isAnnotationPresent(GoalResult.class))
					{
						pojoresultreadaccess = f;
						pojoresultwriteaccess = f;
						break;
					}
				}
				if(pojoresultwriteaccess==null)
					pojoresultwriteaccess = Boolean.FALSE;
			}
		}
		
		if(!Boolean.FALSE.equals(pojoresultwriteaccess))
		{
			return pojoresultwriteaccess;
		}
		else
		{
			return null;
		}
	}
	
	/**
	 *  Create a pojo goal instance.
	 */
	public Object createPojoInstance(BDIAgentInterpreter ip, RGoal parent)
	{
		Object ret = null;
		ClassLoader cl = ip.getClassLoader();
		Class<?> pojocl = getTargetClass(cl);
		if(pojocl!=null)
		{
			try
			{
				Constructor<?> c = pojocl.getDeclaredConstructor(new Class[0]);
				ret = c.newInstance(new Object[0]);
			}
			catch(Exception e)
			{
				// Find constrcutor with smallest footprint
				Constructor<?> sc = null;
				Constructor<?>[] cs = pojocl.getDeclaredConstructors();
				for(Constructor<?> c: cs)
				{
					if(sc==null || c.getParameterTypes().length<sc.getParameterTypes().length)
					{
						sc = c;
					}
				}
				if(sc!=null)
				{
					try
					{
						Object[] pvals = ip.getInjectionValues(sc.getParameterTypes(), null, this, null, null, parent);
						ret = sc.newInstance(pvals);
					}
					catch(Exception ex)
					{
						throw new RuntimeException(ex);
					}
				}
			}
		}
		return ret;
	}
	
	/**
	 *  Add a condition to the goal.
	 */
	public void addCondition(String type, MCondition cond)
	{
		if(conditions==null)
			conditions = new HashMap<String, List<MCondition>>();
		List<MCondition> conds = conditions.get(type);
		if(conds==null)
		{
			conds = new ArrayList<MCondition>();
			conditions.put(type, conds);
		}
		conds.add(cond);
	}
	
	/**
	 *  Get the conditions of a type.
	 */
	public List<MCondition> getConditions(String type)
	{
		return conditions==null? null: conditions.get(type);
	}
	
	/**
	 *  Get all conditions.
	 */
	public Map<String, List<MCondition>> getConditions()
	{
		return conditions;
	}

	/**
	 *  Get the parameters.
	 *  @return The parameters.
	 */
	public List<MParameter> getParameters()
	{
		return parameters;
	}
	
	/**
	 *  Get a parameter by name.
	 */
	public MParameter getParameter(String name)
	{
		MParameter ret = null;
		if(parameters!=null && name!=null)
		{
			for(MParameter param: parameters)
			{
				if(param.getName().equals(name))
				{
					ret = param;
					break;
				}
			}
		}
		return ret;
	}
	
	/**
	 *  Test if goal has a parameter.
	 */
	public boolean hasParameter(String name)
	{
		return getParameter(name)!=null;
	}

	/**
	 *  Set the parameters.
	 *  @param parameters The parameters to set.
	 */
	public void setParameters(List<MParameter> parameters)
	{
		this.parameters = parameters;
	}
	
	/**
	 * 
	 */
	public void addServiceParameterMapping(String name, MethodInfo m)
	{
		if(spmappings==null)
			spmappings = new HashMap<String, MethodInfo>();
		spmappings.put(name, m);
	}

	/**
	 * 
	 */
	public MethodInfo getServiceParameterMapping(String name)
	{
		return spmappings==null? null: spmappings.get(name);
	}
	
	/**
	 * 
	 */
	public void addServiceResultMapping(String name, MethodInfo m)
	{
		if(spmappings==null)
			spmappings = new HashMap<String, MethodInfo>();
		spmappings.put(name, m);
	}
	
	/**
	 *  Get the service result mapping.
	 */
	public MethodInfo getServiceResultMapping(String name)
	{
		return srmappings==null? null: srmappings.get(name);
	}

	/**
	 *  Get the spmappings.
	 *  @return The spmappings.
	 */
	public Map<String, MethodInfo> getServiceParameterMappings()
	{
		return spmappings;
	}

	/**
	 *  Get the srmappings.
	 *  @return The srmappings.
	 */
	public Map<String, MethodInfo> getServiceResultMappings()
	{
		return srmappings;
	}

	/**
	 *  Get the triggergoals.
	 *  @return The triggergoals.
	 */
	public List<ClassInfo> getTriggerGoals()
	{
		return triggergoals;
	}

	/**
	 *  Set the triggergoals.
	 *  @param triggergoals The triggergoals to set.
	 */
	public void setTriggerGoals(List<ClassInfo> triggergoals)
	{
		this.triggergoals = triggergoals;
	}
	
	/**
	 *  Get the triggergoals.
	 *  @return The triggergoals.
	 */
	public List<MGoal> getTriggerMGoals(MCapability mcapa)
	{
		if(mtriggergoals==null && triggergoals!=null)
		{
			mtriggergoals = new ArrayList<MGoal>();
			
			for(ClassInfo cl: triggergoals)
			{
				MGoal mgoal = mcapa.getGoal(cl.getTypeName());
				if(mgoal==null)
				{
					throw new RuntimeException("Goal not for for pojo class: "+cl);
				}
				mtriggergoals.add(mgoal);
			}
		}
		
		return mtriggergoals;
	}
}
