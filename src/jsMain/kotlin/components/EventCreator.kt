package components

import com.benasher44.uuid.uuid4
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import mui.icons.material.AddCircle
import mui.material.Button
import mui.material.Dialog
import mui.material.DialogActions
import mui.material.DialogContent
import mui.material.DialogTitle
import mui.material.FormControlMargin
import mui.material.TextField
import org.codecranachan.roster.Event
import org.codecranachan.roster.Guild
import org.w3c.dom.HTMLInputElement
import react.FC
import react.Props
import react.ReactNode
import react.create
import react.dom.events.ChangeEvent
import react.dom.html.InputType
import react.dom.onChange
import react.useContext
import react.useState
import reducers.StoreContext
import reducers.createEvent


external interface SubmitEventProps : Props {
    var guild: Guild
}

val SubmitEvent = FC<SubmitEventProps> { props ->
    val store = useContext(StoreContext)
    val (isOpen, setIsOpen) = useState(false)
    val (selectedDate, setSelectedDate) = useState(Clock.System.todayIn(TimeZone.currentSystemDefault()))

    Button {
        startIcon = AddCircle.create()
        +"Create Event"
        onClick = { setIsOpen(true) }
    }
    Dialog {
        open = isOpen
        onClose = { _, _ -> setIsOpen(false) }
        DialogTitle {
            +"Create new event"
        }
        DialogContent {
            TextField {
                margin = FormControlMargin.dense
                fullWidth = true
                label = ReactNode("Event Date")
                value = selectedDate.toString()
                type = InputType.date
                onChange = {
                    val e = it.unsafeCast<ChangeEvent<HTMLInputElement>>()
                    setSelectedDate(LocalDate.parse(e.target.value))
                }
            }
        }
        DialogActions {
            Button {
                onClick = {
                    store.dispatch(
                        createEvent(
                            Event(
                                uuid4(),
                                props.guild.id,
                                selectedDate
                            )
                        )
                    )
                    setIsOpen(false)
                }
                +"Add"
            }
        }
    }
}
