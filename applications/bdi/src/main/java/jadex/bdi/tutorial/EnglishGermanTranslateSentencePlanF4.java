package jadex.bdi.tutorial;


import java.util.StringTokenizer;

import jadex.bdiv3.runtime.IGoal;
import jadex.bdiv3.runtime.impl.GoalFailureException;
import jadex.bdiv3x.runtime.IMessageEvent;
import jadex.bdiv3x.runtime.Plan;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.fipa.SFipa;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.df.IDF;
import jadex.bridge.service.types.df.IDFComponentDescription;
import jadex.bridge.service.types.df.IDFServiceDescription;
import jadex.commons.SUtil;

/**
 *  Tries to translate a sentence word by word
 *  using a translation service for words.
 */
public class EnglishGermanTranslateSentencePlanF4 extends Plan
{
	//-------- attributes --------

	/** The translation agent. */
	protected IComponentIdentifier ta;

//	//-------- constructors --------
//
//	/**
//	 *  Create a new plan.
//	 */
//	public EnglishGermanTranslateSentencePlanF4()
//	{
//		getLogger().info("Created:"+this);
//	}

	//-------- methods --------

	/**
	 *  The plan body.
	 */
	public void body()
	{
		while(true)
		{
			// Read the user request.
			IMessageEvent mevent = waitForMessageEvent("request_translatesentence");

			// Save the words of the sentence.
			String cont = (String)mevent.getParameter(SFipa.CONTENT).getValue();
			StringTokenizer stok = new StringTokenizer(cont, " ");
			stok.nextToken();
			stok.nextToken();
			String[] words = new String[stok.countTokens()];
			String[] twords = new String[words.length];
			for(int i=0; stok.hasMoreTokens(); i++)
			{
				words[i] = stok.nextToken();
			}

			// Search a translation agent.
			while(ta==null)
			{
				IDF	dfservice	= (IDF)getAgent().getComponentFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>( IDF.class, RequiredServiceInfo.SCOPE_PLATFORM));
				
				// Create a service description to search for.
				IDFServiceDescription sd = dfservice.createDFServiceDescription(null, "translate english_german", null);
				IDFComponentDescription ad = dfservice.createDFComponentDescription(null, sd);

				// Search for a translation agent
				IDFComponentDescription[]	result = dfservice.search(ad, null).get();

				if(result.length>0)
				{
					this.ta = result[0].getName();
				}
				else
				{
					//if(result instanceof Exception)
					//	((Exception)result).printStackTrace();
					System.out.println("No translation agent found.");
					waitFor(5000);
				}
			}

			// Translate the words.
			for(int i=0; i<words.length; i++)
			{
				//getLogger().info("Asking now: "+words[i]);
		     	IGoal tw = createGoal("rp_initiate");
		   		tw.getParameter("action").setValue("translate english_german "+words[i]);
				tw.getParameter("receiver").setValue(this.ta);
				try
				{
					dispatchSubgoalAndWait(tw);
					twords[i] = (String)tw.getParameter("result").getValue();
				}
				catch(GoalFailureException gfe)
				{
					twords[i] = "n/a";
				}
			}

			// Send the reply.
			StringBuffer buf = new StringBuffer();
			buf.append("Translated: ");
			for(int i=0; i<words.length; i++)
			{
				buf.append(words[i]+" ");
			}
			buf.append("\nto: ");
			for(int i=0; i<words.length; i++)
			{
				buf.append(twords[i]+" ");
			}
			//getLogger().info(buf.toString());

			IMessageEvent rep = getEventbase().createReply(mevent, "inform");
			rep.getParameter(SFipa.CONTENT).setValue(buf.toString());
			System.out.println("Sending reply: "+SUtil.arrayToString(rep.getParameterSet(SFipa.RECEIVERS).getValues()));
			sendMessage(rep);
		}
	}
}
