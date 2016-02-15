Introduction
====================================

The Jadex BDI kernel is a Belief-Desire-Intention reasoning engine for intelligent agents. The term reasoning engine means that it can be used together with different kinds of (agent) middleware providing basic agent services such as a communication infrastructure and management facilities. Currently, two mature adapters are available. The first adapter is the Jadex Standalone adapter which is a small but fast environment with a minimal memory footprint and the second is available for the well-known open-source JADE multi-agent platform [Bellifemine et al. 2007].



In this tutorial the Jadex Standalone adapter is used, but in principle the used adapter is not of great importance as it does not change the way Jadex agents are programmed or way the Jadex tools are used. The concepts of the BDI-model initially proposed by Bratman [Bratman 1987] were adapted by Rao and Georgeff [Rao and Georgeff 1995] to a more fomal model that is better suitable for multi-agent systems in the software architectural sense. Systems that are built on these foundations are called Procedural Reasoning Systems (PRS) with respect to their first representative. Jadex builds on experiences gained from leading existing BDI systems such as JACK [Winikoff 2005] and consequently improves previously not-addressed BDI weaknesses like the concurrent handling of inconsistent goals with built-in goal deliberation [Pokahr et al. 2005a].

-   [Chapter 2, Starting an Agent](02%20Starting%20an%20Agent)  describes how to setup the Jadex environment properly and how to start a simple agent.
-   [Chapter 3, Using Plans](03%20Using%20Plans)  explains step by step the usage of plans.
-   [Chapter 4, Using Beliefs](04%20Using%20Beliefs)  introduces beliefs and beliefsets as agent knowledge form.
-   [Chapter 5, Using Capabilities](05%20Using%20Capabilities)  explains how beliefs, goals and plans can be composed into reusable agent modules.
-   [Chapter 6, Using Goals](06%20Using%20Goals)  shows how goals can be used to capture the agent objectives in an intuitive way.
-   [Chapter 7, Using Events](07%20Using%20Events)  covers aspects about information exchange on the intra and inter-agent level and builds up a multi-agent scenario.
-   [Chapter 8, External Processes](08%20External%20Processes)  explains exemplarily the integration of Jadex agents with external processes.
-   [Chapter 9, Conclusion](09%20Conclusion)  finally concludes the lessons.
-   [Bibliography](Bibliography)  contains the references.

## Application Context

In this tutorial a simple translation agent for single words will be implemented. This agent has the basic task to handle translation requests and produce for a given term in some language the translated term in the desired target language. This base functionality will be extended in the different exercises, but it is not our goal to build up a translation agent, that combines all the extensions, because this would lead to difficulties concerning the complexity of the agent. Instead this tutorial will concentrate on setting up simple agents that explain the Jadex concepts step by step.

## How to Use This Tutorial

-   Work through the exercises in order, because later exercises require knowledge from the earlier ones.
-   Don't destroy your solutions of an exercise by modifying the old files. The different exercises often use the plans and agent description files (ADF) of a preceeding exercise. Copy all files and apply a simple naming scheme which contains the name of the exercise in the plan and ADF file names, e.g. the ADF in the exercise A1 is called TranslationA1.agent.xml and in exercise B1 TranslationB1.agent.xml.
-   Help us to make this tutorial better with your feedback. When you find errors or have problems that are directly concerned with the exercise descriptions feel free to let us know or edit the corresponding page directly in the Wiki.
-   Whenever you encounter problems with Jadex we would be happy to help you. Please use primarily the [Jadex mailing list](https://lists.sourceforge.net/lists/listinfo/jadex-develop)  that can be used for asking questions about Jadex.
<!--TODO: outdated-->

