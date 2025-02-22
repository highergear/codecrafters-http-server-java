package constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@RequiredArgsConstructor
@Getter
@Accessors(fluent = true)
public enum Status {

	OK(200, "OK"),
	CREATED(201, "Created"),
	NOT_FOUND(404, "Not Found");

	private final int code;
	private final String phrase;

	public String printResponseLine() {
		return String.format("%d %s", code, phrase);
	}
}
