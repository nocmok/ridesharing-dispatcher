import classes from "./BackButton.module.css"
import {Link} from "react-router-dom";

export function BackButton() {
    return (
        <Link to={-1} className={classes.Link}>
            <div className={classes.BackButton}>
                <img src="/icons/back-button.svg" alt=""/>
            </div>
        </Link>
    )
}