package pa.chan.qrinauth.service;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pa.chan.qrinauth.dto.ParticipantRequestDto;
import pa.chan.qrinauth.dto.ParticipantResponseDto;
import pa.chan.qrinauth.entity.Participant;
import pa.chan.qrinauth.entity.QrCode;
import pa.chan.qrinauth.exceptions.ResourceNotFoundException;
import pa.chan.qrinauth.repository.ParticipantRepository;

import java.util.List;
import java.util.UUID;

@Service
public class ParticipantService {

    private final ParticipantRepository participantRepository;

    public ParticipantService(ParticipantRepository participantRepository) {
        this.participantRepository = participantRepository;
    }

    @Transactional
    public ParticipantResponseDto createParticipant(ParticipantRequestDto request) {
        Participant participant = new Participant(request.fullName());

        participant.addQrCode(new QrCode(UUID.randomUUID(), participant));

        return toDto(participantRepository.save(participant));
    }


    @Transactional(readOnly = true)
    public List<ParticipantResponseDto> getAllParticipants() {
        return participantRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public ParticipantResponseDto getParticipant(Long id) {
        return toDto(findOrThrow(id));
    }

    @Transactional
    public ParticipantResponseDto updateParticipant(Long id, ParticipantRequestDto request) {
        Participant participant = findOrThrow(id);
        participant.setFullName(request.fullName());
        return toDto(participant);
    }

    @Transactional
    public void deleteParticipant(Long id) {
        findOrThrow(id);
        participantRepository.deleteById(id);
    }

    private Participant findOrThrow(Long id) {
        return participantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Participant " + id + " not found"));
    }

    private ParticipantResponseDto toDto(Participant participant) {
        return new ParticipantResponseDto(participant.getId(), participant.getFullName());
    }

}
