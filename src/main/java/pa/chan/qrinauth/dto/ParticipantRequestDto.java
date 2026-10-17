package pa.chan.qrinauth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ParticipantRequestDto(
        @NotBlank
        @Size(max = 255, message = "firstName is too long")
        String firstName,

        @NotBlank
        @Size(max = 255, message = "lastName is too long")
        String lastName
) {
}
