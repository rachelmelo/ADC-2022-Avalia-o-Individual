package pt.unl.fct.di.adc.firstwebapp.resources;

import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.appengine.repackaged.org.apache.commons.codec.digest.DigestUtils;
import com.google.cloud.Timestamp;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.Transaction;


@Path("/register")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class Register {
    
    private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());
    
    private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
    
    public Register() {
        
    }
    
    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response doRegistrationV2(RegistrationData data) {

        if(!data.validRegistration()) {
            return Response.status(Status.BAD_REQUEST).entity("Missing or wrong parameter.").build();
        }

        Transaction txn = datastore.newTransaction();

        try{
            Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.username);
            Entity user = datastore.get(userKey);

            if(user != null){
                txn.rollback();
                return Response.status(Status.BAD_REQUEST).build();
            }
            else{
                user = Entity.newBuilder(userKey)
                        .set("user_name", data.name)
                        .set("user_pwd", DigestUtils.sha512Hex(data.password))
                        .set("user_email", data.email)
                        .set("user_prof", data.prof)
                        .set("user_phone", data.phone)
                        .set("user_cell", data.cell)
                        .set("user_ad", data.ad)
                        .set("user_cad", data.cad)
                        .set("user_postal", data.postal)
                        .set("user_nif", data.nif)
                        .set("user_role", "USER")
                        .set("user_state", "INACTIVE")
                        .build();
            }

            txn.add(user);
            LOG.info("User registered " + data.username);
            txn.commit();

            return Response.ok("User created successfully").build();
        }
        finally {
            if(txn.isActive()){
                txn.rollback();
            }
        }

    }

}
