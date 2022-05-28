import classes from "./ScrollBox.module.css";
import {useEffect, useState} from "react";

export function ScrollBox(props) {

    let [headerOpacity, setHeaderOpacity] = useState(0)
    let [footerOpacity, setFooterOpacity] = useState(0)
    let [scrollBoxContent, setScrollBoxContent] = useState({})

    useEffect(() => {
        updateShadows(scrollBoxContent)
    }, [scrollBoxContent])

    let updateShadows = (scrollBoxContent) => {
        const scrollTop = scrollBoxContent.scrollTop
        const scrollMax = scrollBoxContent.scrollHeight - scrollBoxContent.clientHeight
        if (scrollMax <= 0) {
            setHeaderOpacity(0)
            setFooterOpacity(0)
            return
        }
        const scrollProgress = scrollTop * 1.0 / scrollMax
        setHeaderOpacity(scrollProgress)
        setFooterOpacity(1 - scrollProgress)
    }

    return (<div className={classes.ScrollBox} style={props.style}>
        <div className={classes.ScrollBoxHeader} style={{opacity: headerOpacity}}></div>
        <div className={classes.ScrollBoxFooter} style={{opacity: footerOpacity}}></div>
        <div ref={ref => setScrollBoxContent(ref)} className={classes.ScrollBoxContent}
             onScroll={event => updateShadows(event.target)}>
            {props.children}
        </div>
    </div>)
}