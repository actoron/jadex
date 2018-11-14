package org.activecomponents.udp;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *  Class for monitoring incoming IDs and identifying replays.
 *
 */
public class IdReplayTracker
{
	/** ID lower bound, no ID is valid at or below this value. */
	protected long idlowerbound = Long.MIN_VALUE;
	
	/** ID upper bound, the highest ID checked. */
	protected long idupperbound = Long.MIN_VALUE;
	
	/** Timestamp for the next cleanup of the missing ID map. */
	protected long nextidcleanup = Long.MIN_VALUE;
	
	/** Timeout for missing IDs. */
	protected int idtimeout = 30000;
	
	/** 
	 *  IDs not checked yet between upper and lower bound.
	 *  Missing ID -> Timestamp of last packet.
	 */
	protected Map<Long, Long> missingIds = new HashMap<Long, Long>();
	
	public IdReplayTracker()
	{
	}
	
	public IdReplayTracker(long startid, int idtimeout)
	{
		idlowerbound = startid - 1;
		idupperbound = idlowerbound;
		this.idtimeout = idtimeout;
	}
	
	/**
	 *  Checks if an ID is a replay value, otherwise registers it.
	 *  
	 *  @param id The ID.
	 *  
	 *  @return True, if the same ID was checked before, false otherwise.
	 */
	protected synchronized boolean isReplay(long id)
	{
		if (id <= idlowerbound || (id < idupperbound && !missingIds.containsKey(id)))
		{
//			System.out.println("Replay detected: " + id);
			return true;
		}
		missingIds.remove(id);
		
		while (idupperbound < id - 1)
		{
			++idupperbound;
			missingIds.put(idupperbound, System.currentTimeMillis());
		}
		
		if (idupperbound < id)
		{
			idupperbound = id;
		}
		
		if (nextidcleanup < System.currentTimeMillis())
		{
			long timeout = System.currentTimeMillis() - idtimeout;
			long minmissing = Long.MAX_VALUE;
			for (Iterator<Map.Entry<Long, Long>> it = missingIds.entrySet().iterator(); it.hasNext(); )
			{
				Map.Entry<Long, Long> entry = it.next();
				if (entry.getValue() < timeout)
				{
					it.remove();
				}
				else if (minmissing > entry.getKey())
				{
					minmissing = entry.getKey();
				}
			}
			
			if (missingIds.isEmpty())
			{
				idlowerbound = idupperbound;
			}
			else if (minmissing > idlowerbound + 1)
			{
				idlowerbound = minmissing - 1;
			}
			nextidcleanup = System.currentTimeMillis() + (idtimeout << 1);
			
//			System.out.println("Lower bound set: " + idlowerbound);
		}
		
		return false;
	}
}
