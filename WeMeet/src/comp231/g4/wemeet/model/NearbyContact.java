package comp231.g4.wemeet.model;

import com.google.android.gms.maps.model.LatLng;

public class NearbyContact {
	public String phoneNumber;
    public LatLng location;
    public String lastSeen;
    public double distance;
    
    public NearbyContact(String phoneNumber, LatLng location, double distance, String lastSeen) {
        this.phoneNumber = phoneNumber;
        this.location = location;
        this.lastSeen = lastSeen;
        this.distance = distance;
    }
}
