package firstwebapp.resources;

import com.google.cloud.Timestamp;
import com.google.cloud.datastore.*;
import com.google.gson.Gson;
import firstwebapp.util.*;
import org.apache.commons.codec.digest.DigestUtils;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Path("/ti")
public class RegisterResource {

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
            else {
                Entity.Builder builder = Entity.newBuilder(userKey);

                builder.set("user_username", data.username)
                        .set("user_email", data.email)
                        .set("user_name", data.name)
                        .set("user_pwd", DigestUtils.sha512Hex(data.password));

                builder = data.profile != null ? builder.set("user_profile", data.profile) : builder.set("user_profile", "INDEFINIDO");
                builder = !data.phone.equals("") ? builder.set("user_phone", data.phone) : builder.set("user_phone", "INDEFINIDO");
                builder = !data.cellphone.equals("") ? builder.set("user_cellphone", data.cellphone) : builder.set("user_cellphone", "INDEFINIDO");
                builder = !data.address.equals("") ? builder.set("user_address", data.address) : builder.set("user_address", "INDEFINIDO");
                builder = !data.addressC.equals("") ? builder.set("user_addressC", data.addressC) : builder.set("user_addressC", "INDEFINIDO");
                builder = !data.localidade.equals("") ? builder.set("user_localidade", data.localidade) : builder.set("user_localidade", "INDEFINIDO");
                builder = !data.cp.equals("-") ? builder.set("user_cp", data.cp) : builder.set("user_cp", "INDEFINIDO");
                builder = !data.nif.equals("") ? builder.set("user_nif", data.nif) : builder.set("user_nif", "INDEFINIDO");

                user = builder.set("user_creation_time", Timestamp.now())
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
    public Response deleteUser(DeleteData data) {
        Key userToDeleteKey = datastore.newKeyFactory().setKind("User").newKey(data.usernameToDelete);
        Entity userToDelete = datastore.get(userToDeleteKey);

        if (userToDelete == null) {
            return Response.status(Response.Status.CONFLICT).entity("User to delete does not exist.").build();
        }

        if (!data.verifier.equals("secret")) {
            return Response.status(Response.Status.FORBIDDEN).entity("Wrong verifier.").build();
        }

        if (data.validTo < System.currentTimeMillis()) {
            return Response.status(Response.Status.FORBIDDEN).entity("Token expired.").build();
        }

        if(data.role.equals("USER")) {
            if(!data.user.equals(data.usernameToDelete)) {
                return Response.status(Response.Status.FORBIDDEN).entity("You do not have permissions").build();
            }

        } else if(data.role.equals("GBO")) {
            if(!userToDelete.getString("user_role").equals("USER")) {
                return Response.status(Response.Status.FORBIDDEN).entity("You do not have permissions").build();
            }

        } else if(data.role.equals("GS")) {
            if(!userToDelete.getString("user_role").equals("USER") && !userToDelete.getString("user_role").equals("GBO") ) {
                return Response.status(Response.Status.FORBIDDEN).entity("You do not have permissions").build();
            }

        } else{
            if(data.user.equals(data.usernameToDelete)) {
                return Response.status(Response.Status.FORBIDDEN).entity("You do not have permissions").build();
            }
        }

        //delete
        Transaction txn = datastore.newTransaction();

        try{
            txn.delete(userToDeleteKey);
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
    @Path("/listUsers")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response listUsers(ListUsersData data) {
        Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.username);
        Entity user = datastore.get(userKey);

        if (user == null) {
            return Response.status(Response.Status.CONFLICT).entity("User does not exist.").build();
        }

        if (!data.verifier.equals("secret")) {
            return Response.status(Response.Status.FORBIDDEN).entity("Wrong verifier.").build();
        }

        if (data.validTo < System.currentTimeMillis()) {
            return Response.status(Response.Status.FORBIDDEN).entity("Token expired.").build();
        }


        Query<Entity> query;
        Entity listElement;
        List<UserInfo> userList = new LinkedList<>();

        if(data.role.equals("USER")) {
            query = Query.newEntityQueryBuilder()
                    .setKind("User")
                    .setFilter(StructuredQuery.CompositeFilter.and(
                            StructuredQuery.PropertyFilter.eq("user_role", "USER"),
                            StructuredQuery.PropertyFilter.eq("user_profile", "PUBLICO"),
                            StructuredQuery.PropertyFilter.eq("user_state", "ACTIVE")
                    ))
                    .build();

            QueryResults<Entity> queryResults = datastore.run(query);
            List<UserInfo2> usersList = new LinkedList<>();

            //TODO: so passar username, email e nome
            while(queryResults.hasNext()){
                listElement = queryResults.next();
                usersList.add(new UserInfo2(listElement.getKey().getName(),
                        listElement.getString("user_email"),
                        listElement.getString("user_name")));
            }
            return Response.ok(usersList).build();

        } else if(data.role.equals("GBO")) {
            query = Query.newEntityQueryBuilder()
                    .setKind("User")
                    .setFilter(StructuredQuery.PropertyFilter.eq("user_role", "USER"))
                    .build();

            QueryResults<Entity> queryResults = datastore.run(query);

            //TODO: lista todos os atributos
            while(queryResults.hasNext()){
                listElement = queryResults.next();
                userList.add(new UserInfo(listElement.getKey().getName(),
                        listElement.getString("user_email"),
                        listElement.getString("user_name"),
                        listElement.getString("user_profile"),
                        listElement.getString("user_phone"),
                        listElement.getString("user_cellphone"),
                        listElement.getString("user_address"),
                        listElement.getString("user_addressC"),
                        listElement.getString("user_localidade"),
                        listElement.getString("user_cp"),
                        listElement.getString("user_nif"),
                        listElement.getString("user_state"),
                        listElement.getString("user_role")));
            }

        } else if(data.role.equals("GS")) {
            Query<Entity> query2;
            query = Query.newEntityQueryBuilder()
                    .setKind("User")
                    .setFilter(StructuredQuery.PropertyFilter.eq("user_role", "GBO"))
                    .build();

            query2 = Query.newEntityQueryBuilder()
                    .setKind("User")
                    .setFilter(StructuredQuery.PropertyFilter.eq("user_role", "USER"))
                    .build();

            QueryResults<Entity> queryResults = datastore.run(query);
            QueryResults<Entity> queryResults2 = datastore.run(query2);

            while(queryResults.hasNext()){
                listElement = queryResults.next();
                userList.add(new UserInfo(listElement.getKey().getName(),
                        listElement.getString("user_email"),
                        listElement.getString("user_name"),
                        listElement.getString("user_profile"),
                        listElement.getString("user_phone"),
                        listElement.getString("user_cellphone"),
                        listElement.getString("user_address"),
                        listElement.getString("user_addressC"),
                        listElement.getString("user_localidade"),
                        listElement.getString("user_cp"),
                        listElement.getString("user_nif"),
                        listElement.getString("user_state"),
                        listElement.getString("user_role")));
            }

            while(queryResults2.hasNext()){
                listElement = queryResults2.next();
                userList.add(new UserInfo(listElement.getKey().getName(),
                        listElement.getString("user_email"),
                        listElement.getString("user_name"),
                        listElement.getString("user_profile"),
                        listElement.getString("user_phone"),
                        listElement.getString("user_cellphone"),
                        listElement.getString("user_address"),
                        listElement.getString("user_addressC"),
                        listElement.getString("user_localidade"),
                        listElement.getString("user_cp"),
                        listElement.getString("user_nif"),
                        listElement.getString("user_state"),
                        listElement.getString("user_role")));
            }

        } else{
            query = Query.newEntityQueryBuilder()
                    .setKind("User")
                    .build();

            QueryResults<Entity> queryResults = datastore.run(query);

            //TODO: lista todos os atributos
            while(queryResults.hasNext()){
                listElement = queryResults.next();
                userList.add(new UserInfo(listElement.getKey().getName(),
                        listElement.getString("user_email"),
                        listElement.getString("user_name"),
                        listElement.getString("user_profile"),
                        listElement.getString("user_phone"),
                        listElement.getString("user_cellphone"),
                        listElement.getString("user_address"),
                        listElement.getString("user_addressC"),
                        listElement.getString("user_localidade"),
                        listElement.getString("user_cp"),
                        listElement.getString("user_nif"),
                        listElement.getString("user_state"),
                        listElement.getString("user_role")));
            }
        }

        return Response.ok(g.toJson(userList)).build();
    }



    @POST
    @Path("/modifyUserAtributes")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response modifyUserAttributes(ModifyData data) {
        Key userToModifyKey = datastore.newKeyFactory().setKind("User").newKey(data.userToModify);
        Entity userToModify = datastore.get(userToModifyKey);

        if (userToModify == null) {
            return Response.status(Response.Status.CONFLICT).entity("User to modify does not exist.").build();
        }

        if (!data.verifier.equals("secret")) {
            return Response.status(Response.Status.FORBIDDEN).entity("Wrong verifier.").build();
        }

        if (data.validTo < System.currentTimeMillis()) {
            return Response.status(Response.Status.FORBIDDEN).entity("Token expired.").build();
        }


        if(data.role.equals("USER")) {
            if(!data.user.equals(data.userToModify)) {
                return Response.status(Response.Status.FORBIDDEN).entity("You do not have permissions").build();
            }
            if(!((data.name.equals(userToModify.getString("user_name")) || data.name.equals("")) && (data.email.equals(userToModify.getString("user_email")) || data.email.equals("")))) {
                return Response.status(Response.Status.FORBIDDEN).entity("You cannot change these attributes.").build();
            }

        } else if(data.role.equals("GBO")) {
            if(!userToModify.getString("user_role").equals("USER")) {
                return Response.status(Response.Status.FORBIDDEN).entity("You do not have permissions").build();
            }

        } else if(data.role.equals("GS")) {
            if(!userToModify.getString("user_role").equals("USER") && !userToModify.getString("user_role").equals("GBO") ) {
                return Response.status(Response.Status.FORBIDDEN).entity("You do not have permissions").build();
            }

        } else{
            if(userToModify.getString("user_role").equals("SU")) {
                return Response.status(Response.Status.FORBIDDEN).entity("You do not have permissions").build();
            }
        }

        //modify
        Transaction txn = datastore.newTransaction();

        try{
            userToModify = Entity.newBuilder(userToModifyKey)
                    .set("user_username", userToModify.getString("user_username"))
                    .set("user_name", data.name.equals("") ? userToModify.getString("user_name") : data.name)
                    .set("user_pwd", data.password.equals("") ? userToModify.getString("user_pwd") : DigestUtils.sha512Hex(data.password))
                    .set("user_email", data.email.equals("") ? userToModify.getString("user_email") : data.email)
                    .set("user_profile", data.profile.equals("INDEFINIDO") ? userToModify.getString("user_profile") : data.profile)
                    .set("user_phone", data.phone.equals("") ? userToModify.getString("user_phone") : data.phone)
                    .set("user_cellphone", data.cellPhone.equals("") ? userToModify.getString("user_cellphone") : data.cellPhone)
                    .set("user_address", data.address.equals("") ? userToModify.getString("user_address") : data.address)
                    .set("user_addressC", data.addressC.equals("") ? userToModify.getString("user_addressC") : data.addressC)
                    .set("user_localidade", data.localidade.equals("") ? userToModify.getString("user_localidade") : data.localidade)
                    .set("user_cp", data.cp.equals("-") ? userToModify.getString("user_cp") : data.cp)
                    .set("user_nif", data.nif.equals("") ? userToModify.getString("user_nif") : data.nif)
                    .set("user_creation_time", userToModify.getTimestamp("user_creation_time"))
                    .set("user_role", data.newRole.equals("INDEFINIDO") ? userToModify.getString("user_role") : data.newRole)
                    .set("user_state", data.state.equals("INDEFINIDO") ? userToModify.getString("user_state") : data.state)

                    .build();

            txn.put(userToModify);
            txn.commit();

            return Response.ok("User modified successfully").build();
        }
        finally {
            if(txn.isActive()){
                txn.rollback();
            }
        }
    }



    @POST
    @Path("/changePassword")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response changePassword(ChangePassData data) {
        Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.username);
        Entity user = datastore.get(userKey);

        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("User doesn't exist.").build();
        }

        if (!data.verifier.equals("secret")) {
            return Response.status(Response.Status.FORBIDDEN).entity("Wrong verifier.").build();
        }

        if (data.validTo < System.currentTimeMillis()) {
            return Response.status(Response.Status.FORBIDDEN).entity("Token expired.").build();
        }

        if (!user.getString("user_pwd").equals(DigestUtils.sha512Hex(data.oldPassword))) {
            return Response.status(Response.Status.FORBIDDEN).entity("Wrong password.").build();
        }

        if(!data.newPassword.equals(data.confirmPassword)) {
            return Response.status(Response.Status.FORBIDDEN).entity("New passwords don't match.").build();
        }


        Transaction txn = datastore.newTransaction();

        try{
            user = Entity.newBuilder(userKey)
                    .set("user_name", user.getString("user_name"))
                    .set("user_username", user.getString("user_username"))
                    .set("user_pwd", DigestUtils.sha512Hex(data.newPassword))
                    .set("user_email", user.getString("user_email"))
                    .set("user_profile", user.getString("user_profile"))
                    .set("user_phone", user.getString("user_phone"))
                    .set("user_cellphone", user.getString("user_cellphone"))
                    .set("user_address", user.getString("user_address"))
                    .set("user_addressC", user.getString("user_addressC"))
                    .set("user_cp", user.getString("user_cp"))
                    .set("user_nif", user.getString("user_nif"))
                    .set("user_creation_time", user.getTimestamp("user_creation_time"))
                    .set("user_role", user.getString("user_role"))
                    .set("user_state", user.getString("user_state"))
                    .build();

            txn.put(user);
            txn.commit();

            return Response.ok("Password changed successfully").build();
        }
        finally {
            if(txn.isActive()){
                txn.rollback();
            }
        }
    }



    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(LoginData data) {
        Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.username);
        Entity user = datastore.get(userKey);

        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Utilizador não existe.").build();
        }

        if (!user.getString("user_state").equals("ACTIVE")) {
            return Response.status(Response.Status.FORBIDDEN).entity("Conta inativa.").build();
        }

        if (!user.getString("user_pwd").equals(DigestUtils.sha512Hex(data.password))) {
            return Response.status(Response.Status.FORBIDDEN).entity("Utilizador ou password errada.").build();
        }

        Token token = new Token(data.username, user.getString("user_role"));

        return Response.ok(g.toJson(token)).build();
    }



    @POST
    @Path("/registerProperty")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response registerProperty(PropertyData data){

        Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.username);
        Entity user = datastore.get(userKey);

        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("User doesn't exist.").build();
        }

        if (!data.verifier.equals("secret")) {
            return Response.status(Response.Status.FORBIDDEN).entity("Wrong verifier.").build();
        }

        if (data.validTo < System.currentTimeMillis()) {
            return Response.status(Response.Status.FORBIDDEN).entity("Token expired.").build();
        }

        Transaction txn = datastore.newTransaction();

        try{
            Key propertyKey = datastore.newKeyFactory().setKind("Property").newKey(UUID.randomUUID().toString());
            Entity property = datastore.get(propertyKey);

            if(property != null){
                txn.rollback();
                return Response.status(Response.Status.CONFLICT).entity("Property already exists.").build();
            }
            else{
                property = Entity.newBuilder(propertyKey)
                        .set("property_type", data.propertyType)
                        .set("property_ownerName", data.ownerName)
                        .set("property_ownerNacionality", data.ownerNacionality)
                        .set("property_idDocType", data.idDocType)
                        .set("property_idNum", data.idNum)
                        .set("property_idExpiration", data.idExpiration)
                        .set("property_nif", data.nif)
                        .set("property_parcelVerified", data.role.equals("USER") ? "POR VERIFICAR" : data.parcelVerified)
                        .set("property_uprightLat", data.uprightLat)
                        .set("property_uprightLong", data.uprightLong)
                        .set("property_downleftLat", data.downleftLat)
                        .set("property_downleftLong", data.downleftLong)
                        .build();
            }

            txn.add(property);
            txn.commit();

            return Response.ok("Property registered successfully").build();
        }
        finally {
            if(txn.isActive()){
                txn.rollback();
            }
        }
    }

    @POST
    @Path("/searchProperty")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchProperty(ListPropertiesData data) {

        Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.username);
        Entity user = datastore.get(userKey);

        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("User doesn't exist.").build();
        }

        if (!data.verifier.equals("secret")) {
            return Response.status(Response.Status.FORBIDDEN).entity("Wrong verifier.").build();
        }

        if (data.validTo < System.currentTimeMillis()) {
            return Response.status(Response.Status.FORBIDDEN).entity("Token expired.").build();
        }


        Entity listElement;
        List<String> propertiesList = new LinkedList<>();

        //if pra longitude e if pra latitude? como é q distingo?
        Query<Entity> query = Query.newEntityQueryBuilder()
                .setKind("Property")
                .setFilter(StructuredQuery.PropertyFilter.lt("property_uprightLat", data.uprightLat))
                .setFilter(StructuredQuery.PropertyFilter.lt("property_uprightLong", data.uprightLong))
                .setFilter(StructuredQuery.PropertyFilter.gt("property_downleftLat", data.downleftLat))
                .setFilter(StructuredQuery.PropertyFilter.gt("property_downleftLong", data.downleftLong))
                .setFilter(StructuredQuery.PropertyFilter.eq("property_parcelVerified", "CONFIRMADO"))
                .build();

        QueryResults<Entity> queryResults = datastore.run(query);

        while (queryResults.hasNext()) {
            listElement = queryResults.next();
            propertiesList.add(listElement.getKey().getName());
        }

        return Response.ok(g.toJson(propertiesList)).build();
    }

    @POST
    @Path("/showUserAttributes")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response showUserAttributes(SimpleToken data) {

        Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.username);
        Entity user = datastore.get(userKey);

        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("User doesn't exist.").build();
        }

        if (!data.verifier.equals("secret")) {
            return Response.status(Response.Status.FORBIDDEN).entity("Wrong verifier.").build();
        }

        if (data.validTo < System.currentTimeMillis()) {
            return Response.status(Response.Status.FORBIDDEN).entity("Token expired.").build();
        }

        ShowData sd = new ShowData(user.getString("user_email"), user.getString("user_name"),
                user.getString("user_profile"), user.getString("user_phone"), user.getString("user_cellphone"),
                user.getString("user_address"), user.getString("user_addressC"), user.getString("user_localidade"),
                user.getString("user_cp"), user.getString("user_nif"),  user.getString("user_state"));

        return Response.ok(g.toJson(sd)).build();
    }
}
