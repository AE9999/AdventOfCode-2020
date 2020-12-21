package com.ae.aoc2020.day18

import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.RuntimeException

@SpringBootApplication
class Aoc2020ApplicationDay18 : CommandLineRunner {

	private val logger = LoggerFactory.getLogger(javaClass)

	private fun parseExpression(line: String, pointer: Int) : Pair<Long, Int> {
		var pointer_ = pointer
		var acc = 0L
		var opp: Char = ' '

		while (pointer_ < line.length) {
			when (line[pointer_]) {
				'(' -> {
					val res = parseExpression(line, pointer_ + 1)
					when(opp) {
						'*' -> { acc *= res.first; opp = ' ' }
						'+' -> { acc += res.first; opp = ' ' }
						' ' -> { acc = res.first; opp = ' ' }
						else -> throw RuntimeException("Illegal instruction")
					}
					pointer_ = res.second
				}
				')' -> {
					return Pair(acc, pointer_ + 1)
				}
				'0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> {
					val (matched) = Regex("^(\\d+)").find(line.drop(pointer_))!!.destructured
					when(opp) {
						'*' -> { acc *= matched.toLong(); opp = ' ' }
						'+' -> { acc += matched.toLong(); opp = ' ' }
						' ' -> { acc = matched.toLong(); opp = ' ' }
						else -> throw RuntimeException("Illegal instruction")
					}
					pointer_ += matched.length

				}
				'*', '+' -> {
					opp = line[pointer_]
					pointer_ += 1

				}
				' ' -> {
					pointer_ += 1
				}
			}
		}
		return Pair(acc, pointer_)
	}

	private fun priorityPlusParse(line: String, pointer: Int) : Pair<Long, Int> {
		var pointer_ = pointer

		val accs = ArrayList<Long>()
		var acc = 0L
		var opp = ' '

		while (pointer_ < line.length) {
			when (line[pointer_]) {
				'(' -> {
					val res = priorityPlusParse(line, pointer_ + 1)
					when(opp) {
						'*' -> { accs.add(acc); acc = res.first; opp = ' ' }
						'+' -> { acc += res.first; opp = ' ' }
						' ' -> { acc = res.first; opp = ' ' }
						else -> throw RuntimeException("Illegal instruction")
					}
					pointer_ = res.second
				}
				')' -> {
					accs.add(acc)
					return Pair(accs.reduce(Long::times), pointer_ + 1)
				}
				'0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> {
					val (matched) = Regex("^(\\d+)").find(line.drop(pointer_))!!.destructured
					when(opp) {
						'*' -> { accs.add(acc); acc = matched.toLong(); opp = ' ' }
						'+' -> { acc += matched.toLong(); opp = ' ' }
						' ' -> { acc = matched.toLong(); opp = ' ' }
						else -> throw RuntimeException("Illegal instruction")
					}
					pointer_ += matched.length

				}
				'*', '+' -> {
					opp = line[pointer_]
					pointer_ += 1

				}
				' ' -> {
					pointer_ += 1
				}
			}
		}
		accs.add(acc)
		return Pair(accs.reduce(Long::times), pointer_)
	}

	private fun solve1(file: String) {
		val bufferedReader = BufferedReader(InputStreamReader(Aoc2020ApplicationDay18::class.java.getResourceAsStream("/$file")))
		bufferedReader.useLines { input ->
			logger.info("${input.map { line -> parseExpression(line, 0).first }.sum()}  is the sum of the resulting values ..")
		}
	}

	private fun solve2(file: String) {
		val bufferedReader = BufferedReader(InputStreamReader(Aoc2020ApplicationDay18::class.java.getResourceAsStream("/$file")))
		bufferedReader.useLines { input ->
			logger.info("${input.map { line -> priorityPlusParse(line, 0).first }.sum()}  is the sum of the resulting values ..")
		}
	}

	override fun run(vararg args: String?) {
		solve1(args[0]!!)
		solve2(args[0]!!)
	}
}

fun main(args: Array<String>) {
	runApplication<Aoc2020ApplicationDay18>(*args)
}
