package jadex.bdiv3.tutorial;

import jadex.bdiv3.BDIAgent;
import jadex.bdiv3.annotation.Goal;
import jadex.bridge.service.annotation.Service;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

import java.util.HashMap;
import java.util.Map;

/**
 *  The translation agent A1.
 */
@Agent
@Service
@ProvidedServices(@ProvidedService(type=ITranslationService.class, 
	implementation=@Implementation(expression="$pojoagent")))
public class TranslationB2BDI implements ITranslationService
{
	//-------- attributes --------

	@Agent
	protected BDIAgent agent;
	
	/** The wordtable. */
	protected Map<String, String> wordtable;

	//-------- methods --------

	/**
	 *  Create a new plan.
	 */
	@AgentBody
	public void body()
	{
		System.out.println("Created: "+this);

		this.wordtable = new HashMap<String, String>();
		this.wordtable.put("coffee", "Kaffee");
		this.wordtable.put("milk", "Milch");
		this.wordtable.put("cow", "Kuh");
		this.wordtable.put("cat", "Katze");
		this.wordtable.put("dog", "Hund");
	}
	
	@Goal()
	public class PaintMoneyGoal
	{
	}
	
//	<achievegoal name="getoneeuro">
//	<parameter name="name" class="String">
//		<!-- todo: support also expressions such as $arg0+$arg1 -->
//		<!-- <servicemapping ref="paintservice.arg0"/>  -->
//	</parameter>
//	<parameter name="result" class="String" direction="out">
//		<!-- <servicemapping ref="paintservice.result"/> -->
//	</parameter>
//	<publish class="IPaintMoneyService" method="paintOneEuro"/>
//		<!-- <termination>$beliefbase.painter==null</termination>
//	</publish> -->
//  </achievegoal>
	
//	<plan name="letotherpaintone">
//	<parameter name="name" class="String">
//		<value>$scope.getComponentIdentifier().getName()</value>
//	</parameter>
//	<parameter name="result" class="String" direction="out"/>
//	<body service="paintservices" method="paintOneEuro"/>
//	<trigger>
//		<goal ref="getoneeuro"/>
//	</trigger>
//  </plan>
	
	/**
	 *  Translate an English word to German.
	 *  @param eword The english word.
	 *  @return The german translation.
	 */
	public IFuture<String> translateEnglishGerman(String eword)
	{
		String gword = wordtable.get(eword);
		return new Future<String>(gword);
	}
}
