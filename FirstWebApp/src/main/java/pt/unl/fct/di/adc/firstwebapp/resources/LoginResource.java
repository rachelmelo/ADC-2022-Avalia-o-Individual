package pt.unl.fct.di.adc.firstwebapp.resources;

import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.cloud.datastore.*;
import com.google.gson.Gson;

import pt.unl.fct.di.adc.firstwebapp.util.AuthToken;
import pt.unl.fct.di.adc.firstwebapp.util.LoginData;

@Path("/login")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class LoginResource {
	
	/**
	 * A Logger Object 
	 */
	private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());
	
	private final Gson g = new Gson();
	
	public LoginResource() {} //Could be omitted
	
	@POST
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response login(LoginData data){
        LOG.fine("Login attempt.");
        
        final Datastore ds = DatastoreOptions.getDefaultInstance().getService();

        Key userKey = ds.newKeyFactory().setKind("User").newKey(data.username);
        Entity user = ds.get(userKey);

        AuthToken authToken = new AuthToken(data.username, user.getString("user_role"));

        Key tokenId = ds.newKeyFactory().setKind("Token").newKey(authToken.tokenID);
        Entity token = Entity.newBuilder(tokenId)
                        .set("token_userId", data.username)
                        .set("token_role", authToken.role)
                        .set("token_valid_from", authToken.validFrom)
                        .set("token_valid_to", authToken.validTo).build();

        Transaction txn = ds.newTransaction();

        try{
            if(user != null){
                String password = user.getString("user_pwd");
                if(password.equals(org.apache.commons.codec.digest.DigestUtils.sha512Hex(data.password))){
                    txn.add(token);
                    txn.commit();
                    LOG.info("User " + data.username + " logged in sucessfully.");
                    return Response.ok(g.toJson(authToken)).build();

                }
                else{
                    txn.rollback();
                    LOG.warning("Wrong passwordor username.");
                    return Response.status(Status.FORBIDDEN).build();
                }
            }
        }
        finally {
            if(txn.isActive()){
                txn.rollback();
            }
        }
        LOG.warning("Failed login attempt.");
        return Response.status(Status.FORBIDDEN).build();
    }
}
