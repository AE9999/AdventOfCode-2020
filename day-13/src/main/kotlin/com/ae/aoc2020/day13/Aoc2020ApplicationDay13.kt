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
									 val waitingTime =  (lastArrival + busId) % busId
									 Pair(busId, waitingTime)
								 }.sortedBy {
									it.second
								}

			val amount = busIds.map { it.second * it.first }.first()
			logger.info("${amount} is the ID of the earliest bus you can take to the airport multiplied by the number of minutes you'll need to wait for that bus?")
		}
	}

	// Fuck Originality, stolen from https://rosettacode.org/wiki/Chinese_remainder_theorem#Kotlin
	fun multInv(a: Long, b: Long): Long {
		if (b == 1L) return 1L
		var aa = a
		var bb = b
		var x0 = 0L
		var x1 = 1L
		while (aa > 1L) {
			val q = aa / bb
			var t = bb
			bb = aa % bb
			aa = t
			t = x0
			x0 = x1 - q * x0
			x1 = t
		}
		if (x1 < 0) x1 += b
		return x1
	}

	fun chineseRemainder(n: LongArray, a: LongArray): Long {
		val prod = n.fold(1L) { acc, i -> acc * i }
		var sum = 0L
		for (i in 0 until n.size) {
			val p = prod / n[i]
			sum += a[i] * multInv(p, n[i]) * p
		}
		return sum % prod
	}
	
	private fun solve2(totalFile: String) {
		val bufferedReader = BufferedReader(InputStreamReader(Aoc2020ApplicationDay13::class.java.getResourceAsStream("/$totalFile")))
		bufferedReader.useLines {
			it.drop(1).forEach lit@{
				val busIds = it.split(",")
						       .map { it.toLongOrNull() }
						       .withIndex().filter { it.value != null }
						       .map { Pair(it.index, it.value!!) }
				val n = busIds.map { busId ->  busId.second }.toLongArray()
				val a = busIds.map { busId ->
					                 (busId.second - busId.first.toLong()) % busId.second
									}.toLongArray()
				logger.info("${chineseRemainder(n ,a)}  ..")
			}
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
