package pa.chan.qrinauth.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import pa.chan.qrinauth.BaseIntegrationTest;
import pa.chan.qrinauth.dto.EntryResponseDto;
import pa.chan.qrinauth.dto.ParticipantResponseDto;
import pa.chan.qrinauth.dto.QrCodeRequestDto;
import pa.chan.qrinauth.dto.QrCodeResponseDto;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class QrAuthControllerTest extends BaseIntegrationTest {

    private static final String AUTH_URL = "/api/v1/auth/qr";
    private static final String PARTICIPANTS_URL = "/api/v1/participants";
    private static final UUID IVAN_QR_UUID = UUID.fromString("2e6c3a08-c6a6-47e1-a096-5e6a8ed03d5e");

    @Test
    void enterClub_withValidQr_shouldReturnVisitorAndNewQr() throws Exception {
        mockMvc.perform(post(AUTH_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new QrCodeRequestDto(IVAN_QR_UUID))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Иван"))
                .andExpect(jsonPath("$.lastName").value("Петров"))
                .andExpect(jsonPath("$.newQrCode").exists())
                .andExpect(jsonPath("$.newQrCode", not(IVAN_QR_UUID.toString())));
    }

    @Test
    void enterClub_newlyIssuedQr_shouldWorkForNextEntry() throws Exception {
        String firstResponse = mockMvc.perform(post(AUTH_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new QrCodeRequestDto(IVAN_QR_UUID))))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        UUID newQr = objectMapper.readValue(firstResponse, EntryResponseDto.class).newQrCode();

        mockMvc.perform(post(AUTH_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new QrCodeRequestDto(newQr))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Иван"));
    }

    @Test
    void enterClub_withSameQrTwice_shouldReturn403OnSecondAttempt() throws Exception {
        byte[] body = objectMapper.writeValueAsBytes(new QrCodeRequestDto(IVAN_QR_UUID));

        mockMvc.perform(post(AUTH_URL).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk());

        mockMvc.perform(post(AUTH_URL).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.message").value("The QR code was not found or has already been used."));
    }

    @Test
    void enterClub_withUnknownQr_shouldReturn403() throws Exception {
        mockMvc.perform(post(AUTH_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new QrCodeRequestDto(UUID.randomUUID()))))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("The QR code was not found or has already been used."));
    }

    @Test
    void enterClub_withQrOfDeletedParticipant_shouldReturn403() throws Exception {
        ParticipantResponseDto created = createParticipant("Удалённый", "Участник");

        String listResponse = mockMvc.perform(get(PARTICIPANTS_URL + "/{id}/qr-codes", created.id()))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        UUID uuid = Arrays.stream(objectMapper.readValue(listResponse, QrCodeResponseDto[].class))
                .filter(QrCodeResponseDto::active)
                .findFirst().orElseThrow().uuid();

        mockMvc.perform(delete(PARTICIPANTS_URL + "/{id}", created.id()))
                .andExpect(status().isNoContent());

        mockMvc.perform(post(AUTH_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new QrCodeRequestDto(uuid))))
                .andExpect(status().isForbidden());
    }

    @Test
    void enterClub_withoutUuid_shouldReturn400() throws Exception {
        mockMvc.perform(post(AUTH_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"uuid\": null}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("must be not null")));
    }
}
