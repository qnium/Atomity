import React, { Component } from 'react';
import Glyphicon from 'react-bootstrap/lib/Glyphicon';
import ListController from '../js/ListController';

class QOverlay extends Component
{
    constructor(props)
    {
        super(props);
        let self = this;
        this.targetCtrl = this.props.targetListCtrlName;
        this.state = {
            inProgress: false
        }

        this.ctrlStateListener = function(target) {
            self.setState({inProgress: target.actionInProgress});
        }
    }

    componentDidMount() {
        window.QEventEmitter.addListener(ListController.buildEvent(this.targetCtrl, ListController.event.stateChanged), this.ctrlStateListener);
    };    
    
    componentWillUnmount() {
        window.QEventEmitter.removeListener(this.ctrlStateListener);
    };

    render() {
        return (                
            <Glyphicon className="pull-right" glyph='repeat' style={{color: this.state.inProgress ? '#a0a0a0' : null}}>
                {this.props.children}
            </Glyphicon>
        );
    }    
}

export default QOverlay;