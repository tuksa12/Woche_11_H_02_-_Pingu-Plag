package pgdp.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;

public class PinguPlagWebServer {

	static int port = 80;
	PinguTextCollection textCollection;
	HtmlGenerator htmlGenerator;


	// TODO

	public PinguPlagWebServer() throws IOException {
		// TODO
	}

	public static void main(String[] args) throws IOException {
		PinguPlagWebServer pinguPlagWebServer = new PinguPlagWebServer();
		pinguPlagWebServer.run();
	}

	public void run() {
		// TODO
	}

	HttpResponse handleRequest(String firstLine, String body) {
		return null; // TODO
	}

	HttpResponse handleStartPage(HttpRequest request) {
		return null; // TODO
	}

	HttpResponse handleTextDetails(HttpRequest request) {
		return null; // TODO
	}

	HttpResponse handleNewText(HttpRequest request) {
		return null; // TODO
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
