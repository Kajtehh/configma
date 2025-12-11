package dev.kajteh.configma.example;

import dev.kajteh.configma.annotation.*;
import dev.kajteh.configma.annotation.meta.*;

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
public class ExampleConfig {

    enum Environment {
        PRODUCTION,
        TEST
    }

    Environment environment = Environment.TEST;

    @Nested DatabaseConfig database = new DatabaseConfig();

    public static class DatabaseConfig {

        String host = "localhost";
        int port = 5432;
        String user = "root";
        String password = "secrete";
    }
}
