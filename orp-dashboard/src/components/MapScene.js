import React, {Component} from "react";
import {Map} from "../map/Map";
import {OdintsovoMapTile} from "../map/objects/OdintsovoMapTile";
import {ResizeMapAction} from "../map/actions/ResizeMapAction";
import {DragCameraAction} from "../map/actions/DragCameraAction";

export class MapScene extends Component {

    componentDidMount = () => {
        this.setupMap()
        this.setupActions()
        this.startAnimationLoop()
    }

    setupMap = () => {
        this.map = new Map()
        this.map.resize(window.innerWidth, window.innerHeight)
        this.map.lookAt(0, 0)
        this.mount.appendChild(this.map.renderer.domElement)
        this.map.addObject(new OdintsovoMapTile())
    }

    setupActions = () => {
        new ResizeMapAction(this.map)
        new DragCameraAction(this.map)
    }

    startAnimationLoop = () => {
        window.requestAnimationFrame(this.startAnimationLoop);
        this.map.render()
    }

    render() {
        return (
            <div ref={ref => (this.mount = ref)}/>
        )
    }
}