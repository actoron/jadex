# Events

An important property of agents is the ability to react timely to different kinds of events. Jadex supports two kinds of application-level events, which can be defined by the developer in the ADF. Internal events can be used to denote an occurrence inside an agent, while message events represent a communication between two or more agents. Events are usually handled by plans. For example the ping plan gets triggered when a ping request message arrives. When an event occurs in Jadex and no plan is found to handle this event a warning message is generated, which can be printed to the console depending on the logging settings (see [Properties](12%20Properties.md)).

![](jadexeventsadf.png)

*Figure 1: The Jadex events XML schema part*

Two kinds of events are supported in �Jadex: message events and internal events, as well as references to these types, as shown in Figure 1. Generally, all event types are parameter elements meaning that any number of parameter(set)s can be specified for a user-defined kind of event. A parameter itself can be used for passing values from the source to the consumer of the event. Unlike goals, events are single points in time and therefore only support "in" parameters, which denote the "source to consumer"-direction of value passing. In addition all kinds of events share the common attributes: "posttoall" and "randomselection". The "posttoall" flag determines if the event should be dispatched to a single receiver or to all applicable plans. �The "randomselection" flag can be used to turn off the importance of the declaration order of plans for the plan selection process. Nevertheless, the priority of a plan is still respected.

| Flags         | Default Value |
|---------------|---------------|
|posttoall      | internal event=true, otherwise false|
|randomselection| false         |

*Event Flags*

At runtime, e.g., when accessed from plans, instances of the elements are represented by the *jadex.bdi.runtime.IMessageEvent* and *jadex.bdi.runtime.IInternalEvent* interfaces. Both interfaces inherit from the common super interface *jadex.bdi.runtime.IEvent*. The following sections will describe these different types of events in more detail.

## Internal Events

Internal events are the typical way in Jadex for explicit one-way communication of an interesting occurrence inside the agent. The usage of internal events is characterized �by the fact that an information should be passed from a source to some consumers (similar to the object oriented observer pattern). Hence, if an internal event occurs within the system, e.g., because some plan dispatches one, it is distributed to all plans that have declared their interest in this kind of event by using a corresponding trigger or by waiting for this kind of internal event. The internal event can transport arbitrary information to the consumers if custom parameter(set)s are defined in the type for that purpose. A typical use case for resorting to internal events is, e.g., updating a GUI.
 �

```xml
...
<events>
��<internalevent name="gui-update">
����<parameter name="content" class="String"/>
��</internalevent>
</events>
...
```

*Example of the declaration of an internal event*�

```java
...
public void body()
{
��String update_info;
��...
 �//�"gui_update" internal event type must be defined in the ADF
��IInternalEvent event = createInternalEvent("gui_update");
��// Setting the content parameter to the update info
��event.getParameter("content").setValue(update_info);
��dispatchInternalEvent(event);
��...
}
```

*Plan snippet showing the creation and dispatching of the internal event*

## Message Events

