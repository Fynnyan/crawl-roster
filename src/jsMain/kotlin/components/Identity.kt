package components

import api.updateUserId
import org.codecranachan.roster.Identity
import org.reduxkotlin.Store
import react.*
import react.dom.html.ReactHTML.a
import react.dom.html.ReactHTML.button
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.input
import reducers.ApplicationState

external interface IdentityProps : Props {
    var store: Store<ApplicationState>
}

val Identity = FC<IdentityProps> { props ->
    val (userIdentity, setUserIdentity) = useState<Identity?>(null)

    useEffect {
        val unsubscribe = props.store.subscribe { setUserIdentity(props.store.state.identity.profile) }
        cleanup {
            unsubscribe()
        }
    }

    if (userIdentity == null) {
        div {
            a {
                href = "/auth/login"
                +"Proceed to Login"
            }
        }
    } else {
        div {
            +"Logged in as ${userIdentity.name} - "
            a {
                href = "/auth/logout"
                +"Logout"
            }
        }
    }
}