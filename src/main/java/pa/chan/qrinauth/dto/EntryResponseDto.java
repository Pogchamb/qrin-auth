package pa.chan.qrinauth.dto;

import java.util.UUID;

public record EntryResponseDto(String fullName, UUID newQrCode) {
}
