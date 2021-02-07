package pgdp.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;

public class PinguPlagWebServer {
	//Attributes
	static int port = 80;
	private final PinguTextCollection textCollection;
	private final HtmlGenerator htmlGenerator;
	BufferedReader in;
	PrintWriter out;

	//Constructor
	public PinguPlagWebServer() throws IOException {
		this.textCollection = new PinguTextCollection();
		this.htmlGenerator = new HtmlGenerator();
	}

	//Main
	public static void main(String[] args) throws IOException {
		PinguPlagWebServer pinguPlagWebServer = new PinguPlagWebServer();
		pinguPlagWebServer.run();
	}

	//Run method
	public void run() {
		try(ServerSocket serverSocket = new ServerSocket(port)) {
			while(!Thread.currentThread().isInterrupted()){
				connectWithClient(serverSocket.accept());
			}
		} catch(IOException E){
			System.err.println("PinguPlagWebServer run() failed:" + E);
		}
	}

	//Helper method to connect with the client
	private void connectWithClient(Socket client) {
		try {
			in = new BufferedReader(new InputStreamReader(client.getInputStream(), StandardCharsets.UTF_8));
			out = new PrintWriter(client.getOutputStream(), true, StandardCharsets.UTF_8);

			handleRequest(in.readLine().substring(0,in.readLine().indexOf("\r\n")), tryReadBody(in));
		} catch (Exception e) {
			System.err.println("Client connection failed: " + e);
		}
	}

	//handleRequest method to call each other method depending on the Strings from the request
	HttpResponse handleRequest(String firstLine, String body) {
		if(firstLine.contains("GET") && !firstLine.contains("/texts")){
			return handleStartPage(new HttpRequest(firstLine,body));
		} else if(firstLine.contains("GET /texts/")){
			return handleTextDetails(new HttpRequest(firstLine,body));
		} else if(firstLine.contains("POST /texts")){
			return handleNewText(new HttpRequest(firstLine,body));
		}else if(firstLine.contains("POST") && !firstLine.contains("/texts") || firstLine.contains("GET") && firstLine.contains("GET /texts")){
			return new HttpResponse(HttpStatus.METHOD_NOT_ALLOWED,body);
		} else{
			return new HttpResponse(HttpStatus.BAD_REQUEST,body);
		}
	}

	//Generates a start page and returns to the client
	HttpResponse handleStartPage(HttpRequest request) {
		try{
			String startPage = htmlGenerator.generateStartPage(textCollection.getAll());
			out.println(startPage);
		} catch (Exception e){
			return new HttpResponse(HttpStatus.NOT_FOUND, request.body);
		}
		return new HttpResponse(HttpStatus.OK,request.body);
	}

	//Tries to handle the text details, getting the ID and finding the plagiarism
	HttpResponse handleTextDetails(HttpRequest request) {
		try{
			int ID = Integer.parseInt(request.firstLine.substring(request.firstLine.indexOf(11)));
			if(textCollection.getAll().stream().anyMatch(pinguText-> pinguText.getId()==ID)){
				out.println(textCollection.findPlagiarismFor(ID));
				return new HttpResponse(HttpStatus.OK,request.body);
			} else{
				return new HttpResponse(HttpStatus.NOT_FOUND,request.body);
			}
		} catch (Exception e){
			return new HttpResponse(HttpStatus.BAD_REQUEST,request.body);
		}
	}

	//Handles new texts by adding them to the collection
	HttpResponse handleNewText(HttpRequest request) {
		try {
			Map<String, String> result = request.getParameters();
			textCollection.add(result.get("title"),result.get("author"),result.get("text"));
			return new HttpResponse(HttpStatus.SEE_OTHER,"/texts/" + textCollection.getAll().stream()
					.filter(text -> text.getText() == result.get("text")).findAny().get().getId());
		} catch (Exception e){
			return new HttpResponse(HttpStatus.BAD_REQUEST,request.body);
		}
	}

	//Returns the textCollection
	PinguTextCollection getPinguTextCollection(){
		return textCollection;
	}

	/**
	 * Tries to read a HTTP request body from the given {@link BufferedReader}.
	 * Returns null if no body was found. This method consumes all lines of the
	 * request, read the first line of the HTTP request before using this method.
	 */
	static String tryReadBody(BufferedReader br) throws IOException {
		String contentLengthPrefix = "Content-Length: ";
		int contentLength = -1;
		String line = null;
		while ((line = br.readLine()) != null) {
			if (line.isEmpty()) {
				if (contentLength == -1)
					return null;
				char[] content = new char[contentLength];
				int read = br.read(content);
				if (read == -1)
					return null;
				if (read < content.length)
					content = Arrays.copyOf(content, read);
				return new String(content);
			}
			if (line.startsWith(contentLengthPrefix)) {
				try {
					contentLength = Integer.parseInt(line.substring(contentLengthPrefix.length()));
				} catch (@SuppressWarnings("unused") RuntimeException e) {
					// ignore and just continue
				}
			}
		}
		return null;
	}
}
