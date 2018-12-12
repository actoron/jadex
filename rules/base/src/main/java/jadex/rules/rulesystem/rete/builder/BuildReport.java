package jadex.rules.rulesystem.rete.builder;

import java.util.ArrayList;
import java.util.List;

import jadex.rules.rulesystem.IRule;

/**
 *  The report for storing time information about building rules.
 */
public class BuildReport
{
	//-------- attributes --------
	
	/** The build infos. */
	protected List buildinfos;
	
	//-------- constructors --------
	
	/**
	 *  Create a new report.
	 */
	public BuildReport()
	{
		buildinfos = new ArrayList();
	}
	
	//-------- methods --------
	
	/**
	 *  Add a new info. 
	 */
	public void addInfo(IRule rule, long time)
	{
		buildinfos.add(new BuildInfo(rule, time));
	}
	
	/**
	 *  Get build infos.
	 *  @return The build infos.
	 */
	public List getBuildInfos()
	{
		return buildinfos;
	}

	/**
	 *  Get the string representation.
	 *  @return The string representation.
	 */
	public String toString()
	{
		StringBuffer buf = new StringBuffer();
		
		buf.append("Individual rule build times: \n\n");
		long sum = 0;
		for(int i=0; i<buildinfos.size(); i++)
		{
			BuildInfo bi = (BuildInfo)buildinfos.get(i);
			buf.append(buildinfos.get(i)+"\n");
			sum += bi.getTime();
		}
		
		long mean = buildinfos.size()>0? sum/buildinfos.size(): 0;
		buf.append("\nMean build time: "+mean);
		
		return buf.toString();
	}
}