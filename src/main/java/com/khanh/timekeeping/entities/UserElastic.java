package com.khanh.timekeeping.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.khanh.timekeeping.constants.Gender;
import com.khanh.timekeeping.entities.enums.UserStatus;
import com.khanh.timekeeping.requests.UserRequest;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
@Document(indexName = "users_index")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserElastic {

    @Id
    private Long id;

    @Field(name = "fullName", type = FieldType.Text)
    private String fullName;

    @Field(name = "username", type = FieldType.Text)
    private String username;

    @Field(name = "dailyWage", type = FieldType.Double)
    private BigDecimal dailyWage;

    @Field(name = "created_at", type = FieldType.Date)
    private LocalDate createdAt;

    @Field(name = "modified_at", type = FieldType.Date)
    private LocalDate  modifiedAt;
}
