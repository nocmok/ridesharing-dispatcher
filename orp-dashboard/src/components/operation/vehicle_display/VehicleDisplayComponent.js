import {Component} from "react";
import {TextButton} from "../../ui/text_button/TextButton";
import classes from "./VehicleDisplayComponent.module.css"

export class VehicleDisplayComponent extends Component {

    constructor(props) {
        super(props)
    }

    render() {
        return (<div className={classes.VehicleDisplayComponent}>
            <div className="Heading3" style={{
                marginBottom: "10px"
            }}>{this.props.title}</div>
            <div className={classes.KeyValue}>
                <div className="Heading3">ID</div>
                <div>{this.props.id}</div>
            </div>
            <div className={classes.KeyValue}>
                <img src="/icons/car.svg" alt=""/>
                <TextButton style={{color: "#7c7c7c"}}>Подробнее</TextButton>
            </div>
        </div>)
    }
}