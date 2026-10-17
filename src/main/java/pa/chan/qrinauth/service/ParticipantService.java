package pa.chan.qrinauth.service;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pa.chan.qrinauth.dto.ParticipantRequestDto;
import pa.chan.qrinauth.dto.ParticipantResponseDto;
import pa.chan.qrinauth.entity.Participant;
import pa.chan.qrinauth.entity.QrCode;
import pa.chan.qrinauth.repository.ParticipantRepository;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ParticipantService {

    private final ParticipantRepository participantRepository;

    public ParticipantService(ParticipantRepository participantRepository) {
        this.participantRepository = participantRepository;
    }

    @Transactional
    public ParticipantResponseDto createParticipant(ParticipantRequestDto request) {
        Participant participant = new Participant(request.firstName(), request.lastName());

        participant.addQrCode(new QrCode(UUID.randomUUID(), participant));

        return toDto(participantRepository.save(participant));
    }


    @Transactional(readOnly = true)
    public Page<ParticipantResponseDto> getAllParticipants(Pageable pageable) {
        return participantRepository.findAll(pageable)
                .map(this::toDto);
    }

    @Transactional(readOnly = true)
    public ParticipantResponseDto getParticipant(Long id) {
        return toDto(participantRepository.findByIdOrThrow(id));
    }

    @Transactional
    public ParticipantResponseDto updateParticipant(Long id, ParticipantRequestDto request) {
        Participant participant = participantRepository.findByIdOrThrow(id);
        participant.setFirstName(request.firstName());
        participant.setLastName(request.lastName());
        return toDto(participant);
    }

    @Transactional
    public void deleteParticipant(Long id) {
        Participant participant = participantRepository.findByIdOrThrow(id);
        LocalDateTime now = LocalDateTime.now();

        participant.setDeleted(true);
        participant.setDeletedAt(now);

        for (QrCode qrCode : participant.getQrCodes()) {
            qrCode.setDeleted(true);
            qrCode.setDeletedAt(now);
            qrCode.setActive(false);
        }
    }

    private ParticipantResponseDto toDto(Participant participant) {
        return new ParticipantResponseDto(participant.getId(), participant.getFirstName(), participant.getLastName());
    }

}
