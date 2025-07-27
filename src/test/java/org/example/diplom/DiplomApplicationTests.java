package org.example.diplom;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import io.restassured.RestAssured;
import org.apache.commons.io.FileUtils;
import org.example.diplom.entity.Session;
import org.example.diplom.entity.User;
import org.example.diplom.repository.SessionRepository;
import org.example.diplom.repository.UserRepository;
import org.example.diplom.service.FilesStorageService;
import org.example.diplom.service.FilesStorageServiceImpl;
import org.example.diplom.service.SessionService;
import org.example.diplom.service.UserService;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DiplomApplicationTests {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SessionRepository sessionRepository;

    static private FilesStorageService storageService = new FilesStorageServiceImpl();
    final private String login = "user1";
    final private String password = "password1";
    final private Path path = Paths.get("src/test/java/org/example/diplom/examples");
    static private String testDirectory = "test_uploads";

    @LocalServerPort
    private Integer port;

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest");

    @BeforeAll
    static void beforeAll() {
        postgres.start();
        storageService.setRoot(Paths.get(testDirectory));
        storageService.init();
    }
    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost:" + port;
        userRepository.deleteAll();
    }

    @AfterAll
    static void afterAll() throws IOException {
        postgres.stop();
        FileUtils.cleanDirectory(Paths.get(testDirectory).toFile());
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Test
    void testUserEndpoints() {
        User user = new User(login, password);
        User wrongLoginUser = new User(login + "wrong", password);
        User wrongPasswordUser = new User(login, password + "wrong");

        // Юзер регистрируется
        with()
                .body(user).contentType("application/json\r\n")
            .when()
                .request("POST","/cloud/signin")
            .then()
                .statusCode(200);

        // Юзер вводит нверный логин
        with()
                .body(wrongLoginUser).contentType("application/json\r\n")
            .when()
                .request("POST","/cloud/login")
            .then()
                    .statusCode(404);

        // Юзер вводит нверный пароль
        with()
                .body(wrongPasswordUser).contentType("application/json\r\n")
            .when()
                .request("POST","/cloud/login")
            .then()
                .statusCode(400);

        // Юзер вводит верные данные
        String uuid = with().
                    body(user).contentType("application/json\r\n")
                .when()
                    .request("POST","/cloud/login")
                .then()
                    .statusCode(200).body("$", hasKey("auth-token"))
                .extract()
                    .response().path("auth-token");

        // Юзер выходит
        given()
                .header("auth-token", uuid)
            .when()
                .request("POST","/cloud/logout")
            .then()
                .statusCode(200);
    }

    @Test
    void testFileEndpoints() throws IOException, JSONException {
        User user = userRepository.save(new User(login, password));
        String uuid = sessionRepository.save(new Session(user)).getUuid();

        // Успешная загрузка файла
        given()
                .header("auth-token", uuid)
                .multiPart("file", path.resolve("file.txt").toFile())
            .when()
                .post("/cloud/file?filename=myFile")
            .then()
                .statusCode(200);

        // НЕ можем загрузить два файла с одинаковым названием
        given()
                .header("auth-token", uuid)
                .multiPart("file", path.resolve("file.txt").toFile())
            .when()
                .post("/cloud/file?filename=myFile")
            .then()
                .statusCode(400).body("$", hasKey("message"));

        // Неверный токен
        given()
                .header("auth-token", UUID.randomUUID())
                .multiPart("file", path.resolve("file.txt").toFile())
            .when()
                .post("/cloud/file?filename=myFile")
            .then()
                .statusCode(401);

        JSONObject requestParams = new JSONObject();
        requestParams.put("name", "renamed");

        // Успешное переименование файла
        with()
                .header("auth-token", uuid)
                .body(requestParams.toString()).contentType("application/json\r\n")
            .when()
                .request("PUT","/cloud/file?filename=myFile.txt")
            .then()
                .statusCode(200);

        // Попытка переименовать несуществующий файл
        with()
                .header("auth-token", uuid)
                .body(requestParams.toString()).contentType("application/json\r\n")
            .when()
                .request("PUT","/cloud/file?filename=myFile.txt")
            .then()
                .statusCode(400);

        // Успешная загрузка файла
        with()
                .header("auth-token", uuid)
            .when()
                .request("GET","/cloud/file?filename=renamed.txt")
            .then()
                .statusCode(200).body("$", hasKey("file"));

        // Попытка загузить несуществующий файл
        with()
                .header("auth-token", uuid)
            .when()
                .request("GET","/cloud/file?filename=myFile.txt")
            .then()
                .statusCode(400);

        // Просмотр списка файлов
        with()
                .header("auth-token", uuid)
            .when()
                .request("GET","/cloud/list?limit=5")
            .then()
                .statusCode(200).body("size()", is(1));

        // Удаление файла
        with()
                .header("auth-token", uuid)
                .contentType("application/json\r\n")
            .when()
                .request("DELETE","/cloud/file?filename=renamed.txt")
            .then()
                .statusCode(200);

        // Просмотр списка файлов
        with()
                .header("auth-token", uuid)
            .when()
                .request("GET","/cloud/list?limit=5")
            .then()
                .statusCode(200).body("size()", is(0));

        FileUtils.cleanDirectory(Paths.get(testDirectory).toFile());
    }
}