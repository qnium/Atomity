import React, { Component } from 'react'
import FormGroup from 'react-bootstrap/lib/FormGroup';
import ControlLabel from 'react-bootstrap/lib/ControlLabel';
import FormControl from 'react-bootstrap/lib/FormControl';
import ListController from '../js/ListController';

class QSimpleFilter extends Component 
{    
    constructor(props)
    {
        super(props);
        
        this.targetCtrl = this.props.targetListCtrlName;

        this.filter = {
            field: this.props.filteringField,
            operation: 'like',
            value: this.props.initialValue
        }

        this.onChangeFilterValue = (e) => {
            this.applyFilter(e.target.value);
        }
    }
    
    applyFilter(filterValue)
    {
        this.filter.value = filterValue;
        window.QEventEmitter.emitEvent(ListController.buildEvent(this.targetCtrl, ListController.action.applyFilter), [this.filter]);
    }

    componentDidMount(){
        this.applyFilter(this.props.initialValue);
    }

    render()
    {
        return (
            <form>
                <FormGroup controlId="1">
                    <ControlLabel>{this.props.title}</ControlLabel>
                    <FormControl id="1" type="text" placeholder={this.props.placeholder} defaultValue={this.props.initialValue} onChange={this.onChangeFilterValue} />
                </FormGroup>
            </form>
        )
    }
}

export default QSimpleFilter;