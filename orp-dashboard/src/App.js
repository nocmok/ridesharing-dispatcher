import './App.css';
import {MapScene} from "./components/map/MapScene";
import {CreateSessionPanel} from "./components/operation/create_session_panel/CreateSessionPanel";
import './App.css'
import './style/Common.css'
import React, {Component, Fragment} from "react";
import {CreateRequestPanel} from "./components/operation/create_request_panel/CreateRequestPanel";
import {OperationPanel} from "./components/operation/operation_panel/OperationPanel";
import {Route, Routes, Navigate} from "react-router-dom";
import {RequestInfoPanel} from "./components/operation/request_info_panel/RequestInfoPanel";
import {SessionInfoPanel} from "./components/operation/session_info_panel/SessionInfoPanel";
import {OrdersPanel} from "./components/operation/orders_panel/OrdersPanel";
import {SessionsPanel} from "./components/operation/sessions_panel/SessionsPanel";

export class App extends Component {

    render() {
        return (
            <div className="App">

                <MapScene di={this.props.di}></MapScene>
                <div className="Modal">
                    <div className="UI">
                        <Routes>
                            <Route path={"/dashboard"} element={<OperationPanel di={this.props.di}></OperationPanel>}/>

                            <Route path={"/sessions/create"}
                                   element={<CreateSessionPanel di={this.props.di}></CreateSessionPanel>}/>

                            <Route path={"/requests/create"}
                                   element={<CreateRequestPanel di={this.props.di}></CreateRequestPanel>}/>

                            <Route path={"/orders"}
                                   element={<OrdersPanel di={this.props.di}></OrdersPanel>}/>

                            <Route path={"/sessions"}
                                   element={<SessionsPanel di={this.props.di}></SessionsPanel>}/>

                            <Route path="*" element={<Navigate to="/dashboard"/>}/>

                            <Route path="/order/:orderId"
                                   element={<RequestInfoPanel di={this.props.di}></RequestInfoPanel>}/>
                            <Route path="/session/:sessionId"
                                   element={<SessionInfoPanel di={this.props.di}></SessionInfoPanel>}/>
                        </Routes>
                    </div>
                </div>
            </div>
        )
    }
}