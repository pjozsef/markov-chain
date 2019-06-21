package com.github.pjozsef.markovchain.util

object TransitionRule {
    fun fromWords(words: List<String>, order: Int = 1, end: String = "#"): Map<String, Map<String, Number>> =
        words.map { it + end }.let { wordsWithEnding ->
            (1..order).map {
                wordsWithEnding.generateNgrams(it)
            }.reduce { acc, curr ->
                acc + curr
            }
        }
}

private fun List<String>.generateNgrams(order: Int): Map<String, Map<String, Number>> =
    this.flatMap {
        it.windowed(order + 1)
    }.map {
        it.dropLast(1) to it.takeLast(1)
    }.fold(mapOf<String, List<String>>()) { acc, curr ->
        acc merge curr
    }.mapValues { (_, value) ->
        value.groupingBy { it }.eachCount()
    }

private infix fun <K, V> Map<K, List<V>>.merge(pair: Pair<K, V>): Map<K, List<V>> {
    val (key, value) = pair
    return this + mapOf(key to (this.getOrDefault(key, listOf())) + listOf(value))
}

fun Map<String, Map<String, Number>>.asDice(): Map<String, WeightedDice<String>> =
    this.mapValues { (_, value) -> WeightedDice(value) }


fun main() {
    listOf(
        "foo",
        "bar",
        "baz",
        "qux",
        "quux",
        "quuz"
    ).joinToString().toCharArray().also(::println).toSet().let(::println)
}