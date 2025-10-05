package com.makibeans.dto.search;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Search request with filtering, sorting, and pagination options.")
public class SearchRequestDTO {

        @Schema(description = "Free-text search term applied to searchable fields.")
        private String search;

        @Schema(description = "Dynamic filters as key-value pairs, e.g. {\"categoryId\": 1, \"required\": true}.")
        private Map<String, Object> filters;

        @Schema(description = "Field to sort by, e.g. 'name' or 'createdAt'. Defaults to 'id'.")
        private String sortBy;

        @Schema(description = "Sort order: 'asc' or 'desc'. Defaults to 'asc'.")
        private String sortOrder;

        @Schema(description = "Whether to include deleted records in the results. Defaults to false.")
        private Boolean includeDeleted = false;

        @Min(value = 0, message = "Page number must be 0 or greater.")
        @Schema(description = "Page number starting from 0. Defaults to 0.")
        private Integer page;

        @Min(value = 1, message = "Page size must be at least 1.")
        @Schema(description = "Number of results per page. Defaults to 20.")
        private Integer size;
}
