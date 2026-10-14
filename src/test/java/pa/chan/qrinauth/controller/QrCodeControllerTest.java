package pa.chan.qrinauth.controller;

import org.junit.jupiter.api.Test;
import pa.chan.qrinauth.BaseIntegrationTest;
import pa.chan.qrinauth.dto.QrCodeResponseDto;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class QrCodeControllerTest extends BaseIntegrationTest {

    private static final String PARTICIPANTS_URL = "/api/v1/participants";
    private static final String QR_CODES_URL = "/api/v1/qr-codes";
    private static final UUID SEED_UUID = UUID.fromString("2e6c3a08-c6a6-47e1-a096-5e6a8ed03d5e");

    @Test
    void generateQrCode_shouldDeactivateOldAndReturnNewActive() throws Exception {
        Long participantId = createParticipant("Геннадий", "Кодов").id();

        String response = mockMvc.perform(post(PARTICIPANTS_URL + "/{id}/qr-codes", participantId))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.active").value(true))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        QrCodeResponseDto newCode = objectMapper.readValue(response, QrCodeResponseDto.class);

        List<QrCodeResponseDto> codes = getQrCodes(participantId);
        List<QrCodeResponseDto> activeCodes = codes.stream().filter(QrCodeResponseDto::active).toList();

        assertThat(codes).hasSize(2);
        assertThat(activeCodes).hasSize(1);
        assertThat(activeCodes.get(0).uuid()).isEqualTo(newCode.uuid());
    }

    @Test
    void generateQrCode_whenParticipantNotFound_shouldReturn404() throws Exception {
        mockMvc.perform(post(PARTICIPANTS_URL + "/999/qr-codes"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Participant 999 not found"));
    }

    @Test
    void getQrCodes_shouldReturnParticipantCodes() throws Exception {
        List<QrCodeResponseDto> codes = getQrCodes(1L); // участник из data.sql

        assertThat(codes).hasSize(1);
        assertThat(codes.get(0).uuid()).isEqualTo(SEED_UUID);
        assertThat(codes.get(0).active()).isTrue();
    }

    @Test
    void getQrCodes_whenParticipantNotFound_shouldReturn404() throws Exception {
        mockMvc.perform(get(PARTICIPANTS_URL + "/999/qr-codes"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteQrCode_shouldRemoveCode() throws Exception {
        mockMvc.perform(delete(QR_CODES_URL + "/1"))
                .andExpect(status().isNoContent());

        mockMvc.perform(delete(QR_CODES_URL + "/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("QrCode 1 not found"));
    }

    @Test
    void deleteQrCode_whenNotFound_shouldReturn404() throws Exception {
        mockMvc.perform(delete(QR_CODES_URL + "/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("QrCode 999 not found"));
    }

    private List<QrCodeResponseDto> getQrCodes(Long participantId) throws Exception {
        String response = mockMvc.perform(get(PARTICIPANTS_URL + "/{id}/qr-codes", participantId))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        return Arrays.asList(objectMapper.readValue(response, QrCodeResponseDto[].class));
    }
}
