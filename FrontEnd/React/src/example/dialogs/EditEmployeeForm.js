import React from 'react';
import Button from 'react-bootstrap/lib/Button';
import FormGroup from 'react-bootstrap/lib/FormGroup';
import ControlLabel from 'react-bootstrap/lib/ControlLabel';
import FormControl from 'react-bootstrap/lib/FormControl';
import InputGroup from 'react-bootstrap/lib/InputGroup';
import Glyphicon from 'react-bootstrap/lib/Glyphicon';
import EditDepartmentWF from '../workflows/EditDepartmentWF';
import { QForm, QFormControl, QInputControl, QSelectControl } from 'atomity-react';

class EditEmployeeForm extends React.Component
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
            <QForm okButtonText="Save" cancelButtonText="Cancel" title="Edit employee" entityObject={this.props.val} entitiesName="employee"
                okAction="update" onDialogClose={this.props.onDialogClose} useArray={true}>
                <form>
                    
                    <FormGroup>
                        <ControlLabel>ID</ControlLabel>
                        <FormControl.Static>
                            {this.props.val.id}
                        </FormControl.Static>
                    </FormGroup>
                    
                    <FormGroup controlId="1">
                        <ControlLabel>Email</ControlLabel>
                        <QFormControl id="1" type={QInputControl} placeholder="Enter email" bindingField="email" />
                    </FormGroup>
                    
                    <FormGroup controlId="2">
                        <ControlLabel>Department</ControlLabel>
                        <InputGroup>
                            <QFormControl id="2" type={QSelectControl} bindingField="department" relatedEntitiesName="department" readAction="read"
                                valueField="id" displayField="name" onChange={this.depChanged.bind(this)} onInit={selectedItem => this.setState({selectedDep: selectedItem})} >
                                    <option value="" disabled>--Select department--</option>
                            </QFormControl>
                            <InputGroup.Button>
                                <Button onClick={this.editDepartment.bind(this)} disabled={this.state.selectedDep ? false : true} title="Edit department" ><Glyphicon glyph="pencil" /></Button>                                
                            </InputGroup.Button>
                        </InputGroup>
                    </FormGroup>

                  </form>
            </QForm>
        )
    }
}

export default EditEmployeeForm;
