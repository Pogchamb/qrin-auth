package pa.chan.qrinauth.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import pa.chan.qrinauth.BaseIntegrationTest;
import pa.chan.qrinauth.dto.ParticipantRequestDto;
import pa.chan.qrinauth.dto.ParticipantResponseDto;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ParticipantControllerTest extends BaseIntegrationTest {

    private static final String PARTICIPANTS_URL = "/api/v1/participants";

    // POST /participants ----------

    @Test
    void create_shouldReturn201WithLocationAndBody() throws Exception {
        mockMvc.perform(post(PARTICIPANTS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new ParticipantRequestDto("Витя", "Мамонтов"))))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string(HttpHeaders.LOCATION,
                        matchesPattern(".*/api/v1/participants/\\d+")))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.firstName").value("Витя"))
                .andExpect(jsonPath("$.lastName").value("Мамонтов"));
    }

    @Test
    void create_shouldIssueOneActiveQrCode() throws Exception {
        ParticipantResponseDto created = createParticipant("Витя", "Мамонтов");

        mockMvc.perform(get(PARTICIPANTS_URL + "/{id}/qr-codes", created.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].active").value(true))
                .andExpect(jsonPath("$[0].uuid").exists());
    }

    @Test
    void create_withBlankFirstName_shouldReturn400() throws Exception {
        mockMvc.perform(post(PARTICIPANTS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"\",\"lastName\":\"Мамонтов\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message", containsString("firstName")));
    }

    @Test
    void create_withTooLongLastName_shouldReturn400() throws Exception {
        String longName = "a".repeat(256);

        mockMvc.perform(post(PARTICIPANTS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new ParticipantRequestDto("Витя", longName))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("lastName is too long")));
    }

    @Test
    void create_withMalformedJson_shouldReturn400() throws Exception {
        mockMvc.perform(post(PARTICIPANTS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"Витя\""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Malformed or missing request body"));
    }

    // GET /participants

    @Test
    void getAll_shouldReturnPagedParticipants() throws Exception {
        mockMvc.perform(get(PARTICIPANTS_URL)
                        .param("page", "0")
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    // GET /participants/{id}

    @Test
    void getById_shouldReturnParticipant() throws Exception {
        mockMvc.perform(get(PARTICIPANTS_URL + "/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("Иван"))
                .andExpect(jsonPath("$.lastName").value("Петров"));
    }

    @Test
    void getById_whenNotFound_shouldReturn404() throws Exception {
        mockMvc.perform(get(PARTICIPANTS_URL + "/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Participant 999 not found"));
    }

    //  PUT /participants/{id}

    @Test
    void update_shouldChangeNames() throws Exception {
        ParticipantResponseDto created = createParticipant("Старое", "Имя");

        mockMvc.perform(put(PARTICIPANTS_URL + "/{id}", created.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new ParticipantRequestDto("Новое", "Фамилия"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(created.id()))
                .andExpect(jsonPath("$.firstName").value("Новое"));

        mockMvc.perform(get(PARTICIPANTS_URL + "/{id}", created.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Новое"));
    }

    @Test
    void update_whenNotFound_shouldReturn404() throws Exception {
        mockMvc.perform(put(PARTICIPANTS_URL + "/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new ParticipantRequestDto("Витя", "Мамонтов"))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Participant 999 not found"));
    }

    @Test
    void update_withBlankNames_shouldReturn400() throws Exception {
        Long id = createParticipant("Витя", "Мамонтов").id();

        mockMvc.perform(put(PARTICIPANTS_URL + "/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"\",\"lastName\":\"\"}"))
                .andExpect(status().isBadRequest());
    }

    // DELETE /participants/{id}

    @Test
    void delete_shouldHideParticipant() throws Exception {
        ParticipantResponseDto created = createParticipant("Витя", "Мамонтов");

        mockMvc.perform(delete(PARTICIPANTS_URL + "/{id}", created.id()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get(PARTICIPANTS_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    void delete_whenNotFound_shouldReturn404() throws Exception {
        mockMvc.perform(delete(PARTICIPANTS_URL + "/999"))
                .andExpect(status().isNotFound());
    }
}
