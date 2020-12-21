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

	private fun solve(file: String) {
		val bufferedReader = BufferedReader(InputStreamReader(Aoc2020ApplicationDay21::class.java.getResourceAsStream("/$file")))
		bufferedReader.useLines { input ->
			val foods = input.map { parseFood(it) }.toList()
			val allergen2ingredientCandidates = HashMap<String, Set<String>>()
			val allergen2ingredient = HashMap<String, String>()
			val ingredient2allergen = HashMap<String, String>()
			foods.forEach { food ->
				food.knownAllergens.forEach { allergen ->
					if (!allergen2ingredientCandidates.containsKey(allergen)) {
						allergen2ingredientCandidates[allergen] = food.ingredients
					}
					val intersection = allergen2ingredientCandidates[allergen]!!.intersect(food.ingredients)
					if (intersection.isEmpty()) throw RuntimeException("Your logic sux ..")
					allergen2ingredientCandidates[allergen] = intersection
				}
			}
			while (allergen2ingredientCandidates.isNotEmpty()) {
				val solved = allergen2ingredientCandidates.filter { it.value.size == 1 }
				if (solved.isEmpty()) throw RuntimeException("No progress was made ..")
				solved.forEach {
					allergen2ingredient[it.key] = it.value.first()
					ingredient2allergen[it.value.first()] = it.key
					allergen2ingredientCandidates.remove(it.key)
					allergen2ingredientCandidates.keys.forEach { allergen ->
						allergen2ingredientCandidates[allergen] = allergen2ingredientCandidates[allergen]!!.minus(it.value.first())
						if (allergen2ingredientCandidates[allergen]!!.isEmpty()) throw RuntimeException("Your logic sux ..")
					}

				}
			}

			val answer = foods.map {
				food -> food.ingredients.filter { ingredient ->
					!allergen2ingredient.values.contains(ingredient)
				}.size
			}.sum()

			logger.info("${answer} times do any of those ingredients appear")

			val canonicalList = ingredient2allergen.entries.sortedBy { it.value }.map { it.key }.joinToString(",")

			logger.info("${canonicalList}  is my canonical dangerous ingredient list")

		}
	}

	override fun run(vararg args: String?) {
		solve(args[0]!!)
	}
}

fun main(args: Array<String>) {
	runApplication<Aoc2020ApplicationDay21>(*args)
}
