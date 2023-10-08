package cn.dpc.ab.persistent.featrue.db;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;


/**
 * FeatureRepository is the repository for FeatureDb.
 */
public interface FeatureRepository extends R2dbcRepository<FeatureDb, String>, ReactiveCrudRepository<FeatureDb, String> {

    Mono<FeatureDb> findByFeatureKey(String featureKey);
}
