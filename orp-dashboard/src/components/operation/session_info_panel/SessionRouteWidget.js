import classes from "./SessionRouteWidget.module.css"
import {useEffect, useState} from "react";
import {Route} from "../../../map/objects/Route";
import * as SessionApi from "../../../api/SessionApi";

export function SessionRouteWidget(props) {
    const di = props.di
    const map = di.map
    const sessionId = props.sessionId

    let routeObject = null
    let [sessionRoute, setSessionRoute] = useState([])

    useEffect(() => {
        SessionApi.route({
            sessionId: sessionId
        }).then(response => {
            setSessionRoute(response.route)
        })
    }, [])

    useEffect(() => {
        if (routeObject) {
            map.removeObject(routeObject)
        }
        routeObject = new Route(sessionRoute.map(node => node.coordinates))
        map.addObject(routeObject)
        return () => {
            if (routeObject) {
                map.removeObject(routeObject)
            }
        }
    }, [sessionRoute])

    return (<div className={classes.SessionRouteWidget}></div>)
}