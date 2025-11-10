package app.controllers;

import app.config.ApplicationConfig;
import app.config.HibernateConfig;
import app.dtos.CandidateDTO;
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
    private static final String BASE = "/api/v1";

    @BeforeAll
    static void setup() {
        emf = HibernateConfig.getEntityManagerFactoryForTest();

        // --- CLEANUP: remove previous test data so populator always starts from clean state ---
        // Order matters because of FK constraints: delete association rows first
        try (var em = emf.createEntityManager()) {
            em.getTransaction().begin();
            // JPQL deletes (safer across JPA providers than native SQL when using entity names)
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
        //anyCandidateId = populator.getTestCandidateId(); // FIXME: dette mangler for at getById virker!
        //anySkillId = populator.getTestSkillId();
    }

    @AfterAll
    static void teardown() {
        if (emf != null && emf.isOpen()) emf.close();
    }


    @Test
    void create() {
        CandidateDTO dto = CandidateDTO.builder() // FIXME: Jeg bruger den forkerte DTO til at create.
                // alle create metoder
                .name("Test Candidate")
                .phone("+45 11 22 33 44")
                .education("BSc Testing")
                .skillIds(Set.of())
                .build();

        given()
                .contentType(ContentType.JSON)
                .body(dto)
                .when()
                .post("/candidates")
                .then()
                .statusCode(201)
                .contentType(ContentType.JSON)
                .body("id", notNullValue())
                .body("name", equalTo("Test Candidate"));
    }

    @Test
    void getAll() {
        given()
                .accept(ContentType.JSON)
                .when()
                .get("/candidates")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("$", not(empty()))
                .body("[0].id", notNullValue())
                .body("[0].name", notNullValue());
    }

    @Test
    void getAllCandidates() {
        given()
                .accept(ContentType.JSON)
                .queryParam("category", "PROG_LANG")
                .when()
                .get("/candidates")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("$", notNullValue());
    }

    @Test
    void getById() {
        Assumptions.assumeTrue(anyCandidateId > 0, "No candidate id available from populator");

        given()
                .accept(ContentType.JSON)
                .when()
                .get("/candidates/{id}", anyCandidateId)
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", equalTo((int) anyCandidateId))
                .body("name", notNullValue());
    }

    /*
    @Test
    void update() {
        Assumptions.assumeTrue(anyCandidateId > 0, "No candidate id available from populator");

        CandidateDTO dto = CandidateDTO.builder()
                .name("Updated Candidate")
                .phone("+45 99 88 77 66")
                .education("MSc Updated")
                .skillIds(Set.of())
                .build();

        given()
                .contentType(ContentType.JSON)
                .body(dto)
                .when()
                .put("/candidates/{id}", anyCandidateId)
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", equalTo((int) anyCandidateId))
                .body("name", equalTo("Updated Candidate"));
    }

    @Test
    void delete() {
        int createdId = given()
                .contentType(ContentType.JSON)
                .body(CandidateDTO.builder()
                        .name("Temp Delete")
                        .phone("+45 90000000")
                        .education("Temp")
                        .skillIds(Set.of())
                        .build())
                .when()
                .post("/candidates")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        given()
                .when()
                .delete("/candidates/{id}", createdId)
                .then()
                .statusCode(204);

    }

     */
}
