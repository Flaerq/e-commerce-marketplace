package com.example.ecommercemarketplace.utils;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;

import java.util.List;

public class ESUtil {

    public static Query buildMultiLineSearchQuery(String query, List<String> fields) {
        NativeQueryBuilder queryBuilder = new NativeQueryBuilder();

        return queryBuilder.withQuery(QueryBuilders.multiMatch(builder ->
                builder
                        .query(query)
                        .fields(fields)
                        .fuzziness("AUTO")
                        .autoGenerateSynonymsPhraseQuery(true))
        ).build();
    }
}


