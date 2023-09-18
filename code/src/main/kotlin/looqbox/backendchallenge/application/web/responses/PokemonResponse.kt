package looqbox.backendchallenge.application.web.responses

data class PokemonResponse(
    val results: List<String>
) {
    override fun toString() = results.toString()
}
