package cn.dpc.ab.api.file;

import cn.dpc.ab.api.file.config.FileProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.nio.file.Path;
import java.util.UUID;

/**
 * FileApi represents the API for files.
 */
@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileApi {
    public static final String FILES_CONTEXT = "/files/";
    private final FileProperties fileProperties;

    /**
     * FileUploadResponse represents the response for a file upload.
     */
    public record FileUploadResponse(String url) {}

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    Mono<FileUploadResponse> upload(@RequestPart("file") FilePart file) {
        String fileName = UUID.randomUUID() + file.filename().substring(file.filename().lastIndexOf("."));
        return file.transferTo(Path.of(fileProperties.getUploadDir() + "/" + fileName))
                .then(Mono.just(fileName))
                .map(name -> fileProperties.getFileBaseUrl() + FILES_CONTEXT + name)
                .map(FileUploadResponse::new);
    }

}