All message types an agent wants to send or receive need to be specified within the ADF. The message event (class *jadex.bdi.runtime.IMessageEvent* denotes the arrival or sending of a message. �Note, that only incoming messages are handled by the event dispatching mechanism, while outgoing messages are just sent. The native underlying message object (which is platform dependent) can be retrieved using the *getMessage()* method. In addition, the message type, which may be FIPA or some other message format, can be retrieved using the *getMessageType()* method. The message type (subclass of *jadex.bridge.MessageType*) is meant to represent the way a message is composed, i.e. it defines which parameter and parametersets exists and (partially) what their meaning is. Hence, the message type can e.g. be used to determine which parameter(set) contains the receivers of the message. The message type is comparable to metainformation of a specific kind of message such as FIPA. It represents an extension point is Jadex and allows for introducing new message types without having to modify underlying message passing infrastructure. An agent developer normally can ignore the message type and use the default FIPA for all messages.��

The message passing mechanism is based on using *jadex.bridge.IComponentIdentifier*s for the unambiguous identification of agents. A component identifier hence contains an agents globally unique name which consists of a local part followed by the "@" character and the platforms name (schema: &lt;agentname>@&lt;platformname>, example: ams@lars). In addition to the name a component identifier can contain additional information. On the one hand arbitrary many transport addresses might be present. These addresses can be used to contact the agent from a remote platform and normally represent the address of platform wide transport mechanisms (schema: &lt;transportname>:*&lt;address>, example: [http://www.fipa.org/specs/fipa00023/SC00023J.html](http://www.fipa.org/specs/fipa00023/SC00023J.html).
A component identifier of another agent can be obtained in two ways. If all details about the agent are known an agent identifier can directly be created using a local or global name by using the *IComponentManagementService*. Please note that it is not allowed to create a component identifier via a simple constructor call, because the concrete implementation may vary in different platform infrastrutures. The needed creation code is illustrated in the code snippet below. If the details of an agent are not known in advance, an agent may search for other agents using either the CMS listing all agents on a platform or the DF, which allows to search for agents providing a given service. Searching CMS and DF is explained in detail in [Predefined Capabilities](16%20Predefined%20Capabilities.md).

```java
IComponentManagementService ces= (IComponentManagementService)container.getService(IComponentManagementService.class);
IComponentIdentifier cid = ces.createComponentIdentifier("Heinz", true, null));
```

*Creation of a component identifier*

Templates for message events are defined in the ADF in the &lt;events> section. The direction attribute can be used to declare whether the agent wants to receive, send or do both (default) for a given event. Possible values for that attribute are "send", "receive" and "send_receive" respectively. The type of the message constrains the available parameters of a message. Currently, the only available type is "fipa" which automatically creates parameter(set)s according to the FIPA message specification (e.g., parameters for the receivers, content, sender, etc. are introduced). Through this message typing Jadex does not require that only FIPA messages are being sent, as other options may be added in future. In the following table, all available parameter(set)s are itemized. For details about the meaning of the FIPA parameters, see the FIPA specifications available at [FIPA ACL Message Structure Specification](http://www.fipa.org/specs/fipa00061/SC00061G.html). The meanings of all of these parameters are explained in the following subsections.

| Name| Class| Meaning |
|-----|------|---------|
|performative| String| Speech act of the message that expresses the senders intention towards the message content. You can use the constants from jadex.base.fipa.SFipa.{ACCEPT_PROPOSAL, AGREE, ...}|
|sender| IComponentIdentifier| The senders agent identifier, which contains besides other things its globally unique name.|
|reply_to| IComponentIdentifier| The agent identifier of the agent to which should be replied.|
|receivers `[set]`| IComponentIdentifier| Arbitrary many (at least one) agent identifier of the intended receiver agents.|
|content| Object| The content (string or object) of the message. If the content is an object it has to be specified how the content can be marshalled for |transmission. For this puropose codecs are used. Jadex has built in support for marshalling arbitrary Java beans via setting the language of the message to *jadex.base.fipa.SFipa.JADEX_XML*.|
|language| String| The language in which the content of the message should be encoded.|
|encoding| String| The encoding of the message.|
||ontology| String| The ontology that can be used for understanding the message content. Can also be used for deciding how to marshal the content.|
|protocol| String| The interaction protocol of the the message if it belongs to a conversation. There are constants available for the predefined FIPA interaction protocols in *jadex.base.fipa.SFipa.PROTOCOL_{REQUEST, QUERY, ...}*|
|reply_with| String| Reply_with is used for assigning a reply to a original message. The receiver of the message should respond to this message by putting the reply_with value in the in_reply_to field of the answer. Unique ids can e.g. be generated via the method SFipa.createUniqueId().|
|in_reply_to| String| Used in reply messages and should contain the reply_with content of the answered message.|
|conversation_id| String| The conversation_id is used in interactions for identifying messages that belong to a specific conversation. All messages of one interaction should share the same conversation_id. Unique ids can e.g. be generated via the method SFipa.createUniqueId().|
|reply_by| Date| The reply_by field can contain the latest time for a response message.|

*Reserved FIPA message event parameters*

## Receiving Messages

Typically in the ADF of an agent a number of message event types for sending and receiving message events are declared for the application domain. Examples for such user-defined message event types might be "inform_time", "request_vision", etc. As those message types are defined for each agent separately there are consequently no global message types. So how does an agent know the message type of a newly received message? For this purpose a simple matching process is used. This means that all locally known message types of an agent and its subcapabilities (with direction "receive" or "send_receive") are matched against the newly received message and the best fitting is selected. For the matching process the parameter values of a message type are checked against the values in the received message. For this purpose only parameters with direction="fixed" are considered important, as they represent fixed type information. In addition to fixed parameter values, message matching can be fine-tuned by using a match expression that can be specified for each message event. As shown in the second example below, in the match expression the parameters of a message can be accessed by prepending a "$" before the parameter name.
Additionally, it is not allowed having variable names in Java that contain a "-" character as this is interpreted as minus. Therefore, in all parameter(set)s names the "-" characters have been replaced by a "_" character. This means you need to write e.g. "$reply_with" instead of "$reply-with".
A message event type matches an incoming message if all fixed parameter values are the same in the received message and the match expression evaluates to true.

```xml
<imports>
��<import>jadex.base.fipa.SFipa</import>
</imports>
...
<events>
��<!--�A query-ref message with content "ping"�-->
��<messageevent name="query-ping" type="fipa" direction="receive">
����<parameter name="performative" class="String" direction="fixed">
������<value>SFipa.QUERY_REF</value>
����</parameter>
����<parameter name="content" class="String" direction="fixed">
������<value>"ping"</value>
����</parameter>
��</messageevent>
 ���
��<!--�An inform message where content contains the word "hello"�-->
��<messageevent name="inform_hello" type="fipa" direction="receive">
����<parameter name="performative" class="String" direction="fixed">
������<value>SFipa.INFORM</value>
����</parameter>
����<match>((String)$content).indexOf("hello") != -1</match>
��</messageevent>
</events>
```

*Examples for receiving messages*

There are several reasons why an agent may fail to correctly process an incoming message. These are indicated by different logging outputs at different logging levels:

|Level  | Output |
|-------|--------|
|INFO   | &lt;agentname> multiple events matching message, using message event with highest specialization degree|
|WARNING| &lt;agentname> cannot process message, no message event matches         |
|SEVERE | &lt;agentname> cannot decide which event matches message, using first   |

*Possible problems when matching messages*

In the first case, if more than one message event type has a match with the incoming event the most specific match will be used. The number of fixed parameters and the presence of a match expression are used as indicator for the specificity. As this is a common case, it is only logged at level *INFO*. When a message is received, which does not match any of the declared message events of the agent, a *WARNING* is generated, indicating that this message is ignored by the agent. Finally, when there are two or more message events, which all match an incoming message to the same degree (e.g., all have the same number of fixed parameters) the system cannot decide which message event to use, and has to choose one arbitrarily. As this probably indicates a programming error in the ADF, a *SEVERE* ouput is produced.

## Sending Messages

Messages to be sent also have to be declared in the ADF. The actual sending is usually done inside a plan, which instantiates the declared message event, fills in desired parameter values, and dispatches the message using one of the ```sendMessage...()``` methods. The super class of both plan types (*jadex.bdi.runtime.AbstractPlan*) provides several convenience methods to create message events. To send a message, a message event has to be created using the *createMessageEvent(Strint type)* method supplying the declared message event type name as parameter. The receivers of fipa messages are specified by agent identifiers (interface *jadex.bdige.IComponentIdentifier*). The message content can be supplied as String or as Object with *getParameter(SFipa.CONTENT).setValue(Object content)*. If the content is provided as Object it must be ensured that the agent can encode it into a transmissable representation as described in the following section about content languages.
�
To actually send the message event it is sufficient to call the *sendMessage(IMessageEvent me)* method with the prepared message event as parameter. It is also possible to send a message and directly wait for a reply with an optional timeout by using the `sendMessageAndWait(IMessageEvent me [, timeout])` method. This is described in the following XML:

```xml
<imports>
��<import>jadex.base.fipa.SFipa</import>
</imports>
...
<events>
��<!--�A query-ref message with content "ping"�-->
��<messageevent name="query-ping" type="fipa" direction="send">
����<parameter name="performative" class="String">
������<value>SFipa.QUERY-REF</value>
����</parameter>
����<parameter name="content" class="String">
������<value>"ping"</value>
����</parameter>
��</messageevent>
</events>
```

*Example of declaration for a message*

```java
public void body()
{
��IMessageEvent me = createMessageEvent("query_ref");
��me.getParameterSet(SFipa.RECEIVERS).addValue(cid);
  // Set/change content if necessary
 �me.getParameter(SFipa.CONTENT).setValue("ping 2");�
��sendMessage(me);
}
```

*Plan snippet showing the creation and sending of the message*

## Using Ontologies and Content Languages

Message based communication allows that agents can communicate even when they are distributed across the network. One important property in the context of message based communication is the separaration of address spaces, i.e., that agents do not have direct access to the data inside other agents. Therefore data needs to be encoded into a message before sending and decoded from a message after receival. In the context of multi-agent systems, so called content languages and ontologies are responsible for describing how data should be encoded into messages. A content language defines the syntactical mechanism used to represent data and an ontology specifies the meaning of the concepts used in the message. Together, content language and ontology assure a shared common understanding among agents.

The data inside a Jadex agent is usually represented as a collection of Java objects referencing each other. The Jadex framework provides some useful features that allow to encode/decode object structures, such that they can be used directly for the communication between agents. For this purpose, the agent knows about so called *content codecs*, some of which are available by default, but can also be extended with custom codecs by the agent programmer. These codecs are selected automatically, when sending and receiving messages and are used to encode or decode the content of a message. From the viewpoint of an agent programmer, the agent is just sending or receiving messages containing Java objects. All the encoding and decoding works behind the scenes.

Two simple examples for sending and receiving a Java object inside a message are shown below (taken from the marsworld classic example). These examples use a *Target* object from package *jadex.bdi.examples.marsworld_classic*. On the sender side, the message defines to use the language *SFipa.JADEX_XML*, which is per default available in each agent.
The corresponding codec can handle arbitrary Java Beans (i.e. Java objects, which provide public getter and setter methods for their properties). For detailed information about JavaBean you should have a look at the [JavaBeans Specification](http://java.sun.com/products/javabeans/docs/spec.html).

```xml
<!--�Message declaration in the ADF -->
<messageevent name="inform_target" type="fipa" direction="send">
��<parameter name="performative" class="String" direction="fixed">
����<value>SFipa.INFORM</value>
��</parameter>
��<parameter name="language" class="String" direction="fixed">
����<value>SFipa.JADEX_XML</value>
��</parameter>
��<parameter name="ontology" class="String" direction="fixed">
����<value>MarsOntology.ONTOLOGY_NAME</value>
��</parameter>
</messageevent>
```

*Message template sending declaration*

```java
public void body()
{
 �//�Message sending in the plan.
��IComponentIdentifier receiver = ...
��Target target = ...
��IMessageEvent me = createMessageEvent("inform_target");
��me.getParameterSet(SFipa.RECEIVERS).addValue(receiver);
  // The Java object is directly used as content.
��me.getParameter(SFipa.CONTENT).setValue(target);�
��sendMessage(me);
}
```

*Example of sending an object inside a message*

As the decoded object is already availble for matching an incoming message, on the receiver side, the match expression can be used to only match messages containing a *Target* object.

```xml
<!--�Message declaration in the ADF�-->
<messageevent name="target_inform" type="fipa" direction="receive">
��<parameter name="performative" class="String" direction="fixed">
����<value>SFipa.INFORM</value>
��</parameter>
��<parameter name="ontology" class="String" direction="fixed">
����<value>MarsOntology.ONTOLOGY_NAME</value>
��</parameter>
��<match>$content instanceof Target</value>
</messageevent>
```

*Message template receiving declaration*

```java
public void body()
{
 �//�Message receiving in the plan.
��IMessageEvent msg = (IMessageEvent)getReason();
��Target target = (Target)msg.getParameter(SFipa.CONTENT).getValue();
��...
}
```

*Example of receiving an object inside a message**

Three content languages are predefined in Jadex itself and therefore are available on all platforms. These languages are defined in the constants *SFipa.JAVA_XML* and *SFipa.NUGGETS_XML* and *SFipa.JADEX_XML*. All three are Java bean converters. The Java XML language uses the bean encoder available in the JDK, to convert Java objects adhering to the JavaBeans specification to standardized XML files. The nuggets XML language is a proprietary language in Jadex, that works similar to the Java XML language but the encoding and decoding is much faster. Finally, the third alternative is also a Jadex variant, which is part of the Jadex XML databinding framework and is meant to replace nuggets in the long term. All languages allow marshalling content objects independently from the underlying ontology as they rely completely on the Java Bean specification. Using these languages requires that Java bean information about the content object can be found or inferred by the Java bean introspector.
Please have a look at the Beanynizer tool (available from the [Jadex Homepage](http://vsis-www.informatik.uni-hamburg.de/projects/jadex/)) if you are interested in converting an ontology to Java beans including the necessary bean infos.
Other content languages are available depending on the underlying platform (e.g. the JADE platform supports the FIPA SL language). The usage of these platform-specific languages is specified as property:

```xml
<properties>
��<property name="contentcodec.my-codec"<new MyContentCodec()</property>
</properties>
```

If you want to use your own mechanism for encoding and decoding of message contents, you can implement the interface *IContentCodec* from package *jadex.bridge*. The interface requires you to implement three methods. The *match()* method is used by Jadex, to determine if your codec applies to a given message. For this decision, the important message properties (e.g. langauge and ontology) are supplied. The other two methods are called to *encode()* an object to a string for sending and to *decode()* a string back to an object, when receiving a message. To register a custom content codec in an agent, it is sufficient to add a property starting with *contentcodec.* in the properties section of an agent:

*Include a custom content codec*

## Using Conversations for Managing Sequences of Messages

Normally messages are not sent in isolation, but occur inside a conversation of many messages that are sent and received. Because of this, you often want to identify a certain message as belonging to a specific conversation or being a direct reply to some other message sent before. In the FIPA message structure, three parameters are responsible for this kind of conversation management. A unique *conversation_id* can be used to group together several messages belonging to a single conversation In addition the *in_repy_to* parameter allows to identify a message as being an answer to a previous message with a corresponding *reply_with* parameter value.

In Jadex, the relation between messages is used to achieve two things: First, it allows to wait for a specific message while ignoring other messages that do not belong to an ongoing conversation or are a reply to another message. Thanks to this, e.g., when two plans simultaneously wait for the same type of message, a received message will automatically be posted to the correct plan, from which the previous message of the conversation was sent.

Second, it allows to restrict message receival to a certain capability, namely the capability from which an earlier message was sent. This means, e.g., that if an agent defines two similar message events in two different capabilities (as is commonly the case, when the same capability is included twice in an agent), the message will automatically be routed to the correct capability where the corresponding conversation originated.

In both cases, the mechanism is based on the usage of the *conversation_id* and/or *in_repy_to* and *reply_with* parameters. The developer has to make sure that, when sending an initial message a useful value has been set to one or more of these parameters. When replying to an initial message (by using *msg.createReply(...)*), the parameter values are set automatically, based on the values of the initial message (i.e. the conversation-id is retained while the reply-with is copied to the in-reply-to parameter). The setting of initial parameter values can directly be done in the message declaration as shown in following example. In the example, the method *createUniqueId()* is used to generate a unique id for the conversation, whenever an instance of the message is created. The plan can send the message using *dispatchMessageAndWait()* and directly receive the correponding reply message. When using a timout in *dispatchMessageAndWait()* and the message is not received before the timeout has elapsed, a *jadex.runtime.TimeoutException* is thrown (see also [Plans](08%20Plans.md)). For a reply message (e.g. the inform below) no special settings have to defined in the ADF.
 ��

```xml
<events>
��<messageevent name="request" type="fipa" direction="send">
����<parameter name="performative" class="String">
������<value>SFipa.REQUEST</value>
����</parameter>
����<parameter name="conversation-id" class="String">
������<value>SFipa.createUniqueId($scope.getAgentName())</value>
����</parameter>
��</messageevent>
��<messageevent name="inform" type="fipa" direction="receive">
����<parameter name="performative" class="String" direction="fixed">
������<value>SFipa.INFORM</value>
����</parameter>
��</messageevent>
</events>
```

*Message declarations*

```java
public void body()
{
��IMessageEvent me = createMessageEvent("request");
��//Set other parameters as desired��
��...


��IMessageEvent reply = sendMessageAndWait(me);
��//Handle reply message��
��...�
}
```

*Example of an initial conversation message*

On the other hand, if you have received a message event and want to reply to the sender you don't have to create a new message event from scratch but can directly create a reply. This ensures that all important information such as the conversation-id or in-reply-to also appears in the answer. Moreover, message properties, which should not change during a conversation (e.g. protocol, language and ontology) are also automatically copied into the reply. A reply can be created by calling `createReply(String type [, Object content])` method directly on the received message event. This method takes the message event type for the reply as parameter. Note that the message type with which you are replying also has to be present in your ADF as shown in the following example.
Example for Replying to a Message:

```xml
<events>
��<messageevent name="request" type="fipa" direction="receive">
����<parameter name="performative" class="String" direction="fixed">
������<value>SFipa.REQUEST</value>
����</parameter>
��</messageevent>
��<messageevent name="inform" type="fipa" direction="send">
����<parameter name="performative" class="String">
������<value>SFipa.INFORM</value>
����</parameter>
��</messageevent>
<events>
```

*Message declarations*

```java
public void body()
{
 �//�Message receiving in the plan.
��IMessageEvent msg = (IMessageEvent)getInitialEvent();
��Object content = ...�// Prepare content for reply
��IMessageEvent reply = msg.createReply("inform", content);
��sendMessage(reply); //�Take care to send 'reply' and not 'msg'!
}
```

*Example code for creating and sending a reply message**

The way of handling conversations described in this section is pretty different to programming agents based on abstract goals, as the programmer has to directly deal with all alternatives of the interaction flow. This process can be tedious and error-prone. Therefore, in Jadex a predefined capability is available, that already implements common use cases of interactions as specified in standardized FIPA interaction protocols (e.g. request, contract-net, auctions). The protocols capability allows to focus on the goals of the agents participating in a conversation. The protocols capability is described in detail in [Predefined Capabilities](16%20Predefined%20Capabilities.md). Even if you want to implement your own custom interaction protocol, you should have a look at the protocols capability, because it introduces helpful patterns that can be applied to other interactions as well.
��
