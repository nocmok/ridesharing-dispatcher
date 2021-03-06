import {Component} from "react";
import classes from "./VehicleDisplayComponent.module.css"
import {Link} from "react-router-dom";

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
                <Link to={"/session/" + this.props.id} style={{color: "#7c7c7c"}}>Подробнее</Link>
            </div>
        </div>)
    }
}