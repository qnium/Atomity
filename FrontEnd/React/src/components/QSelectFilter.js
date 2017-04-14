import React, { Component } from 'react'
import FormGroup from 'react-bootstrap/lib/FormGroup';
import ControlLabel from 'react-bootstrap/lib/ControlLabel';
import FormControl from 'react-bootstrap/lib/FormControl';
import SelectFilterController from '../controllers/SelectFilterController';

class QSelectFilter extends Component 
{    
    constructor(props)
    {
        super(props);
        
        this.filterCtrl = new SelectFilterController(this.props);
        
        this.state = {
            options: []
        };

        this.onChangeFilterValue = (e) => {
            this.filterCtrl.applyFilter(e.target.value);
        }
    }
    
    componentDidMount()
    {
        this.filterCtrl.loadOptions().then(result => {
            this.setState({options: result});
        });
    }

    renderOptions()
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
                        {this.renderOptions()}
                    </FormControl>
                </FormGroup>
            </form>
        )
    }
}

export default QSelectFilter;