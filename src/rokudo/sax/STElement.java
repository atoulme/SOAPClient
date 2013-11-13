/**
 * Copyright (C) 2011, Paolo Sasso.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package rokudo.sax;

import java.io.StringWriter;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.xml.soap.Node;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class STElement {
	protected SOAPElement el;

	static private STElement ST_NULL = new STElement(null);

	protected STElement (SOAPElement el) { this.el = el; }

	public STElement addChild(String tag)
	{
		return addChild(tag, null);
	}

	public STElement addChild(String tag, String ns) 
	{
		if (el == null) return ST_NULL;
		try {
			SOAPElement soapElement;
			if (ns != null)
				soapElement = el.addChildElement(tag, ns);
			else
				soapElement = el.addChildElement(tag);
			return new STElement(soapElement);
		} catch (SOAPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ST_NULL;
		}
	}

	public STElement setText(Object obj)
	{
		if (el != null && obj != null) {
			try {
				el.addTextNode(obj.toString());
			} catch (SOAPException e) {
				e.printStackTrace();
			}
		}
		return this;
	}

	public String getText()
	{
		if (el == null) return "";
		String text = el.getTextContent();
		return text == null ? "" : text;
	}

	public String getName()
	{
		if (el == null) return null;
		return el.getElementName().getLocalName();
	}

	public String getPrefix()
	{
		if (el == null) return null;
		return el.getElementName().getPrefix();
	}

	public String getFullName()
	{
		if (el == null) return null;
		return el.getElementName().getQualifiedName();
	}

	public SOAPElement getSOAPElement()
	{
		return el;
	}

	public STElement getChild(String tagName)
	{
		if (el == null) return ST_NULL;
		@SuppressWarnings("rawtypes")
		Iterator iterator = el.getChildElements();
		while (iterator.hasNext()) {
			Node n = (Node)iterator.next();
			if (n instanceof SOAPElement) {
				SOAPElement e = (SOAPElement) n;
				if (e.getElementName().getLocalName().equals(tagName))
					return new STElement(e);
			}
		}
		return ST_NULL;
	}

	public List<STElement> getChilds(String tagName)
	{
		List<STElement> list = new LinkedList<STElement>();
		if (el == null) return list;

		@SuppressWarnings("rawtypes")
		Iterator iterator = el.getChildElements();
		while (iterator.hasNext()) {
			Node n = (Node)iterator.next();
			if (n instanceof SOAPElement) {
				SOAPElement e = (SOAPElement) n;
				if (e.getElementName().getLocalName().equals(tagName))
					list.add(new STElement(e));
			}
		}

		return list;
	}

	public String getChildContent(String tagName)
	{
		return getChild(tagName).getText();
	}

	public boolean isNull()
	{
		return el == null;
	}

	@Override
	public String toString()
	{
		StringWriter sw = new StringWriter();
		try {
			Transformer t = TransformerFactory.newInstance().newTransformer();
			t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			t.transform(new DOMSource(el), new StreamResult(sw));
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		return sw.toString();
	}
	
	public String toStringEx()
	{
		StringWriter sw = new StringWriter();
		try {
			Transformer t = TransformerFactory.newInstance().newTransformer();
			t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			t.setOutputProperty(OutputKeys.INDENT, "yes");
			t.transform(new DOMSource(el), new StreamResult(sw));
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		return sw.toString();
	}
	

}
