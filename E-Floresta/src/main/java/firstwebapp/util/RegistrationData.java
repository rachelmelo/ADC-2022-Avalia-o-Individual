package firstwebapp.util;

public class RegistrationData {

    public String username;
    public String email;
    public String name;
    public String password;
    public String confirmation;
    public String profile;
    public String phone;
    public String cellPhone;
    public String address;
    public String addressC;
    public String localidade;
    public String cp;
    public String nif;

    public RegistrationData() {

    }

    public RegistrationData(String username, String email, String name, String password, String confirmation, String profile,
                            String phone, String cellPhone, String address, String addressC, String localidade,
                            String cp, String nif) {
        this.username = username;
        this.password = password;
        this.confirmation = confirmation;
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
    }

    public boolean validRegistration() {
        boolean checkNull = !(username.equals("") || password.equals("") || confirmation.equals("") || email.equals("") || name.equals(""));
        boolean checkPassword = password.equals(confirmation) && password.length()>=6;
        boolean checkEmail = true;

        String[] emailC = email.split("@");
        if(emailC.length<2){
            checkEmail = false;
        }

        String[] dns = emailC[1].split("\\.");
        if(dns.length<2){
            checkEmail = false;
        }

        return checkNull && checkPassword && checkEmail;
    }
}
