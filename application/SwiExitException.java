package application;

public class SwiExitException extends Exception{
	private static final String message = "Swi_exit: ";
	public SwiExitException(String msg){
		super(message+msg);
	}
}