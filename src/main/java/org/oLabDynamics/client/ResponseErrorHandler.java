package org.oLabDynamics.client;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;

public class ResponseErrorHandler implements org.springframework.web.client.ResponseErrorHandler {

	@Override
	public void handleError(ClientHttpResponse response) throws IOException {
		if (response.getStatusCode() != HttpStatus.OK) {
			throw new ExecShareException(response.getStatusText());
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
