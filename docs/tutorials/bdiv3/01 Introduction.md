${SorryOutdatedv3}

# Introduction
The Jadex BDI V3 kernel is a Belief-Desire-Intention reasoning engine for intelligent agents. 

This tutorial is a good starting point for agent developers that want to learn programming Jadex BDI agents in small hands-on exercises. 
Each lesson of this tutorial covers one important concept and tries to illustrate why and especially how the concept can be used in Jadex. 
Nonetheless, the tutorial cannot illustrate all available concepts and the reader is encouraged to also have a look at the example source contained in the distribution or take a look into the [BDIV3 User Guide](../../guides/bdiv3/01%20Introduction.md)).

Contents of this tutorial:

-   [Chapter 2, Your first BDI Agent](02%20Your%20first%20BDI%20Agent.md)  describes how to setup the Jadex environment properly and how to start a simple agent.
-   [Chapter 3, Using Plans](03%20Using%20Plans.md)  explains step by step the usage of plans.
-   [Chapter 4, Using Beliefs](04%20Using%20Beliefs.md)  introduces beliefs as agent knowledge form.
-   [Chapter 5, Using Goals](05%20Using%20Goals.md)  shows how goals can be used to capture the agent objectives in an intuitive way.
-   [Chapter 6, Using Capabilities](06%20Using%20Capabilities.md)  explains how beliefs, goals and plans can be composed into reusable agent modules.
-   [Chapter 7, Using Services](07%20Using%20Services.md)  covers aspects about BDI service integration including goal delegation to other agents.
-   [Chapter 8, External Processes](08%20External%20Processes.md)  explains exemplarily the integration of Jadex agents with external processes.
-   [Chapter 9, Conclusion](09%20Conclusion.md)  finally concludes the lessons.

##Application Context

In this tutorial a simple translation agent for single words will be implemented. This agent has the basic task to handle translation requests and produce for a given term in some language the translated term in the desired target language. This base functionality will be extended in the different exercises, but it is not our goal to build up a translation agent, that combines all the extensions, because this would lead to difficulties concerning the complexity of the agent. Instead this tutorial will concentrate on setting up simple agents that explain the Jadex concepts step by step.

##How to Use This Tutorial

-   Work through the exercises in order, because later exercises require knowledge from the earlier ones.
-   Create a new package for each solution you build that is named in the same way as the exercise (e.g. a1, b1). This helps not to confuse the files of different exercises.
-   Help us to make this tutorial better with your feedback. When you find errors or have problems that are directly concerned with the exercise descriptions feel free to let us know.
-   Whenever you encounter problems with Jadex we would be happy to help you. Please use primarily the [Jadex Forum](https://sourceforge.net/p/jadex/discussion/274112/) that can be used for asking questions about Jadex.