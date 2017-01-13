package net.tetrakoopa.mdu4j.util;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class HttpUtil {
	
	/** Time out par défaut en ms */
	public static final int DEFAULt_READ_TIMEOUT = 60 * 1000;
	
	public static class Header {

		public final static String SET_COOKIE = "Set-Cookie";
		public final static String COOKIE = "Cookie";
		public final static String LOCATION = "Location";

	}

	public static enum ReponseStatusCode {
		UNKNOWN(0), OK(200);

		private int code;
		
		private ReponseStatusCode(int code) {
			this.code = code;
		}

		public int getCode() {
			return code;
		}

	}

	public static final Header HEADER = new Header();

	public static class UserAgent {
	
		public final static String INTERNET_EXPLORER_E7_WIN_VISTA = "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.0)";
		public final static String FIREFOX_3_WIN_XP = "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.9.0.1) Gecko/2008070208 Firefox/3.0.1";
		public final static String FIREFOX_2_WIN_XP = "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.8.1.14) Gecko/20080404 Firefox/2.0.0.14";
	}

	public static final UserAgent USER_AGENT = new UserAgent();
	
	public static enum RequestMethod {
		
		POST,
		GET,
		PUT,
		HEAD;
		
		private final String httpName;
		
		private RequestMethod() {
			httpName = name();
		}
		
		public String getHttpName() {
			return httpName;
		}
		
	}
	
	public interface RequestEventHandler {
		public void onResponseReceived(int status, String content, Map<String, String> headers, long duration);
	}
	
	public static class Response {

		private int status;
		private final Map<String, String> headers = new HashMap<String, String>();
		private String content;

		private long requestDuration;

		public int getStatus() {
			return status;
		}

		public void setStatus(int status) {
			this.status = status;
		}

		public Map<String, String> getHeaders() {
			return headers;
		}
		public String getContent() {
			return content;
		}

		public long getRequestDuration() {
			return requestDuration;
		}

		public void setRequestDuration(long requestDuration) {
			this.requestDuration = requestDuration;
		}
	}

	public static int request(final String url, final RequestMethod method, final Map<String, Object> parameters, final Response response)
 {

		final String serializedContent;
		if (parameters == null)
			serializedContent = null;
		else
			serializedContent = (method == RequestMethod.POST) ? serializeContentParameters(parameters) : serializeUrlParameters(parameters);

		return request(url, method, serializedContent, response);
	}

	public static int request(final String url, final RequestMethod method, String content, final Response response) {

		return doRequest(url, method, content, new RequestEventHandler() {

			@Override
			public void onResponseReceived(int status, String content, Map<String, String> headers, long duration) {
				if (response != null) {
					response.setStatus(status);
					response.headers.clear();
					response.headers.putAll(headers);
					response.content = content;
					response.setRequestDuration(duration);
				}
			}

		});
	}

	public static int doRequest(final String url, final RequestMethod method, final Map<String, Object> parameters)
 {
		return request(url, method, parameters, null);
	}
	
	public static int doRequest(final String url, final RequestMethod method, final RequestEventHandler handler)
 {
		return doRequest(url, method, (String) null, handler);
	}
	
	public static int doRequest(final String url, final RequestMethod method, final Map<String, Object> parameters, final RequestEventHandler handler)
 {

		final String serializedContent;
		if (parameters == null)
			serializedContent = null;
		else
			serializedContent = (method == RequestMethod.POST) ? serializeContentParameters(parameters) : serializeUrlParameters(parameters);
		return doRequest(url, method, serializedContent, handler);
	}

	public static int doRequest(final String url, final RequestMethod method, final String content, final RequestEventHandler handler) {
		
		final String requestContent;
		switch (method) {
			case GET:
				requestContent = "";
				break;
			case POST:
			requestContent = content == null ? "" : content;
				break;
			default:
				throw new IllegalStateException("RequestMethod " + method + " Not Yet Implemented");
		}
		
		final URL rurl;
		try {
			switch (method) {
				case GET:
				rurl = new URL((content == null) ? url : url + "?" + content);
					break;
				default:
					rurl = new URL(url);
			}
		} catch (MalformedURLException ex) {
			throw new IllegalArgumentException(ex.getMessage(), ex);
		}
		
		long beginTime = System.nanoTime();

		try {
			final HttpURLConnection connection = (HttpURLConnection) rurl.openConnection();
			
			try {

				connection.setDoOutput(true);
				connection.setDoInput(true);
				connection.setInstanceFollowRedirects(false);
				connection.setRequestMethod(method.httpName);
				connection.setRequestProperty("Charset", "utf-8");
				if (method == RequestMethod.POST) {
					connection.setRequestProperty("Content-Length", "" + Integer.toString(requestContent.length()));
					connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
				}
				connection.setUseCaches(false);
				connection.setReadTimeout(DEFAULt_READ_TIMEOUT);

				final DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
				if (method == RequestMethod.POST) {
					wr.writeBytes(requestContent);
				}
				wr.flush();
				wr.close();

				final String responseContent = IOUtil.readString(connection.getInputStream());

				final int code = connection.getResponseCode();
				if (handler != null) {
					final Map<String, String> headers = new HashMap<String, String>();
					extractHeadersFromConnectionResponse(connection, headers);
					long endTime = System.nanoTime();
					long duration = endTime - beginTime;
					handler.onResponseReceived(code, responseContent, headers, duration);
				}
				return code;
			} finally {

				connection.disconnect();

			}
		} catch (IOException ioex) {
			return 0;
		}
	}
	
	public static String serializeUrlParameters(final Map<String, Object> parameters) {
		return serializeUrlParameters(parameters, "&", "=", true);
	}
	
	public static String serializeContentParameters(final Map<String, Object> parameters) {
		return serializeUrlParameters(parameters, "\r\n", "=", false);
	}

	private static void extractHeadersFromConnectionResponse(final HttpURLConnection connection, final Map<String, String> headers)
 {
		//extractHeadersFromResponseStream(connection.getInputStream(), headers);
		extractHeadersFromResponseHeaders(connection.getHeaderFields(), headers);
	}
	
	private static void extractHeadersFromResponseHeaders(final Map<String, List<String>> receivedHeaders, final Map<String, String> headers) {
		for (final Map.Entry<String, List<String>> entry : receivedHeaders.entrySet()) {
			if (entry.getValue().size() > 0) {
				headers.put(entry.getKey(), entry.getValue().get(0));
			}
		}
	}
	
	private static String serializeUrlParameters(final Map<String, Object> parameters, final String parameterSeparator, final String parameterSetter, final boolean encode) {
		if (parameters == null) {
			return null;
		}
		
		final StringBuffer result = new StringBuffer();
		boolean firstAddo = true;
		
		for (String key : parameters.keySet()) {
			if (firstAddo) {
				firstAddo = false;
			} else {
				result.append(parameterSeparator);
			}
			
			String value = parameters.get(key).toString();
			
			if (encode) {
				try {
					key = URLEncoder.encode(key, "UTF-8");
					value = URLEncoder.encode(value, "UTF-8");
				} catch (final UnsupportedEncodingException e) {
					throw new IllegalArgumentException("Unable to encode value from UTF-8 to URL");
				}
			}
			
			result.append(key + parameterSetter + value);
		}
		
		return result.toString();
	}
	
	public static String decode(InputStream inputStream) throws IOException {
		String content = IOUtil.readString(inputStream);
		return URLDecoder.decode(content, "UTF-8");
	}

	public static Map<String, String> extractKeyValueFromHeaderValue(final String headerValue) {
		final String pieces[] = headerValue.split(";");
		final Map<String, String> values = new HashMap<String, String>();
		for (String piece : pieces) {
			if (piece == null) {
				continue;
			}
			piece = piece.trim();
			if ("".equals(piece)) {
				continue;
			}
			final String piecesPieces[] = piece.split("=");
			if (piecesPieces.length != 2) {
				continue;
			}
			String key = piecesPieces[0];
			String value = piecesPieces[1];
			if (key != null) {
				key = key.trim();
			}
			if (value != null) {
				value = value.trim();
			}
			values.put(key, value);
		}
		return values;
	}
	
	public static String escapeURL(final String string)
	throws UnsupportedEncodingException {
		return URLEncoder.encode(string, "UTF-8");
	}
	
	public static int get(final String url, Response reponse) {
		return request(url, RequestMethod.GET, (String) null, reponse);
	}

	public static int get(final String url, RequestEventHandler handler) {
		return doRequest(url, RequestMethod.GET, handler);
	}

	public static int get(final String url) {
		return doRequest(url, RequestMethod.GET, (String) null, null);
	}

	public static int zzz() {
		return 200;
	}

	public static int post(final String url, String content, Response reponse) {
		return request(url, RequestMethod.POST, content, reponse);
	}

	public static int post(final String url, String content, RequestEventHandler handler) {
		return doRequest(url, RequestMethod.POST, content, handler);
	}

	public static int post(final String url, String content) {
		return doRequest(url, RequestMethod.POST, content, null);
	}


	public static String fetchURL(String url) {
		final Response response = new Response();
		if (get(url, response) != 200) {
			return null;
		}
		return response.content;
	}

}
