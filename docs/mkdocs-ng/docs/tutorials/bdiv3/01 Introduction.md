# Introduction
The Jadex BDI V3 kernel is a Belief-Desire-Intention reasoning engine for intelligent agents. 
As the name indicates it is the third version of the Jadex BDI kernel. 
The V1 kernel version was based on XML and Java and introduced an goal-oriented reasoning mechanism embraces the full BDI reasoning cycle including the selection of goals to pursue (goal deliberation) and the realization phase in which different plans can be tried out to achieve a goal.

In BDI kernel V2 the programming model was kept the same but the engine itself was completely rebuilt based on a RETE rule engine operating on BDI rules.

Finally, in V3 the main objective was to create a new programming model that allows fast prototyping and hides as much of the framework as possible. Thus, in the new V3 kernel BDI agents are written in Java only (no XMLs any more) and annotations are used to designate BDI elements. 
Another important aspect is the much stronger integration of BDI and object oriented concepts in the new kernel, i.e. it becomes much simpler to program BDI agent having a solid background on object-oriented concepts (supporting e.g. inheritance, POJO programming, dependency injection).     

This tutorial is a good starting point for agent developers, that want to learn programming Jadex BDI agents in small hands-on exercises. Each lesson of this tutorial covers one important concept and tries to illustrate why and especially how the concept can be used in Jadex. 
Nonetheless, the tutorial cannot illustrate all available concepts and the reader is encouraged to also have a look at the example source contained in the distribution. 

-   [Chapter 2, Starting an Agent](02 Starting an Agent)  describes how to setup the Jadex environment properly and how to start a simple agent.
-   [Chapter 3, Using Plans](03 Using Plans)  explains step by step the usage of plans.
-   [Chapter 4, Using Beliefs](04 Using Beliefs)  introduces beliefs as agent knowledge form.
-   [Chapter 6, Using Goals](05 Using Goals)  shows how goals can be used to capture the agent objectives in an intuitive way.
-   [Chapter 5, Using Capabilities](06 Using Capabilities)  explains how beliefs, goals and plans can be composed into reusable agent modules.
-   [Chapter 7, Using Services](07 Using Services)  covers aspects about BDI service integration including goal delegation to other agents.
-   [Chapter 8, External Processes](08 External Processes)  explains exemplarily the integration of Jadex agents with external processes.
-   [Chapter 9, Conclusion](09 Conclusion)  finally concludes the lessons.

##Application Context

In this tutorial a simple translation agent for single words will be implemented. This agent has the basic task to handle translation requests and produce for a given term in some language the translated term in the desired target language. This base functionality will be extended in the different exercises, but it is not our goal to build up a translation agent, that combines all the extensions, because this would lead to difficulties concerning the complexity of the agent. Instead this tutorial will concentrate on setting up simple agents that explain the Jadex concepts step by step.

##How to Use This Tutorial

-   Work through the exercises in order, because later exercises require knowledge from the earlier ones.
-   Create a new package for each solution you build that is named in the same way as the exercise (e.g. a1, b1). This helps not to confuse the files of different exercises.
-   Help us to make this tutorial better with your feedback. When you find errors or have problems that are directly concerned with the exercise descriptions feel free to let us know or edit the corresponding page directly in the Wiki.
-   Whenever you encounter problems with Jadex we would be happy to help you. Please use primarily the [Jadex Forum](${URLJadexForum}) that can be used for asking questions about Jadex.


