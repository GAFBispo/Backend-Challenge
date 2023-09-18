package looqbox.backendchallenge.utils.mocks

import looqbox.backendchallenge.application.web.requests.PokemonRequest
import looqbox.backendchallenge.domain.enums.SortTypeEnum
import looqbox.backendchallenge.domain.models.Pokemon

object PokemonMock {

    fun samplePokemonRequest(query: String, sort: SortTypeEnum) = PokemonRequest(
        query = query,
        sort = sort
    )

    fun samplePokemonList(names: List<String>): List<Pokemon> = names.map {
        samplePokemon(name = it)
    }

    private fun samplePokemon(name: String) = Pokemon(
        name = name
    )
}
