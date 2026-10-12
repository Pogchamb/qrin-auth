package pa.chan.qrinauth.entity;


import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "participants")
public class Participant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @OneToMany(mappedBy = "participant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QrCode> qrCodes = new ArrayList<>();

    public Participant() {
    }

    public Participant(String fullName) {
        this.fullName = fullName;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public List<QrCode> getQrCodes() {
        return qrCodes;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void addQrCode(QrCode qrCode) {
        this.qrCodes.add(qrCode);
        qrCode.setParticipant(this);
    }
}
