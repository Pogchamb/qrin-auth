package pa.chan.qrinauth.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import pa.chan.qrinauth.BaseIntegrationTest;
import pa.chan.qrinauth.repository.QrCodeRepository;

import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class QrCodeControllerTest extends BaseIntegrationTest {
    private static final String QR_CODES_URL = "/api/v1/qr-codes";
    private static final UUID TEST_QR_UUID = UUID.fromString("2e6c3a08-c6a6-47e1-a096-5e6a8ed03d5e");

    @Autowired
    QrCodeRepository qrCodeRepository;

    @Test
    @Sql(scripts = "/sql/insertOneParticipantForTest.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void handleDeleteQrCodeReturnNoContent() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders.delete(QR_CODES_URL + "/1");

        mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isNoContent()
                );
    }

    @Test
    @Sql(scripts = "/sql/insertOneParticipantForTest.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void deleteQrCodeWhenCodeDoesNotExistReturnsNotFound() throws Exception {
        Long nonExistentId = 1234L;
        RequestBuilder requestBuilder = MockMvcRequestBuilders.delete(QR_CODES_URL + "/{id}", nonExistentId);

        mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isNotFound()
                );

    }

    @Test
    @Sql(scripts = "/sql/insertOneParticipantForTest.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void deleteQrCodeWhenCodeIsAlreadyDeletedReturnsNotFound() throws Exception {
        qrCodeRepository.deleteById(1L);
        qrCodeRepository.flush();

        RequestBuilder requestBuilder = MockMvcRequestBuilders.delete(QR_CODES_URL + "/{id}", 1L);

        mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isNotFound()
                );
    }
}
