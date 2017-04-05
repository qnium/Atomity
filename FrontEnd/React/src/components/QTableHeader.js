import React, { Component } from 'react';
import ListController from '../js/ListController';

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
            //console.log("Render header", this.props.sortingField);
            self.setState({
                sortingField: target.currentSort.field,
                sortingValue: target.currentSort.value
            });
        }
    }

    componentDidMount() {
        window.QEventEmitter.addListener(ListController.buildEvent(this.targetCtrl, ListController.event.stateChanged), this.ctrlStateListener);
    };    
    
    componentWillUnmount() {
        window.QEventEmitter.removeListener(this.ctrlStateListener);
    };

    render() {
        console.log("Render header", this.props.sortingField);
        return <th onClick={this.props.onClick}>{this.props.children}</th>
        // return <th>{React.Children.map(this.props.children,
        //     (child) =>
        //     React.cloneElement(child, {
        //         onClick: this.props.onClick
        //     })
        // , this)}</th>
    }
}

export default QTableHeader;