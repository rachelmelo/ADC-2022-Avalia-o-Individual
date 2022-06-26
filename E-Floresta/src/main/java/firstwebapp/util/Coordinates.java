package firstwebapp.util;

public class Coordinates {
    public float uprightLat;
    public float uprightLong;
    public float downleftLat;
    public float downleftLong;


    public Coordinates() {

    }

    public Coordinates(float uprightLat, float uprightLong, float downleftLat, float downleftLong) {
        this.uprightLat = uprightLat;
        this.uprightLong = uprightLong;
        this.downleftLat = downleftLat;
        this.downleftLong = downleftLong;
    }
}
