package pa.chan.qrinauth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pa.chan.qrinauth.entity.Participant;
import pa.chan.qrinauth.exceptions.ResourceNotFoundException;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {

    default Participant findByIdOrThrow(Long id) {
        return findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Participant " + id + " not found"));
    }

    Participant getParticipantsById(Long id);
}
