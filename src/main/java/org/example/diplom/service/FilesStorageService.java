package org.example.diplom.service;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import org.example.diplom.entity.FileInfo;
import org.example.diplom.entity.FileMessage;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FilesStorageService {
    public void init();

    public Path getRoot();

    public void setRoot(Path path);

    public void save(MultipartFile file, String subfolderName, String fileName);

    public FileMessage load(String filename, String subfolderName);

    public void delete(String filename, String subfolderName);

    public void rename(String newFileName, String filename, String subfolderName);

    public List<FileInfo> loadAll(String subfolderName, int limit);
}
