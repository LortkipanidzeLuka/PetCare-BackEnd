package ge.edu.freeuni.petcarebackend.controller.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ge.edu.freeuni.petcarebackend.repository.entity.AdvertisementImageEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.AssertTrue;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdvertisementImageDTO {

    private String title;

    private String content;

    private Boolean isPrimary;

    public AdvertisementImageDTO(AdvertisementImageEntity advertisementImage) {
        this.title = advertisementImage.getTitle();
        this.content = advertisementImage.getContent();
        this.isPrimary = advertisementImage.getIsPrimary();
    }

    @JsonIgnore
    @AssertTrue
    public boolean isValid() {
        if (content == null) {
            return false;
        }
        Matcher matcher = Pattern.compile("^data:image/(jpg|jpeg|png);base64,").matcher(content);
        if (!matcher.matches()) {
            return false;
        }
        byte[] decodedImageBytes;
        try {
            decodedImageBytes = Base64.getMimeDecoder().decode(content.substring(content.indexOf(',')));
        } catch (Exception e) {
            return false;
        }
        return decodedImageBytes.length < 5 * 1024 * 1024;
    }
}
