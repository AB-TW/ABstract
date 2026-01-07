package cn.dpc.ab.persistent.feature.db;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;


/**
 * FeatureRepository is the repository for FeatureDb.
 */
public interface FeatureRepository extends R2dbcRepository<FeatureDb, String> {

    Mono<FeatureDb> findByFeatureKey(String featureKey);
}
