package looqbox.backendchallenge.application.web.controllers

import looqbox.backendchallenge.application.web.requests.PokemonRequest
import looqbox.backendchallenge.application.web.responses.PokemonHighlightResponse
import looqbox.backendchallenge.application.web.responses.PokemonResponse
import looqbox.backendchallenge.common.extensions.toPokemonHighlightResponse
import looqbox.backendchallenge.common.extensions.toPokemonResponse
import looqbox.backendchallenge.common.logs.LogMessages
import looqbox.backendchallenge.domain.services.PokemonService
import org.springframework.http.HttpStatus.OK
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.status
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/pokemons")
class BackendChallengeController(
    private val pokemonService: PokemonService
) {

    @GetMapping
    fun getPokemons(
        @RequestBody pokemonRequest: PokemonRequest
    ): ResponseEntity<PokemonResponse> {

        val result = pokemonRequest.also {
            println(LogMessages.STARTING_SEARCHING_POKEMONS_BY_NAME.format(it))
        }.run {
            pokemonService.getPokemonsByName(this.query, this.sort).toPokemonResponse().also {
                println(LogMessages.FINISH_SEARCHING_POKEMONS_BY_NAME.format(it))
            }
        }

        return status(OK).body(result)
    }

    @GetMapping("/highlight")
    fun getPokemonsHighlight(
        @RequestBody pokemonRequest: PokemonRequest
    ): ResponseEntity<PokemonHighlightResponse> {

        val result = pokemonRequest.also {
            println(LogMessages.STARTING_SEARCHING_POKEMONS_WITH_HIGHLIGHT_BY_NAME.format(it))
        }.run {
            pokemonService.getPokemonsByNameHighlighted(this.query, this.sort).toPokemonHighlightResponse().also {
                println(LogMessages.FINISH_SEARCHING_POKEMONS_WITH_HIGHLIGHT_BY_NAME.format(it))
            }
        }

        return status(OK).body(result)
    }
}
