package jadex.bdi.examples.booktrading.common;

import java.util.Date;

import jadex.bridge.service.annotation.Reference;
import jadex.bridge.service.types.clock.IClockService;
import jadex.bridge.service.types.clock.ITimedObject;
import jadex.commons.SimplePropertyChangeSupport;
import jadex.commons.beans.PropertyChangeListener;

/**
 * The order for purchasing or selling books.
 */
@Reference(local=true)
public class Order
{
	//-------- constants --------

	/** The state open. */
	public static final String OPEN = "open";

	/** The state done. */
	public static final String DONE = "done";

	/** The state failed. */
	public static final String FAILED = "failed";

	//-------- attributes --------

	/** The book title. */
	protected String title;

	/** The deadline. */
	protected Date deadline;

	/** The limit price. */
	protected int limit;

	/** The startprice. */
	protected int startprice;

	/** The starttime. */
	protected long starttime;

	/** The execution price. */
	protected Integer exeprice;

	/** The execution date. */
	protected Date exedate;

	/** The flag indicating if it is a buy (or sell) order. */
	protected boolean buyorder;
	
	/** The state. */
	protected String state;

	/** The clock. */
//	public IExternalAccess clock;
	protected IClockService clock;
	
	/** The helper object for bean events. */
	public SimplePropertyChangeSupport pcs;
	
	//-------- constructors --------

	/**
	 * Create a new order.
	 * @param title	The title.
	 * @param deadline The deadline.
	 * @param limit	The limit.
	 * @param start	The start price
	 */
	public Order(String title, Date deadline, int start, int limit, boolean buyorder, IClockService clock)
	{
		this.title = title;
		this.startprice = start;
		this.limit = limit;
		this.buyorder = buyorder;
		this.starttime = clock.getTime();
		this.clock	= clock;
		this.state = OPEN;
		this.pcs = new SimplePropertyChangeSupport(this);
		
		setDeadline(deadline);
	}

	//-------- methods --------

	/**
	 * Get the title.
	 * @return The title.
	 */
	public String getTitle()
	{
		return title;
	}

	/**
	 * Set the title.
	 * @param title The title.
	 */
	public void setTitle(String title)
	{
		String oldtitle = this.title;
		this.title = title;
		pcs.firePropertyChange("title", oldtitle, title);
	}

	/**
	 * Get the deadline.
	 * @return The deadline.
	 */
	public Date getDeadline()
	{
		return deadline;
	}

	/**
	 * Set the deadline.
	 * @param deadline The deadline.
	 */
	public void setDeadline(Date deadline)
	{
		Date olddeadline = this.deadline;
		this.deadline = deadline;
		
		if(this.deadline!=null)
		{
//			System.out.println("Order: "+deadline.getTime()+" "+starttime);
			final long wait = Math.max(0, deadline.getTime()-starttime);
			
			clock.createTimer(wait , new ITimedObject()
			{
				public void timeEventOccurred(long currenttime)
				{
					synchronized(Order.this)
					{
						if(getState().equals(OPEN))
						{
//							System.out.println("Order state failed: "+wait+" "+Order.this);
							setState(FAILED);
						}
					}
				}
			});
		}
		
		pcs.firePropertyChange("deadline", olddeadline, deadline);
	}

	/**
	 * Get the deadline time.
	 * @return The deadline time.
	 * /
	public void getDeadlineTime()
	{
		return 
	}*/
	
	/**
	 * Get the limit.
	 * @return The limit.
	 */
	public int getLimit()
	{
		return limit;
	}

	/**
	 * Set the limit.
	 * @param limit The limit.
	 */
	public void setLimit(int limit)
	{
		int oldlimit = this.limit;
		this.limit = limit;
		pcs.firePropertyChange("limit", oldlimit, limit);
	}

	/**
	 * Getter for startprice
	 * @return Returns startprice.
	 */
	public int getStartPrice()
	{
		return startprice;
	}

	/**
	 * Setter for startprice.
	 * @param startprice The Order.java value to set
	 */
	public void setStartPrice(int startprice)
	{
		int oldstartprice = this.startprice;
		this.startprice = startprice;
		pcs.firePropertyChange("startPrice", oldstartprice, startprice);
	}

	/**
	 * Get the start time.
	 * @return The start time.
	 */
	public long getStartTime()
	{
		return starttime;
	}

	/**
	 * Set the start time.
	 * @param starttime The start time.
	 */
	public void setStartTime(long starttime)
	{
		long oldstarttime = this.starttime;
		this.starttime = starttime;
		pcs.firePropertyChange("startTime", Long.valueOf(oldstarttime), Long.valueOf(starttime));
	}

	/**
	 * Get the execution price.
	 * @return The execution price.
	 */
	public Integer getExecutionPrice()
	{
		return exeprice;
	}

	/**
	 * Set the execution price.
	 * @param exeprice The execution price.
	 */
	public void setExecutionPrice(Integer exeprice)
	{
		Integer oldexeprice = this.exeprice;
		this.exeprice = exeprice;
		pcs.firePropertyChange("executionPrice", oldexeprice, exeprice);
	}

	/**
	 * Get the execution date.
	 * @return The execution date.
	 */
	public Date getExecutionDate()
	{
		return exedate;
	}

	/**
	 * Set the execution date.
	 * @param exedate The execution date.
	 */
	public void setExecutionDate(Date exedate)
	{
		Date oldexedate = this.exedate;
		this.exedate = exedate;
		pcs.firePropertyChange("executionDate", oldexedate, exedate);
	}

	/**
	 * Test if it is a buyorder.
	 * @return True, if buy order.
	 */
	public boolean isBuyOrder()
	{
		return buyorder;
	}

	/**
	 * Set the order type.
	 * @param buyorder True for buyorder.
	 */
	public void setBuyOrder(boolean buyorder)
	{
		boolean oldbuyorder = this.buyorder;
		this.buyorder = buyorder;
		pcs.firePropertyChange("buyOrder", oldbuyorder ? Boolean.TRUE : Boolean.FALSE, buyorder ? Boolean.TRUE : Boolean.FALSE);
	}

	/**
	 *  Get the order state.
	 *  @return The order state.
	 */
	public synchronized String getState()
	{
//		String state = FAILED;
//		if(exedate != null)
//		{
//			state = DONE;
//		}
//		else if(clock.getTime() < deadline.getTime())
//		{
//			state = OPEN;
//		}
		return state;
	}

	/**
	 *  Set the state.
	 *  @param state The state.
	 */
	public synchronized void setState(String state)
	{
		String oldstate = this.state;
		this.state = state;
//		System.out.println("Order changed state: "+oldstate+" "+state);
		pcs.firePropertyChange("state", oldstate, state);
	}
	
	/**
	 * Get a string representation of the order.
	 */
	public String toString()
	{
		StringBuffer sbuf = new StringBuffer();
		sbuf.append(isBuyOrder() ? "Buy '" : "Sell '");
		sbuf.append(getTitle());
		sbuf.append("'");
		return sbuf.toString();
	}

	//-------- property methods --------

	/**
	 * Add a PropertyChangeListener to the listener list.
	 * The listener is registered for all properties.
	 *
	 * @param listener The PropertyChangeListener to be added.
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener)
	{
		pcs.addPropertyChangeListener(listener);
	}

	/**
	 * Remove a PropertyChangeListener from the listener list.
	 * This removes a PropertyChangeListener that was registered
	 * for all properties.
	 *
	 * @param listener The PropertyChangeListener to be removed.
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener)
	{
		pcs.removePropertyChangeListener(listener);
	}
}
