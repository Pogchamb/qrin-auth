package pa.chan.qrinauth.service;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pa.chan.qrinauth.dto.QrCodeResponseDto;
import pa.chan.qrinauth.entity.Participant;
import pa.chan.qrinauth.entity.QrCode;
import pa.chan.qrinauth.exceptions.ResourceNotFoundException;
import pa.chan.qrinauth.repository.ParticipantRepository;
import pa.chan.qrinauth.repository.QrCodeRepository;

import java.util.List;
import java.util.UUID;

@Service
public class QrCodeService {

    private final QrCodeRepository qrCodeRepository;
    private final ParticipantRepository participantRepository;

    public QrCodeService(QrCodeRepository qrCodeRepository, ParticipantRepository participantRepository) {
        this.qrCodeRepository = qrCodeRepository;
        this.participantRepository = participantRepository;
    }

    @Transactional(readOnly = true)
    public List<QrCodeResponseDto> getParticipantQrCodes(Long participantId) {
        Participant participant = participantRepository.findByIdOrThrow(participantId);

        return participant.getQrCodes().stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public QrCodeResponseDto generateNewQrCode(Long participantId) {
        Participant participant = participantRepository.findByIdOrThrow(participantId);

        participant.getQrCodes().forEach(qrCode -> qrCode.setActive(false));

        return toDto(issueNewCode(participant));
    }

    @Transactional
    public QrCode issueNewCode(Participant participant) {
        participant.getQrCodes().forEach(qrCode -> qrCode.setActive(false));
        QrCode newQrCode = new QrCode(UUID.randomUUID(), participant);
        participant.addQrCode(newQrCode);
        return qrCodeRepository.save(newQrCode);
    }

    @Transactional
    public void deleteQrCode(Long qrCodeId) {
        if (!qrCodeRepository.existsById(qrCodeId)) {
            throw new ResourceNotFoundException("QrCode " + qrCodeId + " not found");
        }
        qrCodeRepository.deleteById(qrCodeId);
    }

    private QrCodeResponseDto toDto(QrCode qrCode) {
        return new QrCodeResponseDto(qrCode.getId(), qrCode.getUuid(), qrCode.isActive());
    }
}
