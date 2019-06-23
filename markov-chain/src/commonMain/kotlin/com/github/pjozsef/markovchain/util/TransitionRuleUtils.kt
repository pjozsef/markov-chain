package com.github.pjozsef.markovchain.util

object TransitionRule {
    fun fromWords(words: List<String>, order: Int = 1, delimiter: String = "#"): Map<String, Map<String, Number>> =
        words.map { it + delimiter }.let { wordsWithEnding ->
            (0..order).map {
                wordsWithEnding.generateRules(it)
            }.reduce { acc, curr ->
                acc + curr
            }
        }
}

private fun List<String>.generateRules(order: Int): Map<String, Map<String, Number>> = when (order) {
    0 -> this.generateRules { map { it.take(1) } }
    else -> this.generateRules { flatMap { it.windowed(order + 1) } }
}

private fun List<String>.generateRules(transform: List<String>.() -> List<String>) =
    this.transform()
        .map {
            it.dropLast(1) to it.takeLast(1)
        }.fold(mapOf<String, List<String>>()) { acc, curr ->
            acc merge curr
        }.mapValues { (_, value) ->
            value.groupingBy { it }.eachCount()
        }

private infix fun <K, V> Map<K, List<V>>.merge(pair: Pair<K, V>): Map<K, List<V>> {
    val (key, value) = pair
    return this + mapOf(key to ((this[key]?:listOf())) + listOf(value))
}

fun Map<String, Map<String, Number>>.asDice(): Map<String, WeightedDice<String>> =
    this.mapValues { (_, value) -> WeightedDice(value) }