package cn.dpc.ab.persistent.featrue.db;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

/**
 * FeatureRepository is the repository for FeatureDb.
 */
public interface FeatureRepository extends ReactiveCrudRepository<FeatureDb, String> {
}
