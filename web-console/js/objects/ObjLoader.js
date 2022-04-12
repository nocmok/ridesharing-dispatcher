import { OBJLoader } from "three/OBJLoader";
import { MTLLoader } from "three/MTLLoader";

export class ObjLoader {

    loadModel(mtlPath, objPath) {
        let objLoader = new OBJLoader()
        let mtlLoader = new MTLLoader()

        return mtlLoader.loadAsync(mtlPath).then((mtl) => {
            objLoader.setMaterials(mtl)
            return objLoader.loadAsync(objPath)
        })
    }
}