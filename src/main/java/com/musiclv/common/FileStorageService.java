package com.musiclv.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

/**
 * 관리자가 등록한 상품 이미지를 미디어 디렉터리 아래 uploads/ 에 저장한다.
 * 저장된 파일은 WebConfig 의 /media/** 핸들러가 서빙한다.
 */
@Service
public class FileStorageService {

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "gif", "webp");

    private final Path uploadDir;

    public FileStorageService(@Value("${musiclv.media-dir}") String mediaDir) {
        this.uploadDir = Paths.get(mediaDir).toAbsolutePath().normalize().resolve("uploads");
    }

    /**
     * @return /media/uploads/xxx.jpg 형태의 웹 경로. 파일이 비어 있으면 null.
     */
    public String store(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        String extension = extractExtension(file.getOriginalFilename());
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException("이미지 파일만 등록할 수 있습니다. (jpg, png, gif, webp)");
        }

        String storedName = UUID.randomUUID().toString().replace("-", "") + "." + extension;
        try {
            Files.createDirectories(uploadDir);
            Path target = uploadDir.resolve(storedName);
            try (var in = file.getInputStream()) {
                Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("이미지 저장에 실패했습니다.", e);
        }
        return "/media/uploads/" + storedName;
    }

    private String extractExtension(String originalFilename) {
        String ext = StringUtils.getFilenameExtension(originalFilename);
        return ext == null ? "" : ext.toLowerCase(Locale.ROOT);
    }
}
