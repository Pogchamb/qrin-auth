package pa.chan.qrinauth.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record QrCodeRequestDto(
        @NotNull(message = "uuid  must be not null")
        UUID uuid
) {
}
