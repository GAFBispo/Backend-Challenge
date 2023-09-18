package looqbox.backendchallenge.application.web.requests

data class PokemonRequest(
    val query: String?,
    val sort: String?
)
