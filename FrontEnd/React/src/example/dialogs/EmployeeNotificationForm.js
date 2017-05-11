import React from 'react';
import FormGroup from 'react-bootstrap/lib/FormGroup';
import ControlLabel from 'react-bootstrap/lib/ControlLabel';
import FormControl from 'react-bootstrap/lib/FormControl';
import {QForm} from 'atomity-frontend-react';

class EmployeeNotificationForm extends React.Component
{    
    render = () => (        
        <QForm okButtonText="Send" cancelButtonText="Close" title="Employee notification" entityObject={this.props.val}
            entitiesName="employee" okAction="fakeAction" onDialogClose={this.props.onDialogClose}>
            <form>
                
                <FormGroup controlId="1">
                    <ControlLabel>Reicipient</ControlLabel>
                    <FormControl id="1" type="text" placeholder="Enter email" defaultValue={this.props.val.recipient} />
                </FormGroup>

            </form>
        </QForm>
    )
}

export default EmployeeNotificationForm;
