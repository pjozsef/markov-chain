package com.github.pjozsef.markovchain.util

object WordUtils {
    fun <T> combineWords(starts: Collection<List<T>>, ends: Collection<List<T>>): Set<List<T>> =
        starts.flatMap { start ->
            ends.flatMap { end ->
                combineSingleWords(start, end)
            }
        }.toSet()

    fun <T> combineSingleWords(start: List<T>, end: List<T>): Set<List<T>> =
        start.mapIndexed { i, elementA ->
            end.mapIndexedNotNull { j, elementB ->
                if (elementA == elementB) i to j else null
            }
        }.flatten().map { (firstEnd, secondStart) ->
            start.subList(0, firstEnd) + end.subList(secondStart, end.size)
        }.toSet()

    fun <T> commonPostfixPrefixLength(prefix: List<T>, postfix: List<T>): Int =
        (1..prefix.size).map {
            prefix.takeLast(it)
        }.findLast { end ->
            postfix.startsWith(end)
        }?.size ?: 0

    fun <T> List<T>.startsWith(prefix: List<T>) = this.take(prefix.size) == prefix

    fun <T> List<T>.endsWith(suffix: List<T>) = this.reversed().startsWith(suffix.reversed())

    fun <T> List<T>.containsList(content: List<T>) = if (content.isEmpty()) true else {
        this.windowed(content.size).any { it == content }
    }

    val String.list: List<Char>
        get() = this.toCharArray().toList()

    fun String?.toListOfChar() = this?.toCharArray()?.toList()

    fun Collection<String>?.toListOfChar() = this?.map { it.toCharArray().toList()  }
}
