package ge.edu.freeuni.petcarebackend.repository.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@Entity
@Table(name = "advertisement_images")
public class AdvertisementImageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "advertisement_images")
    @SequenceGenerator(name = "advertisement_images", sequenceName = "seq_advertisement_images", allocationSize = 1)
    private Long id;

    @NotBlank
    private String title;

    @Lob
    @NotNull
    private String content;

    @Column(name = "is_primary")
    private Boolean isPrimary;

    @ManyToOne
    @JoinColumn(name = "advertisement_id")
    @JsonIgnore
    private AdvertisementEntity advertisement;
}
