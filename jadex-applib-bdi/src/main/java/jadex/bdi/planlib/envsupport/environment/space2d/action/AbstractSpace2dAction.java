package jadex.bdi.planlib.envsupport.environment.space2d.action;

import jadex.bdi.planlib.envsupport.environment.ISpaceAction;

public abstract class AbstractSpace2dAction implements ISpaceAction
{
	// Default Actions
	public static final DefaultAction SET_POSITION = new DefaultAction(0);
	
	// Default Action Parameters
	public static final DefaultParameter ACTOR_ID  = new DefaultParameter(0);
	public static final DefaultParameter OBJECT_ID = new DefaultParameter(1);
	public static final DefaultParameter POSITION_ID = new DefaultParameter(2);
	public static final DefaultParameter VELOCITY_ID = new DefaultParameter(3);
	
	private static class DefaultEnumeration
	{
		int id_;
		
		public DefaultEnumeration(int id)
		{
			id_ = id;
		}
		
		public int hashCode()
		{
			return id_;
		}
		
		public boolean equals(Object obj)
		{
			return ((obj.getClass().equals((this.getClass()))) &&
					(((DefaultParameter) obj).id_ == id_));
		}
	}
	
	private static class DefaultParameter extends DefaultEnumeration
	{
		public DefaultParameter(int id)
		{
			super(id);
		}
	}
	
	private static class DefaultAction extends DefaultEnumeration
	{
		public DefaultAction(int id)
		{
			super(id);
		}
	}
}
