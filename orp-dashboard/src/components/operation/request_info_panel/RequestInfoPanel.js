import classes from "./RequestInfoPanel.module.css"
import {Link, useParams} from "react-router-dom";
import * as GodApi from "../../../api/GodApi"
import {useEffect, useState} from "react";
import {CoordinatesDisplayComponent} from "../coordinates_display/CoordinatesDisplayComponent";
import {KeyValueComponent} from "../key_value/KeyValueComponent";
import {VehicleDisplayComponent} from "../vehicle_display/VehicleDisplayComponent";
import {OrderListener} from "../../../websocket/order/OrderListener";
import {BackButton} from "../../ui/back_button/BackButton";
import {ScrollBox} from "../../ui/scrollbox/ScrollBox";

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
        <div className={classes.Header}>
            <BackButton></BackButton>
        </div>
        <div className={classes.Header}>
            <div className={"Heading1 " + classes.Cloud} style={{color: "#7C7C7C"}}>Заказ #{orderId}</div>
        </div>

        <ScrollBox style={{height: "80%", width: "100%"}}>
            <div className={classes.MainControls}>
                <div className={classes.Cloud}>
                    <CoordinatesDisplayComponent di={di} title="Посадка"
                                                 coordinates={requestInfo.recordedOrigin}></CoordinatesDisplayComponent>
                </div>

                <div className={classes.Cloud}>
                    <CoordinatesDisplayComponent di={di} title="Высадка"
                                                 coordinates={requestInfo.recordedDestination}></CoordinatesDisplayComponent>
                </div>

                <div className={classes.Cloud}>
                    <KeyValueComponent title="Пассажиры" value={requestInfo.load}></KeyValueComponent>
                    <KeyValueComponent title="Ограничение сервиса"
                                       value={requestInfo.detourConstraint}></KeyValueComponent>
                    <KeyValueComponent title="Время ожидания (сек)"
                                       value={requestInfo.maxPickupDelaySeconds}></KeyValueComponent>
                    <KeyValueComponent title="Время отправки" value={requestInfo.requestedAt}></KeyValueComponent>
                    <KeyValueComponent title="Статус выполнения" value={requestInfo.status}></KeyValueComponent>
                </div>

                <div className={classes.Cloud}>
                    <VehicleDisplayComponent di={di} title="Исполнитель"
                                             id={requestInfo.servingSessionId}></VehicleDisplayComponent>
                </div>
            </div>
        </ScrollBox>
    </div>)

}