import React, { Component } from 'react';
import Glyphicon from 'react-bootstrap/lib/Glyphicon';

class QProgressIndicator extends Component
{
    constructor(props)
    {
        super(props);
        let self = this;
        this.targetCtrl = this.props.targetListCtrlName;
        this.state = {
            inProgress: false
        }

        this.progressListener = function(target) {
            self.setState({inProgress: target.newState});
        }
        
        //window.QEventEmitter.addListener('ListCtrl-' + this.targetCtrl + '-ProgressStateChanged', this.progressListener);
    }

    componentDidMount() {
        window.QEventEmitter.addListener('ListCtrl-' + this.targetCtrl + '-ProgressStateChanged', this.progressListener);
    };    
    
    componentWillUnmount() {
        window.QEventEmitter.removeListener(this.progressListener);
    };

    render() {
        return (                
            <Glyphicon className="pull-right" glyph='repeat' style={{color: this.state.inProgress ? '#a0a0a0' : null}}>
                {this.props.children}
            </Glyphicon>
        );
    }    
}

export default QProgressIndicator;