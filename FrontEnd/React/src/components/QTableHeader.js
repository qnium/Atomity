import React, { Component } from 'react';
import ListController from '../controllers/ListController';
import Glyphicon from 'react-bootstrap/lib/Glyphicon';
import QGroupActions from './QGroupActions';

class QTableHeader extends Component
{
    constructor(props)
    {
        super(props);
        let self = this;
        this.targetCtrl = this.props.targetListCtrlName;
        this.state = {
            sortingField: undefined,
            sortingValue: undefined
        }

        this.ctrlStateListener = function(target) {
            if(!target.actionInProgress){
                self.setState({
                    sortingField: target.currentSort.field,
                    sortingValue: target.currentSort.value
                });
            }
        }

        this.headerClick = header =>
        {
            if(header.props.sortingField)
            {
                let sortParams = {
                    sortingField: header.props.sortingField
                }
                window.QEventEmitter.emitEvent(ListController.buildEvent(this.targetCtrl, ListController.action.sort), [sortParams]);
            }
        }        
    }

    componentDidMount() {
        window.QEventEmitter.addListener(ListController.buildEvent(this.targetCtrl, ListController.event.stateChanged), this.ctrlStateListener);
    };    
    
    componentWillUnmount() {
        window.QEventEmitter.removeListener(this.ctrlStateListener);
    };

    renderSortIcon(){
        if(this.state.sortingField !== undefined && this.state.sortingField === this.props.sortingField){
            let icon = this.state.sortingValue ? "triangle-bottom" : "triangle-top";
            return <Glyphicon glyph={icon} />
        } else {
            return null;
        }
    }
    
    renderChildren() {
        if(this.props.children && this.props.children.type === QGroupActions) {
            return <QGroupActions {...this.props} targetListCtrlName={this.props.children.props.targetListCtrlName || this.targetCtrl}/>
        } else {
            return this.props.children
        }

        return null;
    }

    render() {
        return <th onClick={this.headerClick.bind(this, this)}>
            {this.renderChildren()}
            {this.renderSortIcon()}
        </th>
    }
}

export default QTableHeader;