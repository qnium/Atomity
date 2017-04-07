import React, { Component } from 'react';
import ButtonGroup from 'react-bootstrap/lib/ButtonGroup';
import Button from 'react-bootstrap/lib/Button';
import DropdownButton from 'react-bootstrap/lib/DropdownButton';
import MenuItem from 'react-bootstrap/lib/MenuItem';
import ListController from '../js/ListController';

class QGroupActions extends Component
{
    constructor(props)
    {
        super(props);
        let self = this;
        this.targetCtrl = this.props.targetListCtrlName;
        
        this.state = {
            allChecked: false
        };

        this.ctrlStateListener = function(target)
        {
            if(!target.actionInProgress) {
                let allChecked = target.pageData.filter(item => item.checked).length === target.pageData.filter(item => !item.dummy).length;
                self.setState({allChecked: allChecked});
            }
        }
        window.QEventEmitter.addListener(ListController.buildEvent(this.targetCtrl, ListController.event.stateChanged), this.ctrlStateListener);
        
        this.checkBoxClick = () => {
            window.QEventEmitter.emitEvent(ListController.buildEvent(this.targetCtrl, ListController.action.setAllChecked), [{newState: !this.state.allChecked}]);
        }        
    }

    render() {
        return (
            <ButtonGroup>
                <Button bsSize="small" onClick={this.checkBoxClick}>
                    <input type="checkbox" checked={this.state.allChecked} />
                </Button>
                <DropdownButton
                    id="input-dropdown-addon"
                    title=""
                    >
                    <MenuItem key="1">Item</MenuItem>
                </DropdownButton>
            </ButtonGroup>
        );
    }
}

export default QGroupActions;