package looqbox.backendchallenge.application.web.controllers

import io.mockk.every
import io.mockk.mockk
import looqbox.backendchallenge.domain.models.PokemonHighlight
import looqbox.backendchallenge.domain.services.PokemonService
import looqbox.backendchallenge.utils.mocks.PokemonHighlightMock.sampleHighlightPokemon
import looqbox.backendchallenge.utils.mocks.PokemonMock.samplePokemonList
import looqbox.backendchallenge.utils.mocks.PokemonMock.samplePokemonRequest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

class BackendChallengeControllerTest {

    private lateinit var backendChallengeController: BackendChallengeController
    private lateinit var pokemonService: PokemonService

    @BeforeEach
    fun setUp() {
        pokemonService = mockk(relaxed = true)
        backendChallengeController = BackendChallengeController(pokemonService)
    }

    @Test
    fun `should return a list of pokemons ordered alphabetically successfully`() {

        val query = "mon"
        val sort = "Alphabetical"
        val expectedSize = 4
        val pokemonRequestMock = samplePokemonRequest(query, sort)
        val pokemonList = samplePokemonList(listOf("hitmonchan", "hitmonlee", "hitmontop", "monferno"))

        every { pokemonService.getPokemonsByName(query, sort) } returns pokemonList

        val result = backendChallengeController.getPokemons(pokemonRequestMock)

        assertNotNull(result)
        assertEquals(expectedSize, result.body!!.results.size)
        assertEquals(result.statusCode, HttpStatus.OK)
        assertTrue(result.body!!.results.all { it.contains(query) })
    }

    @Test
    fun `should return a list of pokemon ordered by name length successfully`() {

        val query = "drag"
        val sort = "length"
        val expectedSize = 5
        val initialHighlight = "<pre>"
        val finalHighlight = "</pre>"
        val pokemonRequestMock = samplePokemonRequest(query, sort)
        val pokemonList: List<PokemonHighlight> = listOf(
            sampleHighlightPokemon("dragalge", "<pre>drag</pre>alge"),
            sampleHighlightPokemon("dragapult", "<pre>drag</pre>apult"),
            sampleHighlightPokemon("dragonair", "<pre>drag</pre>onair"),
            sampleHighlightPokemon("dragonite", "<pre>drag</pre>onite"),
            sampleHighlightPokemon("regidrago", "regi<pre>drag</pre>o"),
        )

        every { pokemonService.getPokemonsByNameHighlighted(query, sort) } returns pokemonList

        val result = backendChallengeController.getPokemonsHighlight(pokemonRequestMock)

        assertNotNull(result)
        assertEquals(expectedSize, result.body!!.results.size)
        assertEquals(result.statusCode, HttpStatus.OK)
        assertTrue(result.body!!.results.all { it.name.contains(query) })
        assertTrue(result.body!!.results.all { it.highlight!!.contains(initialHighlight) })
        assertTrue(result.body!!.results.all { it.highlight!!.contains(finalHighlight) })
    }
}
