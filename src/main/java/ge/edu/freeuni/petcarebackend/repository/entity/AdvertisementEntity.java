package ge.edu.freeuni.petcarebackend.repository.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import ge.edu.freeuni.petcarebackend.security.repository.entity.UserEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuperBuilder
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "advertisement")
@Inheritance(strategy = InheritanceType.JOINED)
public class AdvertisementEntity {

    @Id
    @SequenceGenerator(name = "advertisement", sequenceName = "seq_advertisement", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "advertisement")
    private Long id;

    @NotBlank
    private String header;

    @ManyToOne
    @JoinColumn(name = "creator_user")
    private UserEntity creatorUser;

    @Column(name = "create_date")
    private LocalDate createDate;

    private BigDecimal longitude;

    private BigDecimal latitude;

    @NotNull
    @Enumerated(EnumType.STRING)
    private City city;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    @NotNull
    private AdvertisementType advertisementType;

    @ElementCollection
    @CollectionTable(name = "tags", joinColumns = @JoinColumn(name = "advertisement_id"))
    @Column(name = "value")
    private List<String> tags = new ArrayList<>();

    @Size(max = 10)
    @OneToMany(mappedBy = "advertisement", cascade = {CascadeType.REMOVE, CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<AdvertisementImageEntity> images = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AdvertisementEntity that = (AdvertisementEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    public void setImages(List<AdvertisementImageEntity> images) {
        this.images.clear();
        if (images != null) {
            images.forEach(advertisementImageEntity -> advertisementImageEntity.setAdvertisement(this));
            this.images.addAll(images);
        }
    }
}
