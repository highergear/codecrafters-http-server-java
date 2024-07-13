package model;

import constant.Status;

public record Response (
	Status status,
	Headers headers,
	byte[] body
) {

	public static Response status(Status status) {
		return new Response(status, new Headers(), new byte[0]);
	}

	public static Response plainText(String content) {
		final var bytes = content.getBytes();

		return new Response(Status.OK, new Headers().put(Headers.CONTENT_TYPE, "text/plain"), bytes);
	}

}
