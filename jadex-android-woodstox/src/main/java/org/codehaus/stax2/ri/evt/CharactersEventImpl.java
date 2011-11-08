package org.codehaus.stax2.ri.evt;

import java.io.IOException;
import java.io.Writer;

import javaxx.xml.stream.*;
import javaxx.xml.stream.events.Characters;

import org.codehaus.stax2.XMLStreamWriter2;

public class CharactersEventImpl
    extends BaseEventImpl
    implements Characters
{
    final String mContent;

    final boolean mIsCData;
    final boolean mIgnorableWS;

    boolean mWhitespaceChecked = false;
    boolean mIsWhitespace = false;

    /**
     * Constructor for regular unspecified (but non-CDATA) characters
     * event type, which may or may not be all whitespace, but is not
     * specified as ignorable white space.
     */
    public CharactersEventImpl(Location loc, String content, boolean cdata)
    {
        super(loc);
        mContent = content;
        mIsCData = cdata;
        mIgnorableWS = false;
    }

    /**
     * Constructor for creating white space characters...
     */
    private CharactersEventImpl(Location loc, String content,
                                boolean cdata, boolean allWS, boolean ignorableWS)
    {
        super(loc);
        mContent = content;
        mIsCData = cdata;
        mIsWhitespace = allWS;
        if (allWS) {
            mWhitespaceChecked = true;
            mIgnorableWS = ignorableWS;
        } else {
            mWhitespaceChecked = false;
            mIgnorableWS = false;
        }
    }

    public final static CharactersEventImpl createIgnorableWS(Location loc, String content) {
        return new CharactersEventImpl(loc, content, false, true, true);
    }

    public final static CharactersEventImpl createNonIgnorableWS(Location loc, String content) {
        return new CharactersEventImpl(loc, content, false, true, false);
    }

    /*
    /////////////////////////////////////////////////////
    // Implementation of abstract base methods, overrides
    /////////////////////////////////////////////////////
     */

    public Characters asCharacters() { // overriden to save a cast
        return this;
    }

    public int getEventType() {
        return mIsCData ? CDATA : CHARACTERS;
    }

    public boolean isCharacters() { return true; }

    public void writeAsEncodedUnicode(Writer w)
        throws XMLStreamException
    {
        try {
            if (mIsCData) {
                w.write("<![CDATA[");
                w.write(mContent);
                w.write("]]>");
            } else {
                writeEscapedXMLText(w, mContent);
            }
        } catch (IOException ie) {
            throwFromIOE(ie);
        }
    }

    public void writeUsing(XMLStreamWriter2 w) throws XMLStreamException
    {
        if (mIsCData) {
            w.writeCData(mContent);
        } else {
            w.writeCharacters(mContent);
        }
    }

    /*
    ///////////////////////////////////////////
    // Characters implementation
    ///////////////////////////////////////////
     */

    public String getData() {
        return mContent;
    }

    public boolean isCData() {
        return mIsCData;
    }

    public boolean isIgnorableWhiteSpace() {
        return mIgnorableWS;
    }

    public boolean isWhiteSpace() {
        // Better only do white space check, if it's done already...
        if (!mWhitespaceChecked) {
            mWhitespaceChecked = true;
            String str = mContent;
            int i = 0;
            int len = str.length();
            for (; i < len; ++i) {
                if (str.charAt(i) > 0x0020) {
                    break;
                }
            }
            mIsWhitespace = (i == len);
        }
        return mIsWhitespace;
    }

    /*
    ///////////////////////////////////////////
    // Additional public, but non-Stax-API methods
    ///////////////////////////////////////////
     */

    public void setWhitespaceStatus(boolean status)
    {
        mWhitespaceChecked = true;
        mIsWhitespace = status;
    }

    /*
    ///////////////////////////////////////////
    // Standard method impl
    ///////////////////////////////////////////
     */

    public boolean equals(Object o)
    {
        if (o == this) return true;
        if (o == null) return false;
        if (!(o instanceof Characters)) return false;

        Characters other = (Characters) o;
        // Obviously textual content has to match
        if (mContent.equals(other.getData())) {
            // But how about type (CDATA vs CHARACTERS)?
            // For now, let's require type match too
            return isCData() == other.isCData();
        }
        return false;
    }

    public int hashCode()
    {
        return mContent.hashCode();
    }

    /*
    ///////////////////////////////////////////
    // Internal methods
    ///////////////////////////////////////////
     */

    protected static void writeEscapedXMLText(Writer w, String text)
        throws IOException
    {
        final int len = text.length();
        
        int i = 0;
        while (i < len) {
            int start = i;
            char c = '\u0000';

            for (; i < len; ) {
                c = text.charAt(i);
                if (c == '<' || c == '&') {
                    break;
                }
                if (c == '>' && i >= 2 && text.charAt(i-1) == ']'
                    && text.charAt(i-2) == ']') {
                    break;
                }
                ++i;
            }
            int outLen = i - start;
            if (outLen > 0) {
                w.write(text, start, outLen);
            } 
            if (i < len) {
                if (c == '<') {
                    w.write("&lt;");
                } else if (c == '&') {
                    w.write("&amp;");
                } else if (c == '>') {
                    w.write("&gt;");
                }
            }
            ++i;
        }
    }
}
