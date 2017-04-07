import React, { Component } from 'react';
import '../css/bootstrap/css/bootstrap.css';
import Tabs from 'react-bootstrap/lib/Tabs';
import Tab from 'react-bootstrap/lib/Tab';
import Panel from 'react-bootstrap/lib/Panel';
import Row from 'react-bootstrap/lib/Row';
import Col from 'react-bootstrap/lib/Col';
import Badge from 'react-bootstrap/lib/Badge';

import ButtonGroup from 'react-bootstrap/lib/ButtonGroup';
import Button from 'react-bootstrap/lib/Button';
import DropdownButton from 'react-bootstrap/lib/DropdownButton';
import MenuItem from 'react-bootstrap/lib/MenuItem';
import Checkbox from 'react-bootstrap/lib/Checkbox';

import FormGroup from 'react-bootstrap/lib/FormGroup';
import InputGroup from 'react-bootstrap/lib/InputGroup';
import FormControl from 'react-bootstrap/lib/FormControl';

import QAction from '../components/QAction';
import QTable from '../components/QTable';
import QTableHeader from '../components/QTableHeader';
import QColumn from '../components/QColumn';
import QPagination from '../components/QPagination';
import QProgressIndicator from '../components/QProgressIndicator';
import QInputFilter from '../components/QInputFilter';
import QSelectFilter from '../components/QSelectFilter';
import QDateFilter from '../components/QDateFilter';
import QRowChecker from '../components/QRowChecker';

import EventEmitter from '../../node_modules/wolfy87-eventemitter/EventEmitter.min.js';
import EditEmployee from '../dialogs/EditEmployee';
import ListController from '../js/ListController';
import moment from 'moment';
import 'moment-timezone';

class App extends Component
{
  constructor(props){
    super(props);
  	this.state = { };
    window.QEventEmitter = new EventEmitter();
    moment.tz.setDefault("America/New_York");
  }
  
