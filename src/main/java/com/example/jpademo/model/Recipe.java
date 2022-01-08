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
@SuperBuilder(builderMethodName = "recipe")
public class Recipe extends JooqModel<Long, Recipe.RecipeBuilder<?, ?>> {
    private final String title;
    private final String description;

    public Recipe.RecipeBuilder<?, ?> toBuilder() {
        return Recipe.recipe()
            .isPersisted(isPersisted())
            .id(hasId() ? Optional.of(id()) : Optional.empty())
            .title(title)
            .description(description);
    }
}
