package jadex.bdiv3.model;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jadex.bdiv3.annotation.GoalAPLBuild;
import jadex.bdiv3.annotation.GoalResult;
import jadex.bdiv3.features.impl.BDIAgentFeature;
import jadex.bdiv3.runtime.impl.RGoal;
import jadex.bridge.IInternalAccess;
import jadex.commons.MethodInfo;
import jadex.commons.SReflect;


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

	// default values copied from Goal annotation for xml version
	
	/** The retry flag. */
	protected boolean retry = true; 
	
	/** The recur flag. */
	protected boolean recur = false;
	
	/** The retry delay. */
	protected long retrydelay = -1;
	
	/** The recur delay. */
	protected long recurdelay = -1;
	
	/** The procedual success flag. */
	protected boolean orsuccess = true;
	
	/** The unique. */
	protected boolean unique = false;
	
	/** The metagoal flag. */
	protected boolean metagoal = false;
	
	/** The deliberation. */
	protected MDeliberation deliberation;
	
	/** The trigger (other goals) if this goal is used as plan. */
//	protected List<String> triggergoals; // classname (pojo) or typename (xml)
//	protected List<MGoal> mtriggergoals;
	protected MTrigger trigger;
	
	/** The pojo result access (field or method). */
	protected Object pojoresultreadaccess;
	protected Object pojoresultwriteaccess;
	
	/** The goal conditions. */
	protected Map<String, List<MCondition>> conditions;
	
	/** The goal service parameter mappings. */
	protected Map<String, MethodInfo> spmappings;
	
	/** The goal service result mappings. */
	protected Map<String, MethodInfo> srmappings;
	
	/** The method info for building apl. */
	protected MethodInfo buildaplmethod;
	
	//-------- additional xml attributes --------
	
	/** The unique relevant attributes */
	protected List<MParameter> relevants;
	
	/** The unique parameter excludes. */
	protected Set<String> excludes;
	
	
	/**
	 *	Bean Constructor. 
	 */
	public MGoal()
	{
	}
	
	/**
	 *  Create a new goal model element.
	 */
	public MGoal(String name, String target, boolean posttoall, boolean randomselection, ExcludeMode excludemode,
		boolean retry, boolean recur, long retrydelay, long recurdelay, 
		boolean orsuccess, boolean unique, MDeliberation deliberation, List<MParameter> parameters,
		Map<String, MethodInfo> spmappings, Map<String, MethodInfo> srmappings, MTrigger trigger)//, List<String> triggergoals)
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
		this.trigger = trigger;
