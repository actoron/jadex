Chapter A1 - Conversation Center
=============================================

![A1 Conversation Center@convcenter\_ov.png](convcenter_ov.png)

The conversation center is a tool that can be used to send messages to specific components. This can e.g. be used to test if a component reacts as desired on reception of a message. The tool offers a message composition panel (cf. screenshot above on the right), in which the different parameters of a message can be set. Currently, the tool supports the definition of [FIPA](http://www.fipa.org/)  messages only (FIPA is a standard for agent communication). The properties of a message are the following:

-   **Performative:** The speech act of the message. It describes the intention of the message. Examples are inform to send information, cfp to initiate a call for proposal etc.
-   **Sender:** The component identifier of the sender. Per default the platform is set as sender of the message.
-   **Receivers:** The list of receivers as component identifiers. Using the '...' button a receiver can be selected from the known components.
-   **Reply to:** The reply to field can be used to set an alternative component identifier that should be used by the receiver to send a message reply.
-   **Protocol:** The protocol this message belongs to. Examples include fipa-request and fipa-subscribe.
-   **Conversation id:** A conversation id should be set to a unique id and is used to find messages belonging to the same conversation instance.
-   **Reply with:** Similar to a conversation id but used only in request-response scheme. This field can be set by the sender of a message.
-   **In reply to:** In this field a receiver of a message with a non-empty reply with field is expected to copy the value of that field. The original sender can check if the value fits to its value in the reply with field.
-   **Reply by:** The deadline until which a response is expected at latest.
-   **Language:** The language that should be used to encode the messge. In Jadex predefined languages are jadex-xml and jadex-binary.
-   **Encoding:** The encoding type of the message.
-   **Ontology:** The ontology name of the message.
-   **Content:** The content of the message as string representation.

It has to be noted that most of the parameters are optional except the receiver(s) of a message. Using the Send button the message will be delivered to the actual receivers. Using the Clear button all fields will be emptied.

In the left area two lists of messages are presented. The first one called Sent Messages contains messages that have been sent from the conversation center to other parties. The sencond list called Received messages contains all messages that have been sent from other to the current platform. To inspect a received message further it can be double clicked in the list. This will open a new tab in the right area in which the parameter values of the received message are displayed. The tab will offer a Close and a Reply button in the tab. The reply button will automatically create an editable message as reply to the received one, i.e. the corresponding field values will be copied/exchanged.\
After having altered the paramter values as desired the reply message can be sent back to the original sender using the Send button. 
