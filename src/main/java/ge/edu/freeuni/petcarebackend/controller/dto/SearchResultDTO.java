package ge.edu.freeuni.petcarebackend.controller.dto;

import java.util.List;

public record SearchResultDTO<T>(List<T> items, long totalCount) {
}
