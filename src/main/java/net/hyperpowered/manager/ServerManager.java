package net.hyperpowered.manager;

import net.hyperpowered.logger.PteroLogger;
import net.hyperpowered.requester.ApplicationEndpoint;
import net.hyperpowered.server.*;
import net.hyperpowered.server.builder.DatabaseBuilder;
import net.hyperpowered.server.builder.ServerBuilder;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ServerManager extends Manager {

    private static final PteroLogger LOGGER = new PteroLogger("SERVER");

    public CompletableFuture<List<Server>> listServers() {
        CompletableFuture<List<Server>> response = new CompletableFuture<>();
        fetch(ApplicationEndpoint.SERVERS.getEndpoint()).thenAccept(responseJson -> {
            List<Server> servers = new ArrayList<>();
            try {
                JSONObject responseObject = responseJson.getJSONObject("response");
                JSONArray serverJson = responseObject.getJSONArray("data");
                for (Object serverDetails : serverJson) {
                    Server server = parseServer(serverDetails.toString());
                    servers.add(server);
                }
            } catch (Exception e) {
                response.completeExceptionally(e);
            }

            response.complete(servers);
        }).exceptionally(throwable -> {
            LOGGER.severe("OCORREU UM ERRO AO CARREGAR OS SERVIDORES: " + throwable.getMessage() + "\n");
            sendError(throwable);
            return null;
        });

        return response;
    }

    public CompletableFuture<List<Database>> listDatabases(long serverID) {
        CompletableFuture<List<Database>> response = new CompletableFuture<>();
        fetch(ApplicationEndpoint.SERVERS.getEndpoint() + "/" + serverID + "/databases?include=password,host").thenAccept(responseJson -> {
            List<Database> databases = new ArrayList<>();
            try {
                JSONObject responseObject = responseJson.getJSONObject("response");
                JSONArray databaseJson = responseObject.getJSONArray("data");
                for (Object databaseDetails : databaseJson) {
                    Database database = parseDatabase(databaseDetails.toString());
                    databases.add(database);
                }
            } catch (Exception e) {
                response.completeExceptionally(e);
            }

            response.complete(databases);
        }).exceptionally(throwable -> {
            LOGGER.severe("OCORREU UM ERRO AO CARREGAR AS DATABASES: " + throwable.getMessage() + "\n");
            sendError(throwable);
            return null;
        });

        return response;
    }

    public CompletableFuture<Server> getServer(long serverID) {
        return fetchServer("/", String.valueOf(serverID)).exceptionally(throwable -> {
            LOGGER.severe("OCORREU UM ERRO AO PEGAR OS DADOS DO SERVIDOR: " + throwable.getMessage() + "\n");
            sendError(throwable);
            return null;
        });
    }

    public CompletableFuture<Server> getServer(String externalID) {
        return fetchServer("/external/", externalID).exceptionally(throwable -> {
            LOGGER.severe("OCORREU UM ERRO AO PEGAR OS DADOS DO SERVIDOR: " + throwable.getMessage() + "\n");
            sendError(throwable);
            return null;
        });
    }

    public CompletableFuture<JSONObject> createServer(ServerBuilder builder) {
        return create(builder, ApplicationEndpoint.SERVERS.getEndpoint()).exceptionally(throwable -> {
            LOGGER.severe("OCORREU UM ERRO AO CRIAR O SERVIDOR: " + throwable.getMessage() + "\n");
            sendError(throwable);
            return null;
        });
    }

    public CompletableFuture<JSONObject> updateServer(Server server) {
        JSONObject finalJson = new JSONObject();
        CompletableFuture<JSONObject> response = new CompletableFuture<>();
        updateServerDetails(server).thenAccept(jsonObject -> {
            finalJson.put("details", jsonObject);
        }).exceptionally(throwable -> {
            LOGGER.severe("OCORREU UM ERRO AO ATUALIZAR O STATUS SERVIDOR: " + throwable.getMessage() + "\n");
            sendError(throwable);
            return null;
        });

        updateServerBuild(server).thenAccept(jsonObject -> {
            finalJson.put("build", jsonObject);
        }).exceptionally(throwable -> {
            LOGGER.severe("OCORREU UM ERRO AO ATUALIZAR O STATUS SERVIDOR: " + throwable.getMessage() + "\n");
            sendError(throwable);
            return null;
        });

        updateServerStartup(server).thenAccept(jsonObject -> {
            finalJson.put("startup", jsonObject);
            response.complete(finalJson);
        }).exceptionally(throwable -> {
            LOGGER.severe("OCORREU UM ERRO AO ATUALIZAR O STATUS SERVIDOR: " + throwable.getMessage() + "\n");
            sendError(throwable);
            return null;
        });

        return response;
    }

    public CompletableFuture<JSONObject> updateServerDetails(@NotNull Server server) {
        return update(ApplicationEndpoint.SERVERS.getEndpoint() + "/" + server.getId() + "/details", makeJsonDetails(server)).exceptionally(throwable -> {
            LOGGER.severe("OCORREU UM ERRO AO ATUALIZAR OS DETALHES DO SERVIDOR: " + throwable.getMessage() + "\n");
            sendError(throwable);
            return null;
        });
    }

    public CompletableFuture<JSONObject> updateServerBuild(@NotNull Server server) {
        return update(ApplicationEndpoint.SERVERS.getEndpoint() + "/" + server.getId() + "/build", makeJsonBuild(server)).exceptionally(throwable -> {
            LOGGER.severe("OCORREU UM ERRO AO ATUALIZAR A BUILD DO SERVIDOR: " + throwable.getMessage() + "\n");
            sendError(throwable);
            return null;
        });
    }

    public CompletableFuture<JSONObject> updateServerStartup(@NotNull Server server) {
        return update(ApplicationEndpoint.SERVERS.getEndpoint() + "/" + server.getId() + "/startup", makeJsonStartup(server)).exceptionally(throwable -> {
            LOGGER.severe("OCORREU UM ERRO AO ATUALIZAR O STARTUP DO SERVIDOR: " + throwable.getMessage() + "\n");
            sendError(throwable);
            return null;
        });
    }

    public CompletableFuture<JSONObject> suspendServer(long serverID) {
        return sendAction(ApplicationEndpoint.SERVERS.getEndpoint() + "/" + serverID + "/suspend").exceptionally(throwable -> {
            LOGGER.severe("OCORREU UM ERRO AO SUSPENDER O SERVIDOR: " + throwable.getMessage() + "\n");
            sendError(throwable);
            return null;
        });
    }

    public CompletableFuture<JSONObject> unSuspendServer(long serverID) {
        return sendAction(ApplicationEndpoint.SERVERS.getEndpoint() + "/" + serverID + "/unsuspend").exceptionally(throwable -> {
            LOGGER.severe("OCORREU UM ERRO AO RETIRAR A SUSPENSÃO DO SERVIDOR: " + throwable.getMessage() + "\n");
            sendError(throwable);
            return null;
        });
    }

    public CompletableFuture<JSONObject> reinstallServer(long serverID) {
        return sendAction(ApplicationEndpoint.SERVERS.getEndpoint() + "/" + serverID + "/reinstall").exceptionally(throwable -> {
            LOGGER.severe("OCORREU UM ERRO AO REINSTALAR O SERVIDOR: " + throwable.getMessage() + "\n");
            sendError(throwable);
            return null;
        });
    }

    public CompletableFuture<JSONObject> deleteServer(long serverID) {
        return delete(ApplicationEndpoint.SERVERS.getEndpoint() + "/" + serverID).exceptionally(throwable -> {
            LOGGER.severe("OCORREU UM ERRO AO DELETAR O SERVIDOR: " + throwable.getMessage() + "\n");
            sendError(throwable);
            return null;
        });
    }

    public CompletableFuture<JSONObject> forceDeleteServer(long serverID) {
        return delete(ApplicationEndpoint.SERVERS.getEndpoint() + "/" + serverID + "/force").exceptionally(throwable -> {
            LOGGER.severe("OCORREU UM ERRO AO FORÇAR DELETAR O SERVIDOR: " + throwable.getMessage() + "\n");
            sendError(throwable);
            return null;
        });
    }

    public CompletableFuture<Database> getDatabase(long serverID, Long databaseID) {
        CompletableFuture<Database> response = new CompletableFuture<>();
        fetch(ApplicationEndpoint.SERVERS.getEndpoint() + "/" + serverID + "/databases/" + databaseID).thenAccept(responseJson -> {
            try {
                JSONObject responseDatabaseJson = responseJson.getJSONObject("response");
                response.complete(parseDatabase(responseDatabaseJson.toString()));
            } catch (Exception e) {
                response.completeExceptionally(e);
            }
        }).exceptionally(throwable -> {
            LOGGER.severe("OCORREU UM ERRO AO PEGAR A DATABASE DO SERVIDOR: " + throwable.getMessage() + "\n");
            sendError(throwable);
            return null;
        });

        return response;
    }

    public CompletableFuture<JSONObject> createDatabase(long serverID, DatabaseBuilder builder) {
        return create(builder, ApplicationEndpoint.SERVERS.getEndpoint() + "/" + serverID + "/databases").exceptionally(throwable -> {
            LOGGER.severe("OCORREU UM ERRO AO CRIAR UM SERVIDOR: " + throwable.getMessage() + "\n");
            sendError(throwable);
            return null;
        });
    }

    public CompletableFuture<JSONObject> resetPassword(long serverID, long databaseID) {
        return sendAction(ApplicationEndpoint.SERVERS.getEndpoint() + "/" + serverID + "/databases/" + databaseID + "/reset-password").exceptionally(throwable -> {
            LOGGER.severe("OCORREU UM ERRO AO CRIAR UM SERVIDOR: " + throwable.getMessage() + "\n");
            sendError(throwable);
            return null;
        });
    }

    public CompletableFuture<JSONObject> deleteDatabase(long serverID, long databaseID) {
        return delete(ApplicationEndpoint.SERVERS.getEndpoint() + "/" + serverID + "/databases/" + databaseID).exceptionally(throwable -> {
            LOGGER.severe("OCORREU UM ERRO AO DELETAR UM DATABASE: " + throwable.getMessage() + "\n");
            sendError(throwable);
            return null;
        });
    }

    public Server parseServer(String serverJson) {
        JSONObject serverDetailsGeneral = new JSONObject(serverJson);
        JSONObject serverDetails = serverDetailsGeneral.getJSONObject("attributes");
        JSONObject serverLimitJson = serverDetails.getJSONObject("limits");
        JSONObject futureLimitJson = serverDetails.getJSONObject("feature_limits");
        JSONObject containerJson = serverDetails.getJSONObject("container");
        ServerLimit serverLimit = new ServerLimit(
                serverLimitJson.getLong("memory"),
                serverLimitJson.getLong("swap"),
                serverLimitJson.getLong("disk"),
                serverLimitJson.getLong("io"),
                serverLimitJson.getLong("cpu"),
                serverLimitJson.getLong("threads")
        );
        ServerFutureLimit futureLimit = new ServerFutureLimit(
                futureLimitJson.getLong("databases"),
                futureLimitJson.getLong("allocations"),
                futureLimitJson.getLong("backups")
        );
        ServerContainer container = new ServerContainer(
                containerJson.getString("startup_command"),
                containerJson.getString("image"),
                containerJson.getLong("installed") == 1,
                containerJson.getJSONObject("environment")
        );
        return new Server(
                serverDetails.getLong("id"),
                serverDetails.getString("external_id"),
                UUID.fromString(serverDetails.getString("uuid")),
                serverDetails.getString("identifier"),
                serverDetails.getString("name"),
                serverDetails.getString("description"),
                serverDetails.getBoolean("suspended"),
                serverLimit,
                futureLimit,
                serverDetails.getLong("user"),
                serverDetails.getLong("node"),
                serverDetails.getLong("allocation"),
                serverDetails.getLong("nest"),
                serverDetails.getLong("egg"),
                serverDetails.getString("pack"),
                container,
                serverDetails.getString("created_at"),
                serverDetails.getString("updated_at"));
    }

    public Database parseDatabase(String databaseJson) {
        JSONObject serverDetailsGeneral = new JSONObject(databaseJson);
        JSONObject serverDetails = serverDetailsGeneral.getJSONObject("attributes");
        JSONObject relationships = serverDetails.getJSONObject("relationships");
        return new Database(
                serverDetails.getLong("id"),
                serverDetails.getLong("server"),
                serverDetails.getLong("host"),
                serverDetails.getString("database"),
                serverDetails.getString("username"),
                serverDetails.getString("remote"),
                serverDetails.getLong("max_connections"),
                serverDetails.getString("created_at"),
                serverDetails.getString("updated_at"),
                relationships);
    }

    private @NotNull CompletableFuture<Server> fetchServer(String midPoint, String id) {
        CompletableFuture<Server> response = new CompletableFuture<>();
        fetch(ApplicationEndpoint.SERVERS.getEndpoint() + midPoint + id).thenAccept(responseJson -> {
            try {
                JSONObject responseServerJson = responseJson.getJSONObject("response");
                response.complete(parseServer(responseServerJson.toString()));
            } catch (Exception e) {
                response.completeExceptionally(e);
            }
        }).exceptionally(throwable -> {
            LOGGER.severe("OCORREU UM ERRO AO CARREGAR O SERVER: " + throwable.getMessage() + "\n");
            sendError(throwable);
            return null;
        });

        return response;
    }

    private @NotNull JSONObject makeJsonDetails(@NotNull Server server) {
        JSONObject details = new JSONObject();
        details.put("name", server.getName());
        details.put("user", server.getUser());
        details.put("external_id", server.getExternalID());
        details.put("description", server.getDescription());
        return details;
    }

    private @NotNull JSONObject makeJsonBuild(@NotNull Server server) {
        JSONObject details = new JSONObject();
        ServerLimit serverLimit = server.getServerLimit();
        details.put("allocation", server.getAllocation());
        details.put("memory", serverLimit.getMemory());
        details.put("swap", serverLimit.getSwap());
        details.put("disk", serverLimit.getDisk());
        details.put("io", serverLimit.getIo());
        details.put("cpu", serverLimit.getCpu());
        details.put("threads", serverLimit.getThreads());
        details.put("feature_limits", makeFutureLimit(server));
        return details;
    }

    private @NotNull JSONObject makeFutureLimit(@NotNull Server server) {
        JSONObject futureLimitsJson = new JSONObject();
        ServerFutureLimit futureLimit = server.getServerFutureLimit();
        futureLimitsJson.put("databases", futureLimit.getDatabases());
        futureLimitsJson.put("allocations", futureLimit.getAllocations());
        futureLimitsJson.put("backups", futureLimit.getBackups());
        return futureLimitsJson;
    }

    private @NotNull JSONObject makeJsonStartup(@NotNull Server server) {
        JSONObject startupJson = new JSONObject();
        ServerContainer container = server.getContainer();
        startupJson.put("startup", container.getStartup());
        startupJson.put("environment", container.getEnvironment());
        startupJson.put("egg", server.getEgg());
        startupJson.put("image", container.getImage());
        startupJson.put("skip_scripts", false);
        return startupJson;
    }

}