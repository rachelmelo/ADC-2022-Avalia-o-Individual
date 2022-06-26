package firstwebapp.util;

public class DeleteData {
    public String user;
    public String usernameToDelete;
    public String role;
    public long validTo;
    public String verifier;


    public DeleteData() {

    }

    public DeleteData(String user, String usernameToDelete, String role, long validTo, String verifier) {
        this.user = user;
        this.usernameToDelete = usernameToDelete;
        this.role = role;
        this.validTo = validTo;
        this.verifier = verifier;
    }
}
