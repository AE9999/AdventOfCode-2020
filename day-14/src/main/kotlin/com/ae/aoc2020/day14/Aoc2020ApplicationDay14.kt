package com.ae.aoc2020.day14

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
class Aoc2020ApplicationDay14 : CommandLineRunner {

	private val logger = LoggerFactory.getLogger(javaClass)

	inner class Program() {
		val program = HashMap<Long, String>()

		var currentMask = "101XX10X1X00001010011011X1XXX1001011"

		fun long2string(value: Long) : String = value.toString(2).padStart(36, '0')

		fun string2long(value: String) : Long = value.toLong(2)


		fun applyMask(value: String) : String {
			return value.toCharArray().withIndex().map {
				when(currentMask[it.index]) {
					'1', '0' -> currentMask[it.index]
					else ->  it.value
				}
			}.joinToString("")
		}


		fun applyInstruction(line: String) {
			if(line.startsWith("mem")) {
				val (destination, value) = Regex("mem\\[(\\d+)\\] = (\\d+)").find(line)!!.destructured
				program[destination.toLong()] = applyMask(long2string(value.toLong()))

			} else if (line.startsWith("mask")) {
				currentMask = line.split(" ")[2]
			}
		}

		fun memValue() : Long = program.map { string2long(it.value) }.sum()
	}

	private fun solve1(totalFile: String) {
		val bufferedReader = BufferedReader(InputStreamReader(Aoc2020ApplicationDay14::class.java.getResourceAsStream("/$totalFile")))
		bufferedReader.useLines {
			val program = Program()
			it.forEach { line ->
				program.applyInstruction(line)
			}
			logger.info("${program.memValue()} is the sum of all values left in memory after it completes ..")
		}
	}

	
	private fun solve2(totalFile: String) {
		val bufferedReader = BufferedReader(InputStreamReader(Aoc2020ApplicationDay14::class.java.getResourceAsStream("/$totalFile")))
		bufferedReader.useLines {
		}
	}

	override fun run(vararg args: String?) {
		solve1(args[0]!!)
		solve2(args[0]!!)
	}
}

fun main(args: Array<String>) {
	runApplication<Aoc2020ApplicationDay14>(*args)
}
