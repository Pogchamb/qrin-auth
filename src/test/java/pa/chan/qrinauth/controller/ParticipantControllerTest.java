package pa.chan.qrinauth.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import pa.chan.qrinauth.BaseIntegrationTest;
import pa.chan.qrinauth.dto.ParticipantRequestDto;
import pa.chan.qrinauth.entity.Participant;
import pa.chan.qrinauth.entity.QrCode;
import pa.chan.qrinauth.repository.ParticipantRepository;
import pa.chan.qrinauth.repository.QrCodeRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ParticipantControllerTest extends BaseIntegrationTest {
    private static final String PARTICIPANTS_URL = "/api/v1/participants";

    @Autowired
    ParticipantRepository participantRepository;

    @Autowired
    QrCodeRepository qrCodeRepository;

    // GET /participants/{id}

    @Test
    @Sql(scripts = "/sql/insertOneParticipantForTest.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void handleGetParticipantByIdReturnValidResponseEntity() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(PARTICIPANTS_URL + "/1");

        mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("""
                                {
                                "id": 1,
                                "firstName": "Витя",
                                "lastName": "Мамонтов"
                                }
                                """)
                );
    }

    @Test
    @Sql(scripts = "/sql/insertOneParticipantForTest.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void handleGetParticipantByIdShouldReturn404() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(PARTICIPANTS_URL + "/2");

        mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("""
                                {
                                "status": 404,
                                "message": "Participant 2 not found"
                                }
                                """)
                );
    }

    // GET /participants

    @Test
    @Sql(scripts = "/sql/insertParticipantsForGetAllTests.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void handleGetParticipantsShouldReturnFirstPagePagedParticipants() throws Exception {

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(PARTICIPANTS_URL)
                .param("page", "0")
                .param("size", "5");

        mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("""
                                {
                                "content": [
                                {
                                "id": 1,
                                "firstName": "firstName1",
                                "lastName": "lastName1"
                                },
                                {
                                "id": 2,
                                "firstName": "firstName2",
                                "lastName": "lastName2"
                                },
                                {
                                "id": 3,
                                "firstName": "firstName3",
                                "lastName": "lastName3"
                                },
                                {
                                "id": 4,
                                "firstName": "firstName4",
                                "lastName": "lastName4"
                                },
                                {
                                "id": 5,
                                "firstName": "firstName5",
                                "lastName": "lastName5"
                                }
                                ]
                                }
                                """),
                        jsonPath("$.totalElements").value(13),
                        jsonPath("$.totalPages").value(3),
                        jsonPath("$.number").value(0),
                        jsonPath("$.first").value(true),
                        jsonPath("$.size").value(5)
                );
    }

    @Test
    @Sql(scripts = "/sql/insertParticipantsForGetAllTests.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void handleGetParticipantsShouldReturnLastPagePagedParticipants() throws Exception {

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(PARTICIPANTS_URL)
                .param("page", "2")
                .param("size", "5");

        mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("""
                                {
                                "content": [
                                {
                                "id": 11,
                                "firstName": "firstName11",
                                "lastName": "lastName11"
                                },
                                {
                                "id": 12,
                                "firstName": "firstName12",
                                "lastName": "lastName12"
                                },
                                {
                                "id": 13,
                                "firstName": "firstName13",
                                "lastName": "lastName13"
                                }
                                ]
                                }
                                """),
                        jsonPath("$.totalElements").value(13),
                        jsonPath("$.totalPages").value(3),
                        jsonPath("$.number").value(2),
                        jsonPath("$.last").value(true),
                        jsonPath("$.size").value(5)
                );
    }

    @Test
    @Sql(scripts = "/sql/insertParticipantsForGetAllTests.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void handleGetParticipantsShouldReturnSecondPagePagedParticipants() throws Exception {

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(PARTICIPANTS_URL)
                .param("page", "1")
                .param("size", "5");

        mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("""
                                {
                                "content": [
                                {
                                "id": 6,
                                "firstName": "firstName6",
                                "lastName": "lastName6"
                                },
                                {
                                "id": 7,
                                "firstName": "firstName7",
                                "lastName": "lastName7"
                                },
                                {
                                "id": 8,
                                "firstName": "firstName8",
                                "lastName": "lastName8"
                                },
                                {
                                "id": 9,
                                "firstName": "firstName9",
                                "lastName": "lastName9"
                                },
                                {
                                "id": 10,
                                "firstName": "firstName10",
                                "lastName": "lastName10"
                                }
                                ]
                                }
                                """),
                        jsonPath("$.totalElements").value(13),
                        jsonPath("$.totalPages").value(3),
                        jsonPath("$.number").value(1),
                        jsonPath("$.size").value(5)
                );
    }

    @Test
    @Sql(scripts = "/sql/insertParticipantsForGetAllTests.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void handleGetParticipantsWithOutParamsShouldReturnDefaultPagedParticipants() throws Exception {

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(PARTICIPANTS_URL);

        mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("""
                                {
                                "content": [
                                {
                                "id": 1,
                                "firstName": "firstName1",
                                "lastName": "lastName1"
                                },
                                {
                                "id": 2,
                                "firstName": "firstName2",
                                "lastName": "lastName2"
                                },
                                {
                                "id": 3,
                                "firstName": "firstName3",
                                "lastName": "lastName3"
                                },
                                {
                                "id": 4,
                                "firstName": "firstName4",
                                "lastName": "lastName4"
                                },
                                {
                                "id": 5,
                                "firstName": "firstName5",
                                "lastName": "lastName5"
                                },
                                {
                                "id": 6,
                                "firstName": "firstName6",
                                "lastName": "lastName6"
                                },
                                {
                                "id": 7,
                                "firstName": "firstName7",
                                "lastName": "lastName7"
                                },
                                {
                                "id": 8,
                                "firstName": "firstName8",
                                "lastName": "lastName8"
                                },
                                {
                                "id": 9,
                                "firstName": "firstName9",
                                "lastName": "lastName9"
                                },
                                {
                                "id": 10,
                                "firstName": "firstName10",
                                "lastName": "lastName10"
                                },
                                {
                                "id": 11,
                                "firstName": "firstName11",
                                "lastName": "lastName11"
                                },
                                {
                                "id": 12,
                                "firstName": "firstName12",
                                "lastName": "lastName12"
                                },
                                {
                                "id": 13,
                                "firstName": "firstName13",
                                "lastName": "lastName13"
                                }
                                ]
                                }
                                """),
                        jsonPath("$.totalElements").value(13),
                        jsonPath("$.totalPages").value(1),
                        jsonPath("$.number").value(0),
                        jsonPath("$.size").value(20)
                );
    }

    @Test
    @Sql(scripts = "/sql/insertParticipantsForGetAllTests.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void handleGetParticipantsWithNotExistPageShouldReturn200AndEmptyContent() throws Exception {

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(PARTICIPANTS_URL)
                .param("page", "200")
                .param("size", "5");

        mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("""
                                {
                                "content": []
                                }
                                """),
                        jsonPath("$.totalElements").value(13),
                        jsonPath("$.totalPages").value(3),
                        jsonPath("$.empty").value(true)
                );
    }

    @Test
    @Sql(scripts = "/sql/insertParticipantsForGetAllTests.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void handleGetParticipantsWithNegativePageShouldFallsBackToDefaults() throws Exception {

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(PARTICIPANTS_URL)
                .param("page", "-1")
                .param("size", "0");

        mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("""
                                {
                                "content": [
                                {
                                "id": 1,
                                "firstName": "firstName1",
                                "lastName": "lastName1"
                                },
                                {
                                "id": 2,
                                "firstName": "firstName2",
                                "lastName": "lastName2"
                                },
                                {
                                "id": 3,
                                "firstName": "firstName3",
                                "lastName": "lastName3"
                                },
                                {
                                "id": 4,
                                "firstName": "firstName4",
                                "lastName": "lastName4"
                                },
                                {
                                "id": 5,
                                "firstName": "firstName5",
                                "lastName": "lastName5"
                                },
                                {
                                "id": 6,
                                "firstName": "firstName6",
                                "lastName": "lastName6"
                                },
                                {
                                "id": 7,
                                "firstName": "firstName7",
                                "lastName": "lastName7"
                                },
                                {
                                "id": 8,
                                "firstName": "firstName8",
                                "lastName": "lastName8"
                                },
                                {
                                "id": 9,
                                "firstName": "firstName9",
                                "lastName": "lastName9"
                                },
                                {
                                "id": 10,
                                "firstName": "firstName10",
                                "lastName": "lastName10"
                                },
                                {
                                "id": 11,
                                "firstName": "firstName11",
                                "lastName": "lastName11"
                                },
                                {
                                "id": 12,
                                "firstName": "firstName12",
                                "lastName": "lastName12"
                                },
                                {
                                "id": 13,
                                "firstName": "firstName13",
                                "lastName": "lastName13"
                                }
                                ]
                                }
                                """),
                        jsonPath("$.totalElements").value(13),
                        jsonPath("$.totalPages").value(1),
                        jsonPath("$.number").value(0),
                        jsonPath("$.size").value(20)
                );
    }

    // POST /participants

    @Test
    void handleCreateParticipantShouldReturnValidResponseEntity() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(PARTICIPANTS_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(new ParticipantRequestDto("Витя", "Мамонтов")));

        mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isCreated(),
                        header().exists(HttpHeaders.LOCATION),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("""
                                {
                                "id": 1,
                                "firstName": "Витя",
                                "lastName": "Мамонтов"
                                }
                                """),
                        jsonPath("$.id").exists()
                );
        assertEquals(1, participantRepository.findAll().size());
        final Participant participant = participantRepository.findAll().get(0);
        assertNotNull(participant.getId());
        assertEquals("Витя", participant.getFirstName());
        assertEquals("Мамонтов", participant.getLastName());
    }

    @Test
    void handleCreateParticipantWithBlankFirstNameShouldReturn400() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(PARTICIPANTS_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(new ParticipantRequestDto(null, "Мамонтов")));

        mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isBadRequest(),
                        header().doesNotExist(HttpHeaders.LOCATION),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("""
                                {
                                "status": 400,
                                "message": "firstName: must not be blank"
                                }
                                """)
                );
        assertEquals(0, participantRepository.findAll().size());
    }

    @Test
    void handleCreateParticipantWithBlankLastNameShouldReturn400() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(PARTICIPANTS_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(new ParticipantRequestDto("Витя", null)));

        mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isBadRequest(),
                        header().doesNotExist(HttpHeaders.LOCATION),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("""
                                {
                                "status": 400,
                                "message": "lastName: must not be blank"
                                }
                                """)
                );
        assertEquals(0, participantRepository.findAll().size());
    }

    @Test
    void handleCreateParticipantWithToLongLastNameShouldReturn400() throws Exception {
        String longName = "a".repeat(256);
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(PARTICIPANTS_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(new ParticipantRequestDto("Витя", longName)));

        mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isBadRequest(),
                        header().doesNotExist(HttpHeaders.LOCATION),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("""
                                {
                                "status": 400,
                                "message": "lastName: lastName is too long"
                                }
                                """)
                );
        assertEquals(0, participantRepository.findAll().size());
    }

    @Test
    void handleCreateParticipantWithToLongFirstNameShouldReturn400() throws Exception {
        String longName = "a".repeat(256);
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(PARTICIPANTS_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(new ParticipantRequestDto(longName, "Мамонтов")));

        mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isBadRequest(),
                        header().doesNotExist(HttpHeaders.LOCATION),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("""
                                {
                                "status": 400,
                                "message": "firstName: firstName is too long"
                                }
                                """)
                );
        assertEquals(0, participantRepository.findAll().size());
    }

    //  PUT /participants/{id}

    @Test
    @Sql(scripts = "/sql/insertOneParticipantForTest.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void handleUpdateParticipantShouldReturnValidResponseEntity() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders.put(PARTICIPANTS_URL + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(new ParticipantRequestDto("Ваня", "Катрич")));

        mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("""
                                {
                                "id": 1,
                                "firstName": "Ваня",
                                "lastName": "Катрич"
                                }
                                """),
                        jsonPath("$.id").exists()
                );

        assertEquals(1, participantRepository.findAll().size());
        Participant participantUpdate = participantRepository.getParticipantsById(1L);
        assertEquals("Ваня", participantUpdate.getFirstName());
        assertEquals("Катрич", participantUpdate.getLastName());

    }

    @Test
    @Sql(scripts = "/sql/insertOneParticipantForTest.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void handleUpdateParticipantWhenNotFoundShouldReturn404() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders.put(PARTICIPANTS_URL + "/2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(new ParticipantRequestDto("Ваня", "Катрич")));

        mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("""
                                {
                                "status": 404,
                                "message": "Participant 2 not found"
                                }
                                """)
                );


    }

    @Test
    @Sql(scripts = "/sql/insertOneParticipantForTest.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void handleUpdateParticipantWithBlankNameShouldReturn400() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders.put(PARTICIPANTS_URL + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(new ParticipantRequestDto(null, "Катрич")));

        mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("""
                                {
                                "status": 400,
                                "message": "firstName: must not be blank"
                                }
                                """)
                );

    }

    // DELETE /participants/{id}

    @Test
    @Sql(scripts = "/sql/insertOneParticipantForTest.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void handleDeleteParticipantShouldHideParticipant() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders.delete(PARTICIPANTS_URL + "/1");

        mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isNoContent()
                );
        Boolean isDeleted = jdbcTemplate.queryForObject(
                "SELECT is_deleted FROM participants WHERE id = 1",
                Boolean.class
        );
        assertEquals(true, isDeleted);
    }

    @Test
    void handleDeleteParticipantWhenDoesNotExist_Returns404() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders.delete(PARTICIPANTS_URL + "/2");

        mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isNotFound()
                );
    }

    @Test
    @Sql(scripts = "/sql/insertOneParticipantForTest.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void handleDeleteParticipantWhenAlreadyDeleted_Returns204() throws Exception {
        Participant participant = participantRepository.findByIdOrThrow(1L);
        LocalDateTime now = LocalDateTime.now();
        participant.setDeleted(true);
        participant.setDeletedAt(now);
        RequestBuilder requestBuilder = MockMvcRequestBuilders.delete(PARTICIPANTS_URL + "/1");

        mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isNoContent()
                );
    }

    // POST /participants/{id}/qr-codes

    @Test
    @Sql(scripts = "/sql/insertOneParticipantForTest.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void handleGenerateQrCodeShouldDeactivateOldAndReturnNewActive() throws Exception {
        Participant participant = participantRepository.findAll().get(0);
        QrCode oldQrCode = qrCodeRepository.findAll().get(0);
        UUID oldUUID = oldQrCode.getUuid();

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(PARTICIPANTS_URL + "/{id}/qr-codes", participant.getId());

        mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isCreated(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("""
                                {
                                "id": 2,
                                "active": true
                                }
                                """),
                        jsonPath("$.uuid").isNotEmpty()
                );

        List<QrCode> qrCodes = qrCodeRepository.findAll();
        assertEquals(2, qrCodes.size());

        QrCode updateOldQrCode = qrCodes.stream()
                .filter(qrCode -> qrCode.getUuid().equals(oldUUID))
                .findFirst()
                .orElseThrow();
        assertFalse(updateOldQrCode.isActive());
        QrCode newQrCode = qrCodes.stream()
                .filter(qrCode -> !qrCode.getUuid().equals(oldUUID))
                .findFirst()
                .orElseThrow();
        assertTrue(newQrCode.isActive());
    }

    @Test
    void handleGenerateQrCodeWhenNoPreviousCodesShouldCreatesFirstCode() throws Exception {
        Participant participant = participantRepository.save(new Participant("Иван", "Старый"));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(PARTICIPANTS_URL + "/{id}/qr-codes", participant.getId());

        mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isCreated());

        List<QrCode> qrCodes = qrCodeRepository.findAll();
        assertEquals(1, qrCodes.size());
        assertTrue(qrCodes.get(0).isActive());
    }

    @Test
    void handleGenerateQrCodeWhenParticipantDoesNotExistReturns404() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(PARTICIPANTS_URL + "/{id}/qr-codes", 2);

        mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("$.message").value("Participant 2 not found"));

    }


    // GET /participants/{id}/qr-codes
    @Test
    @Sql(scripts = "/sql/insertOneParticipantForTest.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void handleGetQrCodesShouldReturnResponseEntity() throws Exception {
        Participant participant = participantRepository.findAll().get(0);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(PARTICIPANTS_URL + "/{id}/qr-codes", participant.getId());

        mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("""
                                [
                                {
                                "id": 1,
                                "active": true
                                }
                                ]
                                """)
                );
    }

    @Test
    void handleGetQrCodesWhenParticipantNotFound_shouldReturn404() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(PARTICIPANTS_URL + "/222/qr-codes");

        mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("""
                                {
                                "status": 404,
                                "message": "Participant 222 not found"
                                }
                                """)
                );
    }
}