  render() {

    return (
        <div>
            <Tabs id="mainTabs" defaultActiveKey={1} animation={true}>
                <Tab eventKey={1} title="Employees">
                    <br/>
                    <Row>
                        <Col md={3}>
                            <Panel header="Filters">
                                <QInputFilter targetListCtrlName="employeesCtrl" filteringField="email" title="Email" placeholder="Enter email" />
                                <QSelectFilter targetListCtrlName="employeesCtrl" filteringField="departmentId" title="Department"
                                    entitiesName="department" valueField="id" displayField="name">
                                    <option value="">Any</option>
                                </QSelectFilter>
                                <QInputFilter targetListCtrlName="employeesCtrl" filteringField="departmentId" title="Department name" placeholder="Enter department name"
                                    complexFilter={{entitiesName: "department", filteringField: "name", key: "id"}}
                                />
                            </Panel>
                        </Col>
                        <Col md={9}>
                            <Panel header={
                                    <span>Employees
                                        <QAction targetListCtrlName="employeesCtrl" action={ListController.action.refresh} title="Refresh">
                                            <QProgressIndicator targetListCtrlName='employeesCtrl' />
                                        </QAction>
                                    </span>
                                }
                                footer={
                                    <QPagination targetListCtrlName="employeesCtrl" />
                                }>                                
                                <QTable ctrlName='employeesCtrl' entitiesName='employee' pageDataLength={5} useDummyRows={true}>
                                    
                                    
                                    
                                    <QTableHeader>
                                        {/*<FormGroup>*/}
                                            <ButtonGroup>
                                                {/*<InputGroup.Addon>
                                                <input type="checkbox" />
                                                </InputGroup.Addon>*/}
                                                <Button bsSize="small">
                                                <input type="checkbox" />
                                                </Button>
                                                <DropdownButton
                                                    
                                                    id="input-dropdown-addon"
                                                    title=""
                                                    >
                                                    <MenuItem key="1">Item</MenuItem>
                                                </DropdownButton>
                                                {/*<FormControl type="text" />*/}
                                                {/*<DropdownButton title="Dropdown" id="bg-nested-dropdown">
                                                    <MenuItem eventKey="1">Dropdown link</MenuItem>
                                                    <MenuItem eventKey="2">Dropdown link</MenuItem>
                                                </DropdownButton>*/}
                                            </ButtonGroup>
                                        {/*</FormGroup>*/}
                                    </QTableHeader>
                                    {/*<QTableHeader>
                                        <ButtonGroup>
                                            <Button><input type="checkbox" aria-label="..." /></Button>
                                            <DropdownButton title="Dropdown" id="bg-nested-dropdown">
                                                <MenuItem eventKey="1">Dropdown link</MenuItem>
                                                <MenuItem eventKey="2">Dropdown link</MenuItem>
                                            </DropdownButton>
                                        </ButtonGroup>
                                    </QTableHeader>*/}


                                    <QTableHeader>Actions</QTableHeader>
                                    <QTableHeader sortingField="id">ID</QTableHeader>
                                    <QTableHeader sortingField="email">Email</QTableHeader>
                                    {/*<QTableHeader sortingField="departmentId">Custom</QTableHeader>*/}
                                    
                                    <QColumn><QRowChecker /></QColumn>
                                    <QColumn>
                                        <Badge><QAction targetListCtrlName="employeesCtrl" action={ListController.action.editRecord} title="Edit record" icon="pencil" dialog={EditEmployee} /></Badge>
                                        <Badge><QAction targetListCtrlName="employeesCtrl" action={ListController.action.deleteRecord} title="Delete record" icon="trash" /></Badge>
                                    </QColumn>
                                    <QColumn fieldName="id" />
                                    <QColumn fieldName="email" />
                                    {/*<QColumn>
                                        <div>ID: {item => item.id}</div>
                                        <div>Email: {item => item.email}</div>
                                        <div>Department ID (Department name): <span>{item => item.departmentId + ' (' + item.department.name + ')'}</span></div>
                                        <div><p>Formatted item - {item => <span key={item.id}>ID: {item.id} (<b>{item.email}</b>)</span>}</p></div>
                                    </QColumn>*/}
                                </QTable>
                            </Panel>
                        </Col>
                    </Row>
                </Tab>
                <Tab eventKey={2} title="Departments">
                    <br/>
                    <Row>
                        <Col md={3}>
                            <Panel header="Filters">
                                <QInputFilter targetListCtrlName="departmentsCtrl" filteringField="name" title="Name" placeholder="Enter name" />                                
                                <QDateFilter targetListCtrlName="departmentsCtrl" filteringField="registrationDate" filteringOperation="eq" title="Registration date" placeholder="Select registration date" />
                            </Panel>
                        </Col>
                        <Col md={9}>
                            <Panel header={
                                    <span>Departments
                                        <QProgressIndicator targetListCtrlName='departmentsCtrl' />
                                    </span>
                                }
                                footer={
                                    <QPagination targetListCtrlName="departmentsCtrl" />
                                }>                                
                                <QTable ctrlName='departmentsCtrl' entitiesName='department' pageDataLength={10} useDummyRows={true}>
                                    <QTableHeader>Actions</QTableHeader>
                                    <QTableHeader sortable="">ID</QTableHeader>
                                    <QTableHeader sortable="">Name</QTableHeader>
                                    <QTableHeader sortable="">Registration date</QTableHeader>
                                    <QColumn>
                                        <Badge><QAction targetListCtrlName="departmentsCtrl" action={ListController.action.editRecord} title="Edit record" icon="pencil" /></Badge>
                                        <Badge><QAction targetListCtrlName="departmentsCtrl" action={ListController.action.deleteRecord} title="Delete record" icon="trash" /></Badge>
                                    </QColumn>
                                    <QColumn fieldName="id" />
                                    <QColumn fieldName="name" />
                                    <QColumn>
                                        {item => moment(item.registrationDate).toString()}<br/>
                                    </QColumn>
                                    {/*<QColumn>
                                        {item =>
                                            <Datetime onChange={e => console.log(e.toString())} timeFormat="HH:mm" utc={false}
                                                value={moment(item.registrationDate)} />
                                        }
                                        
                                    </QColumn>*/}
                                </QTable>
                            </Panel>
                        </Col>
                    </Row>
                </Tab>
            </Tabs>
        </div>            
    );
  }
}

export default App;
