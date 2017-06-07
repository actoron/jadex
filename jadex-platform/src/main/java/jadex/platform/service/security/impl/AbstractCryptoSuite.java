package jadex.platform.service.security.impl;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import jadex.base.StarterConfiguration;
import jadex.platform.service.security.ICryptoSuite;

/**
 *  Abstract crypto suite class for handling message IDs / replays.
 *
 */
public abstract class AbstractCryptoSuite implements ICryptoSuite
{
	/** Maximum windows size. */
	protected static final int MAX_WINDOW = 1000000;
	
	/** Time before a delayed message expires. */
	protected long expirationdelay = StarterConfiguration.DEFAULT_LOCAL_TIMEOUT;
	
	protected long nextcheck = System.currentTimeMillis() + expirationdelay;
	
	/** Highest ID received */
	protected long highid;
	
	/** Lowest ID received */
	protected long lowid;
	
	/** Missing IDs with expiration time. (Id, Expiration Time)*/
	protected Map<Long, Long> missingids = new LinkedHashMap<Long, Long>();
	
	/** Checks if a message ID is valid */
	protected boolean isValid(long msgid)
	{
		boolean ret = false;
		
		if (System.currentTimeMillis() > nextcheck || (msgid - (lowid + MAX_WINDOW) > 0))
		{
			lowid = highid;
			for (Iterator<Map.Entry<Long, Long>> it = missingids.entrySet().iterator(); it.hasNext(); )
			{
				Map.Entry<Long, Long> entry = it.next();
				if (entry.getValue() < System.currentTimeMillis())
					it.remove();
				else if (lowid - entry.getKey() > 0)
				{
					lowid = entry.getKey();
				}
			}
			nextcheck = System.currentTimeMillis() + expirationdelay;
		}
		
		if (msgid - lowid >= 0 && ((lowid + MAX_WINDOW) - msgid > 0))
		{
			if (msgid == highid)
			{
				++highid;
				ret = true;
			}
			else if (msgid - highid > 0)
			{
				for (long id = highid; id - msgid <= 0; ++id)
					missingids.put(id, System.currentTimeMillis() + expirationdelay);
				highid = msgid;
				ret = true;
			}
			else
			{
				Long exp = missingids.remove(msgid);
				if (exp != null)
					ret = true;
			}
		}
		
		return ret;
	}
}
