package com.example.jpademo.repository;

import com.example.jpademo.model.JooqModel;
import org.jooq.*;

import java.util.List;
import java.util.Optional;

public abstract class JooqRepository<MODEL extends JooqModel<ID>, RECORD extends UpdatableRecord<RECORD>, ID> {

    protected final DSLContext ctx;
    private final Table<RECORD> table;

    public JooqRepository(DSLContext ctx, Table<RECORD> table) {
        this.ctx = ctx;
        this.table = table;
    }

    public MODEL save(MODEL model) {
        if (model.isPersisted()) {
            final var updatedRecord = ctx.update(table).set(toRecord(model)).where(idField().eq(model.id())).returning().fetchOne();
            return fromRecord(updatedRecord);

        } else {
            final var preparedRecord = toRecord(model);
            if (preparedRecord.get(idField()) == null) {
                preparedRecord.changed(idField(), false);
            }
            final var insertedRecord = ctx.insertInto(table).set(preparedRecord).returning().fetchOne();
            return fromRecord(insertedRecord);
        }
    }

    public Optional<MODEL> findOneWhere(Condition condition) {
        return ctx.selectFrom(table).where(condition).fetchOptional(this::fromRecord);
    }

    public Optional<MODEL> findById(ID id) {
        return findOneWhere(idField().eq(id));
    }

    public List<MODEL> findAllWhere(Condition condition) {
        return ctx.selectFrom(table).where(condition).fetch().map(this::fromRecord);
    }

    protected abstract MODEL fromRecord(RECORD record);

    protected abstract RECORD toRecord(MODEL model);

    private Field<ID> idField() {
        return (Field<ID>) table.field("id");
    }

}
