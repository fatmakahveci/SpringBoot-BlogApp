package com.fatmakahveci.blog.controller;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;

import static org.assertj.core.api.Assertions.assertThat;

class PostPageRequestTests {

    @Test
    void pageAndSizeAreRestrictedToSafeValues() {
        var pageable = PostPageRequest.of(-5, 500, "newest");

        assertThat(pageable.getPageNumber()).isZero();
        assertThat(pageable.getPageSize()).isEqualTo(50);
    }

    @Test
    void unknownSortOptionFallsBackToNewestFirst() {
        var pageable = PostPageRequest.of(0, 10, "unknownProperty");
        Sort.Order createdAt = pageable.getSort().getOrderFor("createdAt");

        assertThat(createdAt).isNotNull();
        assertThat(createdAt.getDirection()).isEqualTo(Sort.Direction.DESC);
        assertThat(PostPageRequest.normalizeSort("unknownProperty")).isEqualTo("newest");
    }

    @Test
    void titleSortUsesOnlyTheWhitelistedTitleProperty() {
        var pageable = PostPageRequest.of(0, 10, "titleAsc");
        Sort.Order title = pageable.getSort().getOrderFor("title");

        assertThat(title).isNotNull();
        assertThat(title.getDirection()).isEqualTo(Sort.Direction.ASC);
    }
}
