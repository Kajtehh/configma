package dev.kajteh.configma.example;

import dev.kajteh.configma.annotation.*;
import dev.kajteh.configma.annotation.meta.*;

import java.util.UUID;

@Header({
        "",
        "░█████╗░░█████╗░███╗░░██╗███████╗██╗░██████╗░███╗░░░███╗░█████╗░",
        "██╔══██╗██╔══██╗████╗░██║██╔════╝██║██╔════╝░████╗░████║██╔══██╗",
        "██║░░╚═╝██║░░██║██╔██╗██║█████╗░░██║██║░░██╗░██╔████╔██║███████║",
        "██║░░██╗██║░░██║██║╚████║██╔══╝░░██║██║░░╚██╗██║╚██╔╝██║██╔══██║",
        "╚█████╔╝╚█████╔╝██║░╚███║██║░░░░░██║╚██████╔╝██║░╚═╝░██║██║░░██║",
        "░╚════╝░░╚════╝░╚═╝░░╚══╝╚═╝░░░░░╚═╝░╚═════╝░╚═╝░░░░░╚═╝╚═╝░░╚═╝",
        ""
})
public class ExampleSettings {

    enum Environment {
        PRODUCTION,
        TEST
    }

    Environment environment = Environment.TEST;

    @Nested DatabaseConfig database = new DatabaseConfig();

    public User user
             = new User(UUID.randomUUID(), UUID.randomUUID().toString());

    public static class DatabaseConfig {

        String host = "localhost";
        int port = 5432;
        String user = "root";
        String password = "secrete";
    }
}
