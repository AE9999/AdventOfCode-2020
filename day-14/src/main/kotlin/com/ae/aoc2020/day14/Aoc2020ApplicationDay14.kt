package com.ae.aoc2020.day14

import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.RuntimeException

@SpringBootApplication
class Aoc2020ApplicationDay14 : CommandLineRunner {

	private val logger = LoggerFactory.getLogger(javaClass)

	fun long2string(value: Long) : String = value.toString(2).padStart(36, '0')

	fun string2long(value: String) : Long = value.toLong(2)

	inner class Program() {
		val program = HashMap<Long, String>()

		var currentMask = ""

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

	inner class ProgramV2() {
		val program = HashMap<Long, String>()

		var currentMask = ""

		fun applyMask(address: String) : List<Long> {
			val size = currentMask.filter { it == 'X' }.length.toDouble()
			val index2position = currentMask.withIndex()
					                        .filter { it.value == 'X' }
											.withIndex()
											.map { Pair(it.value.index, it.index) }
											.toMap()
			val maskedAddress = address.toCharArray().withIndex().map {
				when(currentMask[it.index]) {
					'1', 'X' -> currentMask[it.index]
					'0' -> it.value
					else -> throw RuntimeException("Illegal Input")
				}
			}
			
			return (0..(Math.pow(2.0, size).toInt())).map {
				val repValues = it.toLong().toString(2).padStart(size.toInt(), '0').toCharArray()
				val newValue = maskedAddress.toCharArray().withIndex().map {
					when (it.value) {
						'1', '0' -> it.value
						'X' -> {
							repValues[index2position[it.index]!!]
						}
						else -> throw RuntimeException("Illegal Input")
					}
				}.joinToString("")
				newValue.toLong(2)
			}
		}

		fun applyInstruction(line: String) {
			if(line.startsWith("mem")) {
				val (destination, value) = Regex("mem\\[(\\d+)\\] = (\\d+)").find(line)!!.destructured
				applyMask(long2string(destination.toLong())).forEach { d ->
					program[d] = long2string(value.toLong())
				}
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
			val program = ProgramV2()
			it.forEach { line ->
				program.applyInstruction(line)
			}
			logger.info("${program.memValue()} is the sum of all values left in memory after it completes ..")
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
