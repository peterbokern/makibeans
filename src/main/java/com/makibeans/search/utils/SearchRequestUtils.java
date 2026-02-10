package com.makibeans.search.utils;

import com.makibeans.search.SearchRequest;
import com.makibeans.search.SortDirection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class SearchRequestUtils {

    private static final Logger logger = LoggerFactory.getLogger(SearchRequestUtils.class);

    public static <F> SearchRequest<F> assemble(
            F filters,
            String search,
            Boolean includeDeleted,
            Pageable pageable
    ) {
        SearchRequest<F> req = new SearchRequest<>();
        req.setFilters(filters);
        req.setSearch(search);
        req.setIncludeDeleted(includeDeleted);
        return mergeWithPageable(req, pageable);
    }

    public static <F> SearchRequest<F> mergeWithPageable(
            SearchRequest<F> req,
            Pageable pageable
    ) {
        logger.info("Merging SearchRequest {} with Pageable: {}", pageable.toString(), req.toString());
        if (req.getPage() == null) req.setPage(pageable.getPageNumber());
        if (req.getSize() == null) req.setSize(pageable.getPageSize());
        if (req.getSortBy() == null && pageable.getSort().isSorted()) {
            Sort.Order o = pageable.getSort().iterator().next();
            req.setSortBy(o.getProperty());
            req.setSortDirection(o.getDirection() == Sort.Direction.DESC
                    ? SortDirection.DESC
                    : SortDirection.ASC);
        }
        logger.info("Resulting SearchRequest: {}", req);
        return req;
    }
}
