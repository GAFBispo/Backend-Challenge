package looqbox.backendchallenge.resources.external.api

import looqbox.backendchallenge.domain.models.Pokemon
import looqbox.backendchallenge.domain.models.PokemonListResponse
import org.springframework.web.client.RestTemplate

class RestConfiguration {

    private val rest = RestTemplate()
    private val maxQuantityOfPokemons = 5000
    private val baseUrl: String = "https://pokeapi.co/api/v2"

    fun get(): List<Pokemon> {
        return rest.getForObject(
            "$baseUrl/pokemon?limit=$maxQuantityOfPokemons",
            PokemonListResponse::class.java
        )!!.results
    }

}