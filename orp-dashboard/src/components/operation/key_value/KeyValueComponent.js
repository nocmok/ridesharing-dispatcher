import classes from "./KeyValueComponent.module.css"

export function KeyValueComponent(props) {
    return (<div className={classes.KeyValueComponent}>
        <div className="Heading3" style={{
            maxWidth: "50%"
        }}>{props.title}</div>
        <div className={classes.Value}>{props.value}</div>
    </div>)
}