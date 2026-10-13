package pa.chan.qrinauth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pa.chan.qrinauth.entity.QrCode;
import pa.chan.qrinauth.exceptions.InvalidQrCodeException;

import java.util.Optional;
import java.util.UUID;

public interface QrCodeRepository extends JpaRepository<QrCode, Long> {

    Optional<QrCode> findByUuid(UUID uuid);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update QrCode q set q.active = false where q.uuid = :uuid and q.active = true")
    int deactivateByUuid(@Param("uuid") UUID uuid);

    default QrCode findByUuidOrThrow(UUID uuid) {
        return findByUuid(uuid)
                .orElseThrow(() -> new InvalidQrCodeException("The QR code was not found or has already been used."));
    }

    default void deactivatedByUuidOrThrow(UUID uuid) {
        int deactivated = deactivateByUuid(uuid);
        if (deactivated == 0) {
            throw new InvalidQrCodeException("The QR code was not found or has already been used.");
        }
    }
}
