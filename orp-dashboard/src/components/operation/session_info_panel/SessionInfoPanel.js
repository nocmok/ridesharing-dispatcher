import classes from "./SessionInfoPanel.module.css"
import {Link, useParams} from "react-router-dom";
import {BackButton} from "../../ui/back_button/BackButton";
import {ActiveOrdersWidget} from "./ActiveOrdersWidget";
import {SessionInfoWidget} from "./SessionInfoWidget";
import {SessionRouteWidget} from "./SessionRouteWidget";


export function SessionInfoPanel(props) {
    const di = props.di

    const {sessionId} = useParams()

    return (<div className={classes.SessionInfoPanel}>

        <div className={classes.Header}>
            <BackButton></BackButton>
        </div>
        <div className={classes.Header}>
            <div className={classes.Cloud}>
                <div className="Heading1" style={{color: "#7C7C7C"}}>Сессия #{sessionId}</div>
            </div>
        </div>
        <div className={classes.MainInfo}>
            <SessionInfoWidget di={di} sessionId={sessionId}></SessionInfoWidget>
            <ActiveOrdersWidget di={di} sessionId={sessionId}></ActiveOrdersWidget>
            <SessionRouteWidget di={di} sessionId={sessionId}></SessionRouteWidget>
        </div>
    </div>)
}
