package org.example.expert.domain.log.manager;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QManagerReqLog is a Querydsl query type for ManagerReqLog
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QManagerReqLog extends EntityPathBase<ManagerReqLog> {

    private static final long serialVersionUID = 25437972L;

    public static final QManagerReqLog managerReqLog = new QManagerReqLog("managerReqLog");

    public final org.example.expert.domain.common.entity.QTimestamped _super = new org.example.expert.domain.common.entity.QTimestamped(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath message = createString("message");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final NumberPath<Long> requestUserId = createNumber("requestUserId", Long.class);

    public final EnumPath<ManagerReqStatus> status = createEnum("status", ManagerReqStatus.class);

    public final NumberPath<Long> targetUserId = createNumber("targetUserId", Long.class);

    public final NumberPath<Long> todoId = createNumber("todoId", Long.class);

    public QManagerReqLog(String variable) {
        super(ManagerReqLog.class, forVariable(variable));
    }

    public QManagerReqLog(Path<? extends ManagerReqLog> path) {
        super(path.getType(), path.getMetadata());
    }

    public QManagerReqLog(PathMetadata metadata) {
        super(ManagerReqLog.class, metadata);
    }

}

