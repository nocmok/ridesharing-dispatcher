import './App.css';
import {MapScene} from "./components/map/MapScene";
import {CreateSessionPanel} from "./components/operation/create_session_panel/CreateSessionPanel";
import './App.css'
import './style/Common.css'
import React, {Component} from "react";
import {CreateRequestPanel} from "./components/operation/create_request_panel/CreateRequestPanel";

export class App extends Component {

    render() {
        return (
            <div className="App">
                {/*<CreateSessionPanel di={this.props.di}></CreateSessionPanel>*/}
                <CreateRequestPanel di={this.props.di}></CreateRequestPanel>
                <MapScene di={this.props.di}></MapScene>
            </div>
        )
    }
}