package looqbox.backendchallenge.common.cache

import io.mockk.*
import looqbox.backendchallenge.domain.enums.SortTypeEnum
import looqbox.backendchallenge.domain.models.Pokemon
import looqbox.backendchallenge.domain.models.PokemonHighlight
import looqbox.backendchallenge.utils.mocks.PokemonHighlightMock.sampleHighlightPokemon
import looqbox.backendchallenge.utils.mocks.PokemonMock.samplePokemonList
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach

class CacheConfigurationTest {

    private lateinit var cachePokemon: HashMap<Pair<String, SortTypeEnum>, List<Pokemon>>
    private lateinit var cachePokemonHighlight: HashMap<Pair<String, SortTypeEnum>, List<PokemonHighlight>>
    private lateinit var cacheConfiguration: CacheConfiguration

    @BeforeEach
    fun setUp() {
        cachePokemon = mockk(relaxed = true)
        cachePokemonHighlight = mockk(relaxed = true)
        cacheConfiguration = CacheConfiguration()
    }

    @Test
    fun `should return a list of pokemons from the cache successfully`() {

        val keyMap1 = "pidge"
        val keyMap2 = SortTypeEnum.LENGTH
        val keyValue = samplePokemonList(listOf("pidgetto", "pidgeot", "pidge"))
        val expectedSize = keyValue.size

        cacheConfiguration.savePokemonToCache(keyMap1, keyMap2, keyValue)
        val result = cacheConfiguration.getPokemonFromCache(keyMap1, keyMap2)!!

        assertNotNull(result)
        assertEquals(keyValue, result)
        assertEquals(result.size, expectedSize)
        assertTrue(result.all { it.name.contains(keyMap1) })
    }

    @Test
    fun `should return a list of pokemons successfully highlighted from the cache`() {

        val keyMap1 = "drag"
        val keyMap2 = SortTypeEnum.LENGTH
        val initialHighlight = "<pre>"
        val finalHighlight = "</pre>"
        val keyValue = listOf(
            sampleHighlightPokemon("dragalge", "<pre>drag</pre>alge"),
            sampleHighlightPokemon("dragapult", "<pre>drag</pre>apult"),
            sampleHighlightPokemon("dragonair", "<pre>drag</pre>onair"),
            sampleHighlightPokemon("dragonite", "<pre>drag</pre>onite"),
            sampleHighlightPokemon("regidrago", "regi<pre>drag</pre>o")
        )
        val expectedSize = keyValue.size

        cacheConfiguration.savePokemonHighlightedCache(keyMap1, keyMap2, keyValue)
        val result = cacheConfiguration.getPokemonHighlightedFromCache(keyMap1, keyMap2)!!

        assertNotNull(result)
        assertEquals(keyValue, result)
        assertEquals(result.size, expectedSize)
        assertTrue(result.all { it.name.contains(keyMap1) })
        assertTrue(result.all { it.highlight!!.contains(initialHighlight) })
        assertTrue(result.all { it.highlight!!.contains(finalHighlight) })
    }

    @Test
    fun `should clear all cache data when the clear method is called`() {

        val keyMap1 = "drag"
        val keyMap2 = SortTypeEnum.LENGTH
        val keyValuePokemon = samplePokemonList(listOf("pidgetto", "pidgeot", "pidge"))
        val keyValuePokemonHighlight = listOf(
            sampleHighlightPokemon("dragalge", "<pre>drag</pre>alge"),
            sampleHighlightPokemon("dragapult", "<pre>drag</pre>apult")
        )
        val expectedSizePokemon = keyValuePokemon.size
        val expectedSizePokemonHighlight = keyValuePokemonHighlight.size

        cacheConfiguration.savePokemonToCache(keyMap1, keyMap2, keyValuePokemon)
        cacheConfiguration.savePokemonHighlightedCache(keyMap1, keyMap2, keyValuePokemonHighlight)
        val resultPokemon = cacheConfiguration.getPokemonFromCache(keyMap1, keyMap2)!!
        val resultPokemonHighlight = cacheConfiguration.getPokemonHighlightedFromCache(keyMap1, keyMap2)!!

        assertNotNull(resultPokemon)
        assertNotNull(resultPokemonHighlight)
        assertEquals(expectedSizePokemon, resultPokemon.size)
        assertEquals(expectedSizePokemonHighlight, resultPokemonHighlight.size)

        cacheConfiguration.clearAll()

        val resultBeforeDeletePokemon = cacheConfiguration.getPokemonFromCache(keyMap1, keyMap2)
        val resultBeforeDeletePokemonHighlight = cacheConfiguration.getPokemonHighlightedFromCache(keyMap1, keyMap2)

        assertTrue(resultBeforeDeletePokemon.isNullOrEmpty())
        assertTrue(resultBeforeDeletePokemonHighlight.isNullOrEmpty())
    }
}
