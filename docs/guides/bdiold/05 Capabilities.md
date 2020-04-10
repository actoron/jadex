# Capabilities

The term capability is used for different purposes in the agent community. In the context of Jadex, the term is used to denote an encapsulated agent module composed of beliefs, goals, and plans. The concept of an agent module (and the usage of the term capability) was proposed by [Busetta et al. 99] and first implemented in JACK Agents [Winikoff 05]. Capabilities allow for packaging a subset of beliefs, plans, and goals into an agent module and to reuse this module wherever needed. Capabilities can contain subcapabilities forming arbitrary hierarchies of modules. In Jadex, a revised and extended capability model has been implemented as described in [Braubach et al. 05]. In this model, the connection between a parent (outer) and a child (inner) capability is established by a uniform visibility mechanism for contained elements (see Figure 1).

![](capability.png)

*Figure 1: Capability concept*

# Capability Definition


A capability is basically the same as an agent, but without its own reasoning process. On the other hand, an agent can be seen as a collection (i.e. subcapability hierarchy) of capabilities plus a separate reasoning process shared by all its capabilities. Each agent has at least one capability (sometimes called root capability) which is given by the beliefs, goals, plans, etc. contained in the agent's XML file. To create additional capabilities for reuse in different agents, the developer has to write capability definition files. A capability definition file is similar to an agent definition file, but with the &lt;agent&gt; tag replaced by &lt;capability&gt;. The &lt;capability&gt; tag has the same substructure as the &lt;agent&gt; tag.


Note that the &lt;capability&gt; tag has *name* and *package* attributes. As there are so many similarities between agent definition files and capability definition files, we commonly use the term ADF to denote both.


```xml
<capability xmlns="http://jadex.sourceforge.net/jadex-bdi"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://jadex.sourceforge.net/jadex-bdi
                           http://jadex.sourceforge.net/jadex-bdi-2.0.xsd"
  name="MyCapability" package="mypackage">
    
  <beliefs> ... </beliefs>
  <goals> ... </goals>
  <plans> ... </plans>
  ...
</capability>

```


*Figure 2: Capability XML file header*

# Using Capabilities
 
Agents and capabilities may be composed of any number of subcapabilities which are referenced in a &lt;capabilities&gt; tag. To reference a capability, a local name and the location of the capability definition has to be supplied in the file attribute as absolute or relative file name or capability type name. Type names are resolved using the package and import declarations, and can therefore be unqualified or fully qualified. Capabilities from the jadex.bdi.planlib package, such as the DF capability, which have platform-specific implementations, must always be referenced using a fully qualified type name.   
 

```xml
<agent ...>
  <capabilities>
    <!-- Referencing a capability using a filename. -->
    <capability name="mysubcap" file="mypackage/MyCapability.capability.xml"/>
        
    <!-- Referencing a capability using a fully qualified type name. -->
    <capability name="dfcap" file="jadex.planlib.DF"/>
    ...
  </capabilities>
  ...
</agent>

```
 

# Elements of a Capability

The capability introduces a scoping of the BDI concepts. By default all beliefs, goals, and plans have local scope (i.e., are not exported), that is they can only be used in the capability where they have been defined. This restriction can be relaxed by declaring elements as exported or abstract for making them accessible from the outer capability (cf. Figure 1). In the outer capability such elements can be used when an explicit reference (with its own possibly different name) to those elements is established. In Figure 2 this reference mechanism, which applies to all elements in the same manner, is exemplarily depicted for beliefs. In the following the possible use cases are described.&lt;br/&gt;

![](jadexreferencesadf.png)

*Figure 2: The Jadex references XML schema elements (using beliefs as example)*



# Making an Element Accessible for the Outer Capability


For this purpose the element must declare itself as exported (using the exported="true" attribute) in the inner capability. In the outer capability, a reference (e.g., &lt;beliefref&gt;) has to be declared, which directly references the original element (using dot notation "capname.belname") within the concrete tag. An example for an exported belief is shown below.
   

** Inner Capability A **

```xml
<belief name="myexportedbelief" exported="true" class="MyFact"/>

```


** Outer Capability B includes A under the name mysubcap **

```xml

<beliefref name="mysubbelief">
    <concrete ref="mysubcap.myexportedbelief"/>
</beliefref>

```
   

# Defining an Abstract Element

This means the element itself provides no implementation and needs to be assigned from an outer capability. For this purpose an abstract element reference (e.g., &lt;beliefref&gt;) has to be declared. An outer capability can provide an implementation for this abstract element by defining a concrete element (or another reference) and assigning it to the abstract reference (using the &lt;assignto&gt; tag). In addition, the abstract element can be declared as optional (using the optional="true" attribute of the abstract tag) requiring no outer element assignment. At runtime, such unassigned abstract elements are not accessible, and trying to use them will result in runtime exceptions. For some of the elements (e.g., beliefs) it can be tested at runtime with the *isAccessible()* method from within plans, if a reference is connected.\
   

** Inner Capability A **

```xml

<beliefref name="myabstractbelief" exported="true">
  <abstract/>
</beliefref>

```

** Outer Capability B includes A under the name mysubcap **

```xml

<belief name="mybelief" class="MyFact">
  <assignto ref="mysubcap.myabstractbelief"/>
</belief>

```
