package pa.chan.qrinauth.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import pa.chan.qrinauth.BaseIntegrationTest;
import pa.chan.qrinauth.entity.Participant;
import pa.chan.qrinauth.entity.QrCode;
import pa.chan.qrinauth.repository.ParticipantRepository;
import pa.chan.qrinauth.repository.QrCodeRepository;

import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class QrCodeControllerTest extends BaseIntegrationTest {
    private static final String QR_CODES_URL = "/api/v1/qr-codes";
    private static final UUID TEST_QR_UUID = UUID.fromString("2e6c3a08-c6a6-47e1-a096-5e6a8ed03d5e");

    @Autowired
    QrCodeRepository qrCodeRepository;

    @Autowired
    ParticipantRepository participantRepository;


    @BeforeEach
    void setUpForEnderClubTests() {
        Participant participant = new Participant("Витя", "Мамонтов");
        QrCode qrCode = new QrCode(TEST_QR_UUID, participant);
        qrCode.setActive(true);
        participant.addQrCode(qrCode);
        participantRepository.saveAndFlush(participant);
    }


    @Test
    void handleDeleteQrCodeReturnNoContent() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders.delete(QR_CODES_URL + "/1");

        mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isNoContent()
                );
    }

    @Test
    void deleteQrCodeWhenCodeDoesNotExistReturnsNotFound() throws Exception {
        Long nonExistentId = 1234L;
        RequestBuilder requestBuilder = MockMvcRequestBuilders.delete(QR_CODES_URL + "/{id}", nonExistentId);

        mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isNotFound()
                );

    }

    @Test
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
