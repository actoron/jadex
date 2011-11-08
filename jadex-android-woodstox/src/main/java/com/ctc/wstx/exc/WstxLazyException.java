/* Woodstox XML processor
 *
 * Copyright (c) 2004 Tatu Saloranta, tatu.saloranta@iki.fi
 *
 * Licensed under the License specified in file LICENSE, included with
 * the source code.
 * You may not use this file except in compliance with the License.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ctc.wstx.exc;

import javaxx.xml.stream.XMLStreamException;

import com.ctc.wstx.util.ExceptionUtil;

/**
 * Alternative exception class Woodstox code uses when it is not allowed
 * to throw an instance of {@link XMLStreamException}; this generally
 * happens when doing lazy parsing.
 */
public class WstxLazyException
    extends RuntimeException
{
    private static final long serialVersionUID = 1L;

    final XMLStreamException mOrig;

    public WstxLazyException(XMLStreamException origEx)
    {
        super(origEx.getMessage());
        mOrig = origEx;
        // Let's additionally to set source message
        ExceptionUtil.setInitCause(this, origEx);
    }

    public static void throwLazily(XMLStreamException ex)
        throws WstxLazyException
    {
        throw new WstxLazyException(ex);
    }

    /**
     * Need to override this, to be able to dynamically construct and
     * display the location information...
     */
    public String getMessage()
    {
        return "["+getClass().getName()+"] "+mOrig.getMessage();
    }

    public String toString()
    {
        return "["+getClass().getName()+"] "+mOrig.toString();
    }
}
