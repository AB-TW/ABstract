package cn.dpc.ab.api.file.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


/**
 * FileProperties represents the properties for file storage.
 */
@Data
@ConfigurationProperties(prefix = "file")
@Component
public class FileProperties {
    private String uploadDir = "upload-files";
    private String fileBaseUrl = "http://localhost:8081";
}
