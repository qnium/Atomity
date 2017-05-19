import React from 'react';
import Button from 'react-bootstrap/lib/Button';
import FormGroup from 'react-bootstrap/lib/FormGroup';
import ControlLabel from 'react-bootstrap/lib/ControlLabel';
import InputGroup from 'react-bootstrap/lib/InputGroup';
import Glyphicon from 'react-bootstrap/lib/Glyphicon';
import EditDepartmentWF from '../workflows/EditDepartmentWF';
import { QForm, QFormControl, QInputControl, QSelectControl } from 'atomity-frontend-react';

class AddEmployeeForm extends React.Component
{
    constructor(props)
    {
        super(props);
        this.state = {
            selectedDep: null
        }
    }
    
    depChanged(event)
    {
        this.setState({selectedDep: event.newValue});
    }

    editDepartment()
    {
        EditDepartmentWF.start(this.state.selectedDep);
    }
    
    render()
    {
        return (
            <QForm okButtonText="Add" cancelButtonText="Cancel" title="Add employee" entityObject={this.props.val}
                entitiesName="employee" okAction="create" onDialogClose={this.props.onDialogClose}>
                <form>
                    
                    <FormGroup controlId="1">
                        <ControlLabel>Email</ControlLabel>
                        <QFormControl id="1" type={QInputControl} placeholder="Enter email" bindingField="email" />
                    </FormGroup>
                    
                    <FormGroup controlId="2">
                        <ControlLabel>Department</ControlLabel>
                        <InputGroup>
                            <QFormControl id="2" type={QSelectControl} bindingField="department" foreignField="id" relatedEntitiesName="department" readAction="read"
                                onChange={this.depChanged.bind(this)} onInit={selectedItem => this.setState({selectedDep: selectedItem})} >
                                    <option value="">--Select department--</option>
                            </QFormControl>
                            <InputGroup.Button>
                                <Button onClick={this.editDepartment.bind(this)} disabled={this.state.selectedDep ? false : true} title="Edit department" ><Glyphicon glyph="pencil" /></Button>                                
                            </InputGroup.Button>
                        </InputGroup>
                    </FormGroup>

                    <FormGroup controlId="3">
                        <ControlLabel>Gender</ControlLabel>
                        <QFormControl id="3" type={QSelectControl} bindingField="gender">
                                <option value="">--Select gender--</option>
                                <option value="1">Male</option>
                                <option value="2">Female</option>
                        </QFormControl>
                    </FormGroup>

                  </form>
            </QForm>
        )
    }
}

export default AddEmployeeForm;
