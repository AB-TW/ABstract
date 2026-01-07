package cn.dpc.ab.persistent.feature.db;

import org.springframework.data.r2dbc.repository.R2dbcRepository;

/**
 * FeatureConfigRepository is the repository of FeatureConfigDb.
 */
public interface FeatureConfigRepository extends R2dbcRepository<FeatureConfigDb, String> {
}
