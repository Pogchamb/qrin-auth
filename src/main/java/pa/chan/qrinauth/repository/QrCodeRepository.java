package pa.chan.qrinauth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pa.chan.qrinauth.entity.QrCode;

import java.util.Optional;
import java.util.UUID;

public interface QrCodeRepository extends JpaRepository<QrCode, Long> {

    Optional<QrCode> findByUuid(UUID uuid);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update QrCode q set q.active = false where q.uuid = :uuid and q.active = true")
    int deactivateByUuid(@Param("uuid") UUID uuid);
}
