import React, { Component } from 'react'
import FormGroup from 'react-bootstrap/lib/FormGroup';
import ControlLabel from 'react-bootstrap/lib/ControlLabel';
import FormControl from 'react-bootstrap/lib/FormControl';
import ListController from '../js/ListController';
import dataProvider from '../js/DemoDataProvider'

class QInputFilter extends Component 
{    
    constructor(props)
    {
        super(props);
        
        this.targetCtrl = this.props.targetListCtrlName;
        
        let operation = 'like';
        if(this.props.complexFilter) {
            operation = 'in';
        }

        this.filter = {
            field: this.props.filteringField,
            operation: operation,
            value: undefined
        }

        this.onChangeFilterValue = (e) => {
            this.applyFilter(e.target.value);
        }
    }
    
    applyFilter(filterValue)
    {
        if(this.props.complexFilter)
        {
            let complexFilter = {
                field: this.props.complexFilter.filteringField,
                operation: 'like',
                value: filterValue
            }

            dataProvider.executeAction(this.props.complexFilter.entitiesName, "read", {filter: [complexFilter]})
            .then(result => {
                this.filter.value = result.data.map(item => item[this.props.complexFilter.key]);
            });
        }

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
                <FormGroup controlId={this.props.targetListCtrlName + this.props.filteringField}>
                    <ControlLabel>{this.props.title}</ControlLabel>
                    <FormControl id={this.props.targetListCtrlName + this.props.filteringField} type="text" placeholder={this.props.placeholder} defaultValue={this.props.initialValue} onChange={this.onChangeFilterValue} />
                </FormGroup>
            </form>
        )
    }
}

export default QInputFilter;