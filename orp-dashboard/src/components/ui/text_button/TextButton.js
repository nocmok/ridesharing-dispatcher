import React, {Component} from "react";
import classes from "./TextButton.module.css"

export class TextButton extends Component {

    render() {
        return (
            <button className={classes.TextButton} style={this.props.style} onClick={this.props.onClick}>
                {this.props.children}
            </button>
        )
    }
}