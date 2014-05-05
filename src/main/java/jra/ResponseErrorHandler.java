package jra;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;

public class ResponseErrorHandler implements org.springframework.web.client.ResponseErrorHandler {

	@Override
	public void handleError(ClientHttpResponse response) throws IOException {
		if (response.getStatusCode() != HttpStatus.OK) {
			throw new JRAException(response.getStatusText() + " " + response.toString() + " " + response.getHeaders());
			}
	}

	@Override
	public boolean hasError(ClientHttpResponse response) throws IOException {
		if (response.getStatusCode() != HttpStatus.OK) {
			return true;
		}
		return false;
	}

}
