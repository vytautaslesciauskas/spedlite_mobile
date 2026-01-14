package lt.agmis.spedlite.model

enum class TaskStatus {
    Pending,
    InProgress,
    Finished,
    Aborted,
    Unknown;

    companion object {
        const val STATUS_PENDING = 0
        const val STATUS_IN_PROGRESS = 1
        const val STATUS_FINISHED = 2
        const val STATUS_ABORTED = -1

        fun parse(rawStatus: Int): TaskStatus {
            when (rawStatus) {
                STATUS_PENDING -> return Pending
                STATUS_IN_PROGRESS -> return InProgress
                STATUS_FINISHED -> return Finished
                STATUS_ABORTED -> return Aborted
                else -> return Unknown
            }
        }
    }
}