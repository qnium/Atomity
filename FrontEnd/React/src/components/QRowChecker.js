import React, { Component } from 'react';
import {ListControllerEvents} from '../controllers/ListController';

var events = require('events');

class QRowChecker extends Component
{
    constructor(props)
    {
        super(props);
        this.targetCtrl = this.props.targetListCtrlName;
        this.checkBoxClick = rowIndex => {
            events(ListControllerEvents.setRowChecked).send({targetName: this.targetCtrl, data: {rowIndex: rowIndex}});
        }        
    }

    render() {
        return (<span className="q-row-checker">
            <input type="checkbox" checked={this.props.val.checked} onClick={this.checkBoxClick.bind(this, this.props.val.index)} />
        </span>)
    }
}

export default QRowChecker;