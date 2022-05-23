import React, {Component} from "react";
import classes from './CreateSessionPanel.module.css'
import '../../../style/Common.css'
import {TextInput} from "../../ui/text_input/TextInput";
import {Button} from "../../ui/buttion/Button";
import {Separator} from "../../ui/separator/Separator";
import * as DriverApi from "../../../api/DriverApi"
import {UpdateSelectedRoadAction} from "./UpdateSelectedRoadAction";
import {ShowSelectedCoordinatesAction} from "./ShowSelectedCoordinatesAction";
import {UpdateSelectedCoordinatesAction} from "./UpdateSelectedCoordinatesAction";
import {ShowSelectedRoadAction} from "./ShowSelectedRoadAction";
import {DragMapAction} from "../../../map/actions/DragMapAction";
import {SessionListener} from "../../../websocket/session/SessionListener";
import {Vehicle} from "../../../map/objects/Vehicle";
import {MapObjectPositionUpdater} from "../../../websocket/session/event_handlers/MapObjectPositionUpdater";
import {Link} from "react-router-dom";
import {ScrollBox} from "../../ui/scrollbox/ScrollBox";

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
            let sessionListener = new SessionListener(response.sessionId)

            this.di.sessions[response.sessionId] = {
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
            <div className={classes.CreateSessionPanel}>
                <Link to="/dashboard">Назад</Link>

                <div className="Heading1" style={
                    {
                        color: "#7C7C7C",
                        marginTop: "20px",
                        marginBottom: "20px"
                    }
                }>Создать сессию
                </div>

                <ScrollBox height="80%">
                    <div style={{
                        display: "flex",
                        flexDirection: "column",
                        gap: "20px"
                    }}>
                        <div style={
                            {
                                display: "flex",
                                flexDirection: "column",
                                gap: "10px"
                            }}>
                            <div className="Heading3">Вместимость</div>
                            <TextInput onInput={event => this.setState({capacity: event.target.value})}
                                       value={this.state.capacity}/>
                        </div>

                        <Separator></Separator>

                        <div style={
                            {
                                display: "flex",
                                flexDirection: "column",
                                gap: "20px"
                            }
                        }>
                            <div style={
                                {
                                    display: "flex",
                                    flexDirection: "column",
                                    gap: "10px",
                                }
                            }>
                                <div className="Heading3">Широта</div>
                                <TextInput onInput={event => this.setState({latitude: event.target.value})}
                                           value={this.state.latitude}></TextInput>
                            </div>


                            <div style={
                                {
                                    display: "flex",
                                    flexDirection: "column",
                                    gap: "10px"
                                }
                            }>
                                <div className="Heading3">Долгота</div>
                                <TextInput onInput={event => this.setState({longitude: event.target.value})}
                                           value={this.state.longitude}></TextInput>
                            </div>

                        </div>

                        <Separator></Separator>

                        <Button style=
                                    {
                                        {
                                            backgroundColor: "#1B72E8", color: "#ffffff"
                                        }
                                    }
                                onClick={this.onCreateSession.bind(this)}>
                            Создать
                        </Button>
                    </div>
                </ScrollBox>
            </div>
        )
    }
}