package com.ae.aoc2020.day21

import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.RuntimeException

@SpringBootApplication
class Aoc2020ApplicationDay21 : CommandLineRunner {

	private val logger = LoggerFactory.getLogger(javaClass)

	class Food(val ingredients: Set<String>,
	           val knownAllergens: Set<String>)

	fun parseFood(line: String) : Food {
		val splitIndex = line.indexOf('(')
		val ingredients = line.take(splitIndex).trim().split(' ').toSet()
		val offset = "contains ".length
		val knownAlergens = line.drop(splitIndex + 1 + offset).dropLast(1).split(",").map { it.trim() }.toSet()
		return Food(ingredients, knownAlergens)
	}

	private fun possiblePairs(lenght: Int) : Sequence<Pair<Int, Int>> {
		return sequence {
			(0 until lenght).forEach { i ->
				(0 until lenght).forEach { j ->
					if (i != j) yield(Pair(i,j))
				}
			}
		}
	}

	private fun solve(file: String) {
		val bufferedReader = BufferedReader(InputStreamReader(Aoc2020ApplicationDay21::class.java.getResourceAsStream("/$file")))
		bufferedReader.useLines { input ->
			val foods = input.map { parseFood(it) }.toList()
			val allergen2ingredientSets = HashMap<String, MutableList<MutableSet<String>>>()
			val allergen2ingredient = HashMap<String, String>()
			foods.forEach { food ->
				food.knownAllergens.forEach { allergen ->
					if (!allergen2ingredientSets.containsKey(allergen)) {
						allergen2ingredientSets[allergen] = ArrayList()
					}
					allergen2ingredientSets[allergen]!!.add(food.ingredients.toMutableSet())
				}
			}
			val nallergens = allergen2ingredientSets.size

			while (allergen2ingredient.size != nallergens) {
				logger.info("Starting stuff ..")
				val allergenIngredients = HashSet<Pair<String, String>>()
				allergen2ingredientSets.forEach lit@ { (allergen, ingredientSets) ->

					val minimalSet = ingredientSets.reduce { acc, mutableSet -> acc.intersect(mutableSet).toMutableSet() }

					ingredientSets.filter { it.size == 1 }.forEach { unitSet ->
						allergen2ingredient[allergen] = unitSet.first()
						allergenIngredients.add(Pair(allergen, unitSet.first()))
						return@lit
					}

//					possiblePairs(ingredientSets.size).forEach { pair ->
//						val left = ingredientSets[pair.first]
//						val right = ingredientSets[pair.second]
//						val intersection = left.intersect(right)
//						if (intersection.isEmpty()) {
//							throw RuntimeException("Your logic sucks ..")
//						}
//						if (intersection.size == 1) {
//							allergen2ingredient[allergen] = intersection.first()
//							allergenIngredients.add(Pair(allergen, intersection.first()))
//						}
//					}
				}

				if (allergenIngredients.size == 0) {
					throw RuntimeException("No progress was made ..")
				}

				allergenIngredients.forEach { allergenIngredient ->
					allergen2ingredientSets.remove(allergenIngredient.first)
					allergen2ingredientSets.values.forEach {
						it.forEach {
							it.remove(allergenIngredient.second)
						}
					}
				}
			}

			val answer = foods.map {
				food -> food.ingredients.filter { ingredient ->
					!allergen2ingredient.values.contains(ingredient)
				}.size
			}.sum()

			logger.info("${answer} times do any of those ingredients appear?")

		}
	}

	override fun run(vararg args: String?) {
		solve(args[0]!!)
	}
}

fun main(args: Array<String>) {
	runApplication<Aoc2020ApplicationDay21>(*args)
}
