package firstwebapp.util;

public class ListUsersData {

    public String username;
    public String role;
    public long validTo;
    public String verifier;

    public ListUsersData() {

    }

    public ListUsersData(String username, String role, long validTo, String verifier) {
        this.username = username;
        this.role = role;
        this.validTo = validTo;
        this.verifier = verifier;
    }
}