import React, { Component } from 'react'
import FormGroup from 'react-bootstrap/lib/FormGroup';
import ControlLabel from 'react-bootstrap/lib/ControlLabel';
import FormControl from 'react-bootstrap/lib/FormControl';
import ListController from '../js/ListController';
import dataProvider from '../js/DemoDataProvider'

class QSelectFilter extends Component 
{    
    constructor(props)
    {
        super(props);
        
        this.targetCtrl = this.props.targetListCtrlName;
        this.readAction = this.props.readAction || "read";

        this.filter = {
            field: this.props.filteringField,
            operation: 'eq',
            value: undefined
        }

        this.state = {
            options: []
        };

        this.onChangeFilterValue = (e) => {
            this.applyFilter(e.target.value);
        }
    }
    
    applyFilter(filterValue)
    {
        this.filter.value = filterValue;
        window.QEventEmitter.emitEvent(ListController.buildEvent(this.targetCtrl, ListController.action.applyFilter), [this.filter]);
    }

    componentDidMount()
    {
        if(this.props.entitiesName)
        {
            let self = this;
            dataProvider.executeAction(this.props.entitiesName, this.readAction, {})
            .then(result => {
                self.setState({options: result.data});
            });
        }      
    }

    renderCustomOptions()
    {
        return this.state.options.map((item, index) => {
            return (<option key={index} value={item[this.props.valueField]}>{item[this.props.displayField]}</option>)
        }, this);
    }
    
    render()
    {
        return (
            <form>
                <FormGroup controlId={"SelectFilter" + this.props.targetListCtrlName + this.props.filteringField}>
                    <ControlLabel>{this.props.title}</ControlLabel>
                    <FormControl id={"SelectFilter" + this.props.targetListCtrlName + this.props.filteringField} componentClass="select" defaultValue="" onChange={this.onChangeFilterValue}>
                        {this.props.children}
                        {this.renderCustomOptions()}
                    </FormControl>
                </FormGroup>
            </form>
        )
    }
}

export default QSelectFilter;