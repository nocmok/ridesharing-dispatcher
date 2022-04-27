import classes from "./SessionInfoPanel.module.css"
import {Link, useParams} from "react-router-dom";
import {useEffect, useState} from "react";
import * as GodApi from "../../../api/GodApi";
import {KeyValueComponent} from "../key_value/KeyValueComponent";
import {Route} from "../../../map/objects/Route";


export function SessionInfoPanel(props) {
    const di = props.di
    const map = di.map

    const {sessionId} = useParams()
    let [sessionInfo, setSessionInfo] = useState({})
    let [routeObject, setRouteObject] = useState(null)

    useEffect(() => {
        GodApi.getSessionInfo({
            sessionId: sessionId
        }).then((response) => {
            setSessionInfo(response.sessionInfo)
        })
    }, [])

    useEffect(() => {
        if (!sessionInfo || !sessionInfo.routeScheduled) {
            return
        }
        if (routeObject) {
            map.removeObject(routeObject)
        }
        if (routeObject) {
            map.removeObject(routeObject)
        }
        setRouteObject(new Route(sessionInfo.routeScheduled.map(routeNode => routeNode.coordinates)))
    }, [sessionInfo])

    useEffect(() => {
        if (routeObject) {
            map.addObject(routeObject)
        }
        return () => {
            if (routeObject) {
                map.removeObject(routeObject)
            }
        }
    }, [routeObject])

    return (<div className={classes.SessionInfoPanel}>
        <Link to={-1}>Назад</Link>
        <div className="Heading1" style={
            {
                color: "#7C7C7C",
                marginTop: "20px",
                marginBottom: "20px"
            }
        }>Сессия #{sessionId}</div>

        <KeyValueComponent title="Вместимость" value={sessionInfo.capacity}></KeyValueComponent>
        <KeyValueComponent title="Остаточная вместимость" value={sessionInfo.residualCapacity}></KeyValueComponent>
        <KeyValueComponent title="Статус" value={sessionInfo.sessionStatus}></KeyValueComponent>
        <KeyValueComponent title="Время создания" value={sessionInfo.createdAt}></KeyValueComponent>
        <KeyValueComponent title="Время завершения" value={sessionInfo.completedAt}></KeyValueComponent>
    </div>)
}
