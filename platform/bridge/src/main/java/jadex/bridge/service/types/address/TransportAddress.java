package jadex.bridge.service.types.address;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import jadex.bridge.BasicComponentIdentifier;
import jadex.bridge.IComponentIdentifier;

/**
 *  Class representing a transport address of a specific platform.
 */
public class TransportAddress
{
	/** ID of the platform. */
	protected IComponentIdentifier platformid;
	
	/** Type of transport the address supports. */
	protected String transporttype;
	
	/** The address. */
	protected String address;
	
	/**
	 *  Bean constructor.
	 */
	public TransportAddress()
	{
	}
	
	/**
	 *  Creates the address.
	 * 
	 *  @param platformid The platform ID.
	 *  @param transporttype The type of transport.
	 *  @param address The address.
	 */
	public TransportAddress(IComponentIdentifier platformid, String transporttype, String address)
	{
		this.platformid = platformid;
		this.transporttype = transporttype;
		this.address = address;
	}
	
	/**
	 *  Gets the ID of the platform owning the address.
	 * 
	 *  @return The ID of the platform owning the address.
	 */
	public IComponentIdentifier getPlatformId()
	{
		return platformid;
	}
	
	/**
	 *  Sets the ID of the platform owning the address.
	 * 
	 *  @param platformid The ID of the platform owning the address.
	 */
	public void setPlatformId(IComponentIdentifier platformid)
	{
		this.platformid = platformid;
	}
	
	/**
	 *  Gets the type of transport using the address.
	 *  
	 *  @return The type of transport.
	 */
	public String getTransportType()
	{
		return transporttype;
	}
	
	/**
	 *  Sets the type of transport using the address.
	 *  
	 *  @param transporttype The type of transport.
	 */
	public void setTransportType(String transporttype)
	{
		this.transporttype = transporttype;
	}

	/**
	 *  Gets the address.
	 *  
	 *  @return The address.
	 */
	public String getAddress()
	{
		return address;
	}

	/**
	 *  Sets the address.
	 *
	 *  @param address The address.
	 */
	public void setAddress(String address)
	{
		this.address = address;
	}
	
	/**
	 *  Hash code.
	 */
	public int hashCode()
	{
		int ret = 31;
		ret += platformid.hashCode();
		ret += transporttype.hashCode();
		ret += address.hashCode();
		return ret;
	}
	
	/**
	 *   Equals method.
	 */
	public boolean equals(Object obj)
	{
		boolean ret = false;
		if (obj instanceof TransportAddress)
		{
			TransportAddress ta = (TransportAddress) obj;
			ret = ta.getPlatformId().equals(platformid) && ta.getTransportType().equals(transporttype) && ta.getAddress().equals(address); 
		}
		return ret;
	}
	
	/**
	 *  Get the string rep.
	 */
	public String toString()
	{
		return "TransportAddress [platformid=" + platformid + ", transporttype=" + transporttype + ", address=" + address + "]";
	}

	/**
	 *  Convert a string to transport addresses.
	 *  Format is: platformname{scheme1://addi1,scheme2://addi2}
	 */
	public static TransportAddress[] fromString(String str)
	{
		List<TransportAddress> ret = new ArrayList<TransportAddress>();
		if(str != null)
		{
			StringTokenizer stok = new StringTokenizer(str, "}");
			while(stok.hasMoreTokens())
			{
				String part = stok.nextToken();
				if(part.startsWith(","))
					part = part.substring(1);
				int idxs = part.indexOf("{");
				if(idxs!=-1)
				{
					String name = part.substring(0, idxs);
					String rest = part.substring(idxs+1, part.length());
					StringTokenizer stok2 = new StringTokenizer(rest, ",");
					while(stok2.hasMoreTokens())
					{
						String all = stok2.nextToken();
						int idx = all.indexOf("://");
						String scheme = all.substring(0, idx);
						String addi = all.substring(idx+3);
						ret.add(new TransportAddress(new BasicComponentIdentifier(name), scheme, addi));
					}
				}
			}
		}
		return ret.toArray(new TransportAddress[ret.size()]);
	}
	
	/**
	 *  Main for testing.
	 */
	public static void main(String[] args)
	{
		System.out.println(Arrays.toString(fromString("platformname1{scheme11://addi11,scheme12://addi12},platformname2{scheme21://addi21,scheme22://addi22}")));
	}
}
