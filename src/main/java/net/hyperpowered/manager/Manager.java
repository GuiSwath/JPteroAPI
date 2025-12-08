package net.hyperpowered.manager;

import net.hyperpowered.PteroAPI;
import net.hyperpowered.interfaces.Builder;
import net.hyperpowered.logger.PteroLogger;
import net.hyperpowered.requester.RequestMethod;
import net.hyperpowered.requester.Requester;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.concurrent.CompletableFuture;

public abstract class Manager {

    private static final PteroLogger LOGGER = new PteroLogger("SERVER");

    public CompletableFuture<JSONObject> create(@NotNull Builder builder, String endPoint) {
        Requester requester = new Requester(PteroAPI.getUrl() + endPoint, "Mozilla/5.0", PteroAPI.getApplicationToken());
        CompletableFuture<JSONObject> requestResponse = requester.sendRequest(RequestMethod.POST, builder.buildToJSON());
        requestResponse.thenAccept(requestResponse::complete).exceptionally(throwable -> {
            sendError(throwable);
            return null;
        });

        return requestResponse;
    }

    public CompletableFuture<JSONObject> sendAction(String endPoint) {
        Requester requester = new Requester(PteroAPI.getUrl() + endPoint, "Mozilla/5.0", PteroAPI.getApplicationToken());
        CompletableFuture<JSONObject> requestResponse = requester.sendRequest(RequestMethod.POST, null);
        requestResponse.thenAccept(requestResponse::complete).exceptionally(throwable -> {
            sendError(throwable);
            return null;
        });

        return requestResponse;
    }

    public CompletableFuture<JSONObject> fetch(String endPoint) {
        Requester requester = new Requester(PteroAPI.getUrl() + endPoint, "Mozilla/5.0", PteroAPI.getApplicationToken());
        CompletableFuture<JSONObject> response = requester.sendRequest(RequestMethod.GET, null);
        response.thenAccept(responseJson -> {
            try {
                JSONObject responseObject = responseJson.getJSONObject("response");
                response.complete(responseObject);
            } catch (Exception e) {
                response.completeExceptionally(e);
            }
        }).exceptionally(throwable -> {
            sendError(throwable);
            return null;
        });

        return response;
    }

    public CompletableFuture<JSONObject> delete(String endPoint) {
        Requester requester = new Requester(PteroAPI.getUrl() + endPoint, "Mozilla/5.0", PteroAPI.getApplicationToken());
        CompletableFuture<JSONObject> response = requester.sendRequest(RequestMethod.DELETE, null);
        response.thenAccept(response::complete).exceptionally(throwable -> {
            sendError(throwable);
            return null;
        });

        return response;
    }

    public CompletableFuture<JSONObject> update(String endPoint, JSONObject body) {
        Requester requester = new Requester(PteroAPI.getUrl() + endPoint, "Mozilla/5.0", PteroAPI.getApplicationToken());
        CompletableFuture<JSONObject> requestResponse = requester.sendRequest(RequestMethod.PATCH, body);
        requestResponse.thenAccept(requestResponse::complete).exceptionally(throwable -> {
            sendError(throwable);
            return null;
        });

        return requestResponse;
    }

    public void sendError(@NotNull Throwable throwable) {
        throwable.printStackTrace();
        throw new RuntimeException(throwable);
    }
}
