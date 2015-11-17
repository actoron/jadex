<span>Chapter 1. Introduction</span> 
====================================

The Jadex BDI kernel is a Belief-Desire-Intention reasoning engine for intelligent agents. The term reasoning engine means that it can be used together with different kinds of (agent) middleware providing basic agent services such as a communication infrastructure and management facilities. Currently, two mature adapters are available. The first adapter is the Jadex Standalone adapter which is a small but fast environment with a minimal memory footprint and the second is available for the well-known open-source JADE multi-agent platform \[Bellifemine et al. 2007\].

<div class="wikimodel-emptyline">

</div>

In this tutorial the Jadex Standalone adapter is used, but in principle the used adapter is not of great importance as it does not change the way Jadex agents are programmed or way the Jadex tools are used. The concepts of the BDI-model initially proposed by Bratman \[Bratman 1987\] were adapted by Rao and Georgeff \[Rao and Georgeff 1995\] to a more fomal model that is better suitable for multi-agent systems in the software architectural sense. Systems that are built on these foundations are called Procedural Reasoning Systems (PRS) with respect to their first representative. Jadex builds on experiences gained from leading existing BDI systems such as JACK \[Winikoff 2005\] and consequently improves previously not-addressed BDI weaknesses like the concurrent handling of inconsistent goals with built-in goal deliberation \[Pokahr et al. 2005a\].\\

BEGIN MACRO: html param: clean="false" wiki="true"\
\
&lt;!-- This tutorial is a good starting point for agent developers, that want to learn programming Jadex BDI agents in small hands-on exercises. Each lesson of this tutorial covers one important concept and tries to illustrate why and especially how the concept can be used in Jadex. In the following <span class="wikiexternallink">[Chapter 2, Starting an Agent](02+Starting+an+Agent+(old))</span> it is described how to setup the Jadex environment properly and how to start a simple agent. It is explained step by step how to handle plans (<span class="wikiexternallink">[Chapter 3, Using Plans](03+Using+Plans+(old))</span>), beliefs (<span class="wikiexternallink">[Chapter 4, Using Beliefs](04+Using%20Beliefs+(old))</span>) and goals (<span class="wikiexternallink">[Chapter 6, Using Goals](06+Using+Goals+(old))</span>) and how these elements can be composed (<span class="wikiexternallink">[Chapter 5, Using Capabilities](05+Using+Capabilities+(old))</span>) into reusable agent modules. Another lesson covers some aspects about information exchange on the intra and inter-agent level and builds up a multi-agent scenario <span class="wikiexternallink">[Chapter 7, Using Events](07+Using+Events+(old))</span>. Thereafter, in <span class="wikiexternallink">[Chapter 8, External Processes](08+External+Processes+(old))</span> the integration of Jadex agents with external processes is exemplarily explained. Finally a conclusion and an outlook is given in <span class="wikiexternallink">[Chapter 9, Conclusion and Outlook](09+Conclusion+and+Outlook+(old))</span>. After having worked through this tutorial the reader should be familiar with all basic agent concepts provided by Jadex. Whenever the reader encounters facts that are not explained in detail here but may need some elaboration for a thorough understanding further reading in the <span class="wikiexternallink">[BDI User Guide](BDI%20User%20Guide/01%20Introduction)</span> is recommended. If you are interested in less technical documentation you may also consider reading about Jadex in one of these book chapters \[Pokahr et al. 2005c; Braubach et al. 2005a\]. --&gt;

 END MACRO: html

-   <span class="wikiexternallink">[Chapter 2, Starting an Agent](02%20Starting%20an%20Agent)</span> describes how to setup the Jadex environment properly and how to start a simple agent.
-   <span class="wikiexternallink">[Chapter 3, Using Plans](03%20Using%20Plans)</span> explains step by step the usage of plans.
-   <span class="wikiexternallink">[Chapter 4, Using Beliefs](04%20Using%20Beliefs)</span> introduces beliefs and beliefsets as agent knowledge form.
-   <span class="wikiexternallink">[Chapter 5, Using Capabilities](05%20Using%20Capabilities)</span> explains how beliefs, goals and plans can be composed into reusable agent modules.
-   <span class="wikiexternallink">[Chapter 6, Using Goals](06%20Using%20Goals)</span> shows how goals can be used to capture the agent objectives in an intuitive way.
-   <span class="wikiexternallink">[Chapter 7, Using Events](07%20Using%20Events)</span> covers aspects about information exchange on the intra and inter-agent level and builds up a multi-agent scenario.
-   <span class="wikiexternallink">[Chapter 8, External Processes](08%20External%20Processes)</span> explains exemplarily the integration of Jadex agents with external processes.
-   <span class="wikiexternallink">[Chapter 9, Conclusion](09%20Conclusion)</span> finally concludes the lessons.
-   <span class="wikiexternallink">[Bibliography](Bibliography)</span> contains the references.

<span>Application Context</span> 
--------------------------------

In this tutorial a simple translation agent for single words will be implemented. This agent has the basic task to handle translation requests and produce for a given term in some language the translated term in the desired target language. This base functionality will be extended in the different exercises, but it is not our goal to build up a translation agent, that combines all the extensions, because this would lead to difficulties concerning the complexity of the agent. Instead this tutorial will concentrate on setting up simple agents that explain the Jadex concepts step by step.

<span>How to Use This Tutorial</span> 
-------------------------------------

-   Work through the exercises in order, because later exercises require knowledge from the earlier ones.
-   Don't destroy your solutions of an exercise by modifying the old files. The different exercises often use the plans and agent description files (ADF) of a preceeding exercise. Copy all files and apply a simple naming scheme which contains the name of the exercise in the plan and ADF file names, e.g. the ADF in the exercise A1 is called TranslationA1.agent.xml and in exercise B1 TranslationB1.agent.xml.
-   Help us to make this tutorial better with your feedback. When you find errors or have problems that are directly concerned with the exercise descriptions feel free to let us know or edit the corresponding page directly in the Wiki.
-   Whenever you encounter problems with Jadex we would be happy to help you. Please use primarily the <span class="wikiexternallink">[sourceforge help forum](http://sourceforge.net/projects/jadex/forums/forum/274112)</span> available on the Jadex sourceforge.net page. There is also a <span class="wikiexternallink">[Jadex mailing list](https://lists.sourceforge.net/lists/listinfo/jadex-develop)</span> that can be used for asking questions about Jadex.

