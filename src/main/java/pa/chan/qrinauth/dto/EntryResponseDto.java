package pa.chan.qrinauth.dto;

import java.util.UUID;

public record EntryResponseDto(String firstName, String lastName, UUID newQrCode) {
}
