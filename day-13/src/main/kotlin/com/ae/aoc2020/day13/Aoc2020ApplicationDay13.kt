package com.ae.aoc2020.day13

import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.RuntimeException
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

@SpringBootApplication
class Aoc2020ApplicationDay13 : CommandLineRunner {

	private val logger = LoggerFactory.getLogger(javaClass)


	private fun solve1(totalFile: String) {
		val bufferedReader = BufferedReader(InputStreamReader(Aoc2020ApplicationDay13::class.java.getResourceAsStream("/$totalFile")))
		bufferedReader.useLines {
			val input = it.toList()
			val timeStamp = input[0].toInt()
			val busIds = input[1].split(",").filter { it != "x" }.map { it.toInt() }
								 .map { busId ->
									 val lastArrival = ((timeStamp / busId) * busId)
									 val nextArrival =  lastArrival + busId
									 val waitingTime = if (lastArrival == timeStamp) 0 else nextArrival - timeStamp
									 Pair(busId, waitingTime)
								 }.sortedBy {
									it.second
								}

			val amount = busIds.map { it.second * it.first }.first()!!
			logger.info("${amount} is the ID of the earliest bus you can take to the airport multiplied by the number of minutes you'll need to wait for that bus?")
		}
	}

	private fun solve2(totalFile: String) {
		val bufferedReader = BufferedReader(InputStreamReader(Aoc2020ApplicationDay13::class.java.getResourceAsStream("/$totalFile")))
		bufferedReader.useLines {
		}
	}

	override fun run(vararg args: String?) {
		solve1(args[0]!!)
		solve2(args[0]!!)
	}
}

fun main(args: Array<String>) {
	runApplication<Aoc2020ApplicationDay13>(*args)
}
