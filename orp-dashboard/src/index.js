import React from 'react';
import ReactDOM from 'react-dom/client';
import './index.css';
import {App} from './App';
import 'typeface-roboto'
import {Map} from "./map/Map";

// global dependency container
const di = {};
di.map = new Map()
di.sessions = {}

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
    <App di={di}/>
);
