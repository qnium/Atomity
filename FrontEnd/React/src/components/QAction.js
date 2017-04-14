import React, { Component } from 'react';
import Glyphicon from 'react-bootstrap/lib/Glyphicon';
import Modal from 'react-bootstrap/lib/Modal';
import Button from 'react-bootstrap/lib/Button';
import {ListControllerEvents} from '../controllers/ListController';

var events = require('events')

class QAction extends Component {

    constructor(props){
        super(props);
        
        let self = this;

        this.onClick = function()
        {
            if(self.props.workflow) {
                self.props.workflow.start(self.props.val);
                return;
            }
            
            if(self.props.isCustomAction === true) {
                events(ListControllerEvents.customAction).send({targetName: self.props.targetListCtrlName, data: {action: self.props.action, payload: self.props.val}});
            } else {
                events(self.props.action).send({targetName: self.props.targetListCtrlName, data: self.props.val});
            }
        }
    }

    render()
    {
        let actionTemplate;

        if(this.props.children){
            actionTemplate = (<span onClick={this.onClick}>{this.props.children}</span>)
        } else {
            actionTemplate = (
                <Glyphicon title={this.props.title} glyph={this.props.icon} onClick={this.onClick}>
                    {this.props.children}
                </Glyphicon>
            )
        }
        return (<span className="q-action">{actionTemplate}</span>)
    }
}

export default QAction;