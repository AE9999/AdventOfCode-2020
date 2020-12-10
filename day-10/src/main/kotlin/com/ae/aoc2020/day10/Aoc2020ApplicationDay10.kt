package com.ae.aoc2020.day08

import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.math.abs


@SpringBootApplication
class Aoc2020ApplicationDay10 : CommandLineRunner {

	private val logger = LoggerFactory.getLogger(javaClass)

	data class Adapter(val outputJoltage: Int) {
		fun inputRange() = (outputJoltage-3..outputJoltage)

	}

	fun findMatchedAdapters(availableAdapters: Set<Adapter>,
							linkedAdapters: List<Adapter>) : List<Adapter>? {
		if (availableAdapters.isEmpty()) {
			return linkedAdapters
		}
		val currentOutputJoltage = linkedAdapters.last()!!.outputJoltage
		val unusableAdapters = availableAdapters.filter { adapter -> currentOutputJoltage > adapter.outputJoltage }
		if (unusableAdapters.isNotEmpty()) return null

		val suitableAdapters = availableAdapters.filter { adapter -> currentOutputJoltage in adapter.inputRange() }
		if (suitableAdapters.isEmpty()) return null

		suitableAdapters.forEach {  adapter ->
			val availableAdapters_ = availableAdapters.minus(adapter)
			val linkedAdapters = linkedAdapters.toList() + adapter
			val matchedAdaptors = findMatchedAdapters(availableAdapters_, linkedAdapters)
			if (matchedAdaptors != null) {
				return matchedAdaptors
			}
		}
		logger.info("Refuted $currentOutputJoltage -> linked:${linkedAdapters}, available:${availableAdapters} ..")
		return null
	}

	fun findRearrangements(target: Int, adapters: Set<Adapter>, chainedAdapters: List<Adapter>) : Int {
		return adapters.filter { it.outputJoltage < target && it.outputJoltage + 3 >= target }.map { adapter ->
			if (adapter.outputJoltage == 0)
				1
			else findRearrangements(adapter.outputJoltage, adapters.minus(adapter),
					   chainedAdapters + adapter)
		}.sum()
	}

	private fun solve1(totalFile: String) {
		val bufferedReader = BufferedReader(InputStreamReader(Aoc2020ApplicationDay10::class.java.getResourceAsStream("/$totalFile")))
		bufferedReader.useLines {
			val adapters = it.map { line -> Adapter(line.toInt()) }.toList()
            val matchedAdapters = findMatchedAdapters(adapters.toSet(), listOf(Adapter(0)))!!
			var oneDifferences = 0;
			var threeDifferences = 1; // already count latest
			for (i in (matchedAdapters.indices.drop(1))) {
				val difference = abs(matchedAdapters[i - 1].outputJoltage - matchedAdapters[i].outputJoltage)
				if (difference == 1) oneDifferences += 1
				if (difference == 3) threeDifferences += 1
			}
			logger.info("${oneDifferences * threeDifferences} is the number of 1-jolt differences (${oneDifferences}) multiplied by the number of 3-jolt differences (${threeDifferences})")
		}
	}

	private fun solve2(totalFile: String) {
		val bufferedReader = BufferedReader(InputStreamReader(Aoc2020ApplicationDay10::class.java.getResourceAsStream("/$totalFile")))
		bufferedReader.useLines {
			val adapters = it.map { line -> Adapter(line.toInt()) }.toList()
			val begin =  Adapter(0)
			val end = Adapter(adapters.map { it.outputJoltage }.max()!! + 3)
			val matchedAdapters = (findMatchedAdapters(adapters.toSet(), listOf(begin))!!
					               + end)
			val joltage2Reachable = matchedAdapters.map { adapter ->
				                  val reachable = matchedAdapters.filter { otherAdapter -> otherAdapter.outputJoltage < adapter.outputJoltage
										                                   && otherAdapter.outputJoltage + 3 >= adapter.outputJoltage }.toList()
				                  Pair(adapter, reachable)
			}.toMap()

			val adapter2score = HashMap<Adapter, Long>()
			matchedAdapters.withIndex().forEach {
				if(it.index == 0) {
					adapter2score[it.value] = 1L
				} else {
					adapter2score[it.value] = joltage2Reachable[it.value]!!.map { origAdapter ->
						adapter2score[origAdapter]!!
					}.sum()
				}
			}
			logger.info("Final result ${adapter2score[end]!!}")
		}
	}

	override fun run(vararg args: String?) {
		solve1(args[0]!!)
		solve2(args[0]!!)
	}
}

fun main(args: Array<String>) {
	runApplication<Aoc2020ApplicationDay10>(*args)
}
