package pa.chan.qrinauth.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import pa.chan.qrinauth.BaseIntegrationTest;
import pa.chan.qrinauth.dto.QrCodeRequestDto;
import pa.chan.qrinauth.entity.QrCode;
import pa.chan.qrinauth.repository.QrCodeRepository;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class QrAuthControllerTest extends BaseIntegrationTest {
    private static final String AUTH_URL = "/api/v1/auth/qr";
    private static final UUID TEST_QR_UUID = UUID.fromString("2e6c3a08-c6a6-47e1-a096-5e6a8ed03d5e");

    @Autowired
    QrCodeRepository qrCodeRepository;


    @Test
    @Sql(scripts = "/sql/insertOneParticipantForTest.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void handleEnterClubWithValidQrShouldReturnVisitorAndNewQr() throws Exception {

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(AUTH_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(new QrCodeRequestDto(TEST_QR_UUID)));

        mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("""
                                {
                                "firstName": "Витя",
                                "lastName": "Мамонтов"
                                }
                                """)
                );
        List<QrCode> qrCodes = qrCodeRepository.findAll();
        assertEquals(2, qrCodes.size());

        QrCode newQrCode = qrCodes.stream()
                .filter(qrCode -> !qrCode.getUuid().equals(TEST_QR_UUID))
                .findFirst()
                .orElseThrow();
        assertTrue(newQrCode.isActive());
    }

    @Test
    @Sql(scripts = "/sql/insertOneParticipantForTest.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void handleEnterClubWhenQrCodeDoesNotExistThrowsException() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(AUTH_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(new QrCodeRequestDto(UUID.randomUUID())));

        mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isForbidden(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("""
                                {
                                "status":403,
                                "message":"The QR code was not found or has already been used."
                                }
                                """)
                );
    }

    @Test
    @Sql(scripts = "/sql/insertOneParticipantForTest.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void handleEnterClubWhenRequestIsInvalidReturnsBadRequest() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(AUTH_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(new QrCodeRequestDto(null)));

        mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("""
                                {
                                "status":400,
                                "message":"uuid: uuid  must be not null"
                                }
                                """)
                );
    }

}
