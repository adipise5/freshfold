package com.freshfold.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "file")
public class FileStorageProperties {
    /**
     * Directory where uploaded files will be stored
     * Example default: ./uploads
     */
    private String uploadDir = "./uploads";

    public String getUploadDir() {
        return uploadDir;
    }
    public void setUploadDir(String uploadDir) {
        this.uploadDir = uploadDir;
    }
}
