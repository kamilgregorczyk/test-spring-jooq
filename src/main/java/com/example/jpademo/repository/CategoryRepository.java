package com.example.jpademo.repository;

import com.example.jpademo.model.Category;
import org.jooq.DSLContext;
import org.jooq.generated.default_schema.tables.records.CategoryRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static org.jooq.generated.default_schema.Tables.CATEGORY;

@Repository
public class CategoryRepository extends JooqRepository<Category, CategoryRecord, Long> {

    @Autowired
    public CategoryRepository(DSLContext context) {
        super(context, CATEGORY);
    }

    @Override
    protected Category fromRecord(CategoryRecord record) {
        return Category.category().id(Optional.of(record.getId())).isPersisted(true).title(record.getTitle()).build();
    }

    @Override
    protected CategoryRecord toRecord(CategoryRecord emptyRecord, Category model) {
        return emptyRecord.setTitle(model.title());
    }

    public Optional<Category> findByTitle(String title) {
        return findOneWhere(CATEGORY.TITLE.eq(title));
    }
}
