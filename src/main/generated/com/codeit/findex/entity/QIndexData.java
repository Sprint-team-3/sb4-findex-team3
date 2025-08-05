package com.codeit.findex.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QIndexData is a Querydsl query type for IndexData
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QIndexData extends EntityPathBase<IndexData> {

    private static final long serialVersionUID = -161524054L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QIndexData indexData = new QIndexData("indexData");

    public final com.codeit.findex.entity.base.QBaseEntity _super = new com.codeit.findex.entity.base.QBaseEntity(this);

    public final DatePath<java.time.LocalDate> baseDate = createDate("baseDate", java.time.LocalDate.class);

    public final NumberPath<Double> changeValue = createNumber("changeValue", Double.class);

    public final NumberPath<Double> closingPrice = createNumber("closingPrice", Double.class);

    //inherited
    public final DatePath<java.time.LocalDate> createdAt = _super.createdAt;

    public final BooleanPath enabled = createBoolean("enabled");

    public final NumberPath<Double> fluctuationRate = createNumber("fluctuationRate", Double.class);

    public final NumberPath<Double> highPrice = createNumber("highPrice", Double.class);

    //inherited
    public final NumberPath<Long> id = _super.id;

    public final QIndexInfo indexInfo;

    public final NumberPath<Double> lowPrice = createNumber("lowPrice", Double.class);

    public final NumberPath<Long> marketTotalAmount = createNumber("marketTotalAmount", Long.class);

    public final NumberPath<Double> openPrice = createNumber("openPrice", Double.class);

    public final EnumPath<com.codeit.findex.entityEnum.SourceType> sourceType = createEnum("sourceType", com.codeit.findex.entityEnum.SourceType.class);

    public final NumberPath<Long> tradingValue = createNumber("tradingValue", Long.class);

    public final NumberPath<Integer> tradingVolume = createNumber("tradingVolume", Integer.class);

    //inherited
    public final DatePath<java.time.LocalDate> updatedAt = _super.updatedAt;

    public QIndexData(String variable) {
        this(IndexData.class, forVariable(variable), INITS);
    }

    public QIndexData(Path<? extends IndexData> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QIndexData(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QIndexData(PathMetadata metadata, PathInits inits) {
        this(IndexData.class, metadata, inits);
    }

    public QIndexData(Class<? extends IndexData> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.indexInfo = inits.isInitialized("indexInfo") ? new QIndexInfo(forProperty("indexInfo")) : null;
    }

}

