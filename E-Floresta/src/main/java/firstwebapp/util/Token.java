package firstwebapp.util;

public class Token {

    public String username;
    public String role;
    public long validFrom;
    public long validTo;
    public String verifier;

    public static final long EXPIRATION_TIME = 1000*60*60*2;

    public Token() {

    }

    public Token(String username, String role) {
        this.username = username;
        this.role = role;
        this.validFrom = System.currentTimeMillis();
        this.validTo = this.validFrom + EXPIRATION_TIME;
        this.verifier = "secret";
    }

}
