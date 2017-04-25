import React from 'react';
import Modal from 'react-bootstrap/lib/Modal';
import Button from 'react-bootstrap/lib/Button';
import FormGroup from 'react-bootstrap/lib/FormGroup';
import ControlLabel from 'react-bootstrap/lib/ControlLabel';
import FormControl from 'react-bootstrap/lib/FormControl';
import Col from 'react-bootstrap/lib/Col';
import Row from 'react-bootstrap/lib/Row';
import {ListControllerEvents} from 'atomity-core';
import EditDepartmentWF from '../workflows/EditDepartmentWF'
import {FileDataProvider as dataProvider} from 'atomity-core'
import { QForm } from 'atomity-react'
import { QFormControl } from 'atomity-react'

var events = require('qnium-events')

class EditEmployeeForm extends React.Component
{
    depChanged(event)
    {
        this.selectedDep = event.newValue;        
    }

    editDepartment()
    {
        EditDepartmentWF.start(this.selectedDep);
    }
    
    render()
    {
        return (
            <QForm okButtonText="Save" cancelButtonText="Cancel" title="Edit employee" entityObject={this.props.val}
                entitiesName="employee" okAction="update" onDialogClose={this.props.onDialogClose}>
                <form>
                    
                    <FormGroup>
                        <ControlLabel>ID</ControlLabel>
                        <FormControl.Static>
                            {this.props.val.id}
                        </FormControl.Static>
                    </FormGroup>
                    
                    <FormGroup controlId="1">
                        <ControlLabel>Email</ControlLabel>
                        <QFormControl id="1" type="text" placeholder="Enter email" bindingField="email" />
                    </FormGroup>
                    
                    <FormGroup controlId="2">
                        <ControlLabel>Department</ControlLabel>
                        <Row>
                            <Col md={10} xs={10}>
                                <QFormControl id="2" type="select" bindingField="departmentId" relatedEntitiesName="department" readAction="read"
                                    valueField="id" displayField="name" onChange={this.depChanged.bind(this)} >
                                        <option value="" disabled>--Select department--</option>
                                </QFormControl>
                            </Col>
                            <Col md={2} xs={2}>
                                <Button onClick={this.editDepartment.bind(this)}>Edit</Button>
                            </Col>
                        </Row>  
                    </FormGroup>

                  </form>
            </QForm>
        )
    }
}

export default EditEmployeeForm;
