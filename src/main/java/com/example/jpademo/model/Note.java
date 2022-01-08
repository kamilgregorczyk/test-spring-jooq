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
@SuperBuilder(builderMethodName = "note")
public class Note extends JooqModel<Long, Note.NoteBuilder> {
    private final String title;
    private final String description;
    private final Long recipeId;

    @Override
    public NoteBuilder toBuilder() {
        return Note.note()
            .isPersisted(isPersisted())
            .id(hasId() ? Optional.of(id()) : Optional.empty())
            .title(title)
            .description(description)
            .recipeId(recipeId);
    }
}
