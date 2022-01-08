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
@SuperBuilder(builderMethodName = "recipeToCategory")
public class RecipeToCategory extends JooqModel<Long, RecipeToCategory.RecipeToCategoryBuilder> {
    private final Long recipeId;
    private final Long categoryId;

    @Override
    public RecipeToCategoryBuilder toBuilder() {
        return RecipeToCategory.recipeToCategory()
            .isPersisted(isPersisted())
            .id(hasId() ? Optional.of(id()) : Optional.empty())
            .categoryId(categoryId)
            .recipeId(recipeId);
    }
}
