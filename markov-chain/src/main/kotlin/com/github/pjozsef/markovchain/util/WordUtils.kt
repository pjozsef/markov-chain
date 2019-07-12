package com.github.pjozsef.markovchain.util

object WordUtils {
    fun combineWords(starts: List<String>, ends: List<String>): List<String> =
            starts.flatMap { start ->
                ends.flatMap { end ->
                    combineWords(start, end)
                }
            }.distinct()

    fun combineWords(start: String, end: String): List<String> =
        start.mapIndexed { i, charA ->
            end.mapIndexedNotNull { j, charB ->
                if (charA == charB) i to j else null
            }
        }.flatten().map { (firstEnd, secondStart) ->
            start.substring(0, firstEnd) + end.substring(secondStart)
        }.distinct()
}