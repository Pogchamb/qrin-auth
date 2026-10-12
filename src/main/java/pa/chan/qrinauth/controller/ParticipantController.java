package pa.chan.qrinauth.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pa.chan.qrinauth.dto.ParticipantRequestDto;
import pa.chan.qrinauth.dto.ParticipantResponseDto;
import pa.chan.qrinauth.service.ParticipantService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/participants")
public class ParticipantController {

    private final ParticipantService participantService;

    public ParticipantController(ParticipantService participantService) {
        this.participantService = participantService;
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

    @GetMapping
    public ResponseEntity<List<ParticipantResponseDto>> getAll() {
        return ResponseEntity.ok(participantService.getAllParticipants());
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
