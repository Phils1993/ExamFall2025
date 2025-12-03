package app.controllers;

import app.config.ApplicationConfig;
import app.config.HibernateConfig;
import app.dtos.CandidateCreateDTO;
import app.testPopulator.TestPopulator;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

import java.util.Set;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CandidateControllerTest {

    private static EntityManagerFactory emf;
    private static long anyCandidateId;
    private static long anySkillId;
    private static String jwtToken;
    private static final String BASE = "/api/v1";

    @BeforeAll
    static void setup() {
        emf = HibernateConfig.getEntityManagerFactoryForTest();

        try (var em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM CandidateSkill cs").executeUpdate();
            em.createQuery("DELETE FROM Candidate c").executeUpdate();
            em.createQuery("DELETE FROM Skill s").executeUpdate();
            em.getTransaction().commit();
        }

        ApplicationConfig.startServer(7070, emf);
        RestAssured.baseURI = "http://localhost" + BASE;
        RestAssured.port = 7070;

        TestPopulator populator = new TestPopulator(emf);
        populator.populate();
        anyCandidateId = populator.getTestCandidateId();
        anySkillId = populator.getTestSkillId();

        // Login and extract JWT token
        jwtToken = given()
                .contentType(ContentType.JSON)
                .body("{\"username\": \"Philip\", \"password\": \"pass12345\"}")
                .when()
                .post("/auth/login/")
                .then()
                .statusCode(200)
                .extract()
                .path("token");
    }

    @AfterAll
    static void teardown() {
        if (emf != null && emf.isOpen()) emf.close();
    }

    @Test
    void create() {
        CandidateCreateDTO dto = CandidateCreateDTO.builder()
                .name("Test Candidate")
                .phone("+45 11 22 33 44")
                .education("BSc Testing")
                .skillIds(Set.of())
                .build();

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + jwtToken)
                .body(dto)
                .when()
                .post("/candidates")
                .then()
                .statusCode(201)
                .body("name", equalTo("Test Candidate"));
    }

    @Test
    void getAll() {
        given()
                .accept(ContentType.JSON)
                .header("Authorization", "Bearer " + jwtToken)
                .when()
                .get("/candidates")
                .then()
                .statusCode(200)
                .body("$", not(empty()));
    }

    @Test
    void getAllCandidates() {
        given()
                .accept(ContentType.JSON)
                .header("Authorization", "Bearer " + jwtToken)
                .queryParam("category", "PROG_LANG")
                .when()
                .get("/candidates")
                .then()
                .statusCode(200);
    }

    @Test
    void getById() {
        Assumptions.assumeTrue(anyCandidateId > 0);

        given()
                .accept(ContentType.JSON)
                .header("Authorization", "Bearer " + jwtToken)
                .when()
                .get("/candidates/{id}", anyCandidateId)
                .then()
                .statusCode(200)
                .body("id", equalTo((int) anyCandidateId));
    }

    @Test
    void update() {
        Assumptions.assumeTrue(anyCandidateId > 0);

        CandidateCreateDTO dto = CandidateCreateDTO.builder()
                .name("Updated Candidate")
                .phone("+45 99 88 77 66")
                .education("MSc Updated")
                .skillIds(Set.of())
                .build();

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + jwtToken)
                .body(dto)
                .when()
                .put("/candidates/{id}", anyCandidateId)
                .then()
                .statusCode(200)
                .body("name", equalTo("Updated Candidate"));
    }

    @Test
    void delete() {
        CandidateCreateDTO createDto = CandidateCreateDTO.builder()
                .name("Temp Delete")
                .phone("+45 90000000")
                .education("Temp")
                .skillIds(Set.of())
                .build();

        Integer idInt = given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + jwtToken)
                .body(createDto)
                .when()
                .post("/candidates")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        Long createdId = idInt != null ? idInt.longValue() : null;

        given()
                .header("Authorization", "Bearer " + jwtToken)
                .when()
                .delete("/candidates/{id}", createdId)
                .then()
                .statusCode(204); // USER and ADMIN role can't delete
    }
}
