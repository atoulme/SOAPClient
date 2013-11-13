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

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

import org.dom4j.Element;
import org.dom4j.io.DOMReader;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;


public class SOAPClient {

	static private XMLWriter writer;

	private String wsdlURL;
	private SOAPMessage soapRequest;
	private SOAPMessage soapResponse;

	private boolean debug = false;

	private SOAPConnection conn;
	
	{
		try {
			writer = new XMLWriter(OutputFormat.createPrettyPrint());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	public SOAPClient(String url, String soapAction) throws UnsupportedOperationException, SOAPException
	{
		// Create the connection
		SOAPConnectionFactory scf = SOAPConnectionFactory.newInstance();
		conn = scf.createConnection();

		// Create message
		MessageFactory mf = MessageFactory.newInstance();
		soapRequest = mf.createMessage();
		wsdlURL = url;
		
		// add SOAP Action
		if (soapAction != null) {
			MimeHeaders hd = soapRequest.getMimeHeaders();
			hd.addHeader("SOAPAction", soapAction);
		}
	}

	public SOAPClient(String wsdlURL) throws UnsupportedOperationException, SOAPException
	{
		this(wsdlURL, null);
	}

	public void addNamespace(String prefix, String name)
	{
		try {
			SOAPPart sp = soapRequest.getSOAPPart();
			SOAPEnvelope env = sp.getEnvelope();
			env.addNamespaceDeclaration(prefix, name);
		} catch (SOAPException e) {
			e.printStackTrace();
		}
	}

	public STElement getRequest(String operation, String prefix) throws SOAPException
	{
		SOAPPart sp = soapRequest.getSOAPPart();
		SOAPEnvelope env = sp.getEnvelope();
		STElement e = new STElement(env.getBody());
		if (operation != null)
			if (prefix != null)
				e = e.addChild(operation, prefix);
			else
				e = e.addChild(operation);
		return e;
	}
	
	public STElement getRequest(String operation) throws SOAPException
	{
		return getRequest(operation, null);
	}

	public STElement getRequest() throws SOAPException
	{
		return getRequest(null, null);
	}

	public STElement call() throws IOException
	{
		try {
			soapRequest.saveChanges();
			if (debug) {
				System.out.print("********* REQUEST:");
				Element e = new DOMReader().read(soapRequest.getSOAPPart()).getRootElement();
				writer.write(e);
				System.out.println();
				System.out.println("------------------");
			}

			soapResponse = conn.call(soapRequest, wsdlURL);

			if (debug) {
				System.out.print("********* RESPONSE:");
				Element e = new DOMReader().read(soapResponse.getSOAPPart()).getRootElement();
				writer.write(e);
				System.out.println();
				System.out.println("------------------");
			}
			
			SOAPPart sp = soapResponse.getSOAPPart();
			SOAPEnvelope env = sp.getEnvelope();
			return new STElement(env.getBody());
		} catch (SOAPException e) {
			e.printStackTrace();
			return null;
		}
	}

	public void setDEBUG()
	{
		debug = true;
	}

	public void unsetDEBUG()
	{
		debug = false;
	}

}
