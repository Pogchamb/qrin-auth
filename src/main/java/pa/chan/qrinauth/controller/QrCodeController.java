package pa.chan.qrinauth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pa.chan.qrinauth.dto.QrCodeResponseDto;
import pa.chan.qrinauth.service.QrCodeService;

import java.util.List;

@RestController
@RequestMapping("/api")
public class QrCodeController {
    private final QrCodeService qrCodeService;

    public QrCodeController(QrCodeService qrCodeService) {
        this.qrCodeService = qrCodeService;
    }

    @GetMapping("/participants/{participantId}/qr-codes")
    public ResponseEntity<List<QrCodeResponseDto>> getQrCodesByParticipant(@PathVariable Long participantId) {
        return ResponseEntity.ok(qrCodeService.getParticipantQrCodes(participantId));
    }

    @PostMapping("/participants/{participantId}/qr-codes")
    public ResponseEntity<QrCodeResponseDto> generateQrCode(@PathVariable Long participantId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(qrCodeService.generateNewQrCode(participantId));
    }

    @DeleteMapping("/qr-codes/{qrCodeId}")
    public ResponseEntity<Void> deleteQrCode(@PathVariable Long qrCodeId) {
        qrCodeService.deleteQrCode(qrCodeId);
        return ResponseEntity.noContent().build();
    }
}
