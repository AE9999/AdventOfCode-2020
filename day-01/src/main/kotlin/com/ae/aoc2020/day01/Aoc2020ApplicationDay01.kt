package com.ae.aoc2020.day01

import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.io.BufferedReader
import java.io.InputStreamReader


@SpringBootApplication
class Aoc2020ApplicationDay01 : CommandLineRunner {

	private val logger = LoggerFactory.getLogger(javaClass)

	private fun solve1(totalFile: String) {
		val bufferedReader = BufferedReader(InputStreamReader(Aoc2020ApplicationDay01::class.java.getResourceAsStream("/$totalFile")))

		bufferedReader.useLines {
			val numbers = it.map { it.toInt() }.toList()
			val pairs = sequence {
				for (i in 0 until numbers.count()) {
					for (j in (i + 1) until numbers.count()) {
						yield(Pair(numbers[i], numbers[j]))
					}
				}
			}.toList()
			pairs.filter { pair -> pair.first +  pair.second ==  2020}
					 .forEach {pair ->
				logger.info("$pair sums to 2020 and multiply to ${pair.first * pair.second} ...")
			}
		}
	}

	private fun solve2(totalFile: String) {
		val bufferedReader = BufferedReader(InputStreamReader(Aoc2020ApplicationDay01::class.java.getResourceAsStream("/$totalFile")))

		bufferedReader.useLines {
			val numbers = it.map { it.toInt() }.toList()
			val pairs = sequence {
				for (i in 0 until numbers.count()) {
					for (j in (i + 1) until numbers.count()) {
						for (k in (j + 1) until numbers.count() ) {
							yield(Triple(numbers[i], numbers[j], numbers[k]))
						}
					}
				}
			}.toList()
			pairs.filter { triple -> triple.first +  triple.second +  triple.third ==  2020}
							.forEach {triple ->
								val multiple = triple.first * triple.second * triple.third
								logger.info("$triple sums to 2020 and multiply to $multiple ...")
							}
		}
	}


	override fun run(vararg args: String?) {
		solve1(args[0]!!)
		solve2(args[0]!!)
	}
}

fun main(args: Array<String>) {
	runApplication<Aoc2020ApplicationDay01>(*args)
}
