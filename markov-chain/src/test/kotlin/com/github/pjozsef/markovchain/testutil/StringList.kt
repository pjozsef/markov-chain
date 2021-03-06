package com.github.pjozsef.markovchain.testutil

val String.l: List<String>
    get() = this.toCharArray().map(Char::toString)

val String.c: List<Char>
    get() = this.toCharArray().toList()

val Map<String, List<String>>.l: Map<List<String>, List<List<String>>>
    get() = this.mapKeys {
        it.key.l
    }.mapValues {
        it.value.map { it.l }
    }
