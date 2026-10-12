package pa.chan.qrinauth.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pa.chan.qrinauth.dto.EntryResponseDto;
import pa.chan.qrinauth.dto.QrCodeRequestDto;
import pa.chan.qrinauth.service.QrAuthService;

@RestController
@RequestMapping("/api/auth")
public class QrAuthController {
    private final QrAuthService qrAuthService;

    public QrAuthController(QrAuthService qrAuthService) {
        this.qrAuthService = qrAuthService;
    }

    @PostMapping("/qr")
    public ResponseEntity<EntryResponseDto> enterClub(@Valid @RequestBody QrCodeRequestDto request) {
        return ResponseEntity.ok(qrAuthService.processEntry(request));
    }

}
