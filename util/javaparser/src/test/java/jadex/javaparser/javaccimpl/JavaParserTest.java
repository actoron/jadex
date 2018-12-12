package jadex.javaparser.javaccimpl;

import java.awt.HeadlessException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import org.junit.Assert;
import org.junit.Test;

import jadex.javaparser.IExpressionParser;
import jadex.javaparser.IParsedExpression;
import jadex.javaparser.SimpleValueFetcher;

/**
 * 
 */
public class JavaParserTest //extends TestCase
{
	// Expressions known to be broken (excluded from test -> todo: fix!)
	protected Set<String>	broken	= new HashSet<String>(Arrays.asList(new String[]
	{
		"(\"term\"==\"term\")?false:true",
		"true?true:false",
		"\"term\"==\"term\"?false:true"
	}));
	
	@Test
	public void	testExpressions() throws IOException
	{
		// Load tests from properties.
		Properties props	= new Properties();
		InputStream	is	= getClass().getResourceAsStream("TestExpressions.properties");
		props.load(is);
		is.close();

		IExpressionParser	parser	= new JavaCCExpressionParser();
		String imports[]	= null;
		if(props.getProperty("imports")!=null)
		{
			String	imp	= (String)props.remove("imports");
			ArrayList<String>	list	= new ArrayList<String>();
			StringTokenizer	stok	= new StringTokenizer(imp, ", ");
			while(stok.hasMoreTokens())
				list.add(stok.nextToken().trim());
			
			imports	= list.toArray(new String[list.size()]);
		}

		SimpleValueFetcher	fetcher	= new SimpleValueFetcher();
		String parameters	= props.getProperty("parameters");
		Map<String, Object>	params	= new HashMap<String, Object>();
		Map<String, Class<?>>	paramtypes	= new HashMap<String, Class<?>>();
		if(parameters!=null)
		{
			props.remove("parameters");
			StringTokenizer	stok	= new StringTokenizer(parameters, ",");
			while(stok.hasMoreTokens())
			{
				String	param	= stok.nextToken().trim();
				String	value	= props.getProperty(param);
				params.put(param, parser.parseExpression(value, imports, params, getClass().getClassLoader()).getValue(fetcher));
				if(params.get(param)!=null)
				{
					paramtypes.put(param, params.get(param).getClass());
				}
				props.remove(param);
				fetcher.setValues(params);
			}
		}

		for(Iterator<Object> it=props.keySet().iterator(); it.hasNext(); )
		{
			// Read expression from properties.
			String	exp	= (String)it.next();
			System.out.println("---> "+exp);
			
			if(broken.contains(exp))
			{
				System.out.println("Broken expression: "+exp);
			}
			else
			{
				//System.out.println(exp);
				String	result	= props.getProperty(exp);
				int	sep	= result.lastIndexOf(":");
				String	value	= result.substring(0, sep).trim();
				String	type	= result.substring(sep+1).trim();
	
				// Constant node expected.
				if(type.startsWith("c"))
				{
					type	= type.substring(1).trim();
				}
				// Parse exception expected.
				boolean	parsex	= false;
				if(type.startsWith("p"))
				{
					parsex	= true;
					type	= type.substring(1).trim();
				}
				// Evaluation exception expected.
				boolean	evalex	= false;
				if(type.startsWith("e"))
				{
					evalex	= true;
					type	= type.substring(1).trim();
				}
	
				// Try to parse.
				IParsedExpression node	= null;
				try
				{
					//node = parser.parseExpression(exp, paramtypes, null);
					node = parser.parseExpression(exp, imports, params, getClass().getClassLoader());
					Assert.assertFalse("Expected parse exception", parsex);
				}
				catch(Throwable e)
				{
//					e.printStackTrace();
					Assert.assertTrue("Unexpected parse exception: "+e, parsex);
	//				if(parsex)
	//				{
	//					assertTrue("Unexpected parse exception type: "+e, e.toString().startsWith(type));
	//				}
				}
	
				// Try to evaluate.
				Object	retval	= null;
				boolean	evaluated	= false;
				if(node!=null && !parsex)
				{
					try
					{
						retval	= node.getValue(fetcher);
						evaluated	= true;
						Assert.assertFalse("Expected evaluation exception", evalex);
					}
					catch(HeadlessException e) {
						// ignore
					}
					catch(Exception e)
					{
						Assert.assertTrue("Unexpected evaluation exception: "+e, evalex);
						if(evalex)
						{
							Assert.assertTrue("Unexpected evaluation exception type: "+e, e.toString().startsWith(type));
						}
					}
				}
	
				// Match return value.
				if(evaluated && !evalex)
				{
					String	rets	= ""+retval;
					Assert.assertTrue("Unexpected value "+rets+", should be "+value+")", rets.startsWith(value));
				}
			}
		}
	}
}
