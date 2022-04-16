import React, {Component} from "react";
import classes from "./TextInput.module.css"

export class TextInput extends Component {

    render() {
        return (
            <input className={classes.TextInput}
                   value={this.props.value}
                   style={this.props.style}
                   onInput={this.props.onInput}>
                {this.props.children}
            </input>
        )
    }
}