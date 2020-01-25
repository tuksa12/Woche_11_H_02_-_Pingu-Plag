package pgdp.net;

import java.util.Objects;

public final class HttpResponse {

	private final HttpStatus status;
	private final String body;
	private final String location;

	/**
	 * Creates a HTTP response with no headers and the given body. This is for
	 * example for {@link HttpStatus#OK} where the body is the HTML of the page.
	 *
	 * @param status the HTTP status, must not be <code>null</code>
	 * @param body   the response body, may be <code>null</code> or empty (in case
	 *               of <code>null</code>, an empty string is used)
	 */
	public HttpResponse(HttpStatus status, String body) {
		this.status = Objects.requireNonNull(status);
		this.body = Objects.requireNonNullElse(body, "");
		this.location = null;
	}

	/**
	 * Creates a HTTP response that includes a location in the response header. This
	 * is used for example for {@link HttpStatus#SEE_OTHER} which then redirects to
	 * the location.
	 *
	 * @param status      the HTTP status, must not be <code>null</code>
	 * @param body        the response body, may be <code>null</code> or empty (in
	 *                    case of <code>null</code>, an empty string is used)
	 * @param locationUrl must not be null or empty
	 */
	public HttpResponse(HttpStatus status, String body, String locationUrl) {
		this.status = Objects.requireNonNull(status);
		this.body = Objects.requireNonNullElse(body, "");
		this.location = Objects.requireNonNull(locationUrl);
		if (location.isBlank())
			throw new IllegalArgumentException("location must not be blank");
	}

	public HttpStatus getStatus() {
		return status;
	}

	public String getBody() {
		return body;
	}

	public String getLocation() {
		return location;
	}

	@Override
	public String toString() {
		StringBuilder response = new StringBuilder();
		response.append("HTTP/1.1 ");
		response.append(status.getCode());
		response.append(" ");
		response.append(status.getText());
		if (location != null) {
			response.append("\r\nLocation: ");
			response.append(location);
		}
		response.append("\r\n\r\n");
		response.append(body);
		return response.toString();
	}
}
