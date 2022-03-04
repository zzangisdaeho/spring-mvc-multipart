package hello.upload.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

@Slf4j
@Controller
@RequestMapping("/spring")
public class SpringUploadController {

    private final Path fileDir;

    public SpringUploadController(@Value("${file.dir}") String fileDir) {

        this.fileDir = Paths.get(fileDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileDir);
        } catch (IOException e) {
            log.error("file dir create fail : {}", this.fileDir);
        }
    }

    @GetMapping("/upload")
    public String newFile() {
        return "upload-form";
    }

    @PostMapping("/upload")
    public String saveFile(@RequestParam String itemName, @RequestParam MultipartFile file, HttpServletRequest request) throws IOException {
        log.info("request={}", request);
        log.info("itemName={}", itemName);
        log.info("multipartFile={}", file);
        if (!file.isEmpty()) {
            Path fullPath = fileDir.resolve(Objects.requireNonNull(file.getOriginalFilename()));
            log.info("파일 저장 fullPath={}", fullPath);
            file.transferTo(fullPath);
        }
        return "upload-form";
    }
}
