import React, { Component } from 'react'
import FormGroup from 'react-bootstrap/lib/FormGroup';
import ControlLabel from 'react-bootstrap/lib/ControlLabel';
import FormControl from 'react-bootstrap/lib/FormControl';
import FilterController from '../js/FilterController';
import Calendar from 'rc-calendar';
//import RcDatePicker from 'rc-calendar/lib/Picker';
//import 'rc-calendar/dist/rc-calendar.css';
import DateTimeField from 'react-bootstrap-datetimepicker';
import 'react-bootstrap-datetimepicker/css/bootstrap-datetimepicker.css';
// import DatePicker from 'react-datepicker';
// import 'react-datepicker/dist/react-datepicker-cssmodules.css';
// import 'react-datepicker/dist/react-datepicker.css';
import Datetime from 'react-datetime';
import 'react-datetime/css/react-datetime.css';
import moment from 'moment';

class QInputFilter extends Component 
{    
    constructor(props)
    {
        super(props);
        
        this.filterCtrl = new FilterController(this.props);
        let self = this;

        this.onChangeFilterValue = (e) =>
        {
            console.log(e.toString());
            console.log(e.utc().toString());
            
            //console.log(e);
            //console.log(new Date(parseInt(e.toString())));

            //self.filterCtrl.applyFilter(e.target.value);
        }
    }
    
    render()
    {
        return (
            <form>
                <FormGroup controlId={this.props.targetListCtrlName + this.props.filteringField}>
                    <ControlLabel>{this.props.title}</ControlLabel>
                    {/*<Calendar id={this.props.targetListCtrlName + this.props.filteringField + this.props.filteringOperation} onChange={this.onChangeFilterValue} />*/}
                    {/*<RcDatePicker id={this.props.targetListCtrlName + this.props.filteringField + this.props.filteringOperation} onChange={this.onChangeFilterValue} />*/}
                    {/*<DatePicker id={this.props.targetListCtrlName + this.props.filteringField + this.props.filteringOperation} onChange={this.onChangeFilterValue} />*/}
                    
                    {/*<DateTimeField id={this.props.targetListCtrlName + this.props.filteringField + this.props.filteringOperation} onChange={this.onChangeFilterValue} />*/}
                    
                    <Datetime id={this.props.targetListCtrlName + this.props.filteringField + this.props.filteringOperation} onChange={this.onChangeFilterValue}
                        utc={false} timeFormat="HH:mm"
                    />
                </FormGroup>
            </form>
        )
    }
}

export default QInputFilter;