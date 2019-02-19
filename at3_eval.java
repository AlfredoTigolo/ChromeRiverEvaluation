/*
* Alfredo Tigolo
* Chrome River Evaluation
* date: 1-21-2011, updated 1-27-2011
* home: 424-646-3493
*
*
*
// ==================================================
NOTE: 

This Chrome River Evaluation at3_eval.java file was created using
Java 2 SE SDK 1.4.2. with the following JRE

java version "1.5.0_07"
Java(TM) 2 Runtime Environment, Standard Edition (build 1.5.0_07-b03)
Java HotSpot(TM) Client VM (build 1.5.0_07-b03, mixed mode)
// ==================================================
* You can invest as much time as you'd like for this evaluation.
* To compile this Java Console Application:
* 
* javac at3_eval
*
* To run this application:
* java -Djavax.net.ssl.trustStore=testkeys at3_eval staging.chromeriver.com 443 /eval/file.txt output
* 
* To get the trust Store file, you must copy the certificate from staging.chromeriver.com.
* Then, run the keytool command:
* source: http://java.sun.com/developer/technicalArticles/Security/secureinternet2/
// ==================================================
// update 1-29-2011 creating the keystore
$keytool -import -keystore testkeys -alias chromeriver -file staging.chromeriver.com
Enter keystore password:  passphrase
Owner: CN=staging.chromeriver.com, OU=Terms of use at www.verisign.com/rpa (c)05, O=Chrome River Technologies, L=Los Angeles, ST=California, C=US
Issuer: CN=VeriSign Class 3 Secure Server CA - G3, OU=Terms of use at https://www.verisign.com/rpa (c)10, OU=VeriSign Trust Network, O="VeriSign, Inc.", C=US
Serial number: 2fdc4d0fec9c167706f910dc569ac15a
Valid from: Wed Oct 13 17:00:00 PDT 2010 until: Fri Oct 21 16:59:59 PDT 2011
Certificate fingerprints:
         MD5:  2A:E5:E7:74:2A:21:1A:85:F1:55:C9:94:03:C4:AB:39
         SHA1: 00:90:D5:B4:84:0F:91:D1:9D:7D:8B:B5:AF:7D:D8:E3:CC:3F:96:77
Trust this certificate? [no]:  yes
Certificate was added to keystore
// ==================================================
*
* Write a Java program that performs the following:
*
* - Read the following file: https://staging.chromeriver.com/eval/file.txt
*
* To read a file, the following classes were used:
* BufferedReader, FileReader, IOException, FileNotFoundException
*
* To read from a URL, the following classes were used:
* URL, URLConnection
*
* To read from a secure ssl connection, the following classes were used:
* HttpsURLConnection, SSLSocket, SSLSocketFactory, and other key management
* classes to handle the certificate sent by https://staging.chromeriver.com
*
* - From the file.txt file, create a new file with the following requirements:
* To create an output file, the following classes are imported from java.io package
* File, DataOutputStream, FileOutputStream, NullPointerException
*
*    - Each line in the file should not have more than 50 characters
*    - Each line should be word wrapped (i.e. there should be no half words on a line)
*    - A word is delimited by 1 or more spaces or a linefeed.
*    - The paragraphs of the original file should be left in tact.
* Used StringTokenizer to help count the number of characters in a word and
* return delimiters are tokens
*
*
* Email the:
* - original file.txt
* - output file that your program created
* - all necessary code
* - instructions to build the program
*
* in a zip file to: evaluation@chromeriver.com
*  
*/
import java.io.BufferedReader;
import java.io.InputStreamReader; //can be used for keyboard input from command line
import java.io.InputStream;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.File;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.DataInputStream;

import java.util.StringTokenizer;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
//import java.net.UnknownHostException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLContext;
import javax.net.ssl.KeyManagerFactory;
//import javax.net.ssl.SSLPeerUnverifiedException;

import java.security.Security;
import java.security.cert.X509Certificate;
import java.security.KeyStore;

