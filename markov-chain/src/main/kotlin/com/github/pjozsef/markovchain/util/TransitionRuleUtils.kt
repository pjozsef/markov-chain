package com.github.pjozsef.markovchain.util

import com.github.pjozsef.markovchain.Transition
import java.util.*

internal typealias RawTransition<T> = Pair<Map<List<T>, Map<List<T>, Number>>, Map<List<T>, Map<List<T>, Number>>>

object TransitionRule {
    fun <T> fromWords(words: List<List<T>>, order: Int = 1, delimiter: List<T>, commentFilter: (List<T>)->Boolean): RawTransition<T> {
        val filteredWords = words.filter(commentFilter)
        return filteredWords.fromWords(order, delimiter) to filteredWords.map { it.reversed() }.fromWords(order, delimiter)
    }
}

fun stringCommentFilter(comment: String = "#"): (List<String>) -> Boolean = {
    !it.first().startsWith(comment)
}

fun charCommentFilter(comment: Char = '#'): (List<Char>) -> Boolean = {
    it.first() != comment
}

private fun <T> List<List<T>>.fromWords(order: Int = 1, delimiter: List<T>): Map<List<T>, Map<List<T>, Number>> =
    this.map { it + delimiter }.let { wordsWithEnding ->
        (0..order).map {
            wordsWithEnding.generateRules(it)
        }.reduce { acc, curr ->
            acc + curr
        }
    }

private fun <T> List<List<T>>.generateRules(order: Int): Map<List<T>, Map<List<T>, Number>> = when (order) {
    0 -> this.generateRules { map { it.take(1) } }
    else -> this.generateRules { flatMap { it.windowed(order + 1) } }
}

private fun <T> List<List<T>>.generateRules(transform: List<List<T>>.() -> List<List<T>>) =
    this.transform()
        .map {
            it.dropLast(1) to it.takeLast(1)
        }.fold(mapOf<List<T>, List<List<T>>>()) { acc, curr ->
            acc merge curr
        }.mapValues { (_, value) ->
            value.groupingBy { it }.eachCount()
        }

private infix fun <K, V> Map<List<K>, List<List<V>>>.merge(pair: Pair<List<K>, List<V>>): Map<List<K>, List<List<V>>> {
    val (key, value) = pair
    return this + mapOf(key to (this.getOrDefault(key, listOf())) + listOf(value))
}

fun <T> RawTransition<T>.asDice(random: Random = Random()) = Transition(
    this.first.mapValues { (_, value) -> WeightedDice(value, random) },
    this.second.mapValues { (_, value) -> WeightedDice(value, random) }
)
