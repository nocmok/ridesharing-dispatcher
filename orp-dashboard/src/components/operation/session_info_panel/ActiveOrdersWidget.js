import classes from "./ActiveOrdersWidget.module.css"
import {ScrollBox} from "../../ui/scrollbox/ScrollBox";
import {useEffect, useState} from "react";
import * as OrderApi from "../../../api/OrderApi"
import {Link} from "react-router-dom";
import {MapPointer} from "../../../map/objects/MapPointer";

export function ActiveOrdersWidget(props) {
    const di = props.di
    const map = di.map
    const sessionId = props.sessionId

    let [activeOrders, setActiveOrders] = useState([])
    let mapPointers = []

    useEffect(() => {
        OrderApi.orders({
            filter: {
                filtering: [
                    {
                        "fieldName": "servingSessionId",
                        "values": [sessionId]
                    },
                    {
                        "fieldName": "completedAt",
                        "values": [null]
                    },
                    {
                        "fieldName": "status",
                        "values": ["ACCEPTED", "PICKUP_PENDING", "SERVING"]
                    },
                ],
                ordering: [
                    {
                        "fieldName": "requestedAt",
                        ascending: true
                    }
                ],
                page: 0,
                pageSize: 2147483647
            }
        }).then(response => {
            setActiveOrders(response.orders)
        })
    }, [])

    useEffect(() => {
        mapPointers.forEach(mapPointer => map.removeObject(mapPointer))
        mapPointers = []
        activeOrders.forEach(order => {
            mapPointers.push(new MapPointer(order.recordedOrigin))
            mapPointers.push(new MapPointer(order.recordedDestination))
        })
        mapPointers.forEach(mapPointer => map.addObject(mapPointer))
        return () => {
            mapPointers.forEach(mapPointer => map.removeObject(mapPointer))
        }
    }, [activeOrders])

    return (<div className={classes.ActiveOrdersWidget}>
        <div className="Heading2" style={{color: "#7c7c7c"}}>Активные заказы</div>
        <div className={classes.ScrollBoxWrapper}>
            <ScrollBox style={{width: "100%", height: "100%" }}>
                <div className={classes.OrdersTable}>
                    {
                        activeOrders.length === 0 ? (<div>Нет активных заказов</div>) : activeOrders.map(order => (
                            <div key={order.requestId} className={classes.OrderInfo}>
                                <img src="/icons/map-pointer.svg" alt=""/>
                                <div>{order.requestId}</div>
                                <Link to={`/order/${order.requestId}`} className={classes.Link}>Подробнее</Link>
                            </div>
                        ))
                    }
                </div>
            </ScrollBox>
        </div>
    </div>)
}