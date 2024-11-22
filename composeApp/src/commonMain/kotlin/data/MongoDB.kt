package data

import domain.RequestState
import domain.ToDoTask
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

// Class to define the MongoDB database and the operations that can be run on device
class MongoDB {
    private var realm: Realm? = null

    init {
        configureTheRealm()
    }

    // Function to configure the Realm database
    private fun configureTheRealm() {
        if (realm == null || realm!!.isClosed()) {
            val config = RealmConfiguration.Builder(
                schema = setOf(ToDoTask::class)
            )
                .compactOnLaunch()
                .build()
            realm = Realm.open(config)
        }
    }

    // Function to read the active tasks from the MongoDB database
    fun readActiveTasks(): Flow<RequestState<List<ToDoTask>>> {
        return realm?.query<ToDoTask>(query = "completed == $0", false)
            ?.asFlow()
            ?.map { result ->
                RequestState.Success(
                    data = result.list.sortedByDescending { task -> task.favorite }
                )
            } ?: flow { RequestState.Error(message = "Realm is not available.") }
    }

    // Function to read the completed tasks from the MongoDB database
    fun readCompletedTasks(): Flow<RequestState<List<ToDoTask>>> = flow {
        val realmInstance = realm
        if (realmInstance == null) {
            emit(RequestState.Error(message = "Realm is not available."))
            return@flow
        }

        realmInstance.query<ToDoTask>("completed == $0", true)
            .asFlow()
            .collect { results ->
                val taskList = mutableListOf<ToDoTask>()
                for (task in results.list) {
                    taskList.add(task)
                }
                emit(RequestState.Success(data = taskList))
            }
    }

    // Function to add a task to the MongoDB database
    suspend fun addTask(task: ToDoTask) {
        realm?.write { copyToRealm(task) }
    }

    // Function to update a task in the MongoDB database
    suspend fun updateTask(task: ToDoTask) {
        realm?.write {
            try {
                val queriedTask = query<ToDoTask>("_id == $0", task._id)
                    .first()
                    .find()
                queriedTask?.let {
                    findLatest(it)?.let { currentTask ->
                        currentTask.title = task.title
                        currentTask.description = task.description
                    }
                }
            } catch (e: Exception) {
                println(e)
            }
        }
    }

    // Function to set a task as completed in the MongoDB database
    suspend fun setCompleted(task: ToDoTask, taskCompleted: Boolean) {
        realm?.write {
            try {
                val queriedTask = query<ToDoTask>(query = "_id == $0", task._id)
                    .find()
                    .first()
                queriedTask.apply { completed = taskCompleted }
            } catch (e: Exception) {
                println(e)
            }
        }
    }

    // Function to set a task as favorite in the MongoDB database
    suspend fun setFavorite( task: ToDoTask, isFavorite: Boolean ) {
        realm?.write {
            try {
                val queriedTask = query<ToDoTask>(query = "_id == $0", task._id)
                    .find()
                    .first()
                queriedTask.apply { favorite = isFavorite }
            } catch (e: Exception) {
                println(e)
            }
        }
    }

    // Function to delete a task from the MongoDB database
    suspend fun deleteTask( task: ToDoTask ) {
        realm?.write {
            try {
                val queriedTask = query<ToDoTask>(query = "_id == $0", task._id)
                    .first()
                    .find()
                queriedTask?.let {
                    findLatest(it)?.let { currentTask ->
                        delete(currentTask)
                    }
                }
            } catch (e: Exception) {
                println(e)
            }
        }
    }
}