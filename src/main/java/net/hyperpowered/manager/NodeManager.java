package net.hyperpowered.manager;

import net.hyperpowered.logger.PteroLogger;
import net.hyperpowered.node.*;
import net.hyperpowered.node.builder.NodeBuilder;
import net.hyperpowered.requester.ApplicationEndpoint;
import net.hyperpowered.server.builder.AllocationBuilder;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class NodeManager extends Manager {

    private final PteroLogger LOGGER = new PteroLogger("NODES");

    public CompletableFuture<List<Node>> listNodes() {
        CompletableFuture<List<Node>> response = new CompletableFuture<>();
        fetch(ApplicationEndpoint.NODES.getEndpoint()).thenAccept(responseJson -> {
            List<Node> nodes = new ArrayList<>();
            try {
                JSONObject responseObject = responseJson.getJSONObject("response");
                JSONArray nodesJson = responseObject.getJSONArray("data");
                for (Object nodeDetails : nodesJson) {
                    Node node = parseNode(nodeDetails.toString());
                    nodes.add(node);
                }
            } catch (Exception e) {
                response.completeExceptionally(e);
            }

            response.complete(nodes);
        }).exceptionally(throwable -> {
            LOGGER.severe("OCORREU UM ERRO AO CARREGAR OS NODES: " + throwable.getMessage() + "\n");
            sendError(throwable);
            return null;
        });

        return response;
    }

    public CompletableFuture<List<Allocation>> getAllocationByPort(long nodeID, int port) {
        CompletableFuture<List<Allocation>> response = new CompletableFuture<>();
        fetch(ApplicationEndpoint.NODES.getEndpoint() + "/" + nodeID + "/allocations?filter[port]=" + port).thenAccept(responseJson -> {
            List<Allocation> allocations = new ArrayList<>();
            try {
                JSONObject responseObject = responseJson.getJSONObject("response");
                JSONArray allocationJson = responseObject.getJSONArray("data");

                for (Object allocationDetails : allocationJson) {
                    Allocation allocation = parseAllocation(allocationDetails.toString());
                    allocations.add(allocation);
                }
            } catch (Exception e) {
                response.completeExceptionally(e);
            }

            response.complete(allocations);
        }).exceptionally(throwable -> {
            LOGGER.severe("OCORREU UM ERRO AO CARREGAR AS PORTAS: " + throwable.getMessage() + "\n");
            sendError(throwable);
            return null;
        });

        return response;
    }

    public CompletableFuture<List<Allocation>> getAllocationByIP(long nodeID, String ip, int port) {
        CompletableFuture<List<Allocation>> response = new CompletableFuture<>();
        fetch(ApplicationEndpoint.NODES.getEndpoint() + "/" + nodeID + "/allocations?filter[port]=" + port+"&filter[ip]="+ip).thenAccept(responseJson -> {
            List<Allocation> allocations = new ArrayList<>();
            try {
                JSONObject responseObject = responseJson.getJSONObject("response");
                JSONArray allocationJson = responseObject.getJSONArray("data");

                for (Object allocationDetails : allocationJson) {
                    Allocation allocation = parseAllocation(allocationDetails.toString());
                    allocations.add(allocation);
                }
            } catch (Exception e) {
                response.completeExceptionally(e);
            }

            response.complete(allocations);
        }).exceptionally(throwable -> {
            LOGGER.severe("OCORREU UM ERRO AO CARREGAR AS PORTAS: " + throwable.getMessage() + "\n");
            sendError(throwable);
            return null;
        });

        return response;
    }

    public CompletableFuture<List<Allocation>> listAllocations(long nodeID) {
        CompletableFuture<List<Allocation>> response = new CompletableFuture<>();
        fetch(ApplicationEndpoint.NODES.getEndpoint() + "/" + nodeID + "/allocations").thenAccept(responseJson -> {
            List<Allocation> allocations = new ArrayList<>();
            try {
                JSONObject responseObject = responseJson.getJSONObject("response");
                JSONArray allocationJson = responseObject.getJSONArray("data");
                JSONObject meta = responseObject.getJSONObject("meta");
                JSONObject pagination = meta.getJSONObject("pagination");
                long pages = pagination.getLong("total_pages");

                for (int i = 1; i < pages; i++) {
                    List<Allocation> allcs = listAllocations(nodeID, (i + 1)).get();
                    allocations.addAll(allcs);
                }

                for (Object allocationDetails : allocationJson) {
                    Allocation allocation = parseAllocation(allocationDetails.toString());
                    allocations.add(allocation);
                }
            } catch (Exception e) {
                response.completeExceptionally(e);
            }

            response.complete(allocations);
        }).exceptionally(throwable -> {
            LOGGER.severe("OCORREU UM ERRO AO CARREGAR AS PORTAS: " + throwable.getMessage() + "\n");
            sendError(throwable);
            return null;
        });

        return response;
    }

    public CompletableFuture<List<Allocation>> listAllocations(long nodeID, int page) {
        CompletableFuture<List<Allocation>> response = new CompletableFuture<>();
        fetch(ApplicationEndpoint.NODES.getEndpoint() + "/" + nodeID + "/allocations?page=" + page).thenAccept(responseJson -> {
            List<Allocation> allocations = new ArrayList<>();
            try {
                JSONObject responseObject = responseJson.getJSONObject("response");
                JSONArray allocationJson = responseObject.getJSONArray("data");
                for (Object allocationDetails : allocationJson) {
                    Allocation allocation = parseAllocation(allocationDetails.toString());
                    allocations.add(allocation);
                }
            } catch (Exception e) {
                response.completeExceptionally(e);
            }

            response.complete(allocations);
        }).exceptionally(throwable -> {
            LOGGER.severe("OCORREU UM ERRO AO CARREGAR AS PORTAS: " + throwable.getMessage() + "\n");
            sendError(throwable);
            return null;
        });

        return response;
    }

    public CompletableFuture<Integer> getAllocationCount(long nodeID) {
        CompletableFuture<Integer> response = new CompletableFuture<>();
        fetch(ApplicationEndpoint.NODES.getEndpoint() + "/" + nodeID + "/allocations").thenAccept(responseJson -> {
            try {
                JSONObject responseObject = responseJson.getJSONObject("response");
                JSONObject meta = responseObject.getJSONObject("meta");
                JSONObject pagination = meta.getJSONObject("pagination");
                int total = Math.toIntExact(pagination.getInt("total"));
                response.complete(total);
            } catch (Exception e) {
                response.completeExceptionally(e);
            }
        }).exceptionally(throwable -> {
            LOGGER.severe("OCORREU UM ERRO AO CARREGAR AS PORTAS: " + throwable.getMessage() + "\n");
            sendError(throwable);
            return null;
        });

        return response;
    }

    public CompletableFuture<Node> getNode(long nodeID) {
        CompletableFuture<Node> response = new CompletableFuture<>();
        fetch(ApplicationEndpoint.NODES.getEndpoint() + "/" + nodeID).thenAccept(responseJson -> {
            try {
                JSONObject responseNodeJson = responseJson.getJSONObject("response");
                response.complete(parseNode(responseNodeJson.toString()));
            } catch (Exception e) {
                response.completeExceptionally(e);
            }
        }).exceptionally(throwable -> {
            LOGGER.severe("OCORREU UM ERRO AO CARREGAR O NODE: " + throwable.getMessage() + "\n");
            sendError(throwable);
            return null;
        });

        return response;
    }

    public CompletableFuture<NodeConfiguration> getNodeConfiguration(long nodeID) {
        CompletableFuture<NodeConfiguration> response = new CompletableFuture<>();
        fetch(ApplicationEndpoint.NODES.getEndpoint() + "/" + nodeID + "/configuration").thenAccept(responseJson -> {
            try {
                JSONObject responseNodeJson = responseJson.getJSONObject("response");
                response.complete(parseNodeConfig(responseNodeJson.toString()));
            } catch (Exception e) {
                response.completeExceptionally(e);
            }
        }).exceptionally(throwable -> {
            LOGGER.severe("OCORREU UM ERRO AO CARREGAR O NODE: " + throwable.getMessage() + "\n");
            sendError(throwable);
            return null;
        });

        return response;
    }

    public CompletableFuture<JSONObject> createNode(NodeBuilder builder) {
        return create(builder, ApplicationEndpoint.NODES.getEndpoint()).exceptionally(throwable -> {
            LOGGER.severe("OCORREU UM ERRO CRIAR UM NODE: " + throwable.getMessage() + "\n");
            sendError(throwable);
            return null;
        });
    }

    public CompletableFuture<JSONObject> updateNode(@NotNull Node node) {
        return update(ApplicationEndpoint.NODES.getEndpoint() + "/" + node.getId(), makeRequestJSON(node)).exceptionally(throwable -> {
            LOGGER.severe("OCORREU UM ERRO AO ATUALIZAR UM NODE: " + throwable.getMessage() + "\n");
            sendError(throwable);
            return null;
        });
    }

    public CompletableFuture<JSONObject> deleteNode(long nodeID) {
        return delete(ApplicationEndpoint.NODES.getEndpoint() + "/" + nodeID).exceptionally(throwable -> {
            LOGGER.severe("OCORREU UM ERRO AO DELETAR UM NODE: " + throwable.getMessage() + "\n");
            sendError(throwable);
            return null;
        });
    }

    public CompletableFuture<JSONObject> createAllocation(AllocationBuilder builder, long nodeID) {
        return create(builder, ApplicationEndpoint.NODES.getEndpoint() + "/" + nodeID + "/allocations").exceptionally(throwable -> {
            LOGGER.severe("OCORREU UM ERRO CRIAR UMA PORTA: " + throwable.getMessage() + "\n");
            sendError(throwable);
            return null;
        });
    }

    public CompletableFuture<JSONObject> deleteAllocation(long nodeID, long allocationID) {
        return delete(ApplicationEndpoint.NODES.getEndpoint() + "/" + nodeID + "/allocations/" + allocationID).exceptionally(throwable -> {
            LOGGER.severe("OCORREU UM ERRO AO DELETAR UMA PORTA: " + throwable.getMessage() + "\n");
            sendError(throwable);
            return null;
        });
    }

    public NodeConfiguration parseNodeConfig(String nodeConfigJson) {
        JSONObject nodeConfigDetailsGeneral = new JSONObject(nodeConfigJson);
        JSONObject nodeConfigAPIJson = nodeConfigDetailsGeneral.getJSONObject("api");
        JSONObject nodeConfigSystem = nodeConfigDetailsGeneral.getJSONObject("system");
        JSONObject nodeConfigSSL = nodeConfigAPIJson.getJSONObject("ssl");
        SSL ssl = new SSL(
                nodeConfigSSL.getBoolean("enabled"),
                nodeConfigSSL.getString("cert"),
                nodeConfigSSL.getString("key")
        );

        NodeConfigApi configApi = new NodeConfigApi(
                nodeConfigAPIJson.getString("host"),
                nodeConfigAPIJson.getInt("port"),
                ssl,
                nodeConfigAPIJson.getInt("upload_limit")
        );

        NodeConfigSystem configSystem = new NodeConfigSystem(
                nodeConfigSystem.getString("data"),
                nodeConfigSystem.getJSONObject("sftp")
        );

        return new NodeConfiguration(
                nodeConfigDetailsGeneral.getBoolean("debug"),
                UUID.fromString(nodeConfigDetailsGeneral.getString("uuid")),
                nodeConfigDetailsGeneral.getString("token_id"),
                nodeConfigDetailsGeneral.getString("token"),
                configApi,
                configSystem,
                nodeConfigDetailsGeneral.getString("remote")
        );
    }

    public Node parseNode(String nodeJson) {
        JSONObject nodeDetailsGeneral = new JSONObject(nodeJson);
        JSONObject nodeDetails = nodeDetailsGeneral.getJSONObject("attributes");
        return new Node(
                nodeDetails.getLong("id"),
                UUID.fromString(nodeDetails.getString("uuid")),
                nodeDetails.getBoolean("public"),
                nodeDetails.getJSONObject("allocated_resources").getInt("memory"),
                nodeDetails.getJSONObject("allocated_resources").getInt("disk"),
                nodeDetails.getString("name"),
                nodeDetails.getString("description"),
                nodeDetails.getLong("location_id"),
                nodeDetails.getString("fqdn"),
                nodeDetails.getString("scheme"),
                nodeDetails.getBoolean("behind_proxy"),
                nodeDetails.getBoolean("maintenance_mode"),
                nodeDetails.getLong("memory"),
                nodeDetails.getLong("memory_overallocate"),
                nodeDetails.getLong("disk"),
                nodeDetails.getLong("disk_overallocate"),
                nodeDetails.getLong("upload_size"),
                nodeDetails.getLong("daemon_listen"),
                nodeDetails.getLong("daemon_sftp"),
                nodeDetails.getString("daemon_base"),
                nodeDetails.getString("created_at"),
                nodeDetails.getString("updated_at")
        );
    }

    public Allocation parseAllocation(String allocationJson) {
        JSONObject allocationDetailsGeneral = new JSONObject(allocationJson);
        JSONObject allocationDetails = allocationDetailsGeneral.getJSONObject("attributes");
        return new Allocation(
                allocationDetails.getLong("id"),
                allocationDetails.getString("ip"),
                allocationDetails.getString("alias"),
                allocationDetails.getLong("port"),
                allocationDetails.getString("notes"),
                allocationDetails.getBoolean("assigned")
        );
    }

    private @NotNull JSONObject makeRequestJSON(@NotNull Node node) {
        JSONObject response = new JSONObject();
        response.put("name", node.getName());
        response.put("description", node.getDescription());
        response.put("location_id", node.getLocation_id());
        response.put("fqdn", node.getFqdn());
        response.put("scheme", node.getScheme());
        response.put("behind_proxy", node.isBehind_proxy());
        response.put("maintence_mode", node.isMaintenance_mode());
        response.put("memory", node.getMemory());
        response.put("memory_overallocate", node.getMemory_overallocate());
        response.put("disk", node.getDisk());
        response.put("disk_overallocate", node.getDisk_overallocate());
        response.put("upload_size", node.getUpload_size());
        response.put("daemon_sftp", node.getDaemon_sftp());
        response.put("daemon_listen", node.getDaemon_listen());
        return response;
    }
}