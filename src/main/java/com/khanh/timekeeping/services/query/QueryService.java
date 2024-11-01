package com.khanh.timekeeping.services.query;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface QueryService {

    void showQuery() throws JsonProcessingException;

    void fetchAllUser();
}
