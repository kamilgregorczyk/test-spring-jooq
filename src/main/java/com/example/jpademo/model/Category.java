package com.example.jpademo.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.util.Optional;

@Getter()
@Accessors(fluent = true)
@ToString
@EqualsAndHashCode(callSuper = true)
@SuperBuilder(builderMethodName = "category")
public class Category extends JooqModel<Long, Category.CategoryBuilder> {
    private final String title;

    @Override
    public CategoryBuilder toBuilder() {
        return Category.category()
            .isPersisted(isPersisted())
            .id(hasId() ? Optional.of(id()) : Optional.empty())
            .title(title);
    }
}
