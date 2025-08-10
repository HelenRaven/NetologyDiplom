package org.example.diplom.controller;

import java.util.List;
import java.util.Map;

import org.example.diplom.entity.FileInfo;
import org.example.diplom.entity.FileMessage;
import org.example.diplom.entity.Message;
import org.example.diplom.service.SessionService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import org.example.diplom.service.FilesStorageService;

@RestController
@CrossOrigin("http://localhost")
@RequestMapping("cloud")
public class FilesController {
    private static final Logger logger = LoggerFactory.getLogger(FilesController.class);
    FilesStorageService storageService;
    SessionService sessionService;

    public FilesController(FilesStorageService storageService,
                           SessionService sessionService) {
        this.storageService = storageService;
        this.sessionService = sessionService;
    }

    @PostMapping(value = "file", produces = "application/json")
    public ResponseEntity<Message> uploadFile(@RequestParam("file") MultipartFile file,
                                              @RequestParam("filename") String fileName,
                                              @RequestHeader("auth-token") String authToken) {
        String message = "";
        String folderName = sessionService.findById(authToken).getUser().getLogin();
        logger.info("Upload file '{}' from user '{}' starting", fileName, folderName);
        storageService.save(file, folderName, fileName);
        logger.info("Uploaded  file '{}' from user '{}' successfully", fileName, folderName);

        return ResponseEntity.status(HttpStatus.OK).body(new Message("Uploaded  file successfully: " + fileName));
    }

    @GetMapping(value = "file", produces = "application/json")
    @ResponseBody
    public ResponseEntity<FileMessage> getFile(@RequestParam("filename") String fileName,
                                               @RequestHeader("auth-token") String authToken) {
        String folderName = sessionService.findById(authToken).getUser().getLogin();
        logger.info("Loading file '{}' for user '{}' starting", fileName, folderName);
        FileMessage file = storageService.load(fileName, folderName);
        logger.info("Loading file '{}' for user '{}' successfully", fileName, folderName);

        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .body(file);
    }

    @PutMapping(value = "file", produces = "application/json")
    @ResponseBody
    public ResponseEntity<Message> renameFile(@RequestParam("filename") String fileName,
                                             @RequestBody Map<String, String> params,
                                             @RequestHeader("auth-token") String authToken) {
        String folderName = sessionService.findById(authToken).getUser().getLogin();
        logger.info("Renaming file '{}', user '{}' starting", fileName, folderName);
        storageService.rename(params.get("name"), fileName, folderName);
        logger.info("Renaming file '{}' to '{}', user '{}' successfully", fileName, params.get("name"), folderName);

        return new ResponseEntity<>(new Message("File renamed"), HttpStatus.OK);
    }

    @DeleteMapping(value = "file", produces = "application/json", consumes = "application/json")
    public ResponseEntity<Message> deleteFile(@RequestParam("filename") String fileName,
                                             @RequestHeader("auth-token") String authToken) {
        String folderName = sessionService.findById(authToken).getUser().getLogin();
        logger.info("Deleting file '{}', user '{}' starting", fileName, folderName);
        storageService.delete(fileName, folderName);
        logger.info("Renaming file '{}', user '{}' successfully", fileName, folderName);

        return ResponseEntity.status(HttpStatus.OK).body(new Message("Delete the file successfully: s" + fileName));
    }

    @GetMapping(value = "list", produces = "application/json")
    public ResponseEntity<List<FileInfo>> getListFiles(@RequestHeader("auth-token") String authToken, @RequestParam int limit) {
        String folderName = sessionService.findById(authToken).getUser().getLogin();
        logger.info("Get list files, user '{}' started", folderName);
        List<FileInfo> files = storageService.loadAll(folderName, limit);
        logger.info("Get list files for user '{}' successfully", folderName);

        return ResponseEntity.status(HttpStatus.OK).body(files);
    }
}
