package jadex.tools.ontology;

import jadex.adapter.base.fipa.IComponentAction;


/**
 *  Java class for concept SendTraces of jadex.tools.tracer ontology.
 */
public class SendTraces implements IComponentAction
{
	//-------- attributes ----------

	/** Attribute for slot tracing. */
	protected Tracing	tracing;

	/** Attribute for slot subscription_time. */
	protected int		subscriptiontime;

	//-------- constructors --------

	/**
	 *  Default Constructor.
	 *  Create a new SendTraces.
	 */
	public SendTraces()
	{
	}

	/**
	 *  Init Constructor. <br>
	 *  Create a new SendTraces.<br>
	 *  Initializes the object with required attributes.
	 * @param subscriptiontime
	 * @param tracing
	 */
	public SendTraces(int subscriptiontime, Tracing tracing)
	{
		this();
		setSubscriptionTime(subscriptiontime);
		setTracing(tracing);
	}

	//-------- accessor methods --------

	/**
	 *  Get the tracing of this SendTraces.
	 * @return tracing
	 */
	public Tracing getTracing()
	{
		return this.tracing;
	}

	/**
	 *  Set the tracing of this SendTraces.
	 * @param tracing the value to be set
	 */
	public void setTracing(Tracing tracing)
	{
		this.tracing = tracing;
	}

	/**
	 *  Get the subscription_time of this SendTraces.
	 * @return subscription_time
	 */
	public int getSubscriptionTime()
	{
		return this.subscriptiontime;
	}

	/**
	 *  Set the subscription_time of this SendTraces.
	 * @param subscriptiontime the value to be set
	 */
	public void setSubscriptionTime(int subscriptiontime)
	{
		this.subscriptiontime = subscriptiontime;
	}

	//-------- additional methods --------

	/**
	 *  Get a string representation of this SendTraces.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "SendTraces(" + "subscriptiontime=" + getSubscriptionTime() + ", tracing=" + getTracing() + ")";
	}

}
