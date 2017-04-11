import React, { Component } from 'react'
import FormGroup from 'react-bootstrap/lib/FormGroup';
import ControlLabel from 'react-bootstrap/lib/ControlLabel';
import InputFilterController from '../controllers/InputFilterController';

//import Calendar from 'rc-calendar';
//import 'rc-calendar/dist/rc-calendar.css';
//import DatePicker from 'react-datepicker';
//import 'react-datepicker/dist/react-datepicker-cssmodules.css';
//import 'react-datepicker/dist/react-datepicker.css';

//import DateTimeField from 'react-bootstrap-datetimepicker';
//import 'react-bootstrap-datetimepicker/css/bootstrap-datetimepicker.css';

import Datetime from 'react-datetime';
import 'react-datetime/css/react-datetime.css';

//import moment from 'moment';

class QDateFilter extends Component 
{    
    constructor(props)
    {
        super(props);
        
        this.filterCtrl = new InputFilterController(this.props);
        let self = this;

        this.onChangeFilterValue = (e) =>
        {
            console.log(e.toString());
            console.log(e);
            self.filterCtrl.applyFilter(e);
        }
    }
    
    render()
    {
        return (
            <form>
                <FormGroup controlId={this.props.targetListCtrlName + this.props.filteringField + this.props.filteringOperation}>
                    <ControlLabel>{this.props.title}</ControlLabel>
                    {/*<Calendar id={this.props.targetListCtrlName + this.props.filteringField + this.props.filteringOperation} onChange={this.onChangeFilterValue} />*/}
                    {/*<RcDatePicker id={this.props.targetListCtrlName + this.props.filteringField + this.props.filteringOperation} onChange={this.onChangeFilterValue} />*/}
                    {/*<DatePicker id={this.props.targetListCtrlName + this.props.filteringField + this.props.filteringOperation} onChange={this.onChangeFilterValue} />*/}
                    {/*<DateTimeField id={this.props.targetListCtrlName + this.props.filteringField + this.props.filteringOperation} onChange={this.onChangeFilterValue} />*/}
                    <Datetime id={this.props.targetListCtrlName + this.props.filteringField + this.props.filteringOperation}
                        onChange={this.onChangeFilterValue}
                        utc={false} timeFormat="HH:mm"
                        inputProps={{
                            id: this.props.targetListCtrlName + this.props.filteringField + this.props.filteringOperation,
                            placeholder: this.props.placeholder
                        }}
                    />
                </FormGroup>
            </form>
        )
    }
}

export default QDateFilter;