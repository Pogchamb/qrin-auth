package pa.chan.qrinauth.dto;

import java.util.UUID;

public record QrCodeResponseDto(
        Long id,
        UUID uuid,
        boolean active
) {
}
