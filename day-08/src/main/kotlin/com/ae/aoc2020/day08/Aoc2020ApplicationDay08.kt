package com.ae.aoc2019.day08

import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.IllegalStateException


@SpringBootApplication
class Aoc2020ApplicationDay08 : CommandLineRunner {

	private val logger = LoggerFactory.getLogger(javaClass)

	data class Instruction(val operation : String, val argument: Int)

	data class Program(val instruction: List<Instruction>) {
		var acc = 0
		var pc  = 0

		fun step() {
			when(instruction[pc].operation) {
				"acc" -> {
					acc += instruction[pc].argument
					pc += 1
				}
				"jmp" -> {
					pc += instruction[pc].argument
				}
				"nop" -> {
					pc += 1
				}
				else -> {
					throw IllegalStateException("Should not have reaced")
				}
			}
		}

		fun runUntilFirstRepeat () : Int {
			val runInstructions = HashSet<Int>()
			while (!runInstructions.contains(pc)) {
				runInstructions.add(pc)
				step();
			}
			return acc
		}
	}

	private fun parseProgram(input: Sequence<String>) : Program {
		return Program(input.map { line ->
			val (operation, sign, operand) = Regex("(\\w+) ([\\+-])(\\d+)").find(line)!!.destructured
			Instruction(operation, (if (sign == "-") -1 else 1) * operand.toInt())
		}.toList() )
	}


	private fun solve1(totalFile: String) {
		val bufferedReader = BufferedReader(InputStreamReader(Aoc2020ApplicationDay08::class.java.getResourceAsStream("/$totalFile")))
		bufferedReader.useLines {
			val program = parseProgram(it)
			logger.info("The value of the accumulator is ${program.runUntilFirstRepeat()} before any instruction is executed a second time")
		}
	}

	private fun solve2(totalFile: String) {
		val bufferedReader = BufferedReader(InputStreamReader(Aoc2020ApplicationDay08::class.java.getResourceAsStream("/$totalFile")))
		bufferedReader.useLines {
		}
	}

	override fun run(vararg args: String?) {
		solve1(args[0]!!)
		solve2(args[0]!!)
	}
}

fun main(args: Array<String>) {
	runApplication<Aoc2020ApplicationDay08>(*args)
}
