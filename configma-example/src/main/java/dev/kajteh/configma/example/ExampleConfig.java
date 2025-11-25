package dev.kajteh.configma.example;

import dev.kajteh.configma.annotation.*;
import dev.kajteh.configma.annotation.decoration.*;
import dev.kajteh.configma.annotation.decoration.comment.Comment;
import dev.kajteh.configma.annotation.decoration.comment.InlineComment;

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
    @Key(value = "Key_ANNOTATION_test", exact = true)
    public boolean productionMode = false;

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

        public String password = "secrete";
    }
}
