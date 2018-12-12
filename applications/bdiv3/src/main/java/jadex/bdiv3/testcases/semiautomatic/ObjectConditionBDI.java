package jadex.bdiv3.testcases.semiautomatic;

import jadex.bdiv3.BDIAgentFactory;
import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.bdiv3.runtime.IPlan;
import jadex.bridge.IInternalAccess;
import jadex.commons.beans.PropertyChangeListener;
import jadex.commons.beans.PropertyChangeSupport;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Configuration;
import jadex.micro.annotation.Configurations;

//import java.beans.PropertyChangeListener;
//import java.beans.PropertyChangeSupport;

@Agent(type=BDIAgentFactory.TYPE)
@Configurations({@Configuration(name="1"),@Configuration(name="2")})
public class ObjectConditionBDI
{
	@Agent
	protected IInternalAccess agent;
	
	@Belief 
	protected Bean mybean = bean;
	
	@AgentBody
	public void body()
	{
//		this.mybean = bean;
		
		if("1".equals(agent.getConfiguration()))
		{
			agent.getFeature(IBDIAgentFeature.class).adoptPlan("wait");
		}
		else if ("2".equals(agent.getConfiguration()))
		{
			agent.getFeature(IBDIAgentFeature.class).adoptPlan("notify");
		}
	}
	
	@Plan
	protected void wait(IPlan plan)
	{
		System.out.println("waiting for notification");
		plan.waitForFactChanged("mybean").get();
		System.out.println("received notification");
	}
	
	@Plan
	protected void notify(IPlan plan)
	{
		System.out.println("notify using bean");
		mybean.setAlive(true);
	}
	
	public static class Bean
	{
		protected PropertyChangeSupport pcs = new PropertyChangeSupport(this);
		
		protected boolean alive;

		public boolean isAlive()
		{
			return alive;
		}

		public void setAlive(boolean alive)
		{
			boolean oldalive = this.alive;
			this.alive = alive;
			pcs.firePropertyChange("alive", Boolean.valueOf(oldalive), Boolean.valueOf(alive));
		}
		
		//-------- property methods --------

	    public void addPropertyChangeListener(PropertyChangeListener listener)
		{
			pcs.addPropertyChangeListener(listener);
	    }

	    public void removePropertyChangeListener(PropertyChangeListener listener)
		{
			pcs.removePropertyChangeListener(listener);
	    }
	}
	
	protected static Bean bean = new Bean();
}
