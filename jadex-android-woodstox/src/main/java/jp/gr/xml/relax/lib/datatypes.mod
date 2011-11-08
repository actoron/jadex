<!--
DTD for RELAX Core, datatypes (Ver 1.0)
$Id: datatypes.mod,v 1.1 2001/08/02 18:29:49 kkawa Exp $
-->
<!--*******************************************************-->
<!--                                                       -->  
<!--         Parameter entities for qualified names        -->
<!--                                                       -->  
<!--*******************************************************-->

<!ENTITY % simpleType "%corePrefix;simpleType">
<!ENTITY % restriction "%corePrefix;restriction">
<!ENTITY % list "%corePrefix;list">
<!ENTITY % maxExclusive "%corePrefix;maxExclusive">
<!ENTITY % minExclusive "%corePrefix;minExclusive">
<!ENTITY % maxInclusive "%corePrefix;maxInclusive">
<!ENTITY % minInclusive "%corePrefix;minInclusive">
<!ENTITY % precision "%corePrefix;precision">
<!ENTITY % scale "%corePrefix;scale">
<!ENTITY % length "%corePrefix;length">
<!ENTITY % minLength "%corePrefix;minLength">
<!ENTITY % maxLength "%corePrefix;maxLength">
<!ENTITY % enumeration "%corePrefix;enumeration">
<!ENTITY % whiteSpace "%corePrefix;whiteSpace">
<!ENTITY % pattern "%corePrefix;pattern">


<!ENTITY % minBound "(%minInclusive; | %minExclusive;)">
<!ENTITY % maxBound "(%maxInclusive; | %maxExclusive;)">
<!ENTITY % bounds "%minBound; | %maxBound;">
<!ENTITY % numeric "%precision; | %scale;">
<!ENTITY % ordered "%bounds; | %numeric;">
<!ENTITY % unordered
   "%pattern; | %enumeration; | %whiteSpace; | %length; | 
    %maxLength; | %minLength;">
<!-- encoding is omitted -->
<!ENTITY % facet "%ordered; | %unordered;">

<!ELEMENT %simpleType; ((%annotationCore;)?, (%restriction; | %list;))>
<!ATTLIST %simpleType; name NMTOKEN #IMPLIED>

<!ELEMENT %restriction; ((%annotationCore;)?, (%facet;)*)>
<!ATTLIST %restriction; base NMTOKEN #IMPLIED>

<!ELEMENT %list; ((%annotationCore;)?)>
<!ATTLIST %list; itemType NMTOKEN #IMPLIED>

<!ELEMENT %maxExclusive; ((%annotationCore;)?)>
<!ATTLIST %maxExclusive; value CDATA #REQUIRED>
<!ATTLIST %maxExclusive; fixed (true) "true">

<!ELEMENT %minExclusive; ((%annotationCore;)?)>
<!ATTLIST %minExclusive; value CDATA #REQUIRED>
<!ATTLIST %minExclusive; fixed (true) "true">

<!ELEMENT %maxInclusive; ((%annotationCore;)?)>
<!ATTLIST %maxInclusive; value CDATA #REQUIRED>
<!ATTLIST %maxInclusive; fixed (true) "true">

<!ELEMENT %minInclusive; ((%annotationCore;)?)>
<!ATTLIST %minInclusive; value CDATA #REQUIRED>
<!ATTLIST %minInclusive; fixed (true) "true">

<!ELEMENT %precision; ((%annotationCore;)?)>
<!ATTLIST %precision; value CDATA #REQUIRED>
<!ATTLIST %precision; fixed (true) "true">

<!ELEMENT %scale; ((%annotationCore;)?)>
<!ATTLIST %scale; value CDATA #REQUIRED>
<!ATTLIST %scale; fixed (true) "true">

<!ELEMENT %length; ((%annotationCore;)?)>
<!ATTLIST %length; value CDATA #REQUIRED>
<!ATTLIST %length; fixed (true) "true">

<!ELEMENT %minLength; ((%annotationCore;)?)>
<!ATTLIST %minLength; value CDATA #REQUIRED>
<!ATTLIST %minLength; fixed (true) "true">

<!ELEMENT %maxLength; ((%annotationCore;)?)>
<!ATTLIST %maxLength; value CDATA #REQUIRED>
<!ATTLIST %maxLength; fixed (true) "true">

<!ELEMENT %enumeration; ((%annotationCore;)?)>
<!ATTLIST %enumeration; value CDATA #REQUIRED>

<!ELEMENT %whiteSpace; ((%annotationCore;)?)>
<!ATTLIST %whiteSpace; value (preserve|replace|collapse) "preserve">

<!ELEMENT %pattern; ((%annotationCore;)?)>
<!ATTLIST %pattern; value CDATA #REQUIRED>

<!-- 
<!ELEMENT encoding ((%annotationCore;)?)>
<!ATTLIST encoding fixed (true) "true">
<!ATTLIST encoding value (hex|base64) "hex">
-->

