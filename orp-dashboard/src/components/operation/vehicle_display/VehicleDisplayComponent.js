import {Component} from "react";
import {TextButton} from "../../ui/text_button/TextButton";
import classes from "./VehicleDisplayComponent.module.css"

export class VehicleDisplayComponent extends Component {

    constructor(props) {
        super(props)
        this.state = {
            title: this.props.title || "Транспортное средство",
            id: 0
        }
    }

    render() {
        return (<div className={classes.VehicleDisplayComponent}>
            <div className="Heading3" style={{
                marginBottom: "10px"
            }}>{this.state.title}</div>
            <div className={classes.KeyValue}>
                <div className="Heading3">ID</div>
                <div>{this.state.coordinates.latitude}</div>
            </div>
            <div className={classes.KeyValue}>
                <img src="/icons/car.svg" alt=""/>
                <TextButton style={{color: "#7c7c7c"}}>Показать на карте</TextButton>
            </div>
        </div>)
    }
}