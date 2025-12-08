package net.hyperpowered.server.builder;

import lombok.Data;
import lombok.experimental.Accessors;
import net.hyperpowered.interfaces.Builder;
import org.json.JSONObject;

@Data
@Accessors(chain = true)
public class ServerLimitBuilder implements Builder {

    private Long memory;
    private Long swap;
    private Long disk;
    private Long io;
    private Long cpu;

    @Override
    public JSONObject buildToJSON() throws IllegalArgumentException {
        if (this.memory == null || this.swap == null || this.disk == null
                || this.io == null || this.cpu == null
        ) throw new IllegalArgumentException("OS ARGUMENTOS NAO PODEM SER NULOS!");

        JSONObject response = new JSONObject();
        response.put("memory", memory);
        response.put("swap", swap);
        response.put("disk", disk);
        response.put("io", io);
        response.put("cpu", cpu);
        return response;
    }
}
