package net.hyperpowered.server.builder;

import lombok.Data;
import lombok.experimental.Accessors;
import net.hyperpowered.interfaces.Builder;
import org.json.JSONObject;

@Data
@Accessors(chain = true)
public class DatabaseBuilder implements Builder {

    private String database;
    private String remote;
    private Long host;

    @Override
    public JSONObject buildToJSON() throws IllegalArgumentException {
        if (this.database == null || this.remote == null || this.host == null)
            throw new IllegalArgumentException("OS ARGUMENTOS NAO PODEM SER NULOS!");

        JSONObject response = new JSONObject();
        response.put("database", this.database);
        response.put("remote", this.remote);
        response.put("host", this.host);
        return response;
    }
}
