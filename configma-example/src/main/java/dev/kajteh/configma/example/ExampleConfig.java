package dev.kajteh.configma.example;

import dev.kajteh.configma.annotation.*;
import dev.kajteh.configma.annotation.decoration.*;
import dev.kajteh.configma.example.user.User;

import java.util.List;
import java.util.UUID;

@Spacing(1)
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
@Footer("Footer")
public class ExampleConfig {

    @Comment("Comment")
    @InlineComment("Inline")
    public boolean productionMode = false;

    @Comment("Users")
    public List<User> users = List.of(
            new User(
                    UUID.randomUUID(),
                    "Kajteh",
                    "me@kajteh.dev"
            )
    );

    @Nested
    @Comment({"Database", "testtesttt"})
    public DatabaseConfig database = new DatabaseConfig();

    public static class DatabaseConfig {

        @Comment("host")
        public String host = "localhost";

        @Comment("port")
        public int port = 5432;

        @Comment("User")
        @InlineComment("Lolol")
        public String user = "root";

        @Comment("haso")
        public String password = "secret";
    }
}
