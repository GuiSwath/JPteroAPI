package net.hyperpowered.node;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.json.JSONObject;

@AllArgsConstructor
@Getter
public class NodeConfigSystem {

    private String data;
    private JSONObject sftp;

}
