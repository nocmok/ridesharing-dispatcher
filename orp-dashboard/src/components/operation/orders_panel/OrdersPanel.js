import classes from "./OrdersPanel.module.css"
import {OrdersTable} from "./OrdersTable";
import {Link} from "react-router-dom";
import {BackButton} from "../../ui/back_button/BackButton";

export function OrdersPanel(props) {

    const di = props.di
    const map = di.map

    return (<div className={classes.Wrapper}>
        <div className={classes.OrdersPanelWrapper}>
            <div className={classes.OrdersPanel}>
                <BackButton></BackButton>
            </div>
        </div>
        <div className={classes.OrdersTableWrapper}>
            <div className={classes.OrdersTable}>
                <OrdersTable></OrdersTable>
            </div>
        </div>
    </div>)
}