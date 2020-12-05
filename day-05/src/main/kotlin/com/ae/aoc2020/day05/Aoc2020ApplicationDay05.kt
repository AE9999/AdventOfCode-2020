package com.ae.aoc2019.day05

import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.io.BufferedReader
import java.io.InputStreamReader


@SpringBootApplication
class Aoc2020ApplicationDay05 : CommandLineRunner {

	private val logger = LoggerFactory.getLogger(javaClass)

	data class BoardingPass(private val content: String) {

		fun row() : Int  {
			var lb = 0
			var ub = 127
			(0..5).forEach {
				val half = (ub - lb + 1) / 2
				if (content[it] == 'F')
					ub -= half
				else
					lb += half

			}
			return if (content[6] == 'F') lb else ub
		}

		fun column(): Int {
			var lb = 0
			var ub = 7
			(7..8).forEach {
				val half = (ub - lb + 1) / 2
				if (content[it] == 'R')
					lb += half
				else
					ub -= half
			}
			return if (content[9] == 'R') ub else lb
		}

		fun id() = (row() * 8) + column()
	}

	private fun solve1(totalFile: String) {
		val bufferedReader = BufferedReader(InputStreamReader(Aoc2020ApplicationDay05::class.java.getResourceAsStream("/$totalFile")))
		bufferedReader.useLines {
			val max = it.map { line -> BoardingPass(line) }.onEach { boardingPass ->
				logger.info("Read $boardingPass, with row: ${boardingPass.row()}, with column: ${boardingPass.column()} and id ${boardingPass.id()}")
			}.map {
				it.id()
			}.max()
			logger.info("Found $max as the highest seat ID on a boarding pass")
		}
	}

	private fun solve2(totalFile: String) {
		val bufferedReader = BufferedReader(InputStreamReader(Aoc2020ApplicationDay05::class.java.getResourceAsStream("/$totalFile")))
		bufferedReader.useLines {
			val ids = it.map { line -> BoardingPass(line) }.map { it.id() }.toSet()
			(0 until (127 * 8) + 7).forEach {  id ->
				if (!ids.contains(id) && ids.contains(id-1) && ids.contains(id+1)) {
					logger.info("${id} is empty but ${id +1} and ${id -1} are not ..")
				}
			}
		}
	}

	override fun run(vararg args: String?) {
		solve1(args[0]!!)
		solve2(args[0]!!)
	}
}

fun main(args: Array<String>) {
	runApplication<Aoc2020ApplicationDay05>(*args)
}
