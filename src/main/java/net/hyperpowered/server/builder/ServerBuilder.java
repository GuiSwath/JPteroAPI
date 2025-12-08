package net.hyperpowered.server.builder;

import lombok.Data;
import lombok.experimental.Accessors;
import net.hyperpowered.interfaces.Builder;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

@Data
@Accessors(chain = true)
public class ServerBuilder implements Builder {

    private String name;
    private Long user;
    private Long egg;
    private String dockerImage;
    private String startup;
    private String externalId;
    private int node = 1;
    private JSONObject environment;
    private Builder serverLimitBuilder;
    private Builder serverFeatureLimitBuilder;
    private Builder serverAllocationBuilder;

    public ServerBuilder appendDockerImage(@NotNull String dockerImage) {
        this.dockerImage = dockerImage.replace("\\", "/");
        return this;
    }

    @Override
    public JSONObject buildToJSON() throws IllegalArgumentException {
        if (this.name == null || this.user == null || this.egg == null || this.dockerImage == null || this.environment == null
                || this.serverLimitBuilder == null || this.serverFeatureLimitBuilder == null || this.serverAllocationBuilder == null
        ) throw new IllegalArgumentException("OS ARGUMENTOS NAO PODEM SER NULOS!");


        JSONObject response = new JSONObject();
        response.put("name", name);
        response.put("user", user);
        response.put("egg", egg);
        response.put("docker_image", dockerImage);
        response.put("environment", environment);
        response.put("limits", serverLimitBuilder.buildToJSON());
        response.put("feature_limits", serverFeatureLimitBuilder.buildToJSON());
        response.put("allocation", serverAllocationBuilder.buildToJSON());
        response.put("node", node);

        if (startup != null) response.put("startup", startup);
        if (externalId != null && !externalId.isEmpty()) response.put("external_id", externalId);

        return response;
    }
}
