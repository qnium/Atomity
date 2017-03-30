import React, { Component } from 'react';
import Badge from 'react-bootstrap/lib/Badge';
import Glyphicon from 'react-bootstrap/lib/Glyphicon';
import Modal from 'react-bootstrap/lib/Modal';
import Button from 'react-bootstrap/lib/Button';

class QAction extends Component {

    constructor(props){
        super(props);
        let self = this;

        this.state = {showDialog : false}
        
        this.onClick = function() {
            console.log(self.props.val);
            self.setState({showDialog: true});
            window.QEventEmitter.emitEvent('ListCtrl-' + self.props.targetListCtrlName + '-' + self.props.action, [self.props.val]);
        }

        this.closeDialog = function(){
            self.setState({showDialog: false});
        }
    }

    render() {

        let actionTemplate;
        let dialogTemplate;

        if(this.state.showDialog && this.props.dialog){
            //return (
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
            // ??? return (<span onClick={this.onClick}>{this.props.children}</span>)
            // return (<span onClick={this.onClick}>{this.props.children}</span>)
            actionTemplate = (<span onClick={this.onClick}>{this.props.children}</span>)
        } else {
            // return (
            actionTemplate = (
                <Badge title={this.props.title} onClick={this.onClick}>
                    <Glyphicon glyph={this.props.icon}>
                        {this.props.children}
                    </Glyphicon>
                </Badge>
            )
        }

        // if(dialogTemplate){
        //     return (<span><span>{actionTemplate}</span><span>{dialogTemplate}</span></span>)
        // } else {
        //     return actionTemplate
        // }
        return (<span><span>{actionTemplate}</span><span>{dialogTemplate}</span></span>)
    }
}

export default QAction;