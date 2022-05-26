package ge.edu.freeuni.petcarebackend.api.dtos;

import java.util.List;

public record SearchResultDTO<T>(List<T> items, long totalCount) {
}
