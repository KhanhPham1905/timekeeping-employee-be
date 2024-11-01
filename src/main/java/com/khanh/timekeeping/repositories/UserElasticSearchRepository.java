package com.khanh.timekeeping.repositories;

import com.khanh.timekeeping.entities.UserElastic;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface UserElasticSearchRepository extends ElasticsearchRepository<UserElastic, Long> {
    @Query("{\"bool\": {\"must\": {\"match_phrase_prefix\": {\"fullName\": \"?0\"}}}}")
    List<UserElastic> customAutoSuggestQuery(String fullName);
}
