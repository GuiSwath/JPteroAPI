package net.hyperpowered.server.builder;

import lombok.Data;
import lombok.experimental.Accessors;
import net.hyperpowered.interfaces.Builder;
import org.json.JSONObject;

@Data
@Accessors(chain = true)
public class ServerFutureLimitBuilder implements Builder {

    private Long databases;
    private Long backups;
    private Long allocations;

    @Override
    public JSONObject buildToJSON() throws IllegalArgumentException {
        if (this.databases == null || this.backups == null)
            throw new IllegalArgumentException("OS ARGUMENTOS NAO PODEM SER NULOS!");

        JSONObject response = new JSONObject();
        response.put("databases", databases);
        response.put("backups", backups);
        response.put("allocations", allocations);

        return response;
    }
}
