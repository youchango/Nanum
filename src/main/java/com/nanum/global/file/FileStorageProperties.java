package com.nanum.global.file;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "file.storage")
public class FileStorageProperties {
    private String type = "local"; // local or remote
    private Local local = new Local();
    private Remote remote = new Remote();

    @Getter
    @Setter
    public static class Local {
        private String path = "/resources/upload/";
    }

    @Getter
    @Setter
    public static class Remote {
        private String host;
        private int port = 21;
        private String username;
        private String password;
        private String path;
    }
}
