package firstwebapp.util;

public class ListPropertiesData {

    public String username;
    public long validTo;
    public String verifier;

    public String parcelVerified;
    public float uprightLat;
    public float uprightLong;
    public float downleftLat;
    public float downleftLong;

    public ListPropertiesData() {

    }

    public ListPropertiesData(String username, long validTo, String verifier, float uprightLat, float uprightLong,
                              float downleftLat, float downleftLong) {
        this.username = username;
        this.validTo = validTo;
        this.verifier = verifier;
        this.uprightLat = uprightLat;
        this.uprightLong = uprightLong;
        this.downleftLat = downleftLat;
        this.downleftLong = downleftLong;
    }
}
