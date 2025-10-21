package com.example.finalexam.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.finalexam.data.UserPreferencesRepository
import com.example.finalexam.network.JokeApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class JokeFetchWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val response = JokeApi.service.getRandomJoke()
            val joke = if (response.joke != null) {
                response.joke
            } else {
                "${response.setup}\n${response.punchline}"
            }
            
            val prefsRepo = UserPreferencesRepository(applicationContext)
            prefsRepo.saveJoke(joke)
            
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
