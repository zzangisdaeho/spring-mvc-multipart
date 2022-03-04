package hello.upload.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

@Slf4j
@Controller
@RequestMapping("/servlet/v2")
public class ServletUploadControllerV2 {

    private final Path fileDir;

    public ServletUploadControllerV2(@Value("${file.dir}") String fileDir) {

        this.fileDir =  Paths.get(fileDir).toAbsolutePath().normalize();
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
    public String saveFileV1(HttpServletRequest request) throws ServletException, IOException {
        log.info("request={}", request);
        String itemName = request.getParameter("itemName");
        log.info("itemName={}", itemName);
        Collection<Part> parts = request.getParts();
        log.info("parts={}", parts);
        for (Part part : parts) {
            log.info("==== PART ====");
            log.info("name={}", part.getName());
            Collection<String> headerNames = part.getHeaderNames();
            for (String headerName : headerNames) {
                log.info("header {}: {}", headerName, part.getHeader(headerName));
            }
            //편의 메서드
            //content-disposition; filename
            log.info("submittedFileName={}", part.getSubmittedFileName());
            log.info("size={}", part.getSize()); //part body size
            //데이터 읽기
            InputStream inputStream = part.getInputStream();
            String body = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
            log.info("body={}", body);
            //파일에 저장하기
            if (StringUtils.hasText(part.getSubmittedFileName())) {
                Path fullPath = fileDir.resolve(part.getSubmittedFileName());
                log.info("파일 저장 fullPath={}", fullPath);
                part.write(fullPath.toString());
            }
        }
        return "upload-form";
    }
}
