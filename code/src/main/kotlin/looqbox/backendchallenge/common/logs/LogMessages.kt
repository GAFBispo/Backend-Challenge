package looqbox.backendchallenge.common.logs

object LogMessages {

    // Controller
    const val STARTING_SEARCHING_POKEMONS_BY_NAME = "\n[CONTROLLER] - Starting searching pokemons by name. [payload: %s]"
    const val FINISH_SEARCHING_POKEMONS_BY_NAME = "[CONTROLLER] - Finish searching pokemons by name with success. [result: %s]"
    const val STARTING_SEARCHING_POKEMONS_WITH_HIGHLIGHT_BY_NAME = "\n[CONTROLLER] - Starting searching pokemons with highlight by name. [payload: %s]"
    const val FINISH_SEARCHING_POKEMONS_WITH_HIGHLIGHT_BY_NAME = "[CONTROLLER] - Finish searching pokemons with highlight by name with success. [result: %s]"

    // Service
    const val FOUND_POKEMONS_BY_NAME_FROM_CACHE = "[SERVICE] - Pokemons were found quickly by Cache. [keyMap: \"%s\", quantity: %s]"
    const val FOUND_POKEMONS_BY_NAME_FROM_POKEAPI = "[SERVICE] - Pokemons were found by PokeAPI. [query: \"%s\", quantity: %s]"
    const val FOUND_POKEMONS_BY_WITH_HIGHLIGHT_NAME_FROM_CACHE = "[SERVICE] - Pokemons with highlight were found quickly by Cache. [keyMap: \"%s\", quantity: %s]"
    const val FOUND_POKEMONS_BY_WITH_HIGHLIGHT_NAME_FROM_POKEAPI = "[SERVICE] - Pokemons with highlight were found by PokeAPI. [query: \"%s\", quantity: %s]"

    // Cache
    const val SAVING_POKEMONS_ON_CACHE = "[CACHE] - Saving pokemons on cache. [keyMap: \"%s\", quantity: %s]"
    const val SAVING_POKEMONS_WITH_HIGHLIGHT_ON_CACHE = "[CACHE] - Saving pokemons with highlight on cache. [keyMap: \"%s\", quantity: %s]"
}