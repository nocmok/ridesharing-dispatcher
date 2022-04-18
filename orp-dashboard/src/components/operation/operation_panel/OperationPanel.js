import {Component} from "react";
import classes from "./OperationPanel.module.css"
import {Link} from "react-router-dom";

export class OperationPanel extends Component {

    render() {
        return (
            <div className={classes.OperationPanel}>
                <div className="Heading1" style={{color: "#7C7C7C"}}>
                    Панель управления
                </div>
                {/*<div className="Heading3" style={{color: "#7C7C7C"}}>Демонстрация</div>*/}
                {/*<div className="Heading3">Создать сессию</div>*/}
                {/*<div className="Heading3">Создать запрос</div>*/}
                <Link to="/sessions/create">Создать сессию</Link>
                <Link to="/requests/create">Создать запрос</Link>
            </div>);
    }
}