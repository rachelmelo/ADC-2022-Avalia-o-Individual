package firstwebapp.util;

public class ShowData {

    public String email;
    public String name;
    public String profile;
    public String phone;
    public String cellPhone;
    public String address;
    public String addressC;
    public String localidade;
    public String cp;
    public String nif;
    public String state;

    public ShowData() {

    }

    public ShowData(String email, String name, String profile, String phone, String cellPhone, String address, String addressC,
                    String localidade, String cp, String nif, String state) {
        this.email = email;
        this.name = name;
        this.profile = profile;
        this.phone = phone;
        this.cellPhone = cellPhone;
        this.address = address;
        this.addressC = addressC;
        this.localidade = localidade;
        this.cp = cp;
        this.nif = nif;
        this.state = state;
    }
}
