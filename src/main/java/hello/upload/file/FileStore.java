package hello.upload.file;

import hello.upload.domain.UploadFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Component
public class FileStore {

    private final Path fileDir;

    public FileStore(@Value("${file.dir}") String fileDir) {

        this.fileDir =  Paths.get(fileDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileDir);
        } catch (IOException e) {
            log.error("file dir create fail : {}", this.fileDir);
        }
    }

    public Path getFullPath(String filename){
        return fileDir.resolve(filename);
    }

    public List<UploadFile> storeFiles(List<MultipartFile> multipartFiles){
        return multipartFiles.stream().map(multipartFile -> {
            try {
                return this.storeFile(multipartFile);
            } catch (IOException e) {
                throw new RuntimeException("파일 저장 실패");
            }
        }).collect(Collectors.toList());
    }

    public UploadFile storeFile(MultipartFile multipartFile) throws IOException {
        if(multipartFile.isEmpty()) return null;

        String originalFilename = multipartFile.getOriginalFilename();

        String storeFileName = createStoreFileName(originalFilename);

        multipartFile.transferTo(this.getFullPath(storeFileName));

        return new UploadFile(originalFilename, storeFileName);
    }

    private String createStoreFileName(String originalFilename) {
        String ext = extractExt(originalFilename);
        return UUID.randomUUID() +"."+ext;
    }

    private String extractExt(String originalFilename) {
        int pos = originalFilename.lastIndexOf('.');
        return originalFilename.substring(pos + 1);
    }

}
