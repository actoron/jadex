package jadex.bdi.tutorial;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

import jadex.bdiv3x.runtime.IExpression;
import jadex.bdiv3x.runtime.Plan;


/**
 *  An english german translation plan can translate
 *  english words to german and is instantiated on demand.
 */
public class EnglishGermanTranslationPlanG1 extends Plan
{
//	//-------- attributes --------
//
//	/** Query the tuples for a word. */
//	protected IExpression	queryword;
//
//	//-------- constructors --------
//
//	/**
//	 *  Create a new plan.
//	 */
//	public EnglishGermanTranslationPlanG1()
//	{
//		getLogger().info("Created:"+this);
//		this.queryword	= getExpression("query_egword");
//	}
//
//	-------- methods --------

	/**
	 *  The plan body.
	 */
	public void body()
	{
		IExpression	queryword = getExpression("query_egword");
		Socket client = (Socket)getParameter("client").getValue();

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
			String	gword	= (String)queryword.execute("$eword", eword);
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