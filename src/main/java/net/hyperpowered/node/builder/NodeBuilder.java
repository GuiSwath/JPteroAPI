package net.hyperpowered.node.builder;

import lombok.Data;
import lombok.experimental.Accessors;
import net.hyperpowered.interfaces.Builder;
import org.json.JSONObject;

@Data
@Accessors(chain = true)
public class NodeBuilder implements Builder {

    private String name;
    private Long location_id;
    private String fqdn;
    private String scheme;
    private Long memory;
    private Long memory_overallocate;
    private Long disk;
    private Long disk_overallocate;
    private Long upload_size;
    private Long daemon_sftp;
    private Long daemon_listen;

    @Override
    public JSONObject buildToJSON() throws IllegalArgumentException {
        if (this.name == null || this.location_id == null || this.fqdn == null || this.scheme == null || this.memory == null || this.memory_overallocate == null || this.disk == null || this.disk_overallocate == null || this.upload_size == null || this.daemon_sftp == null || this.daemon_listen == null) {
            throw new IllegalArgumentException("OS ARGUMENTOS NAO PODEM SER NULOS!");
        }

        JSONObject node = new JSONObject();
        node.put("name", this.name);
        node.put("location_id", this.location_id);
        node.put("fqdn", this.fqdn);
        node.put("scheme", this.scheme);
        node.put("memory", this.memory);
        node.put("memory_overallocate", this.memory_overallocate);
        node.put("disk", this.disk);
        node.put("disk_overallocate", this.disk_overallocate);
        node.put("upload_size", this.upload_size);
        node.put("daemon_sftp", this.daemon_sftp);
        node.put("daemon_listen", this.daemon_listen);
        return node;
    }
}
