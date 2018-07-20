package jadex.bdiv3.tutorial.g1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import jadex.bdiv3.BDIAgentFactory;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.commons.future.IFuture;
import jadex.commons.transformation.annotations.Classname;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.AgentFeature;
import jadex.micro.annotation.AgentKilled;

/**
<H3>TranslationAgent: Lesson G1.</H3>
Using a separate thread to accept http connections.
<H4>Explanation</H4>
The agent opens a server connection at port 9099 and waits
for translation requests.<br>
Direct your browser to e.g.
<a href="http://localhost:9099/dog">http://localhost:9099/dog</a>
to perform a translation.
*/
@Agent(type=BDIAgentFactory.TYPE)
public class TranslationBDI
{
	//-------- attributes --------

	@AgentFeature
	protected IExecutionFeature execFeature;

	@AgentFeature
	protected IBDIAgentFeature bdiFeature;

	@Agent
	protected IInternalAccess agent;
	
	/** The wordtable. */
	protected Map<String, String> wordtable;

	/** The server socket. */
	protected ServerSocket server;

	//-------- methods --------

	/**
	 * 
	 */
	@Goal
	public class Translate
	{
		protected Socket client;

		/**
		 *  Create a new Translate. 
		 */
		public Translate(Socket client)
		{
			this.client = client;
		}

		/**
		 *  Get the client.
		 *  @return The client.
		 */
		public Socket getClient()
		{
			return client;
		}

		/**
		 *  Set the client.
		 *  @param client The client to set.
		 */
		public void setClient(Socket client)
		{
			this.client = client;
		}
	}
	
	/**
	 * 
	 */
	@AgentCreated
	public void init()
	{
//		System.out.println("Created: "+this);
		this.wordtable = new HashMap<String, String>();
		this.wordtable.put("coffee", "Kaffee");
		this.wordtable.put("milk", "Milch");
		this.wordtable.put("cow", "Kuh");
		this.wordtable.put("cat", "Katze");
		this.wordtable.put("dog", "Hund");
		
		final int port = 9099;
		
		try
		{
			server	= new ServerSocket(port);
		}
		catch(IOException e)
		{
			throw new RuntimeException(e.getMessage());
		}

		Runnable run = new Runnable()
		{			
			/**
			 *  The server code.
			 *  This method runs on the separate thread,
			 *  and repeatedly blocks until a client connects.
			 *  @see Runnable
			 */
			public void	run()
			{
//				logger.info("Created: "+Thread.currentThread());

				// Repeatedly listen for connections, until the server has been closed.
				try
				{
					// Accept connections while server is active.
					while(true)
					{
						final Socket client	= server.accept();
						execFeature.scheduleStep(new IComponentStep<Void>()
						{
							@Classname("translate")
							public IFuture<Void> execute(IInternalAccess ia)
							{
								bdiFeature.dispatchTopLevelGoal(new Translate(client));
								return IFuture.DONE;
							}
						});
					}
				}
				catch(IOException e)
				{
					// Server has been closed.
//					e.printStackTrace();
					agent.getLogger().info("Exited: "+Thread.currentThread());
				}
				catch(ComponentTerminatedException e)
				{
					// Agent has died: close server.
					close();
				}
			}
			
			protected void close()
			{
				if(server!=null)
				{
					try
					{
						server.close();
					}
					catch(Exception e)
					{
					}
				}
			}
		};
		
		Thread t = new Thread(run);
		t.start();
	}
	
	/**
	 *  Called when the agent is terminated.
	 */
	@AgentKilled
	public void killed()
	{
		if(server!=null)
		{
			try
			{
				server.close();
			}
			catch(Exception e)
			{
			}
		}
	}
	
	/**
	 *  Plan that translates a request.
	 */
	@Plan(trigger=@Trigger(goals=Translate.class))
	public void translate(Translate trans)
	{
		Socket client = trans.getClient();

		try
		{
			BufferedReader	in	= new BufferedReader(new InputStreamReader(client.getInputStream()));
			String	request	= in.readLine();
			if(request==null)
			{
				throw new RuntimeException("No word received from client.");
			}
			
			int	slash	= request.indexOf("/");
			int	space	= request.indexOf(" ", slash);
			String	eword	= request.substring(slash+1, space);
//			String	gword	= (String)queryword.execute("$eword", eword);
			String gword = wordtable.get(eword);
			System.out.println(request);
//			while(request!=null)
//				System.out.println(request	= in.readLine());
			
			PrintStream	out	= new PrintStream(client.getOutputStream());
			out.print("HTTP/1.0 200 OK\r\n");
			out.print("Content-type: text/html\r\n");
			out.println("\r\n");
			out.println("<html><head><title>TranslationM1 - "+eword+"</title></head><body>");
			out.println("<p>Translated from english to german: "+eword+" = "+gword+".");
			out.println("</p></body></html>");
			out.flush();
			client.close();
		}
		catch(IOException e)
		{
			throw new RuntimeException(e.getMessage());
		}
	}
}

//<!-- Initial plan for starting a server thread waiting for client connections.
//		Adds new sockets with the new connection to the client belief set. -->
//<plan name="server">
//	<parameter name="port" class="int">
//		<value>9099</value>
//	</parameter>
//	<body class="ServerPlanG1"/>
//</plan>
//</plans>
//
//<expressions>
//<!-- This query selects the first matching entry from the English - German
//	dictionary, whereby the parameter $eword is compared to the first
//	element of a belief set tuple. -->
//<expression name="query_egword">
//	select one $wordpair.get(1)
//	from Tuple $wordpair in $beliefbase.getBeliefSet("egwords").getFacts()
//	where $wordpair.get(0).equals($eword)
//	<!-- <parameter name="$eword" class="String"/> -->
//</expression>
//</expressions>
//
//<configurations>
//<configuration name="default">
//	<plans>
//		<initialplan ref="server"/>
//	</plans>
//</configuration>
//</configurations>
