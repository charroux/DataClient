package jra;

public class ConnectionError extends Error{
	
	public ConnectionError(String message){
		super(message);
	}

	public ConnectionError(){
		super();
	}
}
