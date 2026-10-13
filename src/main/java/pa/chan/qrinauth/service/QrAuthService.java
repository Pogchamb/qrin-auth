package pa.chan.qrinauth.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pa.chan.qrinauth.dto.EntryResponseDto;
import pa.chan.qrinauth.dto.QrCodeRequestDto;
import pa.chan.qrinauth.entity.Participant;
import pa.chan.qrinauth.entity.QrCode;
import pa.chan.qrinauth.exceptions.InvalidQrCodeException;
import pa.chan.qrinauth.repository.QrCodeRepository;

@Service
public class QrAuthService {
    private final QrCodeRepository qrCodeRepository;
    private final QrCodeService qrCodeService;

    public QrAuthService(QrCodeRepository qrCodeRepository, QrCodeService qrCodeService) {
        this.qrCodeRepository = qrCodeRepository;
        this.qrCodeService = qrCodeService;
    }

    @Transactional
    public EntryResponseDto processEntry(QrCodeRequestDto request) {
        qrCodeRepository.deactivatedByUuidOrThrow(request.uuid());
        QrCode usedQrCode = qrCodeRepository.findByUuidOrThrow(request.uuid());

        Participant participant = usedQrCode.getParticipant();
        QrCode newQrCode = qrCodeService.issueNewCode(participant);

        return new EntryResponseDto(participant.getFirstName(), participant.getLastName(), newQrCode.getUuid());
    }
}
