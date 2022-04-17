import React, {Component} from "react";
import {OdintsovoMapTile} from "../../map/objects/OdintsovoMapTile";
import {DragCameraAction} from "../../map/actions/DragCameraAction";
import classes from "./MapScene.module.css";
import {ResizeMapAction} from "../../map/actions/ResizeMapAction";

export class MapScene extends Component {

    constructor(props) {
        super(props);

        this.map = props.di.map;
    }

    componentDidMount = () => {
        this.setupMap()
        this.setupActions()
        this.startAnimationLoop()
    }

    componentWillUnmount() {
    }

    setupMap = () => {
        this.map.resize(window.innerWidth, window.innerHeight)
        this.map.lookAt(55.66971000000001, 37.28309499999999)

        let odintsovo = new OdintsovoMapTile();
        odintsovo.setCoordinates(55.66971000000001, 37.28309499999999)

        this.map.addObject(odintsovo)

        this.mount.appendChild(this.map.renderer.domElement)
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
            <div ref={ref => (this.mount = ref)} className={classes.MapScene}>

            </div>
        )
    }
}