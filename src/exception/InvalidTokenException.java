package exception;

public class InvalidTokenException extends Exception {

    public InvalidTokenException(String message)
    {
        super(message);
    }

    public InvalidTokenException(int line, char token)
    {
        super("Error  na Linha("+ line +"): Token '" + token + "' Nao esperado");
    }

    public InvalidTokenException(int line, String token)
    {
        super("Error na Linha("+ line +"): Token '" + token + "' Nao esperado");
    }

}
