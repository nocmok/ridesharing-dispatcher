import { Map } from './map/Map.js'
import { OdintsovoMapTile } from './objects/OdintsovoMapTile.js'
import { ResizeAction } from './action/ResizeAction.js'
import { DragCameraAction } from './action/DragCameraAction.js'

class Application {

    constructor() {
        this.map = new Map()
    }

    getMap() {
        return this.map
    }
}

var app = new Application()

app.getMap().getCamera().lookAt(55.66971000000001, 37.28309499999999);
app.getMap().addObject(new OdintsovoMapTile())

new ResizeAction(app)
new DragCameraAction(app)

function renderLoop() {
    requestAnimationFrame(renderLoop)
    app.getMap().render()
}

renderLoop()