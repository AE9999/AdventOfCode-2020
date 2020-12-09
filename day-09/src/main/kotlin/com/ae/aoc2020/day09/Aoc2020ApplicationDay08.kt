package com.ae.aoc2020.day09

import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.IllegalStateException


@SpringBootApplication
class Aoc2020ApplicationDay09 : CommandLineRunner {

	private val logger = LoggerFactory.getLogger(javaClass)

	data class XMAS(val input: List<Long>,
	                val windowSize: Int) {

		fun isCombined(frameList: Set<Long>, target: Long) : Boolean {
			val frame = frameList.toList()
			for (i in 0 until frame.count()) {
				for (j in (i + 1) until frame.count()) {
					if (frame[i] + frame[j] == target) return true
				}
			}
			return false
		}

		fun findFirstIncorrectNumber() : Long {
			var top = windowSize
			while (top <= input.size) {
				if (!isCombined(input.subList(top - windowSize, top).toSet(),
								input[top])) {
					return input[top]
				}
				top += 1
			}
			return -1
		}

		fun findProofOfContiniousSet(inValidNumber: Long): Pair<Long, Long> {
			var lb = 0
			while (lb < input.size) {
				var i = 1
				var acc = input[lb]
				while(acc <= inValidNumber) {
					acc += input[lb + i]
					if (acc == inValidNumber) {
						val set = input.subList(lb, lb + i + 1)
						return Pair(set.min()!!, set.max()!!)
					}
					i += 1
				}
				lb++
			}
			return Pair(-1, -1)
		}
	}

	private fun solve1(totalFile: String, windowSize: Int) {
		val bufferedReader = BufferedReader(InputStreamReader(Aoc2020ApplicationDay09::class.java.getResourceAsStream("/$totalFile")))
		bufferedReader.useLines {
			val xMas = XMAS(it.map { it.toLong() }.toList(), windowSize)
			logger.info("${xMas.findFirstIncorrectNumber()} is the first number that does not have this property")
		}
	}

	private fun solve2(totalFile: String, windowSize: Int) {
		val bufferedReader = BufferedReader(InputStreamReader(Aoc2020ApplicationDay09::class.java.getResourceAsStream("/$totalFile")))
		bufferedReader.useLines {
			val xMas = XMAS(it.map { it.toLong() }.toList(), windowSize)
			val inValidNumber = xMas.findFirstIncorrectNumber()
			val result = xMas.findProofOfContiniousSet(inValidNumber)
			logger.info("${result.first + result.second} is the encryption weakness in your XMAS-encrypted list of numbers")
		}
	}

	override fun run(vararg args: String?) {
		solve1(args[0]!!, args[1]!!.toInt())
		solve2(args[0]!!, args[1]!!.toInt())
	}
}

fun main(args: Array<String>) {
	runApplication<Aoc2020ApplicationDay09>(*args)
}
