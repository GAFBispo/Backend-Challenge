package looqbox.backendchallenge.domain.services

import io.mockk.*
import looqbox.backendchallenge.common.cache.CacheConfiguration
import looqbox.backendchallenge.domain.enums.SortTypeEnum.*
import looqbox.backendchallenge.domain.models.PokemonHighlight
import looqbox.backendchallenge.resources.external.api.RestConfiguration
import looqbox.backendchallenge.utils.mocks.PokemonHighlightMock.sampleHighlightPokemon
import looqbox.backendchallenge.utils.mocks.PokemonMock.samplePokemonList
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class PokemonServiceTest {

    private lateinit var pokemonService: PokemonService
    private lateinit var restConfiguration: RestConfiguration
    private lateinit var cacheConfiguration: CacheConfiguration

    @BeforeEach
    fun setUp() {
        restConfiguration = mockk(relaxed = true)
        cacheConfiguration = mockk(relaxed = true)
        pokemonService = PokemonService(restConfiguration, cacheConfiguration)
    }

    @AfterEach
    fun clearCache() {
        cacheConfiguration.clearAll()
    }

    @Test
    fun `should return a list of pokemons ordered by length of the PokeAPI name when there is no data saved in cache`() {

        val query = "pidge"
        val sort = "length"
        val pokemonList = samplePokemonList(listOf("pidgetto", "pidgeot", "pidge"))
        val expectedSize = pokemonList.size

        every { cacheConfiguration.getPokemonFromCache(any(), any()) } returns null
        every { restConfiguration.get() } returns pokemonList

        val pokemons = pokemonService.getPokemonsByName(query, sort)

        assertNotNull(pokemons)
        assertEquals(expectedSize, pokemons.size)
        assertTrue(pokemons.all { it.name.contains(query) })

        // Estou validando os parametros que foram salvos no cache =)
        verify(exactly = 1) {
            cacheConfiguration.savePokemonToCache(
                keyMap1 = query,
                keyMap2 = LENGTH,
                keyValue = withArg { pokemon ->
                    assertEquals(pokemon.size, expectedSize)
                    assertTrue(pokemon.all { it.name.contains("pidge") })
                }
            )
        }
    }

    @Test
    fun `should return a list of pokemons saved in cache ordered by name length and not call PokeAPI`() {

        val query = "bul"
        val sort = "Alphabetical"
        val pokemonList = samplePokemonList(listOf("bulbasaur", "granbull", "snubbull", "tapu-bulu", "tadbulb"))
        val expectedSize = pokemonList.size

        every { cacheConfiguration.getPokemonFromCache(query, ALPHABETICAL) } returns pokemonList

        val pokemons = pokemonService.getPokemonsByName(query, sort)

        assertNotNull(pokemons)
        assertEquals(expectedSize, pokemons.size)
        assertTrue(pokemons.all { it.name.contains(query) })

        verify(exactly = 0) {
            restConfiguration.get()
            cacheConfiguration.savePokemonToCache(any(), any(), any())
        }
    }

    @Test
    fun `should return a list of highlighted pokemons in alphabetical order from PokeAPI when there is no data saved in cache`() {

        val query = "loe"
        val sort = "Alphabetical"
        val initialHighlight = "<pre>"
        val finalHighlight = "</pre>"
        val pokemonList = samplePokemonList(listOf("meloetta-aria","floette"))
        val expectedSize = pokemonList.size

        every { cacheConfiguration.getPokemonHighlightedFromCache(any(), any()) } returns null
        every { restConfiguration.get() } returns pokemonList

        val pokemonHighlight = pokemonService.getPokemonsByNameHighlighted(query, sort)

        assertNotNull(pokemonHighlight)
        assertEquals(expectedSize, pokemonHighlight.size)
        assertTrue(pokemonHighlight.all { it.name.contains(query) })
        assertTrue(pokemonHighlight.all { it.highlight!!.contains(initialHighlight) })
        assertTrue(pokemonHighlight.all { it.highlight!!.contains(finalHighlight) })

        // Estou validando os parametros que foram salvos no cache =)
        verify(exactly = 1) {
            cacheConfiguration.savePokemonHighlightedCache(
                keyMap1 = query,
                keyMap2 = ALPHABETICAL,
                keyValue = withArg { pokemon ->
                    assertEquals(pokemon.count(), expectedSize)
                    assertTrue(pokemon.all { it.name.contains("loe") })
                    assertTrue(pokemon.all { it.highlight!!.contains("<pre>") })
                    assertTrue(pokemon.all { it.highlight!!.contains("</pre>") })
                }
            )
        }
    }

    @Test
    fun `should return a list of highlighted pokemons saved in cache in alphabetical order and not call PokeAPI`() {

        val query = "red"
        val sort = "Alphabetical"
        val expectedSize = 5
        val initialHighlight = "<pre>"
        val finalHighlight = "</pre>"
        val pokemonList: List<PokemonHighlight> = listOf(
            sampleHighlightPokemon("loudred", "loud<pre>red</pre>"),
            sampleHighlightPokemon("basculin-red-striped", "basculin-<pre>red</pre>-striped"),
            sampleHighlightPokemon("drednaw", "d<pre>red</pre>naw"),
            sampleHighlightPokemon("minior-red-meteor", "minior-<pre>red</pre>-meteor"),
            sampleHighlightPokemon("giratina-altered", "giratina-alte<pre>red</pre>"),
        )

        every { cacheConfiguration.getPokemonHighlightedFromCache("red", ALPHABETICAL) } returns pokemonList

        val pokemonHighlight = pokemonService.getPokemonsByNameHighlighted(query, sort)

        assertNotNull(pokemonHighlight)
        assertEquals(expectedSize, pokemonHighlight.size)
        assertTrue(pokemonHighlight.all { it.name.contains(query) })
        assertTrue(pokemonHighlight.all { it.highlight!!.contains(initialHighlight) })
        assertTrue(pokemonHighlight.all { it.highlight!!.contains(finalHighlight) })

        verify(exactly = 0) {
            restConfiguration.get()
            cacheConfiguration.savePokemonToCache(any(), any(), any())
        }
    }

    @Test
    fun `should throws exception when an error occurs in the PokeAPI when searching for pokemons`() {

        val query = "red"
        val sort = "Alphabetical"

        every { cacheConfiguration.getPokemonFromCache(query, ALPHABETICAL) } returns null
        every { restConfiguration.get() } throws Exception()

        assertThrows<Exception> {
            pokemonService.getPokemonsByName(query, sort)
        }
    }

    @Test
    fun `should throws exception when an error occurs in the PokeAPI when searching for pokemons with highlight`() {

        val query = "red"
        val sort = "Alphabetical"

        every { cacheConfiguration.getPokemonHighlightedFromCache(query, ALPHABETICAL) } returns null
        every { restConfiguration.get() } throws Exception()

        assertThrows<Exception> {
            pokemonService.getPokemonsByNameHighlighted(query, sort)
        }
    }
}
