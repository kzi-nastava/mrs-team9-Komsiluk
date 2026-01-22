package rs.ac.uns.ftn.iss.Komsiluk.services.exceptions;

public class AccountNotActivatedException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	public AccountNotActivatedException() {
        super("Account is not activated. Please check your email.");
    }
}


