package dev.kajteh.configma.example.user;

import java.time.Instant;
import java.util.UUID;

public record User(
        UUID id,
        String name,
        String email,
        Instant emailVerifiedAt
) {}
