package jadex.adapter.base.envsupport.environment.space2d.action;

import jadex.adapter.base.envsupport.environment.ISpaceAction;

public abstract class AbstractSpace2dAction implements ISpaceAction
{
	// Default Actions
	public static final Object SET_POSITION = SetPosition.class;
	
	// Default Action Parameters
	public static final DefaultParameter ACTOR_ID  = new DefaultParameter(-1);
	public static final DefaultParameter OBJECT_ID = new DefaultParameter(-2);
	public static final DefaultParameter POSITION_ID = new DefaultParameter(-3);
	public static final DefaultParameter VELOCITY_ID = new DefaultParameter(-4);
	
	private static class DefaultParameter
	{
		int id_;
		
		public DefaultParameter(int id)
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
}
