import React, {Component} from "react";

export class Separator extends Component {

    render() {
        return (
            <hr style={
                {
                    width: "100%",
                    height: "1px",
                    borderStyle: "none",
                    backgroundColor: "#CBCBCB",
                }
            }></hr>
        )
    }

}