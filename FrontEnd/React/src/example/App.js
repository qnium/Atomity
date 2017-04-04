import React, { Component } from 'react';
import '../css/bootstrap/css/bootstrap.css';
import Tabs from 'react-bootstrap/lib/Tabs';
import Tab from 'react-bootstrap/lib/Tab';
import Panel from 'react-bootstrap/lib/Panel';
import Row from 'react-bootstrap/lib/Row';
import Col from 'react-bootstrap/lib/Col';
import Badge from 'react-bootstrap/lib/Badge';
import QAction from '../components/QAction';
import QTable from '../components/QTable';
import QTableHeader from '../components/QTableHeader';
import QColumn from '../components/QColumn';
import QPagination from '../components/QPagination';
import QProgressIndicator from '../components/QProgressIndicator';
import QInputFilter from '../components/QInputFilter';
import QSelectFilter from '../components/QSelectFilter';
import EventEmitter from '../../node_modules/wolfy87-eventemitter/EventEmitter.min.js';
import EditEmployee from '../dialogs/EditEmployee';
import ListController from '../js/ListController';

class App extends Component
{
  constructor(props){
    super(props);
  	this.state = { };
    window.QEventEmitter = new EventEmitter();
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
                                <QTable ctrlName='employeesCtrl' entitiesName='employee' pageDataLength={2}>
                                    <QTableHeader>Actions</QTableHeader>
                                    <QTableHeader sortable="">ID</QTableHeader>
                                    <QTableHeader sortable="">Email</QTableHeader>
                                    <QTableHeader sortable="">Custom</QTableHeader>
                                    <QColumn>
                                        <Badge><QAction targetListCtrlName="employeesCtrl" action={ListController.action.editRecord} title="Edit record" icon="pencil" dialog={EditEmployee} /></Badge>
                                        <Badge><QAction targetListCtrlName="employeesCtrl" action={ListController.action.deleteRecord} title="Delete record" icon="trash" /></Badge>
                                    </QColumn>
                                    <QColumn fieldName="id" />
                                    <QColumn fieldName="email" />
                                    <QColumn>
                                        <div>ID: {item => item.id}</div>
                                        <div>Email: {item => item.email}</div>
                                        <div>Department ID (Department name): <span>{item => item.departmentId + ' (' + item.department.name + ')'}</span></div>
                                        <div><p>Formatted item - {item => <span key={item.id}>ID: {item.id} (<b>{item.email}</b>)</span>}</p></div>
                                    </QColumn>
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
                                <QInputFilter targetListCtrlName="departmentsCtrl" filteringField="name" initialValue="" title="Name" placeholder="Enter name" />
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
                                <QTable ctrlName='departmentsCtrl' entitiesName='department' pageDataLength={10}>
                                    <QTableHeader>Actions</QTableHeader>
                                    <QTableHeader sortable="">ID</QTableHeader>
                                    <QTableHeader sortable="">Name</QTableHeader>
                                    <QColumn>
                                        <Badge><QAction targetListCtrlName="departmentsCtrl" action={ListController.action.editRecord} title="Edit record" icon="pencil" /></Badge>
                                        <Badge><QAction targetListCtrlName="departmentsCtrl" action={ListController.action.deleteRecord} title="Delete record" icon="trash" /></Badge>
                                    </QColumn>
                                    <QColumn fieldName="id" />
                                    <QColumn fieldName="name" />
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
