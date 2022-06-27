package firstwebapp.util;

public class PropertyData {

    public String username;
    public String role;
    public long validTo;
    public String verifier;

    public String propertyType;
    public String ownerName;
    public String ownerNacionality;
    public String idDocType;
    public String idNum;
    public String idExpiration;
    public String nif;
    public String parcelVerified;
    public float uprightLat;
    public float uprightLong;
    public float downleftLat;
    public float downleftLong;


    public PropertyData() {

    }

    public PropertyData(String username, String role, long validTo, String verifier, String propertyType, String ownerName,
                        String ownerNacionality, String idDocType, String idNum, String idExpiration, String nif,
                        float uprightLat, float uprightLong, float downleftLat, float downleftLong, String parcelVerified) {
        this.username = username;
        this.role = role;
        this.validTo = validTo;
        this.verifier = verifier;
        this.propertyType = propertyType;
        this.ownerName = ownerName;
        this.ownerNacionality = ownerNacionality;
        this.idDocType = idDocType;
        this.idNum = idNum;
        this.idExpiration = idExpiration;
        this.nif = nif;
        this.uprightLat = uprightLat;
        this.uprightLong = uprightLong;
        this.downleftLat = downleftLat;
        this.downleftLong = downleftLong;
        this.parcelVerified = parcelVerified;
    }


}
