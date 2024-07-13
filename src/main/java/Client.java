import constant.Method;
import constant.Status;
import model.Headers;
import model.Request;
import model.Response;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

public class Client implements Runnable{

	public static final String HTTP_1_1 = "HTTP/1.1";
	public static final byte[] HTTP_1_1_BYTES = HTTP_1_1.getBytes();
	public static final String CRLF = "\r\n";
	public static final byte[] CRLF_BYTES = CRLF.getBytes();
	public static final byte SPACE_BYTE = ' ';
	public static final byte[] COLON_SPACE_BYTES = { ':' , ' ' };
	private static final AtomicInteger ID_INCREMENT = new AtomicInteger();
	private static final Pattern ECHO_PATTERN = Pattern.compile("\\/echo\\/(.*)");
	private static final Pattern FILES_PATTERN = Pattern.compile("\\/files\\/(.+)");

	private final int id;

	private final Socket socket;

	public Client(Socket socket) {
		this.id = ID_INCREMENT.incrementAndGet();
		this.socket = socket;
	}

	@Override
	public void run() {
		System.out.println("%d: connected".formatted(id));

		try (socket) {
			final var inputStream = new BufferedInputStream(socket.getInputStream());
			final var outputStream = new BufferedOutputStream(socket.getOutputStream());

			var request = parseRequest(inputStream);
			System.out.println(request.urlPath());

			var response = handler(request);
			System.out.println(response);

			writeResponse(response, outputStream);
		} catch (IOException e) {
			System.err.println("%d: returned an error: %s".formatted(id, e.getMessage()));
			e.printStackTrace();
		}

		System.out.println("%d: disconnected".formatted(id));
	}

	public Request parseRequest(InputStream inputStream) throws IOException {
		if (inputStream == null) {
			throw new IOException("null input stream");
		}
		final var reader = new BufferedReader(new InputStreamReader(inputStream));

		var line = reader.readLine();
		var scanner = new Scanner(line);

		final var method = Method.valueOf(scanner.next());

		final var urlPath = scanner.next();
		if (!urlPath.startsWith("/")) {
			throw new IllegalStateException("URL path does not starts with '/'");
		}

		final var httpVersion = scanner.next();
		if (!HTTP_1_1.equals(httpVersion)) {
			throw new IllegalStateException(String.format("Server does not support HTTP version %s", httpVersion));
		}

		scanner.close();

		final var headers = new Headers();

		while (!(line = reader.readLine()).isEmpty()) {
			final var headerArr = line.split(":", 2);

			if (headerArr.length != 2) {
				throw new IllegalStateException("missing header value : " + line);
			}

			final var key = headerArr[0];
			final var value = headerArr[1].stripLeading();

			headers.put(key, value);
		}

		if (Method.POST.equals(method)) {
			final var contentLength = headers.getContentLength();
			final var body = inputStream.readNBytes(contentLength);

			return new Request(method, urlPath, headers, body);
		}

		return new Request(method, urlPath, headers, null);
	}

	public Response handler(Request request) {
		return switch (request.method()) {
			case GET -> handleGet(request);
			case POST -> handlePost(request);
		};
	}

	public Response handleGet(Request request) {
		if (request.urlPath().equals("/")) {
			return Response.status(Status.OK);
		}

		if (request.urlPath().equals("/user-agent")) {
			final var userAgent = request.headers().getUserAgent();

			return Response.plainText(userAgent);
		}

		{
			final var matcher = ECHO_PATTERN.matcher(request.urlPath());
			if (matcher.find()) {
				final var content = matcher.group(1);

				return Response.plainText(content);
			}
		}

		return Response.status(Status.NOT_FOUND);
	}

	public Response handlePost(Request request) {

		return Response.status(Status.OK);
	}

	public void writeResponse(Response response, OutputStream outputStream) throws IOException {
		// first section
		outputStream.write(HTTP_1_1_BYTES);
		outputStream.write(SPACE_BYTE);

		outputStream.write(response.status().printResponseLine().getBytes());
		outputStream.write(CRLF_BYTES);

		// second section
		for (final var entry : response.headers().entrySet()) {
			final var key = entry.getKey();
			if (Headers.CONTENT_LENGTH.equals(key)) {
				continue;
			}

			final var value = entry.getValue();
			outputStream.write(key.getBytes());
			outputStream.write(COLON_SPACE_BYTES);
			outputStream.write(value.getBytes());
			outputStream.write(CRLF_BYTES);
		}

		final var body = response.body();
		if (body != null) {
			outputStream.write(Headers.CONTENT_LENGTH.getBytes());
			outputStream.write(COLON_SPACE_BYTES);
			outputStream.write(String.valueOf(body.length).getBytes());
			outputStream.write(CRLF_BYTES);
		}

		outputStream.write(CRLF_BYTES);

		if (body != null) {
			outputStream.write(body);
		}

		outputStream.flush();
	}
}


