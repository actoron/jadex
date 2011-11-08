/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.sun.msv.reader.xmlschema;

import java.io.InputStream;
import java.io.Reader;

import org.w3c.dom.Element;
import org.w3c.dom.ls.LSInput;

import com.sun.msv.reader.DOMLSInput;

/**
 * implementation class for an LSInput where the only data source is an existing DOM element.
 */
public class DOMLSInputImpl implements LSInput, DOMLSInput {
    private String baseURI;
    private String systemId;
    private Element element;

    public DOMLSInputImpl(String baseURI, String systemId, Element data) {
        this.baseURI = baseURI;
        this.element = data;
        this.systemId = systemId;
    }

    public String getBaseURI() {
        return baseURI;
    }

    public InputStream getByteStream() {
        return null;
    }

    public boolean getCertifiedText() {
        return false;
    }

    public Reader getCharacterStream() {
        return null;
    }

    public String getEncoding() {
        return null;
    }

    public String getPublicId() {
        return null;
    }

    public String getStringData() {
        return null;
    }

    public String getSystemId() {
        return systemId;
    }

    public void setBaseURI(String baseURI) {
        this.baseURI = baseURI;
    }

    public void setByteStream(InputStream byteStream) {
        throw new UnsupportedOperationException();
    }

    public void setCertifiedText(boolean certifiedText) {
        throw new UnsupportedOperationException();
    }

    public void setCharacterStream(Reader characterStream) {
        throw new UnsupportedOperationException();
    }

    public void setEncoding(String encoding) {
        throw new UnsupportedOperationException();
    }

    public void setPublicId(String publicId) {
        throw new UnsupportedOperationException();

    }

    public void setStringData(String stringData) {
        throw new UnsupportedOperationException();
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    public Element getElement() {
        return element;
    }
}