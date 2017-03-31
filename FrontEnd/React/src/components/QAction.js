import React, { Component } from 'react';
import Glyphicon from 'react-bootstrap/lib/Glyphicon';
import Modal from 'react-bootstrap/lib/Modal';
import Button from 'react-bootstrap/lib/Button';
import ListController from '../js/ListController';

class QAction extends Component {

    constructor(props){
        super(props);
        let self = this;

        this.state = {showDialog : false}
        
        this.onClick = function() {
            console.log(self.props.val);
            self.setState({showDialog: true});
            window.QEventEmitter.emitEvent(ListController.buildEvent(self.props.targetListCtrlName, self.props.action), [self.props.val]);
        }

        this.closeDialog = function(){
            self.setState({showDialog: false});
        }
    }

    render()
    {
        let actionTemplate;
        let dialogTemplate;

        if(this.state.showDialog && this.props.dialog){
            dialogTemplate = (
                <Modal show={this.state.showDialog} onHide={this.closeDialog}>
                    <Modal.Header closeButton={true}>
                        <Modal.Title>Modal heading</Modal.Title>
                    </Modal.Header>
                    <Modal.Body>
                        {React.createElement(this.props.dialog, {val: this.props.val})}
                    </Modal.Body>
                    <Modal.Footer>
                        <Button onClick={this.closeDialog}>Close</Button>
                        <Button bsStyle="primary" onClick={this.closeDialog}>Save</Button>
                    </Modal.Footer>
                </Modal>
            )
        }
        
        if(this.props.children){
            actionTemplate = (<span onClick={this.onClick}>{this.props.children}</span>)
        } else {
            actionTemplate = (
                    <Glyphicon title={this.props.title} glyph={this.props.icon} onClick={this.onClick}>
                        {this.props.children}
                    </Glyphicon>
            )
        }
        return (<span><span>{actionTemplate}</span><span>{dialogTemplate}</span></span>)
    }
}

export default QAction;