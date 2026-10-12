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
        int deactivated = qrCodeRepository.deactivateByUuid(request.uuid());
        if (deactivated == 0) {
            throw new InvalidQrCodeException("The QR code was not found or has already been used.");
        }

        QrCode usedQrCode = qrCodeRepository.findByUuid(request.uuid())
                .orElseThrow(() -> new InvalidQrCodeException("The QR code was not found or has already been used."));

        Participant participant = usedQrCode.getParticipant();
        QrCode newQrCode = qrCodeService.issueNewCode(participant);

        return new EntryResponseDto(participant.getFullName(), newQrCode.getUuid());
    }
}
