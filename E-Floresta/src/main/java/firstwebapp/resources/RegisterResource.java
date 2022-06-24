package firstwebapp.resources;

import com.google.cloud.Timestamp;
import com.google.cloud.datastore.*;
import com.google.gson.Gson;
import firstwebapp.util.LoginData;
import firstwebapp.util.RegistrationData;
import firstwebapp.util.Token;
import org.apache.commons.codec.digest.DigestUtils;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;

@Path("/ti")
public class RegisterResource {
    private static final Logger LOG = Logger.getLogger(RegisterResource.class.getName());

    private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

    private final Gson g = new Gson();


    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response register(RegistrationData data){
        if(!data.validRegistration()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Missing or wrong parameter.").build();
        }
        Transaction txn = datastore.newTransaction();

        try{
            Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.username);
            Entity user = datastore.get(userKey);

            if(user != null){
                txn.rollback();
                return Response.status(Response.Status.CONFLICT).entity("User already exists.").build();
            }
            else{
                user = Entity.newBuilder(userKey)
                        .set("user_name", data.name)
                        .set("user_pwd", DigestUtils.sha512Hex(data.password))
                        .set("user_email", data.email)
                        .set("user_profile", data.profile)
                        .set("user_phone", data.phone)
                        .set("user_cellPhone", data.cellPhone)
                        .set("user_address", data.address)
                        .set("user_addressC", data.addressC)
                        .set("user_cp", data.cp)
                        .set("user_nif", data.nif)
                        .set("user_creation_time", Timestamp.now())
                        .set("user_role", "USER")
                        .set("user_state", "INACTIVE")
                        .build();
            }

            txn.add(user);
            txn.commit();

            return Response.ok("User created successfully").build();
        }
        finally {
            if(txn.isActive()){
                txn.rollback();
            }
        }

    }


    @POST
    @Path("/deleteUser")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deleteUser(RegistrationData data) {
        if (!data.validRegistration()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Missing or wrong parameter.").build();
        }
        Transaction txn = datastore.newTransaction();

        try {
            Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.username);
            Entity user = datastore.get(userKey);

            if (user != null) {
                txn.rollback();
                return Response.status(Response.Status.CONFLICT).entity("User already exists.").build();
            } else {
                user = Entity.newBuilder(userKey)
                        .set("user_name", data.name)
                        .set("user_pwd", DigestUtils.sha512Hex(data.password))
                        .set("user_email", data.email)
                        .set("user_profile", data.profile)
                        .set("user_phone", data.phone)
                        .set("user_cellPhone", data.cellPhone)
                        .set("user_address", data.address)
                        .set("user_addressC", data.addressC)
                        .set("user_cp", data.cp)
                        .set("user_nif", data.nif)
                        .set("user_creation_time", Timestamp.now())
                        .set("user_role", "USER")
                        .set("user_state", "INACTIVE")
                        .build();
            }

            txn.add(user);
            txn.commit();

            return Response.ok("User created successfully").build();
        } finally {
            if (txn.isActive()) {
                txn.rollback();
            }
        }
    }

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response login(LoginData data) {
        Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.username);
        Entity user = datastore.get(userKey);

        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("User doesn't exist.").build();
        }

        if (!user.getString("user_pwd").equals(DigestUtils.sha512Hex(data.password))) {
            return Response.status(Response.Status.FORBIDDEN).entity("Wrong user or password.").build();
        }

        Token token = new Token(data.username, user.getString("user_role"));

        return Response.ok(g.toJson(token)).build();
    }


}
