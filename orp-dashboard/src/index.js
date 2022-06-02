import React from 'react';
import ReactDOM from 'react-dom/client';
import './index.css';
import {App} from './App';
import 'typeface-roboto'
import {Map} from "./map/Map";
import {BrowserRouter, Router} from "react-router-dom";
import {Bootstrapper} from "./Bootstrapper";
import {BootstrapperComponent} from "./BootstrapperComponent";
import {SessionRegistry} from "./services/SessionRegistry";

// global dependency container
const di = {};

di.map = new Map()

/**
 * {
 *     sessionId: {
 *         sessionListener: ,
 *         mapObject: ,
 *
 *     }
 * }
 */
di.sessions = {}
di.sessionListeners = {}
di.sessionRegistry = new SessionRegistry(di)

const root = ReactDOM.createRoot(document.getElementById('root'));

root.render(
    <BrowserRouter>
        <BootstrapperComponent di={di}/>
    </BrowserRouter>
);
