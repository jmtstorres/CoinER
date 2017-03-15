package com.hammersoft.coiner.core.post.exception;

public class HttpPostException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8659457385547494552L;

	public HttpPostException() {
		super();
	}

	public HttpPostException(String message, Throwable cause) {
		super(message, cause);
	}

	public HttpPostException(String message) {
		super(message);
	}

	public HttpPostException(Throwable cause) {
		super(cause);
	}

}
