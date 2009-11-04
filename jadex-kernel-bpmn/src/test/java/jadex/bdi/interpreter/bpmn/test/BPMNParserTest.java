/**
 * 
 */
package jadex.bdi.interpreter.bpmn.test;

import jadex.bdi.interpreter.bpmn.model.IBpmnState;
import jadex.bdi.interpreter.bpmn.model.ParsedStateMachine;
import jadex.bdi.interpreter.bpmn.parser.BpmnParser;
import jadex.bdi.interpreter.bpmn.parser.BpmnParserException;
import jadex.bdi.interpreter.bpmn.parser.impl.daimler.BpmnPlanParser;

import java.io.File;
import java.io.FileInputStream;
import java.net.URI;
import java.net.URL;
import java.util.Map;

import javax.swing.plaf.metal.MetalIconFactory.FolderIcon16;

import junit.framework.TestCase;

import org.junit.Test;

/**
 * @author claas altschaffel
 *
 * 
 * Partial based on class provided by Daimler
 */
public class BPMNParserTest extends TestCase
{

	private String testCasePlanFile = "jadex/bdi/interpreter/bpmn/test/TestCasePlan.net";
	
	private String testCasePlanDippFolder = "jadex/bdi/interpreter/bpmn/test/dipp/";
	
	/**
	 * Test method for {@link aem.compiler.parser.BpmnParser#getInstance(java.net.URL)}
	 * and {@link aem.compiler.parser.BpmnParser#parseFile()}.
	 */
	public void testBPMNParser()
	{
		URL url = ClassLoader.getSystemResource(testCasePlanFile);
        
		try
		{
			BpmnParser parser = BpmnParser.getInstance(url);
			if (parser instanceof BpmnPlanParser)
	        {
	            ParsedStateMachine psm = (ParsedStateMachine) ((BpmnPlanParser) parser).parseFile();
	            if (psm != null) {
	                Map states = psm.getStateMap();
	                String initialState = psm.getStartStateId();
	            }
	        }
	        else
	        {
	        	fail("getInstance doesn't return a BPMN-Parser");
	        }
		}
		catch (BpmnParserException e)
		{
			fail(e.getMessage());
		}

		
	}
	
	public void testBPMNParserComplete()
	{
		URL url = ClassLoader.getSystemResource(testCasePlanDippFolder);

		File folder = new File(url.getPath());
		File[] files = folder.listFiles();

		for (int i = 0; i < files.length; i++)
		{
			try
			{
				if (files[i].isFile())
				{
					if (files[i].getName().endsWith(".net"))
					{
						// HACK !!!
						String fileString = files[i].getAbsolutePath().replace("/home/claas/development/workspaces/jadex-v2/jadex-bpmn/target/test-classes/", "");
						System.out.println("Checking file: " + fileString);
						URL fileurl = ClassLoader.getSystemResource(fileString);
						System.out.println("URL: " + fileurl);
						BpmnParser parser = BpmnParser.getInstance(fileurl);
//						FileInputStream fis = new FileInputStream(files[i]);
//						BpmnParser parser = BpmnParser.getInstance(fis);
						if (parser instanceof BpmnPlanParser)
						{
							ParsedStateMachine psm = (ParsedStateMachine) ((BpmnPlanParser) parser)
									.parseFile();
							if (psm != null)
							{
								Map states = psm.getStateMap();
								String initialState = psm.getStartStateId();
							}
							System.out.println("SUCCESS");
						}
						else
						{
							System.out.println("FAIL - getInstance doesn't return a BPMN-Parser");
							//fail("getInstance doesn't return a BPMN-Parser");
						}
					}
				}

			}
			catch (Exception e)
			{
				System.out.println("FAIL - caught Exception: " + e);
				//e.printStackTrace();
			}
		}
		
        
		
	}

}