public class at3_eval {

/* ===================
This method takes in an array argument used for host, port, path, and output.
This is modified now to read a text file using a ssl socket connection
 =================== */
public static void readTextfromFile (String[] myArgs, boolean checkSSL) {

		// variables for building the url link
		String host = null;
		int port = -1;
		String path = null;
		for ( int i = 0; i < myArgs.length; i++)
			System.out.println(myArgs[i]);
			
		//checks if its missing variables
		if ( myArgs.length < 4 ) {
			System.out.println (
			"USAGE: java at3_eval " +
			"host  port  filepath outputfile");
			System.exit(-1);
		}
		
		// populates variables for url
		try {
			host=myArgs[0];
			port=Integer.parseInt(myArgs[1]);
			path=myArgs[2];
		} catch (IllegalArgumentException e) {
			System.out.println (
			"USAGE: java at3_eval " +
			"host  port  filepath outputfile");
			System.exit(-1);
		}
		
		// variables for buffering the stream from ssl socket connection
		//String inputfile = myArgs[0];
		String line = null;
		String word = null;		
		//BufferedReader buf = null;
		//BufferedReader in = new BufferedReader (new InputStreamReader (System.in) ); //used for keyboard input from command line
		DataOutputStream dos = null;
		StringTokenizer st;
		String delimiters = " \n"; // \r makes it a double space paragraph
		int line_range = 0;
		
		// source http://www.javaworld.com/javatips/jw-javatip96.html?page=1
		/*
					System.setProperty("java.protocol.handler.pkgs",
						        "com.sun.net.ssl.internal.www.protocol");
		   Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());		
		   Security.addProvider(new sun.security.provider.Sun());
		*/
		
		try {
			
			//url and input stream variables
			
			URLConnection urlConn;
			HttpsURLConnection urlsecureConn;
			InputStream ins;
			
			//the SSLSocketFactory and SSLSocket variables 
			
			SSLSocketFactory factory = null;
			SSLSocket socket = null;
			
			//classes used for a data connection to a url
			
			if ( checkSSL == true ) {
				//URL url = new URL("https://staging.chromeriver.com/eval/file.txt");
				URL url = new URL("https://" + host + path);
				urlsecureConn = (HttpsURLConnection)url.openConnection();
				
				try {
//======================================											
// update 1-29-2011
					// Variables to set up a key manager to do client
					// authentication if required by server.	
					// This was taken from this example SSLSocketClientWithClientAuth.java
					//  source: http://download.oracle.com/javase/1.4.2/docs/guide/security/jsse/samples/
					
//======================================												
					SSLContext ctx;
					KeyManagerFactory kmf;
					KeyStore ks;
					char[] passphrase = "passphrase".toCharArray();

					ctx = SSLContext.getInstance("TLS");
					kmf = KeyManagerFactory.getInstance("SunX509");
					ks = KeyStore.getInstance("JKS");

					ks.load(new FileInputStream("testkeys"), passphrase);
					
					kmf.init(ks, passphrase);
					ctx.init(kmf.getKeyManagers(), null, null);

					factory = ctx.getSocketFactory();
					} catch (Exception e) {
						throw new IOException(e.getMessage());
					}
				factory = urlsecureConn.getSSLSocketFactory();
				socket = (SSLSocket)factory.createSocket (host, port);
				
				// SSL socket will do SSL handshaking first to
				// setup the security attributes.
				
				// SSL handshaking can be initiated by either flushing data
				// down the pipe or by starting the handshaking by hand.
				
				socket.startHandshake();
/*
	//======================================							
	// update 1-29-2011
	// Buffering output from the secure socket stream
	
				java.io.PrintWriter out = new java.io.PrintWriter(
								  new java.io.BufferedWriter(
								  new java.io.OutputStreamWriter(
				     				  socket.getOutputStream())));
					    out.println("GET " + path + " HTTP/1.0");	    
					    //out.write("GET / HTTP/1.0\n\n");
					    out.println();
	    		out.flush();
				
				
				//Make sure there were no surprises
				
					    if (out.checkError())
						System.out.println(						
		    "SSLSocketClient:  java.io.PrintWriter error");
	//======================================									    
*/
				//start connection once certificate key is handled properly
				
				urlsecureConn.setDoInput(true); 		
				urlsecureConn.setUseCaches(false);
				urlsecureConn.setAllowUserInteraction(true);	
				ins = urlsecureConn.getInputStream(); // get the stream from urlSecureConn
				// or
				//ins = socket.getInputStream(); // requires buffering from the socket
			} else {
			
				//connecting without the use off a secure connection
				//URL url = new URL("http://staging.chromeriver.com/eval/file.txt");
				
				URL url = new URL("http://" + host + path);
				urlConn = url.openConnection();
				urlConn.setDoInput(true); 		
				urlConn.setUseCaches(false);
				urlConn.setAllowUserInteraction(true);	
				ins = urlConn.getInputStream();
			}			
						
			
			//classes for the buffered reader 			
			//DataInputStream dis = new DataInputStream(urlConn.getInputStream()); 		
			InputStreamReader isr = new InputStreamReader(ins);
			BufferedReader in = new BufferedReader(isr);	
					

			// checks if file exsists then outputs a message
			File outputfile = new File(myArgs[3]);
			if (outputfile.exists()) {
				System.out.println("File "+ outputfile + " already exists!");
				System.out.print ("Delete it or use a different file name.  Rerun the program!");
				System.exit(1);
			}

				//reading or writing a file from local hard disk
				dos = new DataOutputStream (new FileOutputStream(outputfile)); // creates an output stream
				//buf = new BufferedReader (new FileReader (inputfile)); // creates a buffered reader
				//line = buf.readLine(); // reads the first line
				line = in.readLine();
				//dos.writeBytes(line); // output first line to file
				st = new StringTokenizer(line, delimiters, true);

				while (line != null) {
				
					//System.out.println(line);
					//System.out.println(line.length());
					
					if (line.length() == 0 ) {
						//System.out.println("\n");
						dos.writeChars("\n");					
						dos.writeChars("\r");					
						line_range = 0;
					}
					//line = buf.readLine();				
					line = in.readLine();				
					//dos.writeBytes(line);
					
						while ( st.hasMoreTokens() && line != null ) {
							word = st.nextToken();

							line_range += word.length();

							if ( line_range > 50 ) {
								
								//System.out.println("");
								//System.out.print("\n");
								dos.writeChars("\r");
								//dos.writeChars("\n");																					
								line_range -= 50;
								
							}

							//System.out.print(word);
							dos.writeBytes(word);
						}//inner while

					if ( line != null ) st = new StringTokenizer (line, delimiters, true);

				} //outer while

				if (dos != null ) dos.close(); //close the file
				//buf.close();
				in.close();
				dos.close();
			
		}//end try
		catch ( FileNotFoundException nofile ) {
			//System.out.println(nofile.getMessage());
			nofile.printStackTrace();
		} catch ( IOException ioex ) {
			//System.out.println(ioex.getMessage());
			ioex.printStackTrace();
		} catch ( NullPointerException nullex ) {
			//System.out.println(nullex.getMessage()); //catches null of data output stream (dos)
			nullex.printStackTrace();
		} catch ( ClassCastException cce ) {
			//System.out.println(cce.getMessage());
			cce.printStackTrace();
		}	

}

/* ===================
The main method calls other static methods
===================*/	
	public static void main (String[] args) throws
		IOException, FileNotFoundException, NullPointerException {

		readTextfromFile(args, true); // true ssl / false nossl		
		
	}// end main 
}//end class
