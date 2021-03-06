import {Component} from "react";
import classes from "./CreateRequestPanel.module.css"
import {TextButton} from "../../ui/text_button/TextButton";
import {TextInput} from "../../ui/text_input/TextInput";
import {Separator} from "../../ui/separator/Separator";
import {Button} from "../../ui/buttion/Button";
import {CoordinatesPicker} from "../coordinates_picker/CoordinatesPicker";
import * as GeoApi from '../../../api/GeoApi'
import * as RiderApi from '../../../api/RiderApi'
import {Link, useNavigate} from "react-router-dom";
import {ScrollBox} from "../../ui/scrollbox/ScrollBox";
import {BackButton} from "../../ui/back_button/BackButton";

export function CreateRequestPanel(props) {
    const navigation = useNavigate();
    return <CreateRequestPanelInternal {...props} navigation={navigation}></CreateRequestPanelInternal>
}

class CreateRequestPanelInternal extends Component {

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
                    requestedAt: new Date(Date.now()).toISOString(),
                    load: this.request.load
                }
            })
    }

    onCreateRequest() {
        this.parseRequest()
            .then(request => RiderApi.createRequest(request))
            .then(response => {
                console.log(response);
                return response
            })
            .then((response) => this.props.navigation("/order/" + response.requestId))
    }

    render() {
        return (
            <div className={classes.CreateRequestPanel}>
                <div className={classes.Header}>
                    <BackButton></BackButton>
                </div>
                <div className={classes.Header}>
                    <div className={"Heading1 " + classes.Cloud} style={{color: "#7c7c7c"}}>?????????????? ??????????</div>
                </div>

                <ScrollBox style={{
                    height: "80%",
                    width: "100%",
                }}>
                    <div className={classes.MainControls}>

                        <div className={classes.Cloud}>
                            <div className="Heading2" style={{color: "#7C7C7C"}}>??????????????</div>
                            <CoordinatesPicker di={this.props.di}
                                               onSaveCoordinate={latLon => this.request.origin = latLon}></CoordinatesPicker>
                        </div>

                        <div className={classes.Cloud}>
                            <div className="Heading2" style={{color: "#7C7C7C"}}>??????????????</div>
                            <CoordinatesPicker di={this.props.di}
                                               onSaveCoordinate={latLon => this.request.destination = latLon}></CoordinatesPicker>
                        </div>

                        <div className={classes.Cloud}>
                            <div className="Heading2">???????????????????? ??????????????</div>
                            <TextInput value={this.state.request.load}
                                       onInput={event => {
                                           this.setState({request: {load: event.target.value}})
                                           this.request.load = event.target.value
                                       }}/>
                        </div>

                        <div className={classes.Cloud}>
                            <div className="Heading2">?????????? ????????????????</div>
                            <TextInput value={this.state.request.maxPickupDelaySeconds}
                                       onInput={event => {
                                           this.setState({request: {maxPickupDelaySeconds: event.target.value}})
                                           this.request.maxPickupDelaySeconds = event.target.value
                                       }}/>
                        </div>

                        <div className={classes.Cloud}>
                            <div className="Heading2">??????????????????????</div>
                            <TextInput value={this.state.request.detourConstraint}
                                       onInput={event => {
                                           this.setState({request: {detourConstraint: event.target.value}})
                                           this.request.detourConstraint = event.target.value
                                       }}/>
                        </div>

                        <Button onClick={this.onCreateRequest.bind(this)}>
                            ??????????????
                        </Button>

                    </div>
                </ScrollBox>
            </div>
        );
    }
}