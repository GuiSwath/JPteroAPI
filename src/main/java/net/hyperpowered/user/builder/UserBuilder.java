package net.hyperpowered.user.builder;

import lombok.Data;
import lombok.experimental.Accessors;
import net.hyperpowered.interfaces.Builder;
import org.json.JSONObject;

@Data
@Accessors(chain = true)
public class UserBuilder implements Builder {

    private String externalId;
    private String email;
    private String username;
    private String firstName;
    private String lastName;
    private String password;

    @Override
    public JSONObject buildToJSON() throws IllegalArgumentException {
        if (this.email == null || this.username == null
                || this.firstName == null || this.lastName == null
        ) throw new IllegalArgumentException("OS ARGUMENTOS NAO PODEM SER NULOS!");

        JSONObject user = new JSONObject();
        user.put("email", this.getEmail());
        user.put("username", this.getUsername());
        user.put("first_name", this.getFirstName());
        user.put("last_name", this.getLastName());

        if (password != null && !password.isEmpty()) user.put("password", this.getPassword());
        if (externalId != null && !externalId.isEmpty()) user.put("external_id", externalId);

        return user;
    }
}
