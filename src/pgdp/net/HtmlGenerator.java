package pgdp.net;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class HtmlGenerator {

	static Path startPageTemplatePath = Path.of("templates", "start-page.html");
	static Path textDetailsTemplatePath = Path.of("templates", "text-details.html");

	private static final String TEMPL_ID = "%id";
	private static final String TEMPL_TITLE = "%title";
	private static final String TEMPL_AUTHOR = "%author";
	private static final String TEMPL_TEXT = "%text";
	private static final String TEMPL_TABLE = "%table";

	private static final String HTML_TABLE_START = "<table border=\"1px solid black\">";
	private static final String HTML_LIST_TABLE_HEADER = "<tr><td><b>ID</b></td><td><b>Title</b></td><td><b>Author</b></td></tr>";
	private static final String HTML_PLAG_TABLE_HEADER = "<tr><td><b>ID</b></td><td><b>Title</b></td><td><b>Author</b></td><td><b>Similarity Score</b></td></tr>";
	private static final String HTML_LIST_TABLE_START = HTML_TABLE_START + HTML_LIST_TABLE_HEADER;
	private static final String HTML_PLAG_TABLE_START = HTML_TABLE_START + HTML_PLAG_TABLE_HEADER;
	private static final String HTML_LIST_TABLE_ROW = "<tr><td>%1$d</td><td><a href=\"/texts/%1$d\">%2$s</a></td><td>%3$s</td></tr>";
	private static final String HTML_PLAG_TABLE_ROW = "<tr><td>%1$d</td><td><a href=\"/texts/%1$d\">%2$s</a></td><td>%3$s</td><td>%4$.1f %%</td></tr>";
	private static final String HTML_TABLE_END = "</table>";
	private static final String HTML_LIST_TABLE_NO_RESULTS = "<p>No texts found :(</p>";
	private static final String HTML_PLAG_TABLE_NO_RESULTS = "<p>" + TEMPL_AUTHOR + " isn't a Plagiatuin</p>";

	private final XMLOutputFactory xmlOutputFactory;

	private final TemplateProcessor startPageTemplate;
	private final TemplateProcessor textDetailsTemplate;

	public HtmlGenerator() throws IOException {
		this.startPageTemplate = new TemplateProcessor(startPageTemplatePath);
		this.textDetailsTemplate = new TemplateProcessor(textDetailsTemplatePath);
		this.xmlOutputFactory = XMLOutputFactory.newDefaultFactory();
	}

	/**
	 * Generates the start page which is the text submission page and a list of all
	 * submitted texts using the <code>start-page.html</code> template.
	 */
	public String generateStartPage(List<PinguText> allTextsInTheCollection) {
		String table;
		if (allTextsInTheCollection.isEmpty()) {
			table = HTML_LIST_TABLE_NO_RESULTS;
		} else {
			table = allTextsInTheCollection.stream()
					.map(text -> String.format(HTML_LIST_TABLE_ROW, text.getId(), escape(text.getTitle()),
							escape(text.getAuthor())))
					.collect(Collectors.joining("\n", HTML_LIST_TABLE_START, HTML_TABLE_END));
		}
		return startPageTemplate.replace(Map.of(TEMPL_TABLE, table));
	}

	/**
	 * Generates the text details page for the supplied PinguText and its
	 * plagiarismResults using the <code>text-details.html</code> template.
	 */
	public String generateTextDetailsPage(PinguText text, Map<PinguText, Double> plagiarismResults) {
		String table;
		if (plagiarismResults.isEmpty()) {
			table = HTML_PLAG_TABLE_NO_RESULTS.replace(TEMPL_AUTHOR, text.getAuthor());
		} else {
			table = plagiarismResults.entrySet().stream()
					.sorted(Comparator.comparingDouble(Map.Entry<PinguText, Double>::getValue).reversed())
					.map(entry -> String.format(HTML_PLAG_TABLE_ROW, entry.getKey().getId(),
							escape(entry.getKey().getTitle()), escape(entry.getKey().getAuthor()),
							entry.getValue() * 100.0))
					.collect(Collectors.joining("\n", HTML_PLAG_TABLE_START, HTML_TABLE_END));
		}
		return textDetailsTemplate.replace(
				Map.of(TEMPL_ID, String.valueOf(text.getId()), TEMPL_TITLE, escape(text.getTitle()), TEMPL_AUTHOR,
						escape(text.getAuthor()), TEMPL_TEXT, escape(text.getText()), TEMPL_TABLE, table));
	}

	private String escape(String text) {
		StringWriter stringWriter = new StringWriter(text.length());
		try {
			XMLStreamWriter xmlStreamWriter = xmlOutputFactory.createXMLStreamWriter(stringWriter);
			xmlStreamWriter.writeCharacters(text);
			return stringWriter.toString();
		} catch (@SuppressWarnings("unused") XMLStreamException e) {
			return "ENCODING-ERROR";
		}
	}
}
