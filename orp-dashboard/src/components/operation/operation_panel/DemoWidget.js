import classes from "./DemoWidget.module.css"
import {TextButton} from "../../ui/text_button/TextButton";
import {Link} from "react-router-dom";

export function DemoWidget() {
    return (<div className={classes.DemoWidget}>

        <div className="Heading1" style={{color: "#7c7c7c", marginBottom: "30px"}}>Демонстрация</div>

        <div className={classes.IconedText}>
            <img src="/icons/car.svg"/>
            <Link to="/sessions/create" className={classes.Link}>Создать сессию</Link>
        </div>
        <div className={classes.IconedText}>
            <img src="/icons/map-pointer.svg"/>
            <Link to="/requests/create" className={classes.Link}>Создать заказ</Link>
        </div>
    </div>)
}