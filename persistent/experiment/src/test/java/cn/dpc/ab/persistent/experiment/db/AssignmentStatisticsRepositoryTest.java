package cn.dpc.ab.persistent.experiment.db;

import cn.dpc.ab.persistent.RepositoryTestBase;
import cn.dpc.ab.persistent.experiment.AssignmentTestUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

class AssignmentStatisticsRepositoryTest extends RepositoryTestBase {
    @Autowired
    AssignmentDbRepository repository;

    @Autowired
    AssignmentStatisticsRepository statisticsRepository;

    @BeforeEach
    void setUp() {
        repository.deleteAll().block();
    }

    @AfterEach
    void tearDown() {
        repository.deleteAll().block();
    }

    @Test
    public void should_statisticCountByBucketKey_success() {
        String experimentId = "experimentId1111";
        repository.save(AssignmentTestUtil.createAssignmentDb("123123", experimentId, 111111, "key1"))
                .as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete();

        repository.save(AssignmentTestUtil.createAssignmentDb("1231234", experimentId, 111111, "key2"))
                .as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete();

        repository.save(AssignmentTestUtil.createAssignmentDb("12312346", experimentId, 111111, "key2"))
                .as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete();

        repository.save(AssignmentTestUtil.createAssignmentDb("1231235", experimentId, 111111, null))
                .as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete();

        statisticsRepository.statisticCountByBucketKey(experimentId)
                .collectList()
                .as(StepVerifier::create)
                .expectNextMatches(list -> list.size() == 3
                        && list.stream().anyMatch(item -> "key1".equals(item.bucketKey()) && item.count() == 1L)
                        && list.stream().anyMatch(item -> "key2".equals(item.bucketKey()) && item.count() == 2L)
                        && list.stream().anyMatch(item -> item.bucketKey() == null && item.count() == 1L)
                )
                .verifyComplete();
    }
}