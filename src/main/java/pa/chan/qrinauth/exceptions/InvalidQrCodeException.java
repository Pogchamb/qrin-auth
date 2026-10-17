package pa.chan.qrinauth.exceptions;

public class InvalidQrCodeException extends RuntimeException{
    public InvalidQrCodeException(String message) {
        super(message);
    }
}
