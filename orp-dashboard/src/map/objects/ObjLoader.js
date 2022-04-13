import {OBJLoader} from "three/examples/jsm/loaders/OBJLoader";
import {MTLLoader} from "three/examples/jsm/loaders/MTLLoader";

export class ObjLoader {

    loadModel(mtlPath, objPath) {
        let objLoader = new OBJLoader()
        let mtlLoader = new MTLLoader()

        return mtlLoader
            .loadAsync(mtlPath)
            .then(mtl => {
                objLoader.setMaterials(mtl)
                return objLoader.loadAsync(objPath)
            })
    }
}