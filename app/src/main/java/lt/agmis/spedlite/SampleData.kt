package lt.agmis.spedlite

import lt.agmis.spedlite.model.Task
import lt.agmis.spedlite.model.TaskStatus
import lt.agmis.spedlite.model.TaskType

object SampleData {

    val tasks = listOf(
        Task(
            id = 1L,
            address = "Carrer de Mallorca 401, 08013 Barcelona, Spain",
            lat = 41.3951,
            lng = 2.1734,
            typeRaw = TaskType.PICKUP,
            visited = 1704067200000L,
            statusRaw = TaskStatus.STATUS_PENDING,
            country = "ES"
        ),
        Task(
            id = 2L,
            address = "Calle Gran Vía 32, 28013 Madrid, Spain",
            lat = 40.4203,
            lng = -3.7015,
            typeRaw = TaskType.DELIVERY,
            visited = 1704153600000L,
            statusRaw = TaskStatus.STATUS_IN_PROGRESS,
            country = "ES"
        ),
        Task(
            id = 3L,
            address = "Rue de la Loi 200, 1049 Brussels, Belgium",
            lat = 50.8436,
            lng = 4.3674,
            typeRaw = TaskType.CUSTOMS,
            visited = 1704240000000L,
            statusRaw = TaskStatus.STATUS_FINISHED,
            country = "BE"
        ),
        Task(
            id = 4L,
            address = "Alexanderplatz 1, 10178 Berlin, Germany",
            lat = 52.5219,
            lng = 13.4132,
            typeRaw = TaskType.LOADING,
            visited = 1704326400000L,
            statusRaw = TaskStatus.STATUS_ABORTED,
            country = "DE"
        ),
        Task(
            id = 5L,
            address = "Via Roma 15, 20121 Milan, Italy",
            lat = 45.4654,
            lng = 9.1859,
            typeRaw = TaskType.UNLOADING,
            visited = 1704412800000L,
            statusRaw = TaskStatus.STATUS_PENDING,
            country = "IT"
        ),
        Task(
            id = 6L,
            address = "Shell Station, A1 Highway Exit 45, Netherlands",
            lat = 52.0907,
            lng = 5.1214,
            typeRaw = TaskType.REFUEL,
            visited = 1704499200000L,
            statusRaw = TaskStatus.STATUS_FINISHED,
            country = "NL"
        ),
        Task(
            id = 7L,
            address = "TÜV Center, Industriestraße 12, 60313 Frankfurt, Germany",
            lat = 50.1109,
            lng = 8.6821,
            typeRaw = TaskType.TECH_INSPECTION,
            visited = 1704585600000L,
            statusRaw = TaskStatus.STATUS_IN_PROGRESS,
            country = "DE"
        ),
        Task(
            id = 8L,
            address = "Rest Area E40, 1000 Ljubljana, Slovenia",
            lat = 46.0569,
            lng = 14.5058,
            typeRaw = TaskType.REST,
            visited = 1704672000000L,
            statusRaw = TaskStatus.STATUS_PENDING,
            country = "SI"
        ),
        Task(
            id = 9L,
            address = "Port of Rotterdam, Europaweg 800, Netherlands",
            lat = 51.9066,
            lng = 4.4883,
            typeRaw = TaskType.PICKUP,
            visited = 1704758400000L,
            statusRaw = TaskStatus.STATUS_FINISHED,
            country = "NL"
        ),
        Task(
            id = 10L,
            address = "Avenue des Champs-Élysées 101, 75008 Paris, France",
            lat = 48.8698,
            lng = 2.3075,
            typeRaw = TaskType.DELIVERY,
            visited = 1704844800000L,
            statusRaw = TaskStatus.STATUS_IN_PROGRESS,
            country = "FR"
        ),
        Task(
            id = 11L,
            address = "Warehouse District, Gedimino pr. 9, Vilnius, Lithuania",
            lat = 54.6872,
            lng = 25.2797,
            typeRaw = TaskType.LOADING,
            visited = 1704931200000L,
            statusRaw = TaskStatus.STATUS_PENDING,
            country = "LT"
        ),
        Task(
            id = 12L,
            address = "Unknown Location XYZ",
            lat = 55.7558,
            lng = 37.6173,
            typeRaw = "INVALID_TYPE",
            visited = 1705017600000L,
            statusRaw = 99,
            country = null
        ),
        Task(
            id = 13L,
            address = "Customs Office, Świnoujście Port, Poland",
            lat = 53.9105,
            lng = 14.2475,
            typeRaw = TaskType.CUSTOMS,
            visited = 1705104000000L,
            statusRaw = TaskStatus.STATUS_ABORTED,
            country = "PL"
        ),
        Task(
            id = 14L,
            address = "Praça do Comércio 1, 1100-148 Lisbon, Portugal",
            lat = 38.7083,
            lng = -9.1368,
            typeRaw = TaskType.UNLOADING,
            visited = 1705190400000L,
            statusRaw = TaskStatus.STATUS_FINISHED,
            country = "PT"
        ),
        Task(
            id = 15L,
            address = "Random Street 123, Mystery City",
            lat = 48.2082,
            lng = 16.3738,
            typeRaw = "SOMETHING_NEW",
            visited = 1705276800000L,
            statusRaw = -999,
            country = null
        )
    )

    val singleTask = tasks.first()
}