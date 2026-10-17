package pa.chan.qrinauth.controller;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pa.chan.qrinauth.dto.ParticipantRequestDto;
import pa.chan.qrinauth.dto.ParticipantResponseDto;
import pa.chan.qrinauth.dto.QrCodeResponseDto;
import pa.chan.qrinauth.service.ParticipantService;
import pa.chan.qrinauth.service.QrCodeService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/participants")
public class ParticipantController {

    private final ParticipantService participantService;
    private final QrCodeService qrCodeService;

    public ParticipantController(ParticipantService participantService, QrCodeService qrCodeService) {
        this.participantService = participantService;
        this.qrCodeService = qrCodeService;
    }

    @PostMapping
    public ResponseEntity<ParticipantResponseDto> create(@Valid @RequestBody ParticipantRequestDto request) {
        ParticipantResponseDto created = participantService.createParticipant(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PostMapping("/{id}/qr-codes")
    public ResponseEntity<QrCodeResponseDto> generateQrCode(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(qrCodeService.generateNewQrCode(id));
    }

    @GetMapping("/{id}/qr-codes")
    public ResponseEntity<List<QrCodeResponseDto>> getQrCodesByParticipant(@PathVariable Long id) {
        return ResponseEntity.ok(qrCodeService.getParticipantQrCodes(id));
    }

    @GetMapping
    public ResponseEntity<Page<ParticipantResponseDto>> getAll(Pageable pageable) {
        return ResponseEntity.ok(participantService.getAllParticipants(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ParticipantResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(participantService.getParticipant(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ParticipantResponseDto> update(
            @PathVariable Long id,
            @Valid @RequestBody ParticipantRequestDto request
    ) {
        return ResponseEntity.ok(participantService.updateParticipant(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        participantService.deleteParticipant(id);
        return ResponseEntity.noContent().build();
    }
}
