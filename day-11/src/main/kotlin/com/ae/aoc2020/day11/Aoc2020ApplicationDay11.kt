package com.ae.aoc2020.day11

import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.RuntimeException

@SpringBootApplication
class Aoc2020ApplicationDay11 : CommandLineRunner {

	private val logger = LoggerFactory.getLogger(javaClass)

	data class SeatingSystem(var rows: List<String>) {

		private val logger = LoggerFactory.getLogger(javaClass)

		fun height() = rows.size

		fun width() = rows[0].length

		private fun adjacent(x : Int, y: Int): List<Char> {
			return sequence {
				listOf(-1, 1, 0).forEach { dx ->
					listOf(-1, 1, 0).forEach { dy ->
						yield(Pair(dx,dy))
					}
				}
			}.filter { it.first != 0 || it.second != 0 }
					.filter { x + it.first >= 0 && x + it.first < width()  }
					.filter { y + it.second >= 0 && y + it.second < height()  }
					.map {
						rows[y + it.second][x + it.first]
					}
			 .toList()
		}

		private fun calculateStuff(x: Int, y: Int, d: Pair<Int, Int>) : Char {
			var x_ = x
			var y_ = y
			while (true) {
				x_ += d.first
				y_ += d.second
				if (x_ < 0 || x_ >= width() || y_ < 0 || y_ >= height()) {
					return '.'
				}
				if (rows[y_][x_] != '.') return rows[y_][x_]
			}
		}

		private fun seen(x: Int, y: Int) : List<Char> {
			val trajectories = sequence {
				listOf(-1, 1, 0).forEach { dx ->
					listOf(-1, 1, 0).forEach { dy ->
						yield(Pair(dx,dy))
					}
				}
			}.filter { it.first != 0 || it.second != 0 }.toList()

			return trajectories.map { d ->
				calculateStuff(x, y, d)
			}
		}

		fun occupiedSeats() =
			rows.map { it.filter { it == '#' }.length }.sum()

		fun step() {
			val nextState = rows.indices.map { y ->
				val row = rows[y]
				row.indices.map { x ->
					when(row[x]) {
						'.' -> '.'
						'L' -> if (adjacent(x, y).none { it == '#' }) '#' else 'L'
						'#' -> if (adjacent(x, y).filter { it == '#' }.size >= 4) 'L' else '#'
						else -> RuntimeException("Bullshit ${row[x]}")
					}
				}.joinToString("")
			}.toList()
			rows = nextState
		}

		fun step2() {
			val nextState = rows.indices.map { y ->
				val row = rows[y]
				row.indices.map { x ->
					when(row[x]) {
						'.' -> '.'
						'L' -> if (seen(x, y).none { it == '#' }) '#' else 'L'
						'#' -> if (seen(x, y).filter { it == '#' }.size >= 5) 'L' else '#'
						else -> RuntimeException("Bullshit ${row[x]}")
					}
				}.joinToString("")
			}.toList()
			rows = nextState
		}


		fun dump() {
			logger.info("STARTING DUMP..")
			rows.forEach {
					logger.info(it)
			}
			logger.info("Finished DUMP..")
		}
	}

	private fun solve1(totalFile: String) {
		val bufferedReader = BufferedReader(InputStreamReader(Aoc2020ApplicationDay11::class.java.getResourceAsStream("/$totalFile")))
		bufferedReader.useLines {
			val seatingSystem = SeatingSystem(it.toList())
			var occupiedSeats = seatingSystem.occupiedSeats()
			seatingSystem.step()
			while (seatingSystem.occupiedSeats() != occupiedSeats) {
				occupiedSeats = seatingSystem.occupiedSeats()
				seatingSystem.step()
			}
			logger.info("${occupiedSeats} seats end up occupied")

		}
	}

	private fun solve2(totalFile: String) {
		val bufferedReader = BufferedReader(InputStreamReader(Aoc2020ApplicationDay11::class.java.getResourceAsStream("/$totalFile")))
		bufferedReader.useLines {
			val seatingSystem = SeatingSystem(it.toList())
			var occupiedSeats = seatingSystem.occupiedSeats()
			seatingSystem.step2()
			while (seatingSystem.occupiedSeats() != occupiedSeats) {
				occupiedSeats = seatingSystem.occupiedSeats()
				seatingSystem.step2()
			}
			logger.info("${occupiedSeats} seats end up occupied")
		}
	}

	override fun run(vararg args: String?) {
		solve1(args[0]!!)
		solve2(args[0]!!)
	}
}

fun main(args: Array<String>) {
	runApplication<Aoc2020ApplicationDay11>(*args)
}
