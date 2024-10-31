package com.khanh.timekeeping.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import org.springframework.data.domain.Page;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record DefaultResponse<T>(
        Boolean success,
        String message,
        Pagination pagination,
        T data
) {

    public static <T> DefaultResponse<T> success(Page<?> page, T data) {
        var pagination = new Pagination(page.getNumber(), page.getSize(), page.getTotalPages(), page.getTotalElements());
        return new DefaultResponse<>(Boolean.TRUE, "Thành công", pagination, data);
    }

    public static <T> DefaultResponse<T> success(T data) {
        return new DefaultResponse<>(Boolean.TRUE, "Thành công", null, data);
    }

    public static <T> DefaultResponse<T> error(String message) {
        return new DefaultResponse<>(Boolean.FALSE, message, null, null);
    }

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record Pagination(
            Integer currentPage,
            Integer pageSize,
            Integer totalPages,
            Long totalElements
    ) {

    }

}

