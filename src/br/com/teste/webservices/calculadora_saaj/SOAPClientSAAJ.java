package br.com.teste.webservices.calculadora_saaj;

import java.io.ByteArrayOutputStream;
import java.util.Locale;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

import br.com.teste.webservices.calculadora.Divide;
import br.com.teste.webservices.calculadora.DivideResponse;

public class SOAPClientSAAJ {

	// SAAJ - SOAP Client Testing
	public static void main(String args[]) {
		/*
		 * The example below requests from the Web Service at:
		 * https://www.w3schools.com/xml/tempconvert.asmx?op=CelsiusToFahrenheit
		 * 
		 * 
		 * To call other WS, change the parameters below, which are: - the SOAP Endpoint
		 * URL (that is, where the service is responding from) - the SOAP Action
		 * 
		 * Also change the contents of the method createSoapEnvelope() in this class. It
		 * constructs the inner part of the SOAP envelope that is actually sent.
		 */
		String soapEndpointUrl = "http://www.dneonline.com/calculator.asmx";
		String soapAction = "http://tempuri.org/Divide";

		callSoapWebService(soapEndpointUrl, soapAction);
	}

	private static void createSoapEnvelope(SOAPMessage soapMessage) throws SOAPException {
		SOAPPart soapPart = soapMessage.getSOAPPart();

		// SOAP Envelope
		SOAPEnvelope envelope = soapPart.getEnvelope();
	
		// SOAP Body
		SOAPBody soapBody = envelope.getBody();
		
		Divide divide = new Divide();
		divide.setIntA(10);
		divide.setIntB(2);

		javaToSoap(soapBody, divide);		

		
	}

	private static void callSoapWebService(String soapEndpointUrl, String soapAction) {
		try {
			// Create SOAP Connection
			SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
			SOAPConnection soapConnection = soapConnectionFactory.createConnection();

			// Send SOAP Message to SOAP Server
			SOAPMessage soapResponse = soapConnection.call(createSOAPRequest(soapAction), soapEndpointUrl);

			// Print the SOAP Response
			System.out.println("Response SOAP Message:");
			soapResponse.writeTo(System.out);
			System.out.println();
			
			soapToJava(soapResponse); 
			

			soapConnection.close();
		} catch (Exception e) {
			System.err.println(
					"\nError occurred while sending SOAP Request to Server!\nMake sure you have the correct endpoint URL and SOAPAction!\n");
			e.printStackTrace();
		}
	}


	private static SOAPMessage createSOAPRequest(String soapAction) throws Exception {
		MessageFactory messageFactory = MessageFactory.newInstance();
		//MessageFactory messageFactory = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
		SOAPMessage soapMessage = messageFactory.createMessage();

		createSoapEnvelope(soapMessage);

		MimeHeaders headers = soapMessage.getMimeHeaders();
		headers.addHeader("SOAPAction", soapAction);

		soapMessage.saveChanges();

		/* Print the request message, just for debugging purposes */
		/*System.out.println("Request SOAP Message:");
		soapMessage.writeTo(System.out);		
		System.out.println("\n");
		*/
		
		// Transformando SOAPMessage em string para simular a resposta do servidor http
		System.out.println("Request SOAP Message:");
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		soapMessage.writeTo(out);
		String strMsg = new String(out.toByteArray());
		System.out.println(strMsg);
		
		return soapMessage;
	}
	
	private static void soapToJava(SOAPMessage soapResponse) throws JAXBException, SOAPException {
		
		
		   try {
		        if (soapResponse.getSOAPBody().hasFault()) {
		        	System.err.println("Erro encontrado:");
		            SOAPFault fa = soapResponse.getSOAPBody().getFault();
		            System.err.println("FaultString:"); 
		            System.err.println(fa.getFaultString());
		            //System.err.println("FaultReasonText:");
		            //System.err.println(fa.getFaultReasonText(Locale.ENGLISH));
		            System.err.println("FaultCode: " + fa.getFaultCode() + " - Detail: " + fa.getDetail());
		            return;
		        }
		        
		    } catch (SOAPException ex) {
		        System.err.println("SoapEx " + ex.getMessage());
		        return;
		    }
		
				
		//unmarshalling
		JAXBContext jc = JAXBContext.newInstance(DivideResponse.class);
		Unmarshaller um = jc.createUnmarshaller();
		DivideResponse output = (DivideResponse)um.unmarshal(soapResponse.getSOAPBody().extractContentAsDocument());
		System.out.println("Resposta: "+output.getDivideResult());
	}
	
	private static void javaToSoap(SOAPBody soapBody, Divide divide) {
		System.out.println("Pergunta: ["+divide.getIntA() +"]/["+divide.getIntB()+"]");
		try {
	    	JAXBContext jc = JAXBContext.newInstance(Divide.class);
		    Marshaller marshaller = jc.createMarshaller();
			marshaller.marshal(divide, soapBody);
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}