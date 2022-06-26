package firstwebapp.util;

public class ChangePassData {

    public String username;
    public String role;
    public long validTo;
    public String verifier;

    public String oldPassword;
    public String newPassword;
    public String confirmPassword;

    public ChangePassData() {

    }

    public ChangePassData(String username, String role, long validTo, String verifier, String oldPassword, String newPassword, String confirmPassword) {
        this.username = username;
        this.role = role;
        this.validTo = validTo;
        this.verifier = verifier;
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
        this.confirmPassword = confirmPassword;
    }

}
