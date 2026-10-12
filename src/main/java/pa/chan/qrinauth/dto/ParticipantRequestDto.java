package pa.chan.qrinauth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ParticipantRequestDto(
        @NotBlank
        @Size(max = 255, message = "fullName is too long")
        String fullName
) {
}
