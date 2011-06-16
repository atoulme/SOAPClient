package test;


import java.io.IOException;

import javax.xml.soap.SOAPException;

import rokudo.sax.SOAPClient;
import rokudo.sax.STElement;


public class ConvertTemperature {
	public static void main(String[] args) throws SOAPException, IOException
	{
		String wsdlURL = "http://www.webservicex.net/ConvertTemperature.asmx";
		String action = "http://www.webserviceX.NET/ConvertTemp";

		SOAPClient ws = new SOAPClient(wsdlURL, action);
		ws.setDEBUG();
		
		ws.addNamespace("ws", "http://www.webserviceX.NET/");

		STElement req = ws.getRequest("ConvertTemp", "ws");
		req.addChild("Temperature", "ws").setText(28);
		req.addChild("FromUnit", "ws").setText("degreeCelsius");
		req.addChild("ToUnit", "ws").setText("degreeFahrenheit");
		
		STElement res = ws.call();
		
		res = res.getChild("ConvertTempResponse");
		System.out.println(res.getChildContent("ConvertTempResult"));
	}
}
