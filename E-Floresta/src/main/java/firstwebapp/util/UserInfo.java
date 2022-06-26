package firstwebapp.util;

public class UserInfo {

    public String username;
    public String email;
    public String name;
    public String profile;
    public String phone;
    public String cellphone;
    public String address;
    public String addressC;
    public String localidade;
    public String cp;
    public String nif;
    public String state;
    public String role;


    public UserInfo() {

    }

    public UserInfo(String username, String email, String name, String profile, String phone,
                    String cellphone, String address, String addressC, String localidade, String cp, String nif,
                    String state, String role) {
        this.username = username;
        this.email = email;
        this.name = name;
        this.profile = profile;
        this.phone = phone;
        this.cellphone = cellphone;
        this.address = address;
        this.addressC = addressC;
        this.localidade = localidade;
        this.cp = cp;
        this.nif = nif;
        this.state = state;
        this.role = role;
    }
}
