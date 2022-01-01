package com.example.jpademo

import com.example.jpademo.repository.CategoryRepository
import com.example.jpademo.repository.RecipeRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.MockMvc

import static org.skyscreamer.jsonassert.JSONAssert.assertEquals
import static org.springframework.http.MediaType.APPLICATION_JSON
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class RecipeResourceSpec extends Spec {

    @Autowired
    private MockMvc mvc

    @Autowired
    private RecipeRepository recipeRepository
    @Autowired
    private CategoryRepository categoryRepository

    def "should create recipe"() {
        given:
        def mapper = new ObjectMapper()
        def request = mapper.writerWithDefaultPrettyPrinter()
            .writeValueAsString([
                title      : "Title1",
                description: "Description",
                notes      : [
                    [
                        title      : "a",
                        description: "a"
                    ],
                    [
                        title      : "b",
                        description: "b"
                    ]
                ],
                categories : [
                    [
                        title: "cat3"
                    ]
                ]
            ])

        when:
        def response = mvc.perform(post("/recipes")
            .contentType(APPLICATION_JSON)
            .content(request))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString()

        then:
        assertEquals(response, new JSONObject(["id": 1]), true)
        recipeRepository.findByTitle("Title1").isPresent()
        with(recipeRepository.findByTitle("Title1").orElseThrow(), {
            it.title() == "Title1"
            it.description() == "Description"
        })

    }

    def "should create recipe and not duplicate categories"() {
        given:
        def mapper = new ObjectMapper()
        def request = { title ->
            mapper.writerWithDefaultPrettyPrinter().writeValueAsString(
                [
                    title      : title,
                    description: "Description",
                    notes      : [
                        [
                            title      : "a",
                            description: "a"
                        ],
                        [
                            title      : "b",
                            description: "b"
                        ]
                    ],
                    categories : [
                        [
                            title: "cat1"
                        ]
                    ]
                ]
            )
        }

        when:
        2.times {
            mvc.perform(post("/recipes")
                .contentType(APPLICATION_JSON)
                .content(request.call(it.toString())))
                .andExpect(status().isCreated())
        }

        then:
        recipeRepository.findByTitle("0").isPresent()
        recipeRepository.findByTitle("1").isPresent()
    }

    def "should get recipe"() {
        given:
        def mapper = new ObjectMapper()
        def request = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(
            [
                title      : "Title2",
                description: "Description",
                notes      : [
                    [
                        title      : "a",
                        description: "a"
                    ],
                    [
                        title      : "b",
                        description: "b"
                    ]
                ],
                categories : [
                    [
                        title: "cat1"
                    ],
                    [
                        title: "cat2"
                    ]
                ]
            ]
        )
        and: "create recipe"
        def createdRecipe = mvc.perform(post("/recipes")
            .contentType(APPLICATION_JSON)
            .content(request))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString()
        def createdRecipeId = mapper.readTree(createdRecipe).get("id").asLong()

        when:
        def response = mvc.perform(get("/recipes/${createdRecipeId}"))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString()

        then:
        assertEquals(response, new JSONObject(
            [
                title      : "Title2",
                description: "Description",
                notes      : [
                    [
                        title      : "a",
                        description: "a"
                    ],
                    [
                        title      : "b",
                        description: "b"
                    ]
                ],
                categories : [
                    [
                        title: "cat1"
                    ],
                    [
                        title: "cat2"
                    ]
                ]
            ]), true)

    }
}
