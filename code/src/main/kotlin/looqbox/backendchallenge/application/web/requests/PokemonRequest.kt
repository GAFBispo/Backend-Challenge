package looqbox.backendchallenge.application.web.requests

import looqbox.backendchallenge.domain.enums.SortTypeEnum

data class PokemonRequest(
    val query: String?,
    val sort: SortTypeEnum?
)
