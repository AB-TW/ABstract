package cn.dpc.ab.persistent.experiment.db;

import cn.dpc.ab.domain.experiment.Experiment;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.couchbase.core.index.QueryIndexed;
import org.springframework.data.couchbase.core.mapping.Document;
import org.springframework.data.couchbase.core.mapping.id.GeneratedValue;

import static org.springframework.data.couchbase.core.mapping.id.GenerationStrategy.UNIQUE;

/**
 * AssignmentDb provides for save assignments.
 */
@Document
@Data
public class AssignmentDb {

    @Id
    @GeneratedValue(strategy = UNIQUE)
    private String id;

    @QueryIndexed
    private String featureKey;

    private Experiment.ExperimentId experimentId;
    private String clientId;

}
