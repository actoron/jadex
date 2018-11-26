# 1 Chapter 1 - Introduction

The Jadex Processes project aims at providing modelling and execution facilities for workflows. Main focus is on graphical forms of process representation (e.g. the Business Process Modelling Notation - BPMN) and direct execution of modelled processes (i.e. without prior code generation).

Currently, with BPMN and GPMN two workflow types are supported. Both workflow types are designed as active components and can thus be executed on the Jadex Active Components middleware. The BPMN engine is realized as an interpreter for (extended) BPMN diagrams that are produced by the Jadex BPMN editor. In order to be executable the diagrams need to be annotated with Java expressions describing the semantics of the main elements like activities or branching conditions. GPMN workflows are goal-oriented and allow the definition of processes at a higher abstraction level.

This tutorial provides step-by-step instructions for learning how to use the Jadex process infrastructure and BPMN modelling features. You will learn how to install and use the process infrastructure (i.e. the Jadex platform) for executing modelled processes and how to install and use the extended eclipse BPMN modeller tool. In particular, the following topics are covered in the upcoming chapters:

-   [Installation](../02 Installation) describes the steps necessary to install and run the Jadex process engine and modelling tools.
-   [Basic Processes](../03 Basic Processes) illustrates how to use the editor and platform to create and execute simple processes.
-   [Data and Parameters](../04 Data and Parameters) covers how data can be accessed from and passed between process activities.
-   [Events and Messages](../05 Events and Messages) shows how to react to and issue events as well as send and receive messages.
-   [Custom Functionality](../06 Custom Functionality) describes the various ways to extend Jadex BPMN with custom functionality.

