import React from 'react';
import Modal from 'react-bootstrap/lib/Modal';
import Button from 'react-bootstrap/lib/Button';
import FormGroup from 'react-bootstrap/lib/FormGroup';
import ControlLabel from 'react-bootstrap/lib/ControlLabel';
import FormControl from 'react-bootstrap/lib/FormControl';
import {ListControllerEvents} from 'atomity-core';
import { QForm } from 'atomity-react'
import { QFormControl } from 'atomity-react'

var events = require('qnium-events')

class EditDepartmentForm extends React.Component
{    
    render = () => (
        <QForm okButtonText="Save" cancelButtonText="Cancel" title="Edit department" entityObject={this.props.val}
            entitiesName="department" entitiesToRefresh={["employee"]} okAction="update" onDialogClose={this.props.onDialogClose}>
            <form>
                
                <FormGroup>
                    <ControlLabel>ID</ControlLabel>
                    <FormControl.Static>
                        {this.props.val.id}
                    </FormControl.Static>
                </FormGroup>
                
                <FormGroup controlId="1">
                    <ControlLabel>Name</ControlLabel>
                    <QFormControl id="1" type="text" placeholder="Enter name" bindingField="name" />
                </FormGroup>
                
            </form>
        </QForm>
    )
}
export default EditDepartmentForm;
