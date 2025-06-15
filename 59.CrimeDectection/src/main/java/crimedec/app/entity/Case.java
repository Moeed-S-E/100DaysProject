package crimedec.app.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "cases")
public class Case {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer caseId;
    private String title;
    private String description;
    private String status;
    private Date dateReported;

    @OneToMany(mappedBy = "caseId", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Suspect> suspects;

    @OneToMany(mappedBy = "caseId", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Evidence> evidenceList;
}
