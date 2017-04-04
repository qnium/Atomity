import React, { Component } from 'react'
import FormGroup from 'react-bootstrap/lib/FormGroup';
import ControlLabel from 'react-bootstrap/lib/ControlLabel';
import FormControl from 'react-bootstrap/lib/FormControl';
import FilterController from '../js/FilterController';

class QInputFilter extends Component 
{    
    constructor(props)
    {
        super(props);
        
        let filterCtrl = new FilterController(this.props);
        
        this.onChangeFilterValue = (e) => {
            filterCtrl.applyFilter(e.target.value);
        }
    }
    
    render()
    {
        return (
            <form>
                <FormGroup controlId={this.props.targetListCtrlName + this.props.filteringField}>
                    <ControlLabel>{this.props.title}</ControlLabel>
                    <FormControl id={this.props.targetListCtrlName + this.props.filteringField} type="text" placeholder={this.props.placeholder} defaultValue="" onChange={this.onChangeFilterValue} />
                </FormGroup>
            </form>
        )
    }
}

export default QInputFilter;