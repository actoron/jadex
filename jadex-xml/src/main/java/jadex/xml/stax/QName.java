package jadex.xml.stax;

public class QName implements java.io.Serializable {

    private String namespaceURI;
    private String localPart;

    private String prefix;


    public QName(String localPart) {
        this("", localPart);
    }


    public QName(String namespaceURI, String localPart) {
        this(namespaceURI, localPart, "");
    }

    public QName(String namespaceURI, String localPart, String prefix) {
        if (localPart == null)
            throw
                new IllegalArgumentException("Local part not allowed to be null");

        if (namespaceURI == null)
          namespaceURI = "";

        if (prefix == null)
          prefix = "";

        this.namespaceURI = namespaceURI;
        this.localPart = localPart;
        this.prefix = prefix;
    }

    public String getNamespaceURI() {
        return namespaceURI;
    }

    public String getLocalPart() {
        return localPart;
    }

    public String getPrefix() {
        return prefix;
    }

    public String toString() {
        if (namespaceURI.equals("")) {
            return localPart;
        }
        else {
            return "{" + namespaceURI + "}" + localPart;
        }
    }

    public static QName valueOf(String s) {
        if (s == null || s.equals("")) {
            throw new IllegalArgumentException("invalid QName literal");
        }

        if (s.charAt(0) == '{') {
            // qualified name
            int i = s.indexOf('}');
            if (i == -1) {
                throw new IllegalArgumentException("invalid QName literal");
            }
            if (i == s.length() - 1) {
                throw new IllegalArgumentException("invalid QName literal");
            }
            return new QName(s.substring(1, i), s.substring(i + 1));
        }
        else {
            return new QName(s);
        }
    }

    public final int hashCode() {
        return namespaceURI.hashCode() ^ localPart.hashCode();
    }

    public final boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (!(obj instanceof QName)) {
            return false;
        }

        QName qname = (QName) obj;

        return this.localPart.equals(qname.localPart)
            && this.namespaceURI.equals(qname.namespaceURI);
    }
}
