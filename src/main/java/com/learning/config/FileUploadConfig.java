package com.learning.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class FileUploadConfig {

    @Value("${file.upload.avatar-path:./uploads/avatars}")
    private String avatarUploadPath;

    @Value("${file.upload.post-path:./uploads/posts}")
    private String postUploadPath;

    public String getAvatarUploadPath() {
        return avatarUploadPath;
    }

    public String getPostUploadPath() {
        return postUploadPath;
    }

    public Path getAvatarUploadPathAsPath() {
        return Paths.get(avatarUploadPath).toAbsolutePath().normalize();
    }

    public Path getPostUploadPathAsPath() {
        return Paths.get(postUploadPath).toAbsolutePath().normalize();
    }

    public void init() {
        File avatarDir = new File(avatarUploadPath);
        if (!avatarDir.exists()) {
            avatarDir.mkdirs();
        }
        File postDir = new File(postUploadPath);
        if (!postDir.exists()) {
            postDir.mkdirs();
        }
    }
}
