package com.codeit.findex.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QIntegration is a Querydsl query type for Integration
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QIntegration extends EntityPathBase<Integration> {

    private static final long serialVersionUID = -346002078L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QIntegration integration = new QIntegration("integration");

    public final com.codeit.findex.entity.base.QBaseEntity _super = new com.codeit.findex.entity.base.QBaseEntity(this);

    public final DatePath<java.time.LocalDate> baseDate = createDate("baseDate", java.time.LocalDate.class);

    //inherited
    public final DatePath<java.time.LocalDate> createdAt = _super.createdAt;

    //inherited
    public final NumberPath<Long> id = _super.id;

    public final QIndexData indexData;

    public final QIndexInfo indexInfo;

    public final DateTimePath<java.time.LocalDateTime> jobTime = createDateTime("jobTime", java.time.LocalDateTime.class);

    public final EnumPath<com.codeit.findex.entityEnum.JobType> jobType = createEnum("jobType", com.codeit.findex.entityEnum.JobType.class);

    public final EnumPath<com.codeit.findex.entityEnum.Result> result = createEnum("result", com.codeit.findex.entityEnum.Result.class);

    //inherited
    public final DatePath<java.time.LocalDate> updatedAt = _super.updatedAt;

    public final StringPath worker = createString("worker");

    public QIntegration(String variable) {
        this(Integration.class, forVariable(variable), INITS);
    }

    public QIntegration(Path<? extends Integration> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QIntegration(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QIntegration(PathMetadata metadata, PathInits inits) {
        this(Integration.class, metadata, inits);
    }

    public QIntegration(Class<? extends Integration> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.indexData = inits.isInitialized("indexData") ? new QIndexData(forProperty("indexData"), inits.get("indexData")) : null;
        this.indexInfo = inits.isInitialized("indexInfo") ? new QIndexInfo(forProperty("indexInfo")) : null;
    }

}

