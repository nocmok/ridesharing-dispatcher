import classes from "./BackButton.module.css"
import {Link} from "react-router-dom";

export function BackButton() {
    return (<div className={classes.BackButton}>
        <Link to={-1} className={classes.Link}>
            <img src="/icons/back-button.svg" alt=""/>
        </Link>
    </div>)
}