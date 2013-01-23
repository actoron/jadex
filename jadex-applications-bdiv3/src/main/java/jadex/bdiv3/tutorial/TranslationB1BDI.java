package jadex.bdiv3.tutorial;

import jadex.bdiv3.BDIAgent;
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
public class TranslationB1BDI implements ITranslationService
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

//<?xml version="1.0" encoding="UTF-8"?>
//<!--
//	<H3>TranslationAgent: Lesson B1.</H3>
//	Creating an initial plan.
//	<H4>Explanation</H4>
//	The agent has one initial plan (created when the agent is born)
//	for translating words from English to German. 
//-->
//<agent xmlns="http://jadex.sourceforge.net/jadex"
//	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
//	xsi:schemaLocation="http://jadex.sourceforge.net/jadex
//	                    http://jadex.sourceforge.net/jadex-bdi-2.3.xsd"
//	name="TranslationB1"
//	package="jadex.bdi.tutorial">
//
//	<plans>
//		<!-- A translation plan, from which an instance is created
//			when the agent is born. Reacts on translation request messages. -->
//		<plan name="egtrans">
//			<body class="EnglishGermanTranslationPlanB1"/>
//			<waitqueue>
//				<messageevent ref="request_translation"/>
//			</waitqueue>
//		</plan>
//	</plans>
//
//	<events>
//		<!-- Specifies a translation request being all
//			messages with performative request. -->
//		<messageevent name="request_translation" direction="receive" type="fipa">
//			<parameter name="performative" class="String" direction="fixed">
//				<value>jadex.bridge.fipa.SFipa.REQUEST</value>
//			</parameter>
//		</messageevent>
//	</events>
//
//	<properties>
//		<property name="debugging">false</property>
//    </properties>
//
//	<configurations>
//		<configuration name="default">
//			<plans>
//				<initialplan ref="egtrans"/>
//			</plans>
//		</configuration>
//	</configurations>
//
//</agent>
//

