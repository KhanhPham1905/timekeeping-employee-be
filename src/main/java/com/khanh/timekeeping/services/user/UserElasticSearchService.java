package com.khanh.timekeeping.services.user;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.khanh.timekeeping.entities.UserElastic;
import com.khanh.timekeeping.repositories.UserElasticSearchRepository;
import com.khanh.timekeeping.utils.ElasticSearchUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserElasticSearchService {
//    private final ElasticsearchClient elasticsearchClient;
    private final UserElasticSearchRepository userElasticSearchRepository;
    public UserElastic createIndex(UserElastic userElastic) {

        return userElasticSearchRepository.save(userElastic);
    }

    public UserElastic getIndex(Long id) {
        return userElasticSearchRepository.findById(id).orElse(null);
    }


    public List<String> autoSuggestItemsByNameWithQuery(String name) {
        List<UserElastic> items = userElasticSearchRepository.customAutoSuggestQuery(name);
        log.info("ElastichSearch Response: {}", items.toString());
        return items.stream()
                .map(UserElastic::getFullName)
                .collect(Collectors.toList());
    }
}
