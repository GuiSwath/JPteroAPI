package net.hyperpowered.manager;

import net.hyperpowered.location.Location;
import net.hyperpowered.location.builder.LocationBuilder;
import net.hyperpowered.logger.PteroLogger;
import net.hyperpowered.requester.ApplicationEndpoint;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class LocationManager extends Manager {

    private static final PteroLogger LOGGER = new PteroLogger("LOCATION");

    public CompletableFuture<List<Location>> listLocations() {
        CompletableFuture<List<Location>> response = new CompletableFuture<>();
        fetch(ApplicationEndpoint.LOCATIONS.getEndpoint()).thenAccept(responseJson -> {
            List<Location> locations = new ArrayList<>();
            try {
                JSONObject responseObject = responseJson.getJSONObject("response");
                JSONArray locationJson = responseObject.getJSONArray("data");
                for (Object locationDetails : locationJson) {
                    Location location = parseLocation(locationDetails.toString());
                    locations.add(location);
                }
            } catch (Exception e) {
                response.completeExceptionally(e);
            }

            response.complete(locations);
        }).exceptionally(throwable -> {
            LOGGER.severe("OCORREU UM ERRO AO CARREGAR AS MÁQUINAS: " + throwable.getMessage() + "\n");
            sendError(throwable);
            return null;
        });

        return response;
    }

    public CompletableFuture<Location> getLocation(long locationID) {
        CompletableFuture<Location> response = new CompletableFuture<>();
        fetch(ApplicationEndpoint.LOCATIONS.getEndpoint() + "/" + locationID).thenAccept(responseJson -> {
            try {
                JSONObject responseLocationJson = responseJson.getJSONObject("response");
                response.complete(parseLocation(responseLocationJson.toString()));
            } catch (Exception e) {
                response.completeExceptionally(e);
            }
        }).exceptionally(throwable -> {
            LOGGER.severe("OCORREU UM ERRO AO PEGAR OS DADOS DO SERVIDOR: " + throwable.getMessage() + "\n");
            sendError(throwable);
            return null;
        });

        return response;
    }

    public CompletableFuture<JSONObject> createLocation(LocationBuilder builder) {
        return create(builder, ApplicationEndpoint.LOCATIONS.getEndpoint()).exceptionally(throwable -> {
            LOGGER.severe("OCORREU UM ERRO AO CRIAR UMA MÁQUINA: " + throwable.getMessage() + "\n");
            sendError(throwable);
            return null;
        });
    }

    public CompletableFuture<JSONObject> updateLocation(@NotNull Location location) {
        return update(ApplicationEndpoint.LOCATIONS.getEndpoint() + "/" + location.getId(), makeRequestJSON(location)).exceptionally(throwable -> {
            LOGGER.severe("OCORREU UM ERRO AO ATUALIZAR UMA MÁQUINA: " + throwable.getMessage() + "\n");
            sendError(throwable);
            return null;
        });
    }

    public CompletableFuture<JSONObject> deleteLocation(long id) {
        return delete(ApplicationEndpoint.LOCATIONS.getEndpoint() + "/" + id).exceptionally(throwable -> {
            LOGGER.severe("OCORREU UM ERRO AO DELETAR UMA MÁQUINA: " + throwable.getMessage() + "\n");
            sendError(throwable);
            return null;
        });
    }

    public Location parseLocation(String locationJson) {
        JSONObject locationDetailsGeneral = new JSONObject(locationJson);
        JSONObject locationDetails = locationDetailsGeneral.getJSONObject("attributes");
        return new Location(
                locationDetails.getLong("id"),
                locationDetails.getString("short"),
                locationDetails.getString("long"),
                locationDetails.getString("updated_at"),
                locationDetails.getString("created_at")
        );
    }

    private @NotNull JSONObject makeRequestJSON(@NotNull Location location) {
        JSONObject response = new JSONObject();
        response.put("short", location.getLocationName());
        response.put("long", location.getLocationDescription());
        return response;
    }
}
