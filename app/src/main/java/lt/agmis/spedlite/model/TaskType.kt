package lt.agmis.spedlite.model

import lt.agmis.spedlite.R

enum class TaskType {
    Pickup,
    Delivery,
    Customs,
    TechInspection,
    Refuel,
    Rest,
    Unknown;

    companion object {
        const val LOADING = "LOADING"
        const val PICKUP = "PICKUP"
        const val DELIVERY = "DELIVERY"
        const val UNLOADING = "UNLOADING"
        const val CUSTOMS = "CUSTOMS"
        const val TECH_INSPECTION = "TECH_INSPECTION"
        const val REFUEL = "REFUEL"
        const val REST = "REST"

        fun parse(rawStatus: String): TaskType {
            when (rawStatus) {
                LOADING, PICKUP -> return Pickup
                DELIVERY, UNLOADING -> return Delivery
                CUSTOMS -> return Customs
                TECH_INSPECTION -> return TechInspection
                REFUEL -> return Refuel
                REST -> return Rest
                else -> return Unknown
            }
        }

    }
}

fun TaskType.toIconRes() = when (this) {
    TaskType.Pickup -> R.drawable.ic_pickup
    TaskType.Delivery -> R.drawable.ic_truck
    TaskType.Customs -> R.drawable.ic_customs
    TaskType.TechInspection -> R.drawable.ic_tech
    TaskType.Refuel -> R.drawable.ic_gas_station
    TaskType.Rest -> R.drawable.ic_bed
    TaskType.Unknown -> R.drawable.ic_settings
}

fun TaskType.toStringRes(): Int? = when (this) {
    TaskType.Pickup -> R.string.task_type_pickup
    TaskType.Delivery -> R.string.task_type_delivery
    TaskType.Customs -> R.string.task_type_customs
    TaskType.TechInspection -> R.string.task_type_tech_inspection
    TaskType.Refuel -> R.string.task_type_refuel
    TaskType.Rest -> R.string.task_type_rest
    TaskType.Unknown -> null
}