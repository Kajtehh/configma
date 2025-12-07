package dev.kajteh.configma.example;

import dev.kajteh.configma.annotation.*;
import dev.kajteh.configma.annotation.decoration.*;
import dev.kajteh.configma.annotation.decoration.comment.Comment;
import dev.kajteh.configma.annotation.decoration.comment.InlineComment;

import java.util.List;

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

    private List<String> testList = List.of("test1", "test2", "test3");

    @Nested
    private DatabaseConfig database = new DatabaseConfig();

    public static class DatabaseConfig {

        @Comment("host")
        private String host = "localhost";

        @Comment("port")
        private int port = 5432;

        @Comment("User")
        @InlineComment("Lolol")
        private String user = "root";

        private String password = "secrete";

        public String host() {
            return host;
        }

        public int port() {
            return port;
        }

        public String user() {
            return user;
        }

        public String password() {
            return password;
        }
    }

    public List<String> testList() {
        return testList;
    }

    public DatabaseConfig database() {
        return database;
    }
}
