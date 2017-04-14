import React, { Component } from 'react';
import {ListControllerEvents} from '../controllers/ListController';
import Glyphicon from 'react-bootstrap/lib/Glyphicon';
import QGroupActions from './QGroupActions';

var events = require('events');

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
                events(ListControllerEvents.sort).send({targetName: this.targetCtrl, data: sortParams});
            }
        }        
    }

    componentDidMount() {
        events(ListControllerEvents.stateChanged).handle(event => {
            if(event.sourceName === this.targetCtrl) {
                this.ctrlStateListener(event.data);
            }
        });
    };    
    
    componentWillUnmount() {
        //window.QEventEmitter.removeListener(this.ctrlStateListener);
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
    }

    render() {
        return <th onClick={this.headerClick.bind(this, this)}>
            {this.renderChildren()}
            {this.renderSortIcon()}
        </th>
    }
}

export default QTableHeader;