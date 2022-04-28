import classes from "./RequestInfoPanel.module.css"
import {Link, useParams} from "react-router-dom";
import * as GodApi from "../../../api/GodApi"
import {useEffect, useState} from "react";
import {CoordinatesDisplayComponent} from "../coordinates_display/CoordinatesDisplayComponent";
import {KeyValueComponent} from "../key_value/KeyValueComponent";
import {VehicleDisplayComponent} from "../vehicle_display/VehicleDisplayComponent";
import {OrderListener} from "../../../websocket/order/OrderListener";

export function RequestInfoPanel(props) {

    const di = props.di
    const map = di.map

    const {orderId} = useParams()

    let [requestInfo, setRequestInfo] = useState({
        recordedOrigin: {},
        recordedDestination: {}
    })

    useEffect(() => {
        const notificationListener = new OrderListener(orderId);

        notificationListener.addOrderStatusUpdatedHandler(() => {
            GodApi.getRequestInfo({
                requestId: orderId
            }).then((response) => {
                setRequestInfo(response.requestInfo)
            })
        })
        notificationListener.connect()

        GodApi.getRequestInfo({
            requestId: orderId
        }).then((response) => {
            setRequestInfo(response.requestInfo)
        })

        return () => {
            notificationListener.disconnect()
        }
    }, [])

    return (<div className={classes.RequestInfoPanel}>
        <Link to={-1}>Назад</Link>
        <div className="Heading1" style={
            {
                color: "#7C7C7C",
                marginTop: "20px",
                marginBottom: "20px"
            }
        }>Заказ #{orderId}</div>

        <CoordinatesDisplayComponent di={di} title="Посадка"
                                     coordinates={requestInfo.recordedOrigin}></CoordinatesDisplayComponent>
        <CoordinatesDisplayComponent di={di} title="Высадка"
                                     coordinates={requestInfo.recordedDestination}></CoordinatesDisplayComponent>
        <KeyValueComponent title="Пассажиры" value={requestInfo.load}></KeyValueComponent>
        <KeyValueComponent title="Ограничение сервиса" value={requestInfo.detourConstraint}></KeyValueComponent>
        <KeyValueComponent title="Время ожидания (сек)" value={requestInfo.maxPickupDelaySeconds}></KeyValueComponent>
        <KeyValueComponent title="Время отправки" value={requestInfo.requestedAt}></KeyValueComponent>
        <KeyValueComponent title="Статус выполнения" value={requestInfo.status}></KeyValueComponent>
        <VehicleDisplayComponent di={di} title="Исполнитель"
                                 id={requestInfo.servingSessionId}></VehicleDisplayComponent>

    </div>)

}