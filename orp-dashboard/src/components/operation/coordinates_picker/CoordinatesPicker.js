import {Component} from "react";
import {TextInput} from "../../ui/text_input/TextInput";
import {Button} from "../../ui/buttion/Button";
import {ShowSelectedCoordinatesAction} from "./ShowSelectedCoordinatesAction";
import {DragMapAction} from "../../../map/actions/DragMapAction";
import {UpdateSelectedCoordinatesAction} from "./UpdateSelectedCoordinatesAction";
import {ShowSelectedRoadAction} from "./ShowSelectedRoadAction";
import {UpdateSelectedRoadAction} from "./UpdateSelectedRoadAction";

export class CoordinatesPicker extends Component {

    constructor(props) {
        super(props);

        this.di = props.di
        this.map = props.di.map

        this.coordinates = null

        this.state = {
            coordinates: {
                latitude: "",
                longitude: "",
            },
            isActive: false,
            isPickButtonHidden: false,
            isSaveButtonHidden: true
        }

        this.updateSelectedCoordinatesAction = new UpdateSelectedCoordinatesAction(this);
        this.showSelectedCoordinatesAction = new ShowSelectedCoordinatesAction(this);
        this.showSelectedRoadAction = new ShowSelectedRoadAction(this)
        this.updateSelectedRoadAction = new UpdateSelectedRoadAction(this)

        this.dragMapAction = new DragMapAction(this.map, {
            activate: () => {
                this.updateSelectedCoordinatesAction.activate()
                this.showSelectedCoordinatesAction.activate()
                this.updateSelectedRoadAction.activate()
                this.showSelectedRoadAction.activate()
            },
            deactivate: () => {
                this.updateSelectedCoordinatesAction.deactivate()
                this.showSelectedCoordinatesAction.deactivate()
                this.updateSelectedRoadAction.deactivate()
                this.showSelectedRoadAction.deactivate()
            },
            onDrag: () => {
                if (!this.state.isActive) {
                    return
                }
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
        this.dragMapAction.activate()
    }

    componentWillUnmount() {
        this.dragMapAction.deactivate()
    }

    onPickCoordinate() {
        this.setState({
            isActive: !this.state.isActive,
            isPickButtonHidden: true,
            isSaveButtonHidden: false
        })

        if (this.coordinates) {
            this.map.lookAt(this.coordinates.latitude, this.coordinates.longitude)
        } else {
            this.updateSelectedCoordinatesAction.updateCoordinates()
            this.showSelectedCoordinatesAction.showCoordinates()
        }
    }

    onSaveCoordinate() {
        this.setState({
            isActive: !this.state.isActive,
            isPickButtonHidden: false,
            isSaveButtonHidden: true
        })
        if (this.props.onSaveCoordinate) {
            this.props.onSaveCoordinate({
                latitude: this.coordinates.latitude,
                longitude: this.coordinates.longitude
            })
        }
    }

    render() {
        return (
            <div>
                <div style={
                    {
                        display: "flex",
                        flexDirection: "column",
                        gap: "10px"
                    }
                }>
                    <div style={
                        {
                            display: "flex",
                            flexDirection: "column",
                            gap: "10px",
                        }
                    }>
                        <div className="Heading3">????????????</div>
                        <TextInput value={this.state.coordinates.latitude} onChange={() => {
                        }}></TextInput>
                    </div>


                    <div style={
                        {
                            display: "flex",
                            flexDirection: "column",
                            gap: "10px"
                        }
                    }>
                        <div className="Heading3">??????????????</div>
                        <TextInput value={this.state.coordinates.longitude} onChange={() => {
                        }}></TextInput>
                    </div>

                    <div style={{display: "flex", width: "100%", flexDirection: "column", alignItems: "center", marginTop: "10px"}}>
                        < Button style=
                                     {
                                         {
                                             backgroundColor: "#1B72E8", color: "#ffffff"
                                         }
                                     }
                                 hidden={this.state.isPickButtonHidden}
                                 onClick={this.onPickCoordinate.bind(this)}>
                            ?????????????? ???? ??????????
                        </Button>


                        <Button style=
                                    {
                                        {
                                            backgroundColor: "#1B72E8", color: "#ffffff"
                                        }
                                    }
                                hidden={this.state.isSaveButtonHidden}
                                onClick={this.onSaveCoordinate.bind(this)}>
                            ??????????????????
                        </Button>
                    </div>

                </div>
            </div>
        );
    }
}