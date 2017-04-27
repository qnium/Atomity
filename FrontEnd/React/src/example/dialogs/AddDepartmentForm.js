import React from 'react';
import FormGroup from 'react-bootstrap/lib/FormGroup';
import ControlLabel from 'react-bootstrap/lib/ControlLabel';
import { QForm, QFormControl, QInputControl } from 'atomity-react';

class AddDepartmentForm extends React.Component
{    
    render = () => (
        <QForm okButtonText="Add" cancelButtonText="Cancel" title="Add department" entityObject={this.props.val}
            entitiesName="department" okAction="create" onDialogClose={this.props.onDialogClose}>
            <form>
                
                <FormGroup controlId="1">
                    <ControlLabel>Name</ControlLabel>
                    <QFormControl id="1" type={QInputControl} placeholder="Enter name" bindingField="name" />
                </FormGroup>
                
            </form>
        </QForm>
    )
}
export default AddDepartmentForm;
