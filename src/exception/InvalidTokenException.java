package exception;

public class InvalidTokenException extends Exception {

    public InvalidTokenException(String message)
    {
        super(message);
    }

    public InvalidTokenException(int line, char token)
    {
        super("Error("+ line +"): Token '" + token + "' inválido");
    }

    public InvalidTokenException(int line, String token)
    {
        super("Error("+ line +"): Token '" + token + "' inválido");
    }

}
