import React, { Component } from 'react';
import FormGroup from 'react-bootstrap/lib/FormGroup';
import ControlLabel from 'react-bootstrap/lib/ControlLabel';
import FormControl from 'react-bootstrap/lib/FormControl';

class EditEmployee extends Component
{
    render() {
        return (
            <form>
                <FormGroup>
                    <ControlLabel>ID</ControlLabel>
                    <FormControl.Static>
                        {this.props.val.id}
                    </FormControl.Static>
                </FormGroup>
                <FormGroup controlId="1">
                    <ControlLabel>Email</ControlLabel>
                    <FormControl id="1" type="text" placeholder="Enter email" defaultValue={this.props.val.email} />
                </FormGroup>
                <FormGroup controlId="2">
                    <ControlLabel>Name</ControlLabel>
                    <FormControl id="2" type="text" placeholder="Enter name" defaultValue={this.props.val.name} />
                </FormGroup>
            </form>
        )        
    }
}

export default EditEmployee;