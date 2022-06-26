package firstwebapp.util;

public class ModifyData {

    public String user;
    public String userToModify;
    public String role;
    public long validTo;
    public String verifier;

    public String email;
    public String name;
    public String password;
    public String profile;
    public String phone;
    public String cellPhone;
    public String address;
    public String addressC;
    public String localidade;
    public String cp;
    public String nif;
    public String state;
    public String newRole;


    public ModifyData() {

    }

    public ModifyData(String user, String userToModify, String role, long validTo, String verifier, String email,
                      String name, String password, String profile, String phone, String cellPhone, String address,
                      String addressC, String localidade, String cp, String nif , String state, String newRole) {
        this.user = user;
        this.userToModify = userToModify;
        this.role = role;
        this.validTo = validTo;
        this.verifier = verifier;
        this.email = email;
        this.name = name;
        this.password = password;
        this.profile = profile;
        this.phone = phone;
        this.cellPhone = cellPhone;
        this.address = address;
        this.addressC = addressC;
        this.localidade = localidade;
        this.cp = cp;
        this.nif = nif;
        this.state = state;
        this.newRole = newRole;
    }
}
