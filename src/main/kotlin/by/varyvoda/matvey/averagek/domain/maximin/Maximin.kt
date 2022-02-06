package by.varyvoda.matvey.averagek.domain.maximin

import by.varyvoda.matvey.averagek.domain.KAverage
import by.varyvoda.matvey.averagek.domain.KClass
import kotlin.concurrent.thread

class Maximin<T, Criteria : Comparable<Criteria>>(
    private val kAverage: KAverage<T, Criteria>,
    private val newCoreCriteria: (candidate: T, parentCore: T, cores: List<T>) -> Boolean
) {

    private var workingThread: Thread? = null

    fun calculate(
        items: List<T>,
        stepResultNotifier: (stepResult: List<KClass<T>>) -> Unit
    ) {
        workingThread = thread {
            var kClasses: List<KClass<T>> = listOf(KClass(items.first(), items))
            var prevCoresCount = 0

             while (prevCoresCount != kClasses.size) {
                 prevCoresCount = kClasses.size
                 kClasses = kAverage.redistribute(getCores(kClasses), items, stepResultNotifier)
             }
        }
    }

    fun stop() {
        workingThread?.interrupt()
        workingThread = null
    }

    private fun getCores(kClasses: List<KClass<T>>): List<T> {
        val plainCores = toCores(kClasses)
        for (kClass in kClasses) {
            val candidate: T = kClass.items.stream()
                .max(Comparator.comparing { item -> kAverage.comparator(kClass.core, item) })
                .orElseThrow()
            if (newCoreCriteria(candidate, kClass.core, plainCores)) {
                return plainCores.plus(candidate)
            }
        }
        return plainCores
    }

    private fun toCores(kClasses: List<KClass<T>>): List<T> {
        return kClasses.map { kClass -> kClass.core }
    }
}