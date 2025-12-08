package net.hyperpowered.user;

import lombok.*;

import java.util.UUID;

@RequiredArgsConstructor
@Getter
public class User {

    @NonNull
    private long id;

    @NonNull
    private String externalID;

    @NonNull
    private UUID uuid;

    @NonNull
    @Setter
    private String username;

    @NonNull
    @Setter
    private String email;

    @NonNull
    @Setter
    private String firstName;

    @NonNull
    @Setter
    private String lastName;

    @NonNull
    @Setter
    private String language;

    @Setter
    private String password;

    @Setter
    @NonNull
    private boolean admin;

    @Setter
    @NonNull
    private boolean twoFactors;

    @NonNull
    private String createDate;

    @NonNull
    private String updateDate;

}
