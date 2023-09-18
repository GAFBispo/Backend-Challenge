package looqbox.backendchallenge.utils.mocks

import looqbox.backendchallenge.domain.models.PokemonHighlight

object PokemonHighlightMock {

    fun sampleHighlightPokemon(name: String, highlight: String) = PokemonHighlight(
        name = name,
        highlight = highlight
    )
}
