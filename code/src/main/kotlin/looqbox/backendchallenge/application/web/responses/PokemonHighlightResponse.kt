package looqbox.backendchallenge.application.web.responses

import looqbox.backendchallenge.domain.models.PokemonHighlight

data class PokemonHighlightResponse(
    val results: List<PokemonHighlight>
)
