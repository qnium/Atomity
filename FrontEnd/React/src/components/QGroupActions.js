import React, { Component } from 'react';
import ButtonGroup from 'react-bootstrap/lib/ButtonGroup';
import Button from 'react-bootstrap/lib/Button';
import DropdownButton from 'react-bootstrap/lib/DropdownButton';
import MenuItem from 'react-bootstrap/lib/MenuItem';
import ListController from '../js/ListController';
import QAction from '../components/QAction';

class QGroupActions extends Component
{
    constructor(props)
    {
        super(props);
        let self = this;
        this.targetCtrl = this.props.targetListCtrlName;
        
        this.state = {
            allChecked: false,
            actionsAllowed: false,
            checkedItems: []
        };

        this.ctrlStateListener = function(target)
        {
            if(!target.actionInProgress) {
                let checkedItems = target.pageData.filter(item => item.checked).map(item => item.data);
                let checkedCounter = checkedItems.length;
                let allChecked = checkedCounter === target.pageData.filter(item => !item.dummy).length;
                self.setState({
                    allChecked: allChecked,
                    actionsAllowed: checkedCounter > 0,
                    checkedItems: checkedItems
                });
            }
        }
        window.QEventEmitter.addListener(ListController.buildEvent(this.targetCtrl, ListController.event.stateChanged), this.ctrlStateListener);
        
        this.checkBoxClick = () => {
            window.QEventEmitter.emitEvent(ListController.buildEvent(this.targetCtrl, ListController.action.setAllChecked), [{newState: !this.state.allChecked}]);
        }        
    }

    renderMenuItems()
    {
        return this.props.children.props.children.map((menuItem, index) =>
        {
            let actionTemplate;

            if(menuItem.type === QAction) {
                actionTemplate = <QAction {...menuItem.props} val={this.state.checkedItems} targetListCtrlName={this.targetCtrl}>{menuItem.props.title}</QAction>
            } else {
                return null;
                actionTemplate = menuItem;
            }

            return <MenuItem key={index} disabled={!this.state.actionsAllowed}>{actionTemplate}</MenuItem>
        });
    }

    render() {
        return (
            <ButtonGroup>
                <Button bsSize="small" onClick={this.checkBoxClick}>
                    <input type="checkbox" checked={this.state.allChecked} />
                </Button>
                <DropdownButton id="input-dropdown-addon" title="">
                    {this.renderMenuItems()}
                </DropdownButton>
            </ButtonGroup>
        );
    }
}

export default QGroupActions;