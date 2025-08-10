package org.example.diplom.service;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


import org.example.diplom.entity.FileInfo;
import org.example.diplom.entity.FileMessage;
import org.example.diplom.exception.BadCredentials;
import org.example.diplom.exception.NotFoundData;
import org.example.diplom.exception.ServerError;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FilesStorageServiceImpl implements FilesStorageService {
    private Path root = Paths.get("uploads");

    @Override
    public void init() {
        try {
            Files.createDirectories(root);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize folder for upload!");
        }
    }

    public Path getRoot(){
        return root;
    }

    public void setRoot(Path path){
        this.root = path;
    }

    @Override
    public void save(MultipartFile file, String subfolderName, String fileName) {
        try {
            if (!Files.exists(root.resolve(subfolderName))) {
                Files.createDirectories(root.resolve(subfolderName));
            }
            String[] fileExtension = file.getOriginalFilename().split("\\.");
            String fullFileName = String.format("%s.%s", fileName, fileExtension[fileExtension.length - 1]);
            Files.copy(file.getInputStream(), this.root.resolve(subfolderName).resolve(fullFileName));
        } catch (Exception e) {
            if (e instanceof FileAlreadyExistsException) {
                throw new BadCredentials(String.format("A file with name '%s' already exists.", fileName));
            }
            throw new ServerError(e.getMessage());
        }
    }

    @Override
    public FileMessage load(String filename, String subfolderName) {
        String hash;
        byte[] file;
        try {
            Path filePath = root.resolve(subfolderName).resolve(filename);
            if (Files.exists(filePath)) {
                file = Files.readAllBytes(filePath);
                hash = String.valueOf(filePath.toFile().hashCode());
            } else {
                throw new NotFoundData("File not found: " + filename);
            }
        } catch (IOException e) {
            throw new ServerError(e.getMessage());
        }
        return new FileMessage(file, hash);
    }

    @Override
    public void delete(String filename, String subfolderName) {
        try {
            Path file = root.resolve(subfolderName).resolve(filename);
            if (Files.exists(file)) {
                Files.deleteIfExists(file);
            } else {
                throw new NotFoundData("File not found: " + filename);
            }
        } catch (IOException e) {
            throw new ServerError(e.getMessage());
        }
    }

    @Override
    public List<FileInfo> loadAll(String subfolderName, int limit) {
        Stream<Path> stream;
        List<FileInfo> files;
        if(!Files.exists(this.root.resolve(subfolderName))) {
            return new ArrayList<>();
        }
        try {
            stream = Files.walk(this.root.resolve(subfolderName), 1)
                    .filter(path -> !path.equals(this.root.resolve(subfolderName)))
                    .map(this.root.resolve(subfolderName)::relativize)
                    .limit(limit);

            files = stream.map(path -> {
                String filename = path.getFileName().toString();
                Path globalPath = root.resolve(subfolderName).resolve(filename);
                int size;
                try {
                    size = (int) Files.size(globalPath);
                } catch (IOException e) {
                    throw new ServerError(e.getMessage());
                }
                return new FileInfo(filename, size);
            }).collect(Collectors.toList());
        } catch (IOException e) {
            throw new ServerError("Could not load the files!");
        }
        return files;
    }

    @Override
    public void rename(String newFileName, String filename, String subfolderName) {
        try {
            if (Files.exists(root.resolve(subfolderName).resolve(filename))) {
                Path file = root.resolve(subfolderName).resolve(filename);
                String[] fileNameSplit = filename.split("\\.");
                Files.move(file, file.resolveSibling(String.format("%s.%s", newFileName, fileNameSplit[fileNameSplit.length - 1])));
            } else {
                throw new NotFoundData("File not found: " + filename);
            }
        } catch (IOException e) {
            throw new ServerError("Error: " + e.getMessage());
        }
    }
}