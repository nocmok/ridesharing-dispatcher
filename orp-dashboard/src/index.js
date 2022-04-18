import React from 'react';
import ReactDOM from 'react-dom/client';
import './index.css';
import {App} from './App';
import 'typeface-roboto'
import {Map} from "./map/Map";
import {BrowserRouter, Router} from "react-router-dom";

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

const root = ReactDOM.createRoot(document.getElementById('root'));

root.render(
    <BrowserRouter>
        <App di={di}/>
    </BrowserRouter>
);
