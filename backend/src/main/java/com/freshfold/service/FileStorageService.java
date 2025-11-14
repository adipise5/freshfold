package com.freshfold.service;

import com.freshfold.config.FileStorageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.annotation.PostConstruct;  // ✔ FIXED IMPORT

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class FileStorageService {

    private final Path fileStorageLocation;
    private final String uploadDir;

    // Matches: 123.jpg, 123_1.jpg, 123_2.png etc.
    private static final Pattern ORDER_FILE_PATTERN =
            Pattern.compile("^(\\d+)(?:_(\\d+))?\\.(?i)(jpg|jpeg|png|gif)$");

    @Autowired
    public FileStorageService(FileStorageProperties properties) {
        this.uploadDir = properties.getUploadDir();
        this.fileStorageLocation = Paths.get(this.uploadDir).toAbsolutePath().normalize();
    }

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Could not create upload directory!", ex);
        }
    }

    public String storeFileForOrder(MultipartFile file, Long orderId) {
        String originalFilename = StringUtils.cleanPath(
                Objects.requireNonNull(file.getOriginalFilename())
        );

        String ext = "";
        int lastDot = originalFilename.lastIndexOf('.');
        if (lastDot >= 0) {
            ext = originalFilename.substring(lastDot);
        }

        try {
            if (originalFilename.contains("..")) {
                throw new RuntimeException("Invalid filename " + originalFilename);
            }

            List<String> existing = listFileNamesForOrder(orderId);

            String filename;
            if (existing.isEmpty()) {
                filename = orderId + ext;
            } else {
                int maxIndex = existing.stream()
                        .map(fn -> {
                            Matcher m = ORDER_FILE_PATTERN.matcher(fn);
                            if (m.matches() && m.group(1).equals(orderId.toString())) {
                                String idx = m.group(2);
                                return idx == null ? 0 : Integer.parseInt(idx);
                            }
                            return -1;
                        })
                        .filter(i -> i >= 0)
                        .max(Integer::compareTo)
                        .orElse(0);

                filename = orderId + "_" + (maxIndex + 1) + ext;
            }

            Path target = this.fileStorageLocation.resolve(filename);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

            return filename;

        } catch (IOException ex) {
            throw new RuntimeException("Could not store file " + originalFilename, ex);
        }
    }

    public List<String> listFileNamesForOrder(Long orderId) {
        try (Stream<Path> stream = Files.list(this.fileStorageLocation)) {
            List<String> list = stream
                    .filter(Files::isRegularFile)
                    .map(fileStorageLocation::relativize)
                    .map(Path::toString)
                    .filter(fn -> {
                        Matcher m = ORDER_FILE_PATTERN.matcher(fn);
                        return m.matches() && m.group(1).equals(orderId.toString());
                    })
                    .collect(Collectors.toList());

            return list.stream()
                    .sorted(Comparator.comparingInt(fn -> {
                        Matcher m = ORDER_FILE_PATTERN.matcher(fn);
                        if (m.matches()) {
                            String idx = m.group(2);
                            return idx == null ? 0 : Integer.parseInt(idx);
                        }
                        return Integer.MAX_VALUE;
                    }))
                    .collect(Collectors.toList());

        } catch (IOException ex) {
            throw new RuntimeException("Could not list files for order " + orderId, ex);
        }
    }

    public Resource loadFileAsResource(String filename) {
        try {
            Path filePath = fileStorageLocation.resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) return resource;
            throw new RuntimeException("File not found " + filename);
        } catch (MalformedURLException ex) {
            throw new RuntimeException("File not found " + filename, ex);
        }
    }

    public String buildFileUrl(String filename) {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/uploads/")
                .path(filename)
                .toUriString();
    }
}
