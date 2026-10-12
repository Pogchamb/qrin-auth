package pa.chan.qrinauth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pa.chan.qrinauth.entity.Participant;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {
}
