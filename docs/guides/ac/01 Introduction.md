# Introduction

**Outdated Documentation**: This page is yet to be updated to the latest Jadex version. The documentation is still valid and the explanations still apply. Yet, due to some API changes, not all code examples in this document may be used as such. When in doubt, check the example sources in the *applications* modules available on GitHub, e.g. for [Micro](https://github.com/actoron/jadex/tree/master/applications/micro/src/main/java/jadex/micro) and [BDI](https://github.com/actoron/jadex/tree/master/applications/bdiv3/src/main/java/jadex/bdiv3) agents.

<!-- TODO: Diesen guide am besten rausschmeißen und alles wichtige schon in den ersten Kapiteln erläutern. -->

The Active Components project aims at providing programming and execution facilities for distributed and concurrent systems. The general idea is to consider systems to be composed of components acting as service providers and consumers. Hence, it is very similar to the Service Component Architecture (SCA) approach and extends it in the direction of agents. In contrast to SCA, components are always active entities, i.e. they posses autonomy with respect to what they do and when they perform actions making them akin to agents. In contrast to agents communication is preferably done using service invocations.

This user guide provides a detailed description of the available functionalities of Jadex Active Components. In particular, the following topics are covered in the upcoming chapters:

- [Chapter 02 Active Components](02%20Active%20Components.md)  describes the underlying conceptual foundations.
- [Chapter 03 Asynchronous Programming](03%20Asynchronous%20Programming.md)  gives an introduction to asynchronous methods using futures.
- [Chapter 04 Component Specification](04%20Component%20Specification.md)  illustrates how services can be found and invoked.
- [Chapter 05 Services](05%20Services.md)  explains how service invocations work.
- [Chapter 06 Web Service Integration](06%20Web%20Service%20Integration.md)  describes how web services can be offered and used. 
- [Chapter 07 Platform Awareness](07%20Platform%20Awareness.md)  explains how platforms can automatically find each other.
- [Chapter 08 Security](08%20Security.md)  introduces the main security concepts of Jadex.
