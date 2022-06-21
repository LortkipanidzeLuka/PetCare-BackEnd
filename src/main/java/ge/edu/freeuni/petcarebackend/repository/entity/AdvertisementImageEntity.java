package ge.edu.freeuni.petcarebackend.repository.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
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
