import React, { Component } from 'react';
import ListController from '../js/ListController';

class QRowChecker extends Component
{
    constructor(props)
    {
        super(props);
        this.targetCtrl = this.props.targetListCtrlName;
        this.checkBoxClick = rowIndex => {
            window.QEventEmitter.emitEvent(ListController.buildEvent(this.targetCtrl, ListController.action.setRowChecked), [{rowIndex: rowIndex}]);
        }        
    }

    render() {
        return <input type="checkbox" checked={this.props.val.checked} onClick={this.checkBoxClick.bind(this, this.props.val.index)} />
    }
}

export default QRowChecker;