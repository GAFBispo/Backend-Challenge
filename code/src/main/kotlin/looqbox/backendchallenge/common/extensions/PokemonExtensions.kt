package looqbox.backendchallenge.common.extensions

import looqbox.backendchallenge.application.web.responses.PokemonHighlightResponse
import looqbox.backendchallenge.application.web.responses.PokemonResponse
import looqbox.backendchallenge.domain.models.Pokemon
import looqbox.backendchallenge.domain.models.PokemonHighlight

fun List<Pokemon>.toPokemonResponse() = PokemonResponse(
    results = this.map { it.name }
)

fun List<PokemonHighlight>.toPokemonHighlightResponse() = PokemonHighlightResponse(
    results = this.map { it }
)
