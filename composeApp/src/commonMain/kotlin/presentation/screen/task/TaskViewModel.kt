package presentation.screen.task

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import data.MongoDB
import domain.TaskAction
import domain.ToDoTask
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch

/**
* Class to define the TaskViewModel for the TaskScreen, containing the logic
* for adding and updating tasks in the MongoDB database
**/
class TaskViewModel(private val mongoDB: MongoDB): ScreenModel {

    fun setAction(action: TaskAction) {
        when (action) {
            is TaskAction.Add -> {
                addTask(action.task)
            }

            is TaskAction.Update -> {
                updateTask(action.task)
            }

            else -> {}
        }
    }

    private fun addTask(task: ToDoTask) {
        screenModelScope.launch(Dispatchers.IO) {
            mongoDB.addTask(task)
        }
    }

    private fun updateTask(task: ToDoTask) {
        screenModelScope.launch(Dispatchers.IO) {
            mongoDB.updateTask(task)
        }
    }
}