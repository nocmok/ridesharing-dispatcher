import classes from "./CoordinateDisplayComponent.module.css"
import {KeyValueComponent} from "../key_value/KeyValueComponent";
import {Component} from "react";
import {TextButton} from "../../ui/text_button/TextButton";
import {MapPointer} from "../../../map/objects/MapPointer";

export class CoordinatesDisplayComponent extends Component {

    constructor(props) {
        super(props);
        this.map = this.props.di.map
        this.mapPointer = new MapPointer()
    }

    componentDidMount() {
        this.map.removeObject(this.mapPointer)
        this.mapPointer.setCoordinates(this.props.coordinates.latitude, this.props.coordinates.longitude)
        this.map.addObject(this.mapPointer)
    }

    componentDidUpdate() {
        this.map.removeObject(this.mapPointer)
        this.mapPointer.setCoordinates(this.props.coordinates.latitude, this.props.coordinates.longitude)
        this.map.addObject(this.mapPointer)
    }

    componentWillUnmount() {
        this.map.removeObject(this.mapPointer)
    }

    showCoordinates() {
        this.map.lookAt(this.props.coordinates.latitude, this.props.coordinates.longitude)
    }

    render() {
        return (<div className={classes.CoordinateDisplayComponent}>
            <div className="Heading3" style={{marginBottom: "10px"}}>{this.props.title}</div>
            <KeyValueComponent title="Широта" value={this.props.coordinates.latitude}></KeyValueComponent>
            <KeyValueComponent title="Долгота" value={this.props.coordinates.longitude}></KeyValueComponent>
            <KeyValueComponent
                title={<img src="/icons/flag.svg"/>}
                value={<TextButton style={{color: "#7c7c7c"}} onClick={this.showCoordinates.bind(this)}>Показать на карте</TextButton>}>
            </KeyValueComponent>
        </div>)
    }
}