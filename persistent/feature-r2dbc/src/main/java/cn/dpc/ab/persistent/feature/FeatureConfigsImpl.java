package cn.dpc.ab.persistent.feature;

import cn.dpc.ab.domain.exception.RecordNotFoundException;
import cn.dpc.ab.domain.feature.Feature;
import cn.dpc.ab.domain.feature.FeatureConfig;
import cn.dpc.ab.persistent.feature.db.CustomerFeatureConfigRepository;
import cn.dpc.ab.persistent.feature.db.FeatureConfigDb;
import cn.dpc.ab.persistent.feature.db.FeatureConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * FeatureConfigsImpl is the implementation of Feature.Configs.
 */
@RequiredArgsConstructor
public class FeatureConfigsImpl implements Feature.Configs {
    private final FeatureConfigRepository repository;
    private final CustomerFeatureConfigRepository customerRepository;
    private final Feature.FeatureId featureId;

    @Override
    public Flux<FeatureConfig> all(FilterParams params) {
        return customerRepository.findAll(featureId.featureKey(), params)
                .map(FeatureConfigDb::to);
    }

    @Override
    public Flux<FeatureConfig> subCollection(FilterParams params, long from, long to) {
        int pageSize = (int) (to - from);
        pageSize = pageSize < 0 ? 10 : pageSize;
        int page = (int) (from / pageSize);
        PageRequest pageRequest = PageRequest.of(page, pageSize, Sort.by("updatedAt").descending());

        return customerRepository.findAll(featureId.featureKey(), params, pageRequest)
                .map(FeatureConfigDb::to);
    }

    @Override
    public Mono<Long> size(FilterParams params) {
        return customerRepository.count(featureId.featureKey(), params);
    }

    @Override
    public Mono<FeatureConfig> get(FeatureConfig.FeatureConfigId id) {
        return repository.findById(id.configId())
                .map(FeatureConfigDb::to);
    }

    @Override
    public Mono<FeatureConfig> add(FeatureConfig featureConfig) {
        return repository.save(FeatureConfigDb.from(featureConfig))
                .map(FeatureConfigDb::to);
    }

    @Override
    public Mono<Void> update(FeatureConfig.FeatureConfigId id, FeatureConfig.Description description) {
        return repository.findById(id.configId())
                .switchIfEmpty(Mono.error(new RecordNotFoundException(id.configId())))
                .flatMap(db -> {
                    db.updateDescription(db.to().update(description).getDescription());
                    return repository.save(db);
                }).then();
    }

    @Override
    public Mono<Void> delete(FeatureConfig.FeatureConfigId id) {
        return repository.findById(id.configId())
                .switchIfEmpty(Mono.error(new RecordNotFoundException(id.configId())))
                .flatMap(db -> {
                    if (db.getStaticStatus() == FeatureConfig.Status.StaticStatus.DRAFT) {
                        return repository.delete(db);
                    }

                    db.updateDescription(db.to().delete().getDescription());
                    return repository.save(db);
                }).then();
    }

    @Override
    public Mono<Void> publish(FeatureConfig.FeatureConfigId id) {
        return repository.findById(id.configId())
                .switchIfEmpty(Mono.error(new RecordNotFoundException(id.configId())))
                .flatMap(db -> {
                    db.updateDescription(db.to().publish().getDescription());
                    return repository.save(db);
                }).then();
    }

    @Override
    public Mono<Void> disable(FeatureConfig.FeatureConfigId id) {
        return repository.findById(id.configId())
                .switchIfEmpty(Mono.error(new RecordNotFoundException(id.configId())))
                .flatMap(db -> {
                    db.updateDescription(db.to().disable().getDescription());
                    return repository.save(db);
                }).then();
    }

    @Override
    public Mono<Void> top(FeatureConfig.FeatureConfigId id) {
        return repository.findById(id.configId())
                .switchIfEmpty(Mono.error(new RecordNotFoundException(id.configId())))
                .flatMap(db -> {
                    db.updateDescription(db.to().top().getDescription());
                    return repository.save(db);
                }).then();
    }
}
