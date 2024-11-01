package com.khanh.timekeeping.controllers;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.khanh.timekeeping.responses.DefaultResponse;
import com.khanh.timekeeping.services.query.QueryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/queries")
@RequiredArgsConstructor
@Tag(name = "Query", description = "Query")
public class QueryController {

    private final QueryService queryService;

    @GetMapping
    public ResponseEntity<DefaultResponse<Boolean>> getQueries() throws JsonProcessingException  {
        queryService.showQuery();
//        queryService.fetchAllUser();
        return ResponseEntity.ok(DefaultResponse.success(Boolean.TRUE));
    }
}
