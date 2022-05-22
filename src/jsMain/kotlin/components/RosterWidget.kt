package components

import org.reduxkotlin.Store
import react.FC
import react.Props
import react.dom.html.ReactHTML.div
import react.useEffectOnce
import react.useState
import reducers.ApplicationState

external interface RosterWidgetProps : Props {
    var store: Store<ApplicationState>
}

val RosterWidget = FC<RosterWidgetProps> { props ->
    val (userIdentity, setUserIdentity) = useState(props.store.state.identity.data)
    val (currentGuild, setCurrentGuild) = useState(props.store.state.calendar.selectedGuild)

    useEffectOnce {
        val unsubscribe = props.store.subscribe {
            setUserIdentity(props.store.state.identity.data)
            setCurrentGuild(props.store.state.calendar.selectedGuild)
        }
        cleanup(unsubscribe)
    }

    if (userIdentity == null) {
        div {
            +"Greetings, traveler. You'll have to log in to continue."
        }
    } else {
        div {
            SignUp {
                store = props.store
                profile = userIdentity.profile
            }
            GuildSelector {
                store = props.store
                selectedGuild = currentGuild
            }
            if (userIdentity.profile != null && currentGuild != null) {
                EventCalendar {
                    store = props.store
                    profile = userIdentity.profile
                    guild = currentGuild
                }
            }
        }
    }
}