package model;

import constant.Method;

public record Request(
		Method method,
		String urlPath,
		Headers headers,
		byte[] body
) {}
