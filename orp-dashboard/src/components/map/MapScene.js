import React, {Component} from "react";
import {DragCameraAction} from "../../map/actions/DragCameraAction";
import classes from "./MapScene.module.css";
import {ResizeMapAction} from "../../map/actions/ResizeMapAction";
import {TWEEN} from "three/examples/jsm/libs/tween.module.min";
import {OdintsovoLargeMapTile} from "../../map/objects/OdintsovoLargeMapTile";

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
        this.map.lookAt(55.6704, 37.281649999999985)
        let odintsovoLarge = new OdintsovoLargeMapTile();
        odintsovoLarge.setCoordinates(55.6704, 37.281649999999985)
        this.map.addObject(odintsovoLarge)

        this.mount.appendChild(this.map.renderer.domElement)
        this.map.resize(this.mount.clientWidth, this.mount.clientHeight)
    }

    setupActions = () => {
        new ResizeMapAction(this.map)
        new DragCameraAction(this.map)
    }

    startAnimationLoop = () => {
        window.requestAnimationFrame(this.startAnimationLoop);
        TWEEN.update()
        this.map.render()
    }

    render() {
        return (
            <div ref={ref => (this.mount = ref)} className={classes.MapScene}>

            </div>
        )
    }
}