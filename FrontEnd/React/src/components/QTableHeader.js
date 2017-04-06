import React, { Component } from 'react';
import ListController from '../js/ListController';
import Glyphicon from 'react-bootstrap/lib/Glyphicon';

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
    
    render() {
        return <th onClick={this.props.onClick}>
            {this.props.children}
            {this.renderSortIcon()}
        </th>
    }
}

export default QTableHeader;