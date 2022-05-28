import {Component} from "react";
import classes from "./OperationPanel.module.css"
import {DemoWidget} from "./DemoWidget";
import {AdminWidget} from "./AdminWidget";
import {BackButton} from "../../ui/back_button/BackButton";

export class OperationPanel extends Component {

    render() {
        return (
            <div className={classes.OperationPanel}>
                <BackButton style={{visibility: "hidden"}}></BackButton>
                <div className={"Heading1 " + classes.Title} style={{color: "#7c7c7c"}}>
                    Панель управления
                </div>
                <DemoWidget></DemoWidget>
                <AdminWidget></AdminWidget>
            </div>)
    }
}