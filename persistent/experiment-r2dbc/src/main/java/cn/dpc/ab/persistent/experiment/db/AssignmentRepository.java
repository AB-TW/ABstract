package cn.dpc.ab.persistent.experiment.db;

import org.springframework.data.r2dbc.repository.R2dbcRepository;

/**
 * ExperimentRepository provides a skeleton implementation of the ExperimentRepository interface.
 */
public interface AssignmentRepository extends R2dbcRepository<AssignmentDb, String> {
}
