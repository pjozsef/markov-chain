package com.github.pjozsef.markovchain.util

import java.util.*

class WeightedCoin(val trueProbability: Double, val random: Random = Random()) {

    init {
        require(trueProbability in 0.0..1.0) { "TrueProbability must be between 0.0 and 1.0, but was: $trueProbability" }
    }

    fun flip(): Boolean {
        return random.nextDouble() <= trueProbability
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as WeightedCoin

        if (trueProbability != other.trueProbability) return false
        if (random != other.random) return false

        return true
    }

    override fun hashCode(): Int {
        var result = trueProbability.hashCode()
        result = 31 * result + random.hashCode()
        return result
    }
}

class WeightedDice<T>(val probabilities: Map<T, Number>, val random: Random = Random()) {
    constructor(
        values: List<T>,
        probabilities: List<Number>,
        random: Random = Random()
    ) : this(values.zip(probabilities), random)

    constructor(probabilities: List<Pair<T, Number>>, random: Random = Random()) : this(probabilities.toMap(), random)

    private val n = probabilities.size
    private val alias = IntArray(n)
    private val prob = DoubleArray(n)
    private val values: List<T>

    init {
        val probList = probabilities.toList().normalize()
        probList.map { it.second }.let {
            require(Math.abs(1.0 - it.sum()) <= 0.0001) { "Probabilities must add up to 1.0" }
            require(it.none { it < 0.0 }) { "Probabilities must not be negative" }
        }
        values = probList.unzip().first

        val small = mutableListOf<Int>()
        val large = mutableListOf<Int>()

        val scaledProbs = probList.zip(0 until n) { (_, percent), index ->
            index to percent
        }.map {
            it.first to it.second * n
        }.onEach { (i, value) ->
            if (value < 1.0) small.add(i) else large.add(i)
        }.map {
            it.second
        }.toTypedArray()

        while (small.isNotEmpty() && large.isNotEmpty()) {
            val l = small.removeAt(0)
            val g = large.removeAt(0)
            prob[l] = scaledProbs[l]
            alias[l] = g
            val pgtemp = scaledProbs[g] + scaledProbs[l] - 1
            scaledProbs[g] = pgtemp
            if (pgtemp < 1) small.add(g) else large.add(g)
        }

        while (large.isNotEmpty()) {
            val g = large.removeAt(0)
            prob[g] = 1.0
        }

        while (small.isNotEmpty()) {
            val l = small.removeAt(0)
            prob[l] = 1.0
        }

    }

    fun roll(): T = random.nextInt(n).let { i ->
        if (flipCoin(prob[i], random)) values[i] else values[alias[i]]
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as WeightedDice<*>

        if (probabilities != other.probabilities) return false
        if (random != other.random) return false

        return true
    }

    override fun hashCode(): Int {
        var result = probabilities.hashCode()
        result = 31 * result + random.hashCode()
        return result
    }
}

fun flipCoin(trueProbability: Double, random: Random = Random()): Boolean {
    return WeightedCoin(trueProbability, random).flip()
}

fun intWeightedDice(probabilities: List<Number>, random: Random = Random()) =
    WeightedDice(probabilities.indices.toList().zip(probabilities).toMap(), random)

private fun <T> List<Pair<T, Number>>.normalize(): List<Pair<T, Double>> {
    val listAsDoubles = this.map { (value, probability) -> value to probability.toDouble() }
    val sum = listAsDoubles.sumOf { it.second }
    return listAsDoubles.map { (value, probability) -> value to probability / sum }
}

fun <T> Collection<T>.randomElement(random: Random): T {
    require(this.isNotEmpty()) { "List must not be empty!" }
    return when (this) {
        is List -> this[random.nextInt(this.size)]
        else -> {
            val i = random.nextInt(this.size)
            val drop = kotlin.math.max(i - 1, 0)
            this.drop(drop).first()
        }
    }
}
