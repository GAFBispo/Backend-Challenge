package looqbox.backendchallenge.common.cache

import looqbox.backendchallenge.common.logs.LogMessages
import looqbox.backendchallenge.domain.enums.SortTypeEnum
import looqbox.backendchallenge.domain.models.Pokemon
import looqbox.backendchallenge.domain.models.PokemonHighlight

class CacheConfiguration {

    private val cachePokemon = HashMap<Pair<String, SortTypeEnum>, List<Pokemon>>()
    private val cachePokemonHighlight = HashMap<Pair<String, SortTypeEnum>, List<PokemonHighlight>>()

    fun savePokemonToCache(
        keyMap1: String,
        keyMap2: SortTypeEnum,
        keyValue: List<Pokemon>
    ) {
        Pair(keyMap1, keyMap2).run {
            cachePokemon[this] = keyValue.also {
                println(LogMessages.SAVING_POKEMONS_ON_CACHE.format(keyMap1, it.size))
            }
        }
    }

    fun savePokemonHighlightedCache(
        keyMap1: String,
        keyMap2: SortTypeEnum,
        keyValue: List<PokemonHighlight>
    ) {
        Pair(keyMap1, keyMap2).run {
            cachePokemonHighlight[this] = keyValue.also {
                println(LogMessages.SAVING_POKEMONS_WITH_HIGHLIGHT_ON_CACHE.format(keyMap1, it.size))
            }
        }
    }

    fun getPokemonFromCache(
        keyMap1: String,
        keyMap2: SortTypeEnum
    ): List<Pokemon>? {
        return Pair(keyMap1, keyMap2).run {
            cachePokemon[this]
        }
    }

    fun getPokemonHighlightedFromCache(
        keyMap1: String,
        keyMap2: SortTypeEnum
    ): List<PokemonHighlight>? {
        return Pair(keyMap1, keyMap2).run {
            cachePokemonHighlight[this]
        }
    }

    fun clearAll() {
        cachePokemon.clear()
        cachePokemonHighlight.clear()
    }
}
