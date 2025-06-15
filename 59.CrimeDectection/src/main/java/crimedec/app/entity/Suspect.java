package crimedec.app.entity;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "suspects")
public class Suspect {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer suspectId;

    private Integer caseId;
    private String name;
    private Integer age;
    private String gender;
    private String notes;
}
