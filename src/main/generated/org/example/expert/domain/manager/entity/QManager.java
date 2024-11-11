package org.example.expert.domain.manager.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QManager is a Querydsl query type for Manager
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QManager extends EntityPathBase<Manager> {

    private static final long serialVersionUID = 216623447L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QManager manager = new QManager("manager");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final org.example.expert.domain.todo.entity.QTodo todo;

    public final org.example.expert.domain.user.entity.QUser user;

    public QManager(String variable) {
        this(Manager.class, forVariable(variable), INITS);
    }

    public QManager(Path<? extends Manager> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QManager(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QManager(PathMetadata metadata, PathInits inits) {
        this(Manager.class, metadata, inits);
    }

    public QManager(Class<? extends Manager> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.todo = inits.isInitialized("todo") ? new org.example.expert.domain.todo.entity.QTodo(forProperty("todo"), inits.get("todo")) : null;
        this.user = inits.isInitialized("user") ? new org.example.expert.domain.user.entity.QUser(forProperty("user")) : null;
    }

}

