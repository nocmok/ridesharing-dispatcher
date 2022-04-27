import classes from "./RequestInfoPanel.module.css"
import {Link, useParams} from "react-router-dom";
import * as GodApi from "../../../api/GodApi"
import {useEffect, useState} from "react";
import {Route} from "../../../map/objects/Route";

export function RequestInfoPanel(props) {

    const di = props.di
    const map = di.map
    const {orderId} = useParams()
    let [requestInfo, setRequestInfo] = useState(null)
    let [routeObject, setRouteObject] = useState(null)

    useEffect(() => {
        if (routeObject) {
            map.addObject(routeObject)
        }
    }, [routeObject])

    const cleanup = () => {
        if (routeObject) {
            map.removeObject(routeObject)
        }
    }

    useEffect(() => {
        GodApi.getSessionInfo({
            sessionId: sessionId
        }).then((response) => {
            setSessionInfo(response.sessionInfo)
            routeObject = new Route(response.sessionInfo.routeScheduled.map(node => node.coordinates))
            setRouteObject(routeObject)
        })

        return cleanup
    }, [])

    return (<div className={classes.RequestInfoPanel}>
        <Link to="/dashboard">Назад</Link>
        <div className="Heading1" style={
            {
                color: "#7C7C7C",
                marginTop: "20px",
                marginBottom: "20px"
            }
        }>Сессия #{sessionId}</div>
    </div>)

}