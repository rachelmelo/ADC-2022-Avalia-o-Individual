package pt.unl.fct.di.adc.firstwebapp.util;

import java.util.UUID;

public class AuthToken {
    
    public String username;
    public String tokenID;
    public String role;
    public long validFrom;
    public long validTo;
    
    public static final long EXPIRATION_TIME = 1000*60*60*24*2; //2 days
    
    public AuthToken() {
        
    }
    
    public AuthToken(String username, String role) {
        this.username = username;
        this.tokenID = UUID.randomUUID().toString();
        this.role = role;
        this.validFrom = System.currentTimeMillis();
        this.validTo = this.validFrom + EXPIRATION_TIME;    
    }

}