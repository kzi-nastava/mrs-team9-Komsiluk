package rs.ac.uns.ftn.iss.Komsiluk.services.exceptions;

public class AccountNotActivatedException extends RuntimeException {
    public AccountNotActivatedException() {
        super("Account is not activated. Please check your email.");
    }
}


