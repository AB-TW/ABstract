package cn.dpc.ab.api.experiment.dto;

import cn.dpc.ab.domain.experiment.Experiment;
import lombok.*;

/**
 * ExperimentRequest provides for experiment api.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExperimentRequest {

    private Experiment.ExperimentId id;

    private Experiment.Description description;

    public Experiment to() {
        return new Experiment(this.getId(), this.getDescription());
    }

}
