import React, {Component} from "react";
import classes from './CreateSessionPanel.module.css'
import '../../../style/Common.css'
import {TextInput} from "../../ui/text_input/TextInput";
import {Button} from "../../ui/buttion/Button";
import * as DriverApi from "../../../api/DriverApi"
import {UpdateSelectedRoadAction} from "./UpdateSelectedRoadAction";
import {ShowSelectedCoordinatesAction} from "./ShowSelectedCoordinatesAction";
import {UpdateSelectedCoordinatesAction} from "./UpdateSelectedCoordinatesAction";
import {ShowSelectedRoadAction} from "./ShowSelectedRoadAction";
import {DragMapAction} from "../../../map/actions/DragMapAction";
import {SessionListener} from "../../../websocket/session/SessionListener";
import {Vehicle} from "../../../map/objects/Vehicle";
import {MapObjectPositionUpdater} from "../../../websocket/session/event_handlers/MapObjectPositionUpdater";
import {ScrollBox} from "../../ui/scrollbox/ScrollBox";
import {BackButton} from "../../ui/back_button/BackButton";

export class CreateSessionPanel extends Component {

    constructor(props) {
        super(props);
        this.di = props.di
        this.map = props.di.map
        this.state = {
            capacity: "2",
            latitude: "",
            longitude: "",
            road: "",
            roadPromise: "",
        }

        this.updateSelectedCoordinatesAction = new UpdateSelectedCoordinatesAction(this)
        this.showSelectedCoordinatesAction = new ShowSelectedCoordinatesAction(this)
        this.updateSelectedRoadAction = new UpdateSelectedRoadAction(this)
        this.showSelectedRoadAction = new ShowSelectedRoadAction(this)

        this.dragMapActionsActivator = new DragMapAction(this.map, {
            activate: () => {
                this.updateSelectedCoordinatesAction.activate()
                this.showSelectedCoordinatesAction.activate()
                this.updateSelectedRoadAction.activate()
                this.showSelectedRoadAction.activate()
            },
            deactivate: () => {
                this.showSelectedCoordinatesAction.deactivate()
                this.updateSelectedCoordinatesAction.deactivate()
                this.showSelectedRoadAction.deactivate()
                this.updateSelectedRoadAction.deactivate()
            },
            onDrag: () => {
                this.updateSelectedCoordinatesAction.updateCoordinates()
                this.showSelectedCoordinatesAction.showCoordinates()
            },
            onDragStopped: () => {
                this.updateSelectedRoadAction.updateRoadSegment()
                this.showSelectedRoadAction.showSelectedRoad()
            }
        })
    }

    componentDidMount() {
        this.dragMapActionsActivator.activate()
    }

    componentWillUnmount() {
        this.dragMapActionsActivator.deactivate()
    }

    parseSession() {
        return {
            capacity: this.state.capacity,
            latitude: this.state.latitude,
            longitude: this.state.longitude,
            road: {
                sourceId: this.state.road.source.id,
                targetId: this.state.road.target.id
            },
            createdAt: new Date(Date.now()).toISOString()
        }
    }

    onCreateSession() {
        let session = this.parseSession()
        DriverApi.createSession({
            capacity: session.capacity,
            coordinates: {
                latitude: session.latitude,
                longitude: session.longitude,
            },
            road: session.road,
            createdAt: session.createdAt
        }).then(response => {

            let mapObject = new Vehicle()
            let sessionListener = new SessionListener(response.createdSession.sessionId)

            this.di.sessions[response.createdSession.sessionId] = {
                mapObject: mapObject,
                sessionListener: sessionListener
            }

            let positionUpdater = new MapObjectPositionUpdater(mapObject)
            sessionListener.addTelemetryEventHandler(telemetry => positionUpdater.handleTelemetry(telemetry))

            mapObject.setCoordinates(response.coordinates.latitude, response.coordinates.longitude)
            this.map.addObject(mapObject)

            sessionListener.connect()
        })
    }


    render() {
        return (
            <div onResize={event => console.log(event)} className={classes.CreateSessionPanel}>
                <div className={classes.Header}>
                    <BackButton></BackButton>
                </div>
                <div className={classes.Header}>
                    <div className={"Heading1 " + classes.Cloud} style={{color: "#7c7c7c"}}>Создать сессию</div>
                </div>

                <ScrollBox style={{
                    height: "80%",
                    width: "100%",
                }}>
                    <div className={classes.MainControls}>

                        <div className={classes.Cloud}>
                            <div className="Heading2" style={{color: "#7c7c7c"}}>Вместимость</div>
                            <TextInput onInput={event => this.setState({capacity: event.target.value})}
                                       value={this.state.capacity}/>
                        </div>

                        <div className={classes.Cloud}>
                            <div className="Heading2" style={{color: "#7c7c7c"}}>Начальные координаты</div>

                            <div className={classes.LabeledInput}>
                                <div className="Heading3">Широта</div>
                                <TextInput onInput={event => this.setState({latitude: event.target.value})}
                                           value={this.state.latitude}></TextInput>
                            </div>

                            <div className={classes.LabeledInput}>
                                <div className="Heading3">Долгота</div>
                                <TextInput onInput={event => this.setState({longitude: event.target.value})}
                                           value={this.state.longitude}></TextInput>
                            </div>

                        </div>

                        <Button onClick={this.onCreateSession.bind(this)}>
                            Создать
                        </Button>

                    </div>
                </ScrollBox>
            </div>
        )
    }
}