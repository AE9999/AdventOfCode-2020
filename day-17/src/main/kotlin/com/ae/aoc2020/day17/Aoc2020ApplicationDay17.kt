package com.ae.aoc2020.day17

import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.RuntimeException

@SpringBootApplication
class Aoc2020ApplicationDay17 : CommandLineRunner {

	private val logger = LoggerFactory.getLogger(javaClass)

	data class FourDCoordinate(val x: Int, val y: Int, val z: Int, val w: Int) {
		fun neighbours(): Set<FourDCoordinate> {
			val ofsets = listOf(-1, 0, 1)
			val seq = sequence {
				ofsets.forEach { dx->
					ofsets.forEach { dy->
						ofsets.forEach { dz->
							ofsets.forEach { dw ->
								if (dx != 0 || dy != 0 || dz != 0 || dw != 0) {
									yield(FourDCoordinate(x + dx, y + dy, z + dz, w + dw))
								}
							}
						}
					}
				}
			}
			return seq.toSet()
		}

	}

	inner class HyperCubeConwayCubeSystem(var activeCubes: Set<FourDCoordinate>) {
		private fun calculateInactiveCubes() : Set<FourDCoordinate> {
			return activeCubes.map { it.neighbours() }
					.flatten()
					.filter { candidate -> !activeCubes.contains(candidate) }.toSet()
		}

		fun step() {
			val cubesToDeactivate = activeCubes.filter { cube ->
				cube.neighbours().filter { neighbour ->
					activeCubes.contains(neighbour)
				}.size !in 2..3
			}
			val cubesToActivate = calculateInactiveCubes().filter {
				it.neighbours().filter { neighbour ->
					activeCubes.contains(neighbour)
				}.size == 3
			}
			if (cubesToDeactivate.intersect(cubesToActivate).isNotEmpty()) throw RuntimeException("Should not intersect ..")
			activeCubes = activeCubes.minus(cubesToDeactivate).plus(cubesToActivate)
		}

		fun nactive() = activeCubes.size
	}

	inner class ConwayCubeSystem(var activeCubes: Set<Triple<Int, Int, Int>>) {

		private fun calculateInactiveCubes() : Set<Triple<Int, Int, Int>> {
			return activeCubes.map { it.neighbours() }
					          .flatten()
					          .filter { candidate -> !activeCubes.contains(candidate) }.toSet()
		}

		fun step() {
			val cubesToDeactivate = activeCubes.filter { cube ->
				cube.neighbours().filter { neighbour ->
					activeCubes.contains(neighbour)
				}.size !in 2..3
			}
			val cubesToActivate = calculateInactiveCubes().filter {
				it.neighbours().filter { neighbour ->
					 activeCubes.contains(neighbour)
				}.size == 3
			}
			if (cubesToDeactivate.intersect(cubesToActivate).isNotEmpty()) throw RuntimeException("Should not intersect ..")
			activeCubes = activeCubes.minus(cubesToDeactivate).plus(cubesToActivate)
		}

		fun nactive() = activeCubes.size
	}


	private fun solve1(file: String) {
		val bufferedReader = BufferedReader(InputStreamReader(Aoc2020ApplicationDay17::class.java.getResourceAsStream("/$file")))
		bufferedReader.useLines { input ->
			val conwayCubeSystem = ConwayCubeSystem(input.withIndex()
					                                     .map { yLine ->
				                                                 yLine.value.withIndex()
																		    .filter { it.value == '#' }
																		     .map { xLine ->  Triple(xLine.index, yLine.index, 0) }
			                                                  }
					                                     .flatten()
					                                     .toSet())
			(0..5).forEach {
				conwayCubeSystem.step()
			}
			logger.info("${conwayCubeSystem.nactive()} cubes are left in the active state after the sixth cycle ..")
		}
	}

	private fun solve2(file: String) {
		val bufferedReader = BufferedReader(InputStreamReader(Aoc2020ApplicationDay17::class.java.getResourceAsStream("/$file")))
		bufferedReader.useLines { input ->
			val conwayCubeSystem = HyperCubeConwayCubeSystem(input.withIndex()
					.map { yLine ->
						yLine.value.withIndex()
								.filter { it.value == '#' }
								.map { xLine ->  FourDCoordinate(xLine.index, yLine.index, 1,1) }
					}
					.flatten()
					.toSet())
			(0..5).forEach {
				conwayCubeSystem.step()
			}
			logger.info("${conwayCubeSystem.nactive()} cubes are left in the active state after the sixth cycle ..")

		}
	}

	override fun run(vararg args: String?) {
		solve1(args[0]!!)
		solve2(args[0]!!)
	}
}

private fun Triple<Int, Int, Int>.neighbours(): Set<Triple<Int, Int, Int>> {
	val ofsets = listOf(-1, 0, 1)
	val seq = sequence {
		ofsets.forEach { dx->
			ofsets.forEach { dy->
				ofsets.forEach { dz->
					if (dx != 0 || dy != 0 || dz != 0) {
						yield(Triple(first + dx, second + dy, third + dz ))
					}
				}
			}
		}
	}
	return seq.toSet()
}

fun main(args: Array<String>) {
	runApplication<Aoc2020ApplicationDay17>(*args)
}
