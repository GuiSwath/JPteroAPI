package net.hyperpowered.manager;

import net.hyperpowered.logger.PteroLogger;
import net.hyperpowered.nest.Egg;
import net.hyperpowered.nest.EggScript;
import net.hyperpowered.nest.Nest;
import net.hyperpowered.requester.ApplicationEndpoint;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class NestManager extends Manager {

    public static final PteroLogger LOGGER = new PteroLogger("NEST");

    public CompletableFuture<List<Nest>> listNest() {
        CompletableFuture<List<Nest>> response = new CompletableFuture<>();
        fetch(ApplicationEndpoint.NESTS.getEndpoint()).thenAccept(responseJson -> {
            List<Nest> nests = new ArrayList<>();
            try {
                JSONObject responseObject = responseJson.getJSONObject("response");
                JSONArray nestJson = responseObject.getJSONArray("data");
                for (Object nestDetails : nestJson) {
                    Nest nest = parseNest(nestDetails.toString());
                    nests.add(nest);
                }
            } catch (Exception e) {
                response.completeExceptionally(e);
            }

            response.complete(nests);
        }).exceptionally(throwable -> {
            LOGGER.severe("OCORREU UM ERRO AO CARREGAR OS NESTS: " + throwable.getMessage() + "\n");
            sendError(throwable);
            return null;
        });

        return response;
    }

    public CompletableFuture<List<Egg>> listEggs(long nestID) {
        CompletableFuture<List<Egg>> response = new CompletableFuture<>();
        fetch(ApplicationEndpoint.NESTS.getEndpoint() + "/" + nestID + "/include=nest,servers").thenAccept(responseJson -> {
            List<Egg> eggs = new ArrayList<>();
            try {
                JSONObject responseObject = responseJson.getJSONObject("response");
                JSONArray eggJson = responseObject.getJSONArray("data");
                for (Object eggDetails : eggJson) {
                    Egg egg = parseEgg(eggDetails.toString());
                    eggs.add(egg);
                }
            } catch (Exception e) {
                response.completeExceptionally(e);
            }

            response.complete(eggs);
        }).exceptionally(throwable -> {
            LOGGER.severe("OCORREU UM ERRO AO CARREGAR OS EGGS: " + throwable.getMessage() + "\n");
            sendError(throwable);
            return null;
        });

        return response;
    }

    public CompletableFuture<Nest> getNest(long nestID) {
        CompletableFuture<Nest> response = new CompletableFuture<>();
        fetch(ApplicationEndpoint.NESTS.getEndpoint() + "/" + nestID).thenAccept(responseJson -> {
            try {
                JSONObject responseNestJson = responseJson.getJSONObject("response");
                response.complete(parseNest(responseNestJson.toString()));
            } catch (Exception e) {
                response.completeExceptionally(e);
            }
        }).exceptionally(throwable -> {
            LOGGER.severe("OCORREU UM ERRO AO PEGAR O NEST DO SERVIDOR: " + throwable.getMessage() + "\n");
            sendError(throwable);
            return null;
        });

        return response;
    }

    public CompletableFuture<Egg> getEgg(long nestID, long eggID) {
        CompletableFuture<Egg> response = new CompletableFuture<>();
        fetch(ApplicationEndpoint.NESTS.getEndpoint() + "/" + nestID + "/eggs/" + eggID).thenAccept(responseJson -> {
            try {
                JSONObject responseEggJson = responseJson.getJSONObject("response");
                response.complete(parseEgg(responseEggJson.toString()));
            } catch (Exception e) {
                response.completeExceptionally(e);
            }
        }).exceptionally(throwable -> {
            LOGGER.severe("OCORREU UM ERRO AO PEGAR O EGG DO NEST: " + throwable.getMessage() + "\n");
            sendError(throwable);
            return null;
        });

        return response;
    }

    public Nest parseNest(String nestJson) {
        JSONObject nestDetailsGeneral = new JSONObject(nestJson);
        JSONObject nestDetails = nestDetailsGeneral.getJSONObject("attributes");
        return new Nest(
                nestDetails.getLong("id"),
                UUID.fromString(nestDetails.getString("uuid")),
                nestDetails.getString("author"),
                nestDetails.getString("name"),
                nestDetails.getString("description"),
                nestDetails.getString("created_at"),
                nestDetails.getString("updated_at")
        );
    }

    public Egg parseEgg(String eggJson) {
        JSONObject eggDetailsGeneral = new JSONObject(eggJson);
        JSONObject eggDetails = eggDetailsGeneral.getJSONObject("attributes");
        JSONObject eggScriptDetails = eggDetails.getJSONObject("script");
        EggScript eggScript = new EggScript(
                eggScriptDetails.getBoolean("privileged"),
                eggScriptDetails.getString("install"),
                eggScriptDetails.getString("entry"),
                eggScriptDetails.getString("container")
        );

        return new Egg(
                eggDetails.getLong("id"),
                UUID.fromString(eggDetails.getString("uuid")),
                eggDetails.getLong("nest"),
                eggDetails.getString("author"),
                eggDetails.getString("description"),
                eggDetails.getString("docker_image"),
                eggDetails.getJSONObject("config"),
                eggDetails.getString("startup"),
                eggScript,
                eggDetails.getString("created_at"),
                eggDetails.getString("updated_at")
        );
    }
}
