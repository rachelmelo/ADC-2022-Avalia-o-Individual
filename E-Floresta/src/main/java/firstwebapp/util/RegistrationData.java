package firstwebapp.util;

public class RegistrationData {

    public String username;
    public String email;
    public String name;
    public String password;
    public String confirmation;
    public String profile;
    public String phone;
    public String cellphone;
    public String address;
    public String addressC;
    public String localidade;
    public String cp;
    public String nif;

    public RegistrationData() {

    }

    public RegistrationData(String username, String email, String name, String password, String confirmation, String profile,
                            String phone, String cellphone, String address, String addressC, String localidade,
                            String cp, String nif) {
        this.username = username;
        this.password = password;
        this.confirmation = confirmation;
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
    }

    public boolean validRegistration() {
        boolean valid = true;

        if (username.equals("") || password.equals("") || confirmation.equals("") || email.equals("") || name.equals("")) {
            valid = false;
        }

        if (!password.equals(confirmation) || password.length()<6) {
            valid = false;
        }

        if (!this.email.matches(".+@.+\\..+")) {
            valid = false;
        }

        if (!this.cp.equals("-") && !this.cp.matches("[0-9][0-9][0-9][0-9]-[0-9][0-9][0-9]")) {
            valid = false;
        }


        return valid;
    }
}
