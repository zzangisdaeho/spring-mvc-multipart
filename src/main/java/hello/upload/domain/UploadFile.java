package hello.upload.domain;

import lombok.Data;

@Data
public class UploadFile {
    //user가 올린 파일 이름
    private String uploadFileName;
    //저장된 파일 이름
    private String storeFileName;

    public UploadFile(String uploadFileName, String storeFileName) {
        this.uploadFileName = uploadFileName;
        this.storeFileName = storeFileName;
    }
}
