package views

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import props.TimestampProps
import react.FC
import react.dom.html.ReactHTML.div
import web.cssom.ClassName

val Timestamp = FC<TimestampProps> { props ->
    val createdOn = props.createdOn
    val updatedOn = props.updatedOn

    fun Int.format(digits: Int = 2): String {
        val str = this.toString()
        if (str.length >= digits) return str
        return "0".repeat(digits - str.length) + str
    }

    fun Instant.format() =
        this.toLocalDateTime(TimeZone.currentSystemDefault())
            .run {"${year}-${month.number.format()}-${dayOfMonth.format()} ${hour.format()}:${time.minute.format()}"}

    if (createdOn != null){
        div{
            className = ClassName("timestamp")
            + "Created on: ${createdOn.format()}"
            if (updatedOn != null){
                + ", Updated on: ${updatedOn.format()}"
            }
        }
    }
}
