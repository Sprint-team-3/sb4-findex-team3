package com.codeit.findex.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QIndexInfo is a Querydsl query type for IndexInfo
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QIndexInfo extends EntityPathBase<IndexInfo> {

    private static final long serialVersionUID = -161363026L;

    public static final QIndexInfo indexInfo = new QIndexInfo("indexInfo");

    public final com.codeit.findex.entity.base.QBaseEntity _super = new com.codeit.findex.entity.base.QBaseEntity(this);

    public final NumberPath<Double> baseIndex = createNumber("baseIndex", Double.class);

    public final DatePath<java.time.LocalDate> basepointInTime = createDate("basepointInTime", java.time.LocalDate.class);

    //inherited
    public final DatePath<java.time.LocalDate> createdAt = _super.createdAt;

    public final NumberPath<Integer> employedItemsCount = createNumber("employedItemsCount", Integer.class);

    public final BooleanPath enabled = createBoolean("enabled");

    public final BooleanPath favorite = createBoolean("favorite");

    //inherited
    public final NumberPath<Long> id = _super.id;

    public final StringPath indexClassification = createString("indexClassification");

    public final StringPath indexName = createString("indexName");

    public final EnumPath<com.codeit.findex.entityEnum.SourceType> sourceType = createEnum("sourceType", com.codeit.findex.entityEnum.SourceType.class);

    //inherited
    public final DatePath<java.time.LocalDate> updatedAt = _super.updatedAt;

    public QIndexInfo(String variable) {
        super(IndexInfo.class, forVariable(variable));
    }

    public QIndexInfo(Path<? extends IndexInfo> path) {
        super(path.getType(), path.getMetadata());
    }

    public QIndexInfo(PathMetadata metadata) {
        super(IndexInfo.class, metadata);
    }

}

