package com.makibeans.search;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = false)
public class SearchRequest<F> {

        @Schema(description = "Free-text search across configured fields.")
        private String search;

        @Schema(description = "Domain-specific filter object.")
        @Valid
        @NotNull
        private F filters;

        @Schema(description = "Zero-based page index. Defaults to 0.")
        @Min(0)
        private Integer page;

        @Schema(description = "Page size. Defaults to 20.")
        @Min(1)
        private Integer size;

        @Schema(description = "Sort by field/property name.")
        private String sortBy = "id";

        @Schema(description = "Sort direction (ASC/DESC).")
        @JsonAlias({"sortDir", "sortOrder","direction", "dir"})
        private SortDirection sortDirection = SortDirection.ASC;

        @Schema(description = "Include soft-deleted records (if supported).")
        private Boolean includeDeleted;

}
