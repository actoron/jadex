package jp.gr.xml.relax.sax;

/**
 * RELAXEntityResolver
 *
 * @since   Nov. 23, 2000
 * @version May. 28, 2001
 * @author  ASAMI, Tomoharu (asami@zeomtech.com)
 */
public class RELAXEntityResolver extends SimpleEntityResolver {
    public RELAXEntityResolver() {
        String coreUri =
            getClass()
                .getResource("/jp/gr/xml/relax/lib/relaxCore.dtd")
                .toExternalForm();
        String nsUri =
            getClass()
                .getResource("/jp/gr/xml/relax/lib/relaxNamespace.dtd")
                .toExternalForm();
        String grammarUri =
            getClass()
                .getResource("/jp/gr/xml/relax/lib/relax.dtd")
                .toExternalForm();

        addSystemId("http://www.xml.gr.jp/relax/core1/relaxCore.dtd", coreUri);
        addSystemId("relaxCore.dtd", coreUri);
        addSystemId("relaxNamespace.dtd", nsUri);
        addSystemId("relax.dtd", grammarUri);
        addPublicId("-//RELAX//DTD RELAX Core 1.0//JA", coreUri);
        addPublicId("-//RELAX//DTD RELAX Namespace 1.0//JA", nsUri); // XXX
        addPublicId("-//RELAX//DTD RELAX Grammar 1.0//JA", grammarUri); // XXX
    }
}