//		this.triggergoals = triggergoals;
		
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
	 *  The retry to set.
	 *  @param retry The retry to set
	 */
	public void setRetry(boolean retry)
	{
		this.retry = retry;
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
	 *  The retrydelay to set.
	 *  @param retrydelay The retrydelay to set
	 */
	public void setRetrydelay(long retrydelay)
	{
		this.retrydelay = retrydelay;
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
	 *  The recur to set.
	 *  @param recur The recur to set
	 */
	public void setRecur(boolean recur)
	{
		this.recur = recur;
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
	 *  The recurdelay to set.
	 *  @param recurdelay The recurdelay to set
	 */
	public void setRecurdelay(long recurdelay)
	{
		this.recurdelay = recurdelay;
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
	
	/**
	 *  The deliberation to set.
	 *  @param deliberation The deliberation to set
	 */
	public void setDeliberation(MDeliberation deliberation)
	{
		this.deliberation = deliberation;
	}

	// todo: do not write from instance level!

	/**
	 *  Get the declarative.
	 *  @return The declarative.
	 */
	public boolean isDeclarative()
	{
		return conditions!=null && (conditions.get(CONDITION_TARGET)!=null || conditions.get(CONDITION_MAINTAIN)!=null);
	}

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
		if(target==null)
			return null;
		
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
	public Object createPojoInstance(IInternalAccess ip, RGoal parent)
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
						Object[] pvals = BDIAgentFeature.getInjectionValues(sc.getParameterTypes(), null, this, null, null, parent, ip);
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

//	/**
//	 *  Get the triggergoals.
//	 *  @return The triggergoals.
//	 */
//	public List<String> getTriggerGoals()
//	{
//		return triggergoals;
//	}
//
//	/**
//	 *  Set the triggergoals.
//	 *  @param triggergoals The triggergoals to set.
//	 */
//	public void setTriggerGoals(List<String> triggergoals)
//	{
//		this.triggergoals = triggergoals;
//	}
//	
//	/**
//	 *  Add a trigger goal.
//	 */
//	public void addTriggerGoal(String typename)
//	{
//		if(triggergoals==null)
//			triggergoals = new ArrayList<String>();
//		triggergoals.add(typename);
//	}
	
	/**
	 *  Get the trigger.
	 *  @return The trigger.
	 */
	public MTrigger getTrigger()
	{
		return trigger;
	}

	/**
	 *  Set the trigger.
	 *  @param trigger The trigger to set.
	 */
	public void setTrigger(MTrigger trigger)
	{
		this.trigger = trigger;
	}
	
//	/**
//	 *  Get the triggergoals.
//	 *  @return The triggergoals.
//	 */
//	public List<MGoal> getTriggerMGoals(MCapability mcapa)
//	{
//		if(mtriggergoals==null && triggergoals!=null)
//		{
//			mtriggergoals = new ArrayList<MGoal>();
//			
//			for(String cl: triggergoals)
//			{
//				MGoal mgoal = mcapa.getGoal(cl);
//				if(mgoal==null)
//					throw new RuntimeException("Goal not for for pojo class: "+cl);
//				mtriggergoals.add(mgoal);
//			}
//		}
//		
//		return mtriggergoals;
//	}
	
	/**
	 *  Get the build apl method.
	 */
	public MethodInfo getBuildAPLMethod(ClassLoader cl)
	{
		if(buildaplmethod==null)
		{
			Class<?> tcl = getTargetClass(cl);
			Method[] ms = SReflect.getAllMethods(tcl);
			boolean done = false;
			for(int i=0; !done && i<ms.length; i++)
			{
				if(ms[i].isAnnotationPresent(GoalAPLBuild.class))
				{
					if((ms[i].getModifiers()&Modifier.PUBLIC)!=0)
					{
						buildaplmethod = new MethodInfo(ms[i]);
						done = true;
					}
				}
			}
			if(buildaplmethod==null)
				buildaplmethod = MBody.MI_NOTFOUND;
		}
		
		return buildaplmethod==MBody.MI_NOTFOUND? null: buildaplmethod;
	}

	/**
	 *  Get the metagoal.
	 *  @return The metagoal
	 */
	public boolean isMetagoal()
	{
		return metagoal;
	}

	/**
	 *  The metagoal to set.
	 *  @param metagoal The metagoal to set
	 */
	public void setMetagoal(boolean metagoal)
	{
		this.metagoal = metagoal;
	}
	
	/**
	 *  Get the parameters which are relevant for comparing goals.
	 */
	public List<MParameter>	getRelevantParameters()
	{
		if(relevants==null)
		{
			// Init relevant parameters.
			if(isUnique())
			{
				relevants = new ArrayList<MParameter>();
				//Set	includes	= getIncludes();
				Set<String>	excludes	= getExcludes();
				
				for(MParameter param: getParameters())
				{
					// Excluded parameters are not considered.
					if(!excludes.contains(param.getName()))
					{
						relevants.add(param);
					}
				}
			}
		}
		
		return relevants;
	}

	/**
	 *  Get the excludes.
	 *  Parameters not used in unique checks.
	 *  @return The excludes
	 */
	public Set<String> getExcludes()
	{
		return excludes;
	}

	/**
	 *  The excludes to set.
	 *  Parameters not used in unique checks.
	 *  @param excludes The excludes to set
	 */
	public void setExcludes(Set<String> excludes)
	{
		this.excludes = excludes;
	}

	/**
	 *  Add an excluded parameter
	 */
	public void addExclude(String paramname)
	{
		if(excludes==null)
			excludes = new HashSet<String>();
		excludes.add(paramname);
	}
	
//	/**
//	 *  Get the parameter sets which are relevant for comparing goals.
//	 */
//	public MParameter[]	getRelevantParameterSets()
//	{
//		return relevants;
//	}
}
