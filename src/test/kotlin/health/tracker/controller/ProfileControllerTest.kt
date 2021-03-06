package health.tracker.controller

import com.fasterxml.jackson.databind.ObjectMapper
import health.tracker.model.Profile
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import java.time.LocalDate

@SpringBootTest
class ProfileControllerTest {
    @Autowired
    lateinit var controller: ProfileController

    @Autowired
    lateinit var mapper: ObjectMapper;

    lateinit var client: WebTestClient
    lateinit var profile: String

    @BeforeEach
    fun setup() {
        client = WebTestClient.bindToController(controller).build()
        profile = mapper.writeValueAsString(Profile( firstName = "kotlin", lastName =  "reactive", birthDate = LocalDate.now()))
    }

    @Test
    fun whenRequestProfile_thenStatusShouldBeOk_IdShouldBeNotNull() {
        client.post()
            .uri("/profile")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(profile)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.id")
            .isNotEmpty
    }
}