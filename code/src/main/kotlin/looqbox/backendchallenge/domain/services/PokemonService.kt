package looqbox.backendchallenge.domain.services

import looqbox.backendchallenge.common.cache.CacheConfiguration
import looqbox.backendchallenge.common.logs.LogMessages
import looqbox.backendchallenge.domain.enums.SortTypeEnum
import looqbox.backendchallenge.domain.enums.SortTypeEnum.LENGTH
import looqbox.backendchallenge.domain.enums.SortTypeEnum.ALPHABETICAL
import looqbox.backendchallenge.domain.models.Pokemon
import looqbox.backendchallenge.domain.models.PokemonHighlight
import looqbox.backendchallenge.resources.external.api.RestConfiguration
import org.springframework.stereotype.Service

@Service
class PokemonService(
    private val restConfiguration: RestConfiguration,
    private val cacheConfiguration: CacheConfiguration
) {

    fun getPokemonsByName(
        query: String?,
        sortType: String?
    ): List<Pokemon> {
        try {

            val sort = getSortType(sortType)
            validateQuery(query, sort)?.let { return it }
            val allPokemons = getPokemonByCache(query!!, sort)?.let { return it } ?: getPokemonsByApi()
            val filteredPokemons = filterPokemons(allPokemons.toMutableList(), query)

            return sortPokemons(filteredPokemons, sort).also {
                cacheConfiguration.savePokemonToCache(query, sort, it)
                println(LogMessages.FOUND_POKEMONS_BY_NAME_FROM_POKEAPI.format(query, it.size))
            }

        } catch (ex: Exception) {
            throw ex.also {
                println("An error has occurred. [message: ${it.message}]")
            }
        }
    }

    fun getPokemonsByNameHighlighted(
        query: String?,
        sortType: String?
    ): List<PokemonHighlight> {
        try {

            val sort = getSortType(sortType)
            validateQueryHighlight(query, sort)?.let { return it }
            val allPokemons = getPokemonHighlightedByCache(query!!, sort)?.let { return it } ?: getPokemonsByApi()
            val filteredPokemons = filterPokemons(allPokemons.toMutableList(), query)

            return sortPokemons(filteredPokemons, sort).map {
                PokemonHighlight(it.name, addHighlightOnPokemon(it.name, query))
            }.also {
                cacheConfiguration.savePokemonHighlightedCache(query, sort, it)
                println(LogMessages.FOUND_POKEMONS_BY_WITH_HIGHLIGHT_NAME_FROM_POKEAPI.format(query, it.size))
            }

        } catch (ex: Exception) {
            throw ex.also {
                println("An error has occurred. [message: ${it.message}]")
            }
        }
    }

    private fun getSortType(sort: String?): SortTypeEnum {
        if (sort != null) {
            return when (sort.lowercase()) {
                "length" -> LENGTH
                else -> ALPHABETICAL
            }
        }
        return ALPHABETICAL
    }

    private fun validateQuery(query: String?, sort: SortTypeEnum): List<Pokemon>? {
        if (query.isNullOrEmpty()) {
            val allPokemons = getPokemonsByApi().toMutableList()
            return sortPokemons(allPokemons, sort)
        }

        return null
    }

    private fun validateQueryHighlight(query: String?, sort: SortTypeEnum): List<PokemonHighlight>? {
        if (query.isNullOrEmpty()) {
            val allPokemons = getPokemonsByApi().toMutableList()
            val sortedPokemons = sortPokemons(allPokemons, sort)

            return sortedPokemons.map {
                PokemonHighlight(it.name, "")
            }
        }

        return null
    }

    private fun getPokemonByCache(query: String, sort: SortTypeEnum): List<Pokemon>? {
        return cacheConfiguration.getPokemonFromCache(query, sort)?.let {
            println(LogMessages.FOUND_POKEMONS_BY_NAME_FROM_CACHE.format(query, it.size))
            it
        }
    }

    private fun getPokemonHighlightedByCache(query: String, sort: SortTypeEnum): List<PokemonHighlight>? {
        return cacheConfiguration.getPokemonHighlightedFromCache(query, sort)?.let {
            println(LogMessages.FOUND_POKEMONS_BY_WITH_HIGHLIGHT_NAME_FROM_CACHE.format(query, it.size))
            it
        }
    }

    private fun getPokemonsByApi(): List<Pokemon> = restConfiguration.get()

    private fun sortPokemons(
        pokemonsList: MutableList<Pokemon>,
        sort: SortTypeEnum
    ): List<Pokemon> {
        return when (sort) {
            LENGTH -> sortByLength(pokemonsList)
            ALPHABETICAL -> sortByAlphabetic(pokemonsList)
        }
    }

    private fun filterPokemons(
        pokemonsList: MutableList<Pokemon>,
        query: String
    ): MutableList<Pokemon> {

        val filteredPokemonsList = mutableListOf<Pokemon>()
        val queryLength = query.length
        val queryInLowerCase = query.lowercase()

        pokemonsList.map {
            for (i in 0 until it.name.length - queryLength + 1) {
                val substring = it.name.lowercase().substring(i, i + queryLength)
                if (substring == queryInLowerCase) {
                    filteredPokemonsList.add(it)
                }
            }
        }

        return filteredPokemonsList
        /*
    EXPLICACAO:

    ### Código fonte:
        val filteredPokemonsList = mutableListOf<Pokemon>(): Cria uma lista mutável vazia para salvar os Pokémons filtrados.
        val queryLength = query.length: Calcula o comprimento da string "query" e armazena-o em "queryLength". Isso é usado para determinar quantos caracteres serão comparados em cada iteração.
        val queryInLowerCase = query.lowercase(): Converte a "query" em letras minúsculas.
        pokemonsList.map {: Inicia uma operação de mapeamento na lista "pokemonsList". Isso permite percorrer cada elemento da lista e aplicar uma transformação.
        for (i in 0 until it.name.length - queryLength + 1) {: Inicia um loop for que itera sobre todas as possíveis substrings do nome do Pokémon (it.name) com base no comprimento da consulta. O loop percorre todas as posições iniciais possíveis para uma substring.
        val substring = it.name.lowercase().substring(i, i + queryLength): Obtém uma substring do nome do Pokémon (it.name) começando na posição "i" e indo até "i" + "queryLength".
        if (substring == queryInLowerCase) {: Verifica se a substring atual é igual à consulta. Se for igual, significa que encontramos uma correspondência.
        filteredPokemonsList.add(it): Adiciona o Pokémon atual à lista "filteredPokemonsList", pois ele corresponde à consulta.
        return filteredPokemonsList: Retorna a lista "filteredPokemonsList" com os pokemons filtrados
    ### Conclusão:
        O algoritmo percorre a lista de Pokémon e verifica se o nome de cada Pokémon contém a consulta solicitada.
        Os Pokémons que correspondem à consulta (query) são adicionados e retornados a uma nova lista
        A fim de melhoria eu utilizaria o .contains() do próprio Kotlin/Java.
        */
    }

    private fun sortByLength(pokemonList: MutableList<Pokemon>): List<Pokemon> {
        var switch: Boolean

        do {
            switch = false
            for (i in 0 until pokemonList.size - 1) {
                if (pokemonList[i].name.length > pokemonList[i + 1].name.length) {
                    val tempPokemon = pokemonList[i]
                    pokemonList[i] = pokemonList[i + 1]
                    pokemonList[i + 1] = tempPokemon
                    switch = true
                }
            }
        } while (switch)

        return pokemonList
        /*
    EXPLICACAO:

    ### Método:
        BubbleSort (Ordenação por bolha). Possui uma complexidade de O(n²)
        Este algoritmo continua fazendo passagens na lista, comparando pares de elementos adjacentes e trocando-os se estiverem
        fora de ordem, até que nenhuma troca seja necessária em uma iteração completa, o que indica que a lista está 100% ordenada.
    ### Código fonte:
        var switch: Essa variável será usada para controlar o loop de ordenação.
        do {: Início de um loop "do-while". Isso significa que o bloco de código dentro do loop será executado pelo menos uma vez e repetido enquanto a condição após o while for verdadeira.
        switch = false: Define a variável "switch" como false no início de cada iteração do loop. Isso significa que, a princípio, assumimos que a lista está ordenada e não precisamos fazer mais iterações.
        for (i in 0 until pokemonList.size - 1) {: Início de um loop "for" que percorre os índices da lista "pokemonList" de 0 até ((tamanho da lista) - 1). O índice "i" representa a posição atual na lista.
        if (pokemonList[i].name.length > pokemonList[i + 1].name.length) {: Verifica se o comprimento do nome do Pokémon na posição "i" eh maior do que o comprimento do nome do Pokémon na próxima posição ("i" + 1). Se isso for verdadeiro, significa que os dois Pokémon estão fora de ordem em relação ao comprimento de seus nomes.
        val tempPokemon = pokemonList[i]: Cria uma variável temporária "tempPokemon" para armazenar o Pokémon na posição "i".
        pokemonList[i] = pokemonList[i + 1]: Substitui o Pokémon na posição "i" pelo Pokémon na próxima posição, efetivamente trocando suas posições na lista.
        pokemonList[i + 1] = tempPokemon: Substitui o Pokémon na próxima posição pelo Pokémon armazenado na variável "tempPokemon", concluindo a troca de posições.
        switch = true: Define a variável "switch" como true para indicar que pelo menos uma troca foi feita nesta iteração do loop.
        } while (switch): Fecha o loop "for" e continua repetindo o loop "do-while" enquanto "switch" for verdadeiro, o que significa que pelo menos uma troca foi feita na iteração anterior.
        return pokemonList: Retorna a lista salva na variável "pokemonList" após a ordenação.
    ### Conclusão e possível melhoria.:
        O método BubbleSort eh muito utilizado para ordenação de listas por conta da sua fácil implementação e entendimento,
        porém, ele não eh indicado para listas muito grandes por conta da sua alta volumetria tanto de COMPARAÇÃO quanto
        de TROCAS entre os elementos da lista.
        A fins de melhoria, eu trocaria o algoritmo bubble sort para o algorítimo quick sort, porque eh mais rápido do que o bubble.
        */
    }

    private fun sortByAlphabetic(pokemonList: MutableList<Pokemon>): List<Pokemon> {
        val maxIndex = pokemonList.size

        for (i in 0 until maxIndex - 1) {
            var minIndex = i
            for (j in i + 1 until maxIndex) {
                if (pokemonList[j].name < pokemonList[minIndex].name) {
                    minIndex = j
                }
            }
            if (minIndex != i) {
                val tempPokemon = pokemonList[i]
                pokemonList[i] = pokemonList[minIndex]
                pokemonList[minIndex] = tempPokemon
            }
        }

        return pokemonList
        /*
    EXPLICACAO:

    ### Método:
        SelectionSort (Ordenação por seleção). Possui uma complexidade de O(n²)
        Consiste basicamente em passar sempre a menor opção para a primeira posição da lista.
    ### Código fonte:
        val maxIndex = pokemonList.size: Declara uma variável "maxIndex" para armazenar o tamanho da lista pokemonList.
        for (i in 0 until maxIndex - 1) {: Inicia um loop do tipo "for" que itera sobre os índices de 0 até (maxIndex - 1). O índice "i" representa a posição atual na lista.
        var minIndex = i: Inicializa uma variável "minIndex" com o valor atual de "i". Ela eh usada para rastrear o índice do Pokémon com o nome mais baixo encontrado durante a iteração.
        for (j in i + 1 until maxIndex) {: Inicia um segundo loop do tipo "for" que começa a partir da próxima posição após o "i" e vai até o final da lista (maxIndex). O índice "j" representa a posição atual de um Pokémon que está sendo comparado com o Pokémon na posição "i".
        if (pokemonList[j].name < pokemonList[minIndex].name) {: Compara os nomes dos Pokémons nas posições "j" e "minIndex". Se o nome do Pokémon em "j" for menor (na ordem alfabética) do que o nome do Pokémon em "minIndex", atualiza "minIndex" para "j".
        if (minIndex != i) {: Após o término do loop interno, verifica se "minIndex" eh diferente de "i". Se for, significa que um Pokémon com um nome mais baixo foi encontrado, então eh hora de trocar as posições dos Pokémons.
        val tempPokemon = pokemonList[i]: Cria uma variável temporária "tempPokemon" para armazenar o Pokémon na posição "i".
        pokemonList[i] = pokemonList[minIndex]: Substitui o Pokémon na posição "i" pelo Pokémon na posição "minIndex", iniciando a troca de posições.
        pokemonList[minIndex] = tempPokemon: Substitui o Pokémon na posição "minIndex" pelo Pokémon armazenado na variável "tempPokemon", concluindo a troca de posições.
        }: (linha 181) Fecha o bloco do if que verifica se a troca de posições foi necessária.
        }: (linha 184) Fecha o loop interno que itera sobre os índices "j" para encontrar o Pokémon com o nome mais baixo.
        }: (linha 190) Fecha o loop externo que itera sobre os índices "i" para selecionar os Pokémon em ordem alfabética.
        return pokemonList: Retorna a lista pokemonList após a ordenação.
    ### Conclusão e possível melhoria:
        "O algorítmo eh simples, porém, ineficiente ao ordenar grandes listas pois tem uma complexidade de tempo de O(n^2) no pior caso.
        Uma melhoria pode ser alcançada usando algoritmos de ordenação mais eficientes, como o Merge Sort, Quick Sort.
        */
    }

    private fun addHighlightOnPokemon(
        fullName: String,
        query: String
    ): String {
        if (query.isEmpty()) return fullName

        var currentIndex = 0
        val highlight = StringBuilder()
        val lowerQuery = query.lowercase()
        val lowerFullName = fullName.lowercase()

        while (currentIndex < fullName.length) {
            val startIndex = lowerFullName.indexOf(lowerQuery, currentIndex)
            if (startIndex == -1) {
                highlight.append(fullName.substring(currentIndex))
                break
            }
            highlight.append(fullName.substring(currentIndex, startIndex))
            highlight.append("<pre>")
            val endIndex = startIndex + query.length
            highlight.append(fullName.substring(startIndex, endIndex))
            highlight.append("</pre>")
            currentIndex = endIndex
        }

        return highlight.toString()

        /*
    EXPLICACAO:

    ### Código fonte:
        if (query.isEmpty()) return fullName: Se "query" estiver em branco, retorna o nome sem destaque.
        var currentIndex = 0: Essa variável será usada para controlar a posição atual na string "fullName".
        val highlight = StringBuilder(): Variável "highlight" que eh uma instância de StringBuilder, utilizada para construir a string resultante e o destaque.
        val lowerQuery = query.lowercase(): Transformo a variável "query" para uma versão em letras minúsculas.
        val lowerFullName = fullName.lowercase(): Transformo a variável "fullName" para uma versão em letras minúsculas.
        while (currentIndex < fullName.length) {: Início de um loop while que percorrerá a string "fullName" enquanto a "currentIndex" for menor do que o comprimento total da string.
        val startIndex = lowerFullName.indexOf(lowerQuery, currentIndex): Encontra a primeira ocorrência da "lowerQuery" na "lowerFullName", começando a busca a partir da posição "currentIndex". O índice da primeira ocorrência eh armazenado em "startIndex".
        if (startIndex == -1) {: Verifica se "startIndex" eh igual a -1, o que significa que não foi encontrada nenhuma ocorrência da "lowerQuery" na "lowerFullName".
        highlight.append(fullName.substring(currentIndex)): Se "startIndex" for -1, isso significa que não há mais ocorrências da lowerQuery, então o restante da fullName eh simplesmente adicionado ao highlight.
        break: Encerra o loop while, pois não há mais ocorrências da "lowerQuery" para destacar.
        highlight.append(fullName.substring(currentIndex, startIndex)): Se "startIndex" for diferente de -1, isso significa há ocorrência da "lowerQuery". Portanto, o trecho da fullName entre "currentIndex" e "startIndex" eh adicionado ao "highlight".
        highlight.append("<pre>"): Adiciona a tag <pre> ao "highlight" para iniciar o destaque.
        val endIndex = startIndex + query.length: Calcula o índice do final da ocorrência da "query", somando o "startIndex" com o tamanho da "query". Isso determina onde o destaque acaba.
        highlight.append(fullName.substring(startIndex, endIndex)): Adiciona a parte da "fullName" que corresponde à ocorrência da "query" ao "highlight".
        highlight.append("</pre>"): Adiciona a tag de fechamento </pre> ao "highlight".
        currentIndex = endIndex: Atualiza o "currentIndex" para apontar para o final da última ocorrência da "query".
        return highlight.toString(): Converte o "highlight" de StringBuilder para String e a retorna.
    ### Conclusão:
        Este algoritmo destaca as ocorrências de uma consulta em uma string, tornando as partes procuradas da string em evidência.
        */
    }
}
