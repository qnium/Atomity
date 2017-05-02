import React from 'react';
import FormGroup from 'react-bootstrap/lib/FormGroup';
import ControlLabel from 'react-bootstrap/lib/ControlLabel';
import FormControl from 'react-bootstrap/lib/FormControl';
import { QForm, QFormControl, QInputControl } from 'atomity-react';

class EditDepartmentForm extends React.Component
{    
    render = () => (
        <QForm okButtonText="Save" cancelButtonText="Cancel" title="Edit department" entityObject={this.props.val} entitiesName="department"
            entitiesToRefresh={["employee"]} okAction="update" onDialogClose={this.props.onDialogClose} useArray={true}>
            <form>
                
                <FormGroup>
                    <ControlLabel>ID</ControlLabel>
                    <FormControl.Static>
                        {this.props.val.id}
                    </FormControl.Static>
                </FormGroup>
                
                <FormGroup controlId="1">
                    <ControlLabel>Name</ControlLabel>
                    <QFormControl id="1" type={QInputControl} placeholder="Enter name" bindingField="name" />
                </FormGroup>
                
            </form>
        </QForm>
    )
}
export default EditDepartmentForm;
