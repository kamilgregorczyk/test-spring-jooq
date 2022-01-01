package com.example.jpademo.resource;

import com.example.jpademo.model.Category;
import com.example.jpademo.model.Note;
import com.example.jpademo.model.Recipe;
import com.example.jpademo.repository.CategoryRepository;
import com.example.jpademo.repository.NoteRepository;
import com.example.jpademo.repository.RecipeRepository;
import com.example.jpademo.repository.RecipeToCategoryRepository;
import lombok.Builder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toUnmodifiableList;
import static java.util.stream.Collectors.toUnmodifiableSet;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.ResponseEntity.*;

@RestController
public class RecipeResource {

    private final RecipeRepository recipeRepository;
    private final NoteRepository noteRepository;
    private final RecipeToCategoryRepository recipeToCategoryRepository;
    private final CategoryRepository categoryRepository;

    @Autowired
    public RecipeResource(RecipeRepository recipeRepository,
                          NoteRepository noteRepository,
                          RecipeToCategoryRepository recipeToCategoryRepository,
                          CategoryRepository categoryRepository) {
        this.recipeRepository = recipeRepository;
        this.noteRepository = noteRepository;
        this.recipeToCategoryRepository = recipeToCategoryRepository;
        this.categoryRepository = categoryRepository;
    }

    @PostMapping("/recipes")
    @Transactional
    public ResponseEntity createRecipe(@RequestBody @Valid CreateRecipeRequest request) {
        final var recipe = recipeRepository.save(Recipe.recipe()
            .title(request.title)
            .description(request.description)
            .build());
        final var categories = request.categories.stream().map(this::getOrCreateCategory).collect(toUnmodifiableSet());
        recipeToCategoryRepository.save(recipe, categories);
        request.notes.forEach(noteRequest -> createNote(noteRequest, recipe));
        return status(CREATED).body(Map.of("id", recipeRepository.save(recipe).id()));
    }

    @GetMapping("/recipes/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<RecipeResponse> getRecipe(@PathVariable("id") Long id) {
        return recipeRepository.findById(id)
            .map(recipe -> {
                final var notes = noteRepository.findAllByRecipeId(recipe.id());
                final var categories = recipeToCategoryRepository.findAllByRecipeId(recipe.id());
                return ok().body(recipeToResponse(recipe, notes, categories));
            })
            .orElseGet(() -> notFound().build());
    }

    private Category getOrCreateCategory(CreateCategoryRequest categoryRequest) {
        return categoryRepository.findByTitle(categoryRequest.getTitle())
            .orElseGet(() -> categoryRepository.save(Category.category()
                .title(categoryRequest.getTitle())
                .build()));
    }

    private void createNote(CreateNoteRequest noteRequest, Recipe recipe) {
        noteRepository.save(Note.note()
            .title(noteRequest.title)
            .description(noteRequest.description)
            .recipeId(recipe.id())
            .build());
    }

    private static RecipeResponse recipeToResponse(Recipe recipe,
                                                   List<Note> notes,
                                                   List<Category> categories) {
        return RecipeResponse.recipeResponse()
            .title(recipe.title())
            .description(recipe.description())
            .notes(notes.stream()
                .map(RecipeResource::noteToResponse)
                .sorted(comparing(NoteResponse::getTitle))
                .collect(toUnmodifiableList()))
            .categories(categories.stream()
                .map(RecipeResource::categoryToResponse)
                .sorted(comparing(CategoryResponse::getTitle))
                .collect(toUnmodifiableList()))
            .build();
    }

    private static NoteResponse noteToResponse(Note note) {
        return NoteResponse.noteResponse().title(note.title()).description(note.description()).build();
    }

    public static CategoryResponse categoryToResponse(Category category) {
        return CategoryResponse.categoryResponse()
            .title(category.title())
            .build();
    }

    @Data
    static class CreateRecipeRequest {
        @NotBlank
        public String title;

        @NotBlank
        public String description;

        @NotEmpty
        public Set<CreateNoteRequest> notes;

        @NotEmpty
        public Set<CreateCategoryRequest> categories;
    }

    @Data
    static class CreateNoteRequest {
        @NotBlank
        public String title;

        @NotBlank
        public String description;
    }

    @Data
    static class CreateCategoryRequest {
        @NotBlank
        public String title;
    }

    @Data
    @Builder(builderMethodName = "recipeResponse")
    static class RecipeResponse {
        public String title;
        public String description;
        public List<NoteResponse> notes;
        public List<CategoryResponse> categories;
    }

    @Data
    @Builder(builderMethodName = "noteResponse")
    static class NoteResponse {
        public String title;
        public String description;
    }

    @Data
    @Builder(builderMethodName = "categoryResponse")
    static class CategoryResponse {
        public String title;
    }
}
