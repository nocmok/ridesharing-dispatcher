import React, {Component} from "react";
import {OdintsovoMapTile} from "../../map/objects/OdintsovoMapTile";
import {DragCameraAction} from "../../map/actions/DragCameraAction";
import classes from "./MapScene.module.css";
import {ResizeMapAction} from "../../map/actions/ResizeMapAction";
import {TWEEN} from "three/examples/jsm/libs/tween.module.min";
import {Route} from '../../map/objects/Route'

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

        // let route = new Route([
        //     {
        //         latitude: 55.6703933,
        //         longitude: 37.2813826
        //     },
        //     {
        //         latitude: 55.6702733,
        //         longitude: 37.2815007
        //     },
        //     {
        //         latitude: 55.6698002,
        //         longitude: 37.2819947
        //     },
        //     {
        //         latitude: 55.669213,
        //         longitude: 37.2826038
        //     },
        // ]);
        // this.map.addObject(route)
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