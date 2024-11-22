package domain

/**
 * Sealed class to define the actions that can be performed on a task
 * This is used the command pattern to encapsulate the actions
**/
sealed class TaskAction {
    data class Add(val task: ToDoTask) : TaskAction()
    data class Update(val task: ToDoTask) : TaskAction()
    data class Delete(val task: ToDoTask) : TaskAction()
    data class SetCompleted(val task: ToDoTask, val completed: Boolean) : TaskAction()
    data class SetFavorite(val task: ToDoTask, val isFavorite: Boolean) : TaskAction()
}