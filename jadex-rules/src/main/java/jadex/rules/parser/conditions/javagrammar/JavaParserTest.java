package jadex.rules.parser.conditions.javagrammar;

import jadex.rules.rulesystem.ICondition;
import jadex.rules.rulesystem.rules.AndCondition;
import jadex.rules.rulesystem.rules.BoundConstraint;
import jadex.rules.rulesystem.rules.ObjectCondition;
import jadex.rules.rulesystem.rules.Variable;
import jadex.rules.state.OAVObjectType;
import jadex.rules.state.OAVTypeModel;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;


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
			String c	= "$beliefbase_location != $beliefbase_waste";
//			String c	= "$beliefbase_waste.getDistance($beliefbase_location) > 0.2";

			// Todo: Agent specific handling ($beliefbase etc.s)
//			String c	= "$beliefbase.chargestate > 0.2";
//			String c	= "$beliefbase.waste.getDistance($beliefbase.location) > 0.2";
//			String c	= "$beliefbase.waste.getDistance($beliefbase.location) > 0.2==7";
			
			ANTLRStringStream exp = new ANTLRStringStream(c);
			JavaJadexLexer lexer = new JavaJadexLexer(exp);
			CommonTokenStream tokens = new CommonTokenStream(lexer);
			JavaJadexParser parser = new JavaJadexParser(tokens);
		
			parser.rhs();
			System.out.println("Parsed: "+parser.getStack());
			Constraint[]	constraints	= (Constraint[])parser.getStack()
				.toArray(new Constraint[parser.getStack().size()]);

			ClassLoader	cl	= new URLClassLoader(new URL[]{
				new File("../jadex-applications-bdi/target/classes").toURI().toURL()});
			OAVTypeModel	tmodel	= new OAVTypeModel("cleanertypes", cl);

			OAVObjectType	wastetype	= tmodel.getObjectType("jadex.bdi.examples.cleanerworld.Waste");
			ObjectCondition	wastecon	= new ObjectCondition(wastetype);
			wastecon.addConstraint(new BoundConstraint(null, new Variable("$beliefbase_waste", wastetype)));

			OAVObjectType	locatype	= tmodel.getObjectType("jadex.bdi.examples.cleanerworld.Location");
			ObjectCondition	locacon	= new ObjectCondition(locatype);
			locacon.addConstraint(new BoundConstraint(null, new Variable("$beliefbase_location", locatype)));
			
			ICondition	result	= ConditionBuilder.buildCondition(constraints,
				new AndCondition(new ICondition[]{wastecon, locacon}));
			
			System.out.println("Built: "+result);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
}
