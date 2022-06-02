import classes from "./SessionInfoWidget.module.css"
import {KeyValueComponent} from "../key_value/KeyValueComponent";
import * as SessionApi from "../../../api/SessionApi";
import {useEffect, useState} from "react";

export function SessionInfoWidget(props) {

    const sessionId = props.sessionId
    let [sessionInfo, setSessionInfo] = useState({})

    useEffect(() => {
        SessionApi.sessions({
            filter: {
                filtering: [{
                    fieldName: "sessionId",
                    values: [sessionId]
                }],
                page: 0,
                pageSize: 1
            }
        }).then((response) => {
            setSessionInfo(response?.sessions ? (response.sessions.length > 0 ? response.sessions[0] : {}) : {})
        })
    }, [])

    return (<div className={classes.SessionInfoWidget}>
        <div className="Heading2" style={{color: "#7c7c7c"}}>Основная информация</div>
        <KeyValueComponent title="Вместимость" value={sessionInfo.capacity}></KeyValueComponent>
        <KeyValueComponent title="Остаточная вместимость"
                           value={sessionInfo.residualCapacity}></KeyValueComponent>
        <KeyValueComponent title="Статус" value={sessionInfo.status}></KeyValueComponent>
        <KeyValueComponent title="Время создания" value={sessionInfo.startedAt}></KeyValueComponent>
        <KeyValueComponent title="Время завершения" value={sessionInfo.terminatedAt ? sessionInfo.terminatedAt : "не завершена (активна)"}></KeyValueComponent>
    </div>)
}