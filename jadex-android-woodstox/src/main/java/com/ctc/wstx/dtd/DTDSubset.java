/* Woodstox XML processor
 *
 * Copyright (c) 2004- Tatu Saloranta, tatu.saloranta@iki.fi
 *
 * Licensed under the License specified in the file LICENSE which is
 * included with the source code.
 * You may not use this file except in compliance with the License.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ctc.wstx.dtd;

import java.util.*;

import javaxx.xml.stream.XMLStreamException;

import org.codehaus.stax2.validation.*;

import com.ctc.wstx.sr.InputProblemReporter;

/**
 * This is the abstract base class that implements the standard Stax2
 * validation schema base class ({@link XMLValidationSchema}, as well
 * as specifies extended Woodstox-specific interface for accessing
 * DTD-specific things like entity expansions and notation properties.
 *<p>
 * API is separated from its implementation to reduce coupling; for example,
 * it is possible to have DTD subset implementations that do not implement
 * validation logics, just entity expansion.
 */
public abstract class DTDSubset
    implements DTDValidationSchema
{
    /*
    //////////////////////////////////////////////////////
    // Life-cycle
    //////////////////////////////////////////////////////
     */

    protected DTDSubset() { }

    /**
     * Method that will combine definitions from this internal subset with
     * definitions from passed-in external subset, producing a new combined
     * DTDSubset instance.
     */
    public abstract DTDSubset combineWithExternalSubset(InputProblemReporter rep,
                                                        DTDSubset extSubset)
        throws XMLStreamException;

    /*
    //////////////////////////////////////////////////////
    // XMLValidationSchema implementation
    //////////////////////////////////////////////////////
     */

    public abstract XMLValidator createValidator(ValidationContext ctxt)
        throws XMLStreamException;

    public String getSchemaType() {
        return XMLValidationSchema.SCHEMA_ID_DTD;
    }

    /*
    //////////////////////////////////////////////////////
    // And extended DTDValidationSchema
    //////////////////////////////////////////////////////
     */

    public abstract int getEntityCount();

    public abstract int getNotationCount();

    /*
    //////////////////////////////////////////////////////
    // Woodstox-specific API, caching support
    //////////////////////////////////////////////////////
     */

    public abstract boolean isCachable();

    /**
     * Method used in determining whether cached external subset instance
     * can be used with specified internal subset. If ext. subset references
     * any parameter entities int subset (re-)defines, it can not; otherwise
     * it can be used.
     *
     * @return True if this (external) subset refers to a parameter entity
     *    defined in passed-in internal subset.
     */
    public abstract boolean isReusableWith(DTDSubset intSubset);

    /*
    //////////////////////////////////////////////////////
    // Woodstox-specific API, entity/notation handling
    //////////////////////////////////////////////////////
     */
    
    public abstract HashMap getGeneralEntityMap();

    public abstract List getGeneralEntityList();

    public abstract HashMap getParameterEntityMap();

    public abstract HashMap getNotationMap();

    public abstract List getNotationList();

    public abstract HashMap getElementMap();
}
