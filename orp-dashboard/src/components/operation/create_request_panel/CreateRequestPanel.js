import {Component} from "react";
import classes from "./CreateRequestPanel.module.css"
import {TextButton} from "../../ui/text_button/TextButton";
import {TextInput} from "../../ui/text_input/TextInput";
import {Separator} from "../../ui/separator/Separator";
import {Button} from "../../ui/buttion/Button";
import {CoordinatesPicker} from "../coordinates_picker/CoordinatesPicker";
import * as GeoApi from '../../../api/GeoApi'
import * as RiderApi from '../../../api/RiderApi'
import {Link} from "react-router-dom";


export class CreateRequestPanel extends Component {

    constructor(props) {
        super(props);
        this.di = props.di

        this.state = {
            request: {
                load: 1,
                maxPickupDelaySeconds: 300,
                detourConstraint: 1.3,
                origin: {},
                destination: {},
                pickupRoadSegment: {},
                dropoffRoadSegment: {},
            }
        }

        this.request = {
            load: 1,
            maxPickupDelaySeconds: 300,
            detourConstraint: 1.3,
            origin: {},
            destination: {},
            pickupRoadSegment: {},
            dropoffRoadSegment: {},
        }
    }

    parseRequest() {
        let pickupRoadPromise = GeoApi.getRoadSegmentByLatLon({
            coordinates: this.request.origin,
            rightHandTraffic: true
        })
        let dropoffRoadPromise = GeoApi.getRoadSegmentByLatLon({
            coordinates: this.request.destination,
            rightHandTraffic: true
        })
        return Promise.allSettled([pickupRoadPromise, dropoffRoadPromise])
            .then(segments => {
                return {
                    recordedOrigin: this.request.origin,
                    recordedDestination: this.request.destination,
                    pickupRoadSegment: {
                        sourceId: segments[0].value.road.source.id,
                        targetId: segments[0].value.road.target.id
                    },
                    dropoffRoadSegment: {
                        sourceId: segments[1].value.road.source.id,
                        targetId: segments[1].value.road.target.id
                    },
                    detourConstraint: this.request.detourConstraint,
                    maxPickupDelaySeconds: this.request.maxPickupDelaySeconds,
                    requestedAt: "2022-04-18T14:36:14.324Z",
                    load: this.request.load
                }
            })
    }

    onCreateRequest() {
        this.parseRequest()
            .then(request => RiderApi.createRequest(request))
            .then(response => console.log(response))
    }

    render() {
        return (
            <div className={classes.CreateRequestPanel}>

                {/*<TextButton style={*/}
                {/*    {*/}
                {/*        color: "#7C7C7C"*/}
                {/*    }*/}
                {/*}>Назад</TextButton>*/}

                <Link to="/dashboard">Назад</Link>

                <div className="Heading1" style={
                    {
                        color: "#7C7C7C",
                        marginTop: "20px",
                        marginBottom: "20px"
                    }
                }>Создать запрос
                </div>

                <div className={classes.ScrollBox}>

                    <div className="Heading3" style={{color: "#7C7C7C"}}>Посадка</div>
                    <CoordinatesPicker di={this.props.di}
                                       onSaveCoordinate={latLon => this.request.origin = latLon}></CoordinatesPicker>

                    <Separator></Separator>

                    <div className="Heading3" style={{color: "#7C7C7C"}}>Высадка</div>
                    <CoordinatesPicker di={this.props.di}
                                       onSaveCoordinate={latLon => this.request.destination = latLon}></CoordinatesPicker>

                    <Separator></Separator>

                    <div style={
                        {
                            display: "flex",
                            flexDirection: "column",
                            gap: "10px"
                        }}>
                        <div className="Heading3">Количество человек</div>
                        <TextInput value={this.state.request.load}
                                   onInput={event => {
                                       this.setState({request: {load: event.target.value}})
                                       this.request.load = event.target.value
                                   }}/>
                    </div>

                    <div style={
                        {
                            display: "flex",
                            flexDirection: "column",
                            gap: "10px"
                        }}>
                        <div className="Heading3">Время ожидания</div>
                        <TextInput value={this.state.request.maxPickupDelaySeconds}
                                   onInput={event => {
                                       this.setState({request: {maxPickupDelaySeconds: event.target.value}})
                                       this.request.maxPickupDelaySeconds = event.target.value
                                   }}/>
                    </div>

                    <div style={
                        {
                            display: "flex",
                            flexDirection: "column",
                            gap: "10px"
                        }}>
                        <div className="Heading3">Ограничение</div>
                        <TextInput value={this.state.request.detourConstraint}
                                   onInput={event => {
                                       this.setState({request: {detourConstraint: event.target.value}})
                                       this.request.detourConstraint = event.target.value
                                   }}/>
                    </div>

                    <Button style=
                                {
                                    {
                                        backgroundColor: "#1B72E8", color: "#ffffff"
                                    }
                                }
                            onClick={this.onCreateRequest.bind(this)}>
                        Создать
                    </Button>

                </div>
            </div>
        );
    }
}