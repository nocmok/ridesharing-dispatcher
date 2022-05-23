import classes from "./OrdersPanel.module.css"
import {OrdersTable} from "./OrdersTable";

export function OrdersPanel(props) {

    const di = props.di
    const map = di.map

    return (<div className={classes.Wrapper}>
        <div className={classes.OrdersTableWrapper}>
            <OrdersTable></OrdersTable>
        </div>
    </div>)
}