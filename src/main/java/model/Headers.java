package model;

import java.io.Closeable;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class Headers implements Cloneable {

	private final Map<String, String> storage;

	public static final String ACCEPT_ENCODING = "Accept-Encoding";
	public static final String CONTENT_ENCODING = "Content-Encoding";
	public static final String CONTENT_LENGTH = "Content-Length";
	public static final String CONTENT_TYPE = "Content-Type";
	public static final String USER_AGENT = "User-Agent";

	public Headers() {
		storage = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
	}

	public Headers(Headers headers) {
		this();

		this.storage.putAll(headers.storage);
	}

	public int getContentLength() {
		return getAsInteger(CONTENT_LENGTH, 0);
	}

	public String getUserAgent() {
		return storage.get(USER_AGENT);
	}

	public Headers put(String key, String value) {
		storage.put(key, value);
		return this;
	}

	public int getAsInteger(String key, int defaultValue) {
		final var raw = storage.get(key);
		return raw != null ? Integer.parseInt(raw) : defaultValue;
	}

	public Set<Map.Entry<String, String>> entrySet() {
		return storage.entrySet();
	}

	@Override
	public Headers clone() {
		return new Headers(this);
	}

}
