import {Component} from "react";
import {App} from "./App";
import {Bootstrapper} from "./Bootstrapper";
import {Spinner} from "./components/ui/spinner/Spinner";

export class BootstrapperComponent extends Component {

    constructor(props) {
        super(props);

        this.state = {isBootstrapped: false}

        new Bootstrapper(this.props.di).bootstrap()
            .then(() => {
                this.setState({isBootstrapped: true})
            })
    }

    render() {
        return (<div>
            {this.state.isBootstrapped ||
                <div style={{
                    display: "flex",
                    justifyContent: "center",
                    alignItems: "center",
                    width: "100%",
                    height: "100%",
                    position: "absolute",
                }}>
                    <Spinner></Spinner>
                </div>
            }
            {this.state.isBootstrapped && <App hidden={this.state.isBootstrapped} di={this.props.di}></App>}
        </div>)
    }
}