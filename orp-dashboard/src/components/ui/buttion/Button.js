import React, {Component} from "react";
import classes from "./Button.module.css"

export class Button extends Component {

    render() {
        return (
            <button className={classes.Button}
                    style={this.props.style}
                    onClick={this.props.onClick}
                    onFocus={this.props.onFocus}
                    onBlur={this.props.onBlur}
                    hidden={this.props.hidden}>
                {this.props.children}
            </button>
        )
    }
}