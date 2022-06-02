import classes from "./SessionsPanel.module.css"
import {SessionsTable} from "./SessionsTable";
import {BackButton} from "../../ui/back_button/BackButton";

export function SessionsPanel(props) {

    const di = props.di
    const map = di.map

    return (<div className={classes.Wrapper}>
        <div className={classes.SessionsPanelWrapper}>
            <div className={classes.SessionsPanel}>
                <BackButton></BackButton>
            </div>
        </div>
        <div className={classes.SessionsTableWrapper}>
            <div className={classes.SessionsTable}>
                <SessionsTable di={di}></SessionsTable>
            </div>
        </div>
    </div>)
}