import classes from "./AdminWidget.module.css"
import {TextButton} from "../../ui/text_button/TextButton";
import {Link} from "react-router-dom";

export function AdminWidget() {
    return (<div className={classes.AdminWidget}>

        <div className="Heading1" style={{color: "#7c7c7c", marginBottom: "30px"}}>Администрирование</div>

        <div className={classes.IconedText}>
            <img src="/icons/car.svg"/>
            <Link to="/sessions" className={classes.Link}>
                Просмотр сессий
            </Link>
        </div>
        <div className={classes.IconedText}>
            <img src="/icons/map-pointer.svg"/>
            <Link to="/orders" className={classes.Link}>
                Просмотр заказов
            </Link>
        </div>
    </div>)
}