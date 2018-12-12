package jadex.rules.parser.conditions.javagrammar;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;

import jadex.commons.SUtil;
import jadex.rules.rulesystem.ICondition;
import jadex.rules.rulesystem.rules.AndCondition;
import jadex.rules.rulesystem.rules.BoundConstraint;
import jadex.rules.rulesystem.rules.ObjectCondition;
import jadex.rules.rulesystem.rules.Variable;
import jadex.rules.state.OAVObjectType;
import jadex.rules.state.OAVTypeModel;


/**
 *  Test the functionality of the clips parser.
 */
public class JavaParserTest
{
	/**
	 *  Main for testing only.
	 */
	public static void main(String[] args)
	{
		try
		{
//			String c	= "$waste != $location";
//			String c	= "$waste.getLocation().getDistance($location) > 0.2";
//			String c	= "$waste.getLocation().getDistance($waste2.getLocation()) > 0.2";
//			String c	= "$waste.getLocation().getDistance($waste2.getLocation()) > 0.2 && $waste.getLocation()!=$location";
			String c	= "$waste.getLocation().getDistance($waste2.getLocation()) > 0.2 && $location!=$waste.getLocation()";

			// Todo: Agent specific handling ($beliefbase etc.s)
//			String c	= "$beliefbase.chargestate > 0.2";
//			String c	= "$beliefbase.waste.getDistance($beliefbase.location) > 0.2";
//			String c	= "$beliefbase.waste.getDistance($beliefbase.location) > 0.2==7";
			
			ANTLRStringStream exp = new ANTLRStringStream(c);
			JavaJadexLexer lexer = new JavaJadexLexer(exp);
			CommonTokenStream tokens = new CommonTokenStream(lexer);
			JavaJadexParser parser = new JavaJadexParser(tokens);
		
			ClassLoader	cl	= new URLClassLoader(new URL[]{
			SUtil.findOutputDirs("jadex-applications-bdi", false)[0].toURI().toURL()});
			OAVTypeModel	tmodel	= new OAVTypeModel("cleanertypes", cl);

			OAVObjectType	locatype	= tmodel.getObjectType("jadex.bdi.examples.cleanerworld.Location");
			ObjectCondition	locacon	= new ObjectCondition(locatype);
			locacon.addConstraint(new BoundConstraint(null, new Variable("$location", locatype)));
			
			OAVObjectType	wastetype	= tmodel.getObjectType("jadex.bdi.examples.cleanerworld.Waste");
			ObjectCondition	wastecon	= new ObjectCondition(wastetype);
			wastecon.addConstraint(new BoundConstraint(null, new Variable("$waste", wastetype)));

			ObjectCondition	wastecon2	= new ObjectCondition(wastetype);
			wastecon2.addConstraint(new BoundConstraint(null, new Variable("$waste2", wastetype)));

//			AndCondition	predefined	= new AndCondition(new ICondition[]{wastecon2, wastecon});
			AndCondition	predefined	= new AndCondition(new ICondition[]{locacon, wastecon, wastecon2});
			final BuildContext	context	= new BuildContext(predefined, tmodel);
			
			System.out.println("Predefined condition:\n"+predefined+"\n");

			IParserHelper	helper	= new IParserHelper()
			{
				public Variable getVariable(String name)
				{
					return context.getVariable(name);
				}
				
				public boolean isPseudoVariable(String name)
				{
					return name.equals("$beliefbase");
				}
				
				public List getConditions()
				{
					return context.getConditions();
				}
				public void addVariable(Variable var)
				{
				}
				public BuildContext getBuildContext()
				{
					return context;
				}
				public Object[] getReplacementType(OAVObjectType type)
				{
					return null;
				}
			};
			parser.setParserHelper(helper);

			Expression	pexp	= parser.lhs();

			System.out.println("Parsed expression:\n"+pexp+"\n");

			ICondition	result	= ConstraintBuilder.buildConstraints(pexp, new BuildContext(predefined, tmodel), helper);
			
			System.out.println("Condition after build:\n"+result+"\n");
		}
		catch(MalformedURLException ex)
		{
			ex.printStackTrace();
		}
		catch(RecognitionException ex)
		{
			ex.printStackTrace();
		}
	}
}
