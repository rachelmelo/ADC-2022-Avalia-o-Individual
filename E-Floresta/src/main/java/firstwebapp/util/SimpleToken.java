package firstwebapp.util;

public class SimpleToken {

    public String username;
    public long validTo;
    public String verifier;

    public SimpleToken() {

    }

    public SimpleToken(String username, long validTo) {
        this.username = username;
        this.validTo = validTo;
        this.verifier = "secret";
    }
}
