import React, { Component } from 'react';
import Tabs from 'react-bootstrap/lib/Tabs';
import Tab from 'react-bootstrap/lib/Tab';
import Panel from 'react-bootstrap/lib/Panel';
import Row from 'react-bootstrap/lib/Row';
import Button from 'react-bootstrap/lib/Button';
import Glyphicon from 'react-bootstrap/lib/Glyphicon';
import Col from 'react-bootstrap/lib/Col';
import Badge from 'react-bootstrap/lib/Badge';

import { QAction, QTable, QTableHeader, QColumn, QPagination, QProgressIndicator, QInputFilter, QSelectFilter, QDateFilter,
    QRowChecker, QGroupActions, DeleteConfirmationWF, DialogService } from 'atomity-frontend-react';
import { ListControllerEvents, DataProviderRegistry } from 'atomity-frontend-common';
// eslint-disable-next-line
import { DataProviderJSONFile } from 'atomity-frontend-common';
// eslint-disable-next-line
import { DataProviderJSONService } from 'atomity-frontend-common';

import moment from 'moment';
import 'moment-timezone';

import AddEmployeeWF from './workflows/AddEmployeeWF'
import EditEmployeeWF from './workflows/EditEmployeeWF'
import AddDepartmentWF from './workflows/AddDepartmentWF'
import EditDepartmentWF from './workflows/EditDepartmentWF'
import MessageForm from './dialogs/MessageForm';

// eslint-disable-next-line
let DefaultErrorHandler = function(errorMessage) {
    DialogService.showDialog(MessageForm, {title: "Error...", message: errorMessage});
}

import './css/index.css';

class App extends Component
{
  constructor(props)
  {
    super(props);
  	this.state = { };
    moment.tz.setDefault("America/New_York");

    let dataProv = new DataProviderJSONFile("test_data");
    //let dataProv = new DataProviderJSONService({apiEndpoint: "http://127.0.0.1:8080/api", errorHandler: DefaultErrorHandler});
    DataProviderRegistry.add(dataProv);
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
                                <QSelectFilter targetListCtrlName="employeesCtrl" filteringField="department_id" title="Department"
                                    entitiesName="department" valueField="id" displayField="name">
                                    <option value="">Any</option>
                                </QSelectFilter>
                                <QInputFilter targetListCtrlName="employeesCtrl" filteringField="department_id" title="Department name" placeholder="Enter department name"
                                    complexFilter={{entitiesName: "department", filteringField: "name", keyField: "id", relatedEntities: "employee"}}
                                />
                                <QSelectFilter targetListCtrlName="employeesCtrl" filteringField="gender" title="Gender">
                                    <option value="">Any</option>
                                    <option value="1">Male</option>
                                    <option value="2">Female</option>
                                </QSelectFilter>
                            </Panel>
                        </Col>
                        <Col md={9}>
                            <Panel header={
                                    <span>Employees
                                        <QAction targetListCtrlName="employeesCtrl" action={ListControllerEvents.refresh}>
                                            <QProgressIndicator title="Refresh" targetListCtrlName='employeesCtrl' />
                                        </QAction>
                                    </span>
                                }
                                footer={
                                    <span className="q-bs-pagination">
                                        <span className="pull-left">
                                            <QAction workflow={AddEmployeeWF}>
                                                <Button>
                                                    <Glyphicon title="Add record" glyph="plus"></Glyphicon>
                                                </Button>
                                            </QAction>
                                        </span>
                                        <QPagination targetListCtrlName="employeesCtrl" />
                                    </span>
                                }>                                
                                <QTable ctrlName='employeesCtrl' entitiesName='employee' pageDataLength={5} useDummyRows={true}>
                                    <QTableHeader className="q-no-left-padding">
                                        <QGroupActions>
                                            <QAction action={ListControllerEvents.deleteRecords} title="Delete records" />
                                            <QAction action={ListControllerEvents.deleteRecords} useConfirmation={true} title="Delete records with confirmation" />
                                        </QGroupActions>
                                    </QTableHeader>
                                    <QTableHeader sortingField="id">ID</QTableHeader>
                                    <QTableHeader sortingField="email">Email</QTableHeader>
                                    <QTableHeader sortingField="gender">Gender</QTableHeader>
                                    <QTableHeader sortingField="department_id">Custom</QTableHeader>
                                    <QTableHeader />
                                    <QColumn><QRowChecker /></QColumn>
                                    <QColumn fieldName="id" />
                                    <QColumn fieldName="email" />
                                    
                                    <QColumn fieldName="gender">{
                                        // eslint-disable-next-line
                                        gender => gender == 1 ? "Male" : gender == 2 ? "Female" : ""
                                    }</QColumn>
                                    <QColumn>
                                        <div>ID: {item => item.id}</div>
                                        <div>Email: {item => item.email}</div>
                                        <div>Department ID (Department name): <span>{item => item.department ? (item.department.id + ' (' + item.department.name + ')') : null}</span></div>
                                        <div><p>Formatted item - {item => <span key={item.id}>ID: {item.id} (<b>{item.email}</b>)</span>}</p></div>
                                    </QColumn>
                                    <QColumn isHoverButtons={true}>
                                        <QAction workflow={EditEmployeeWF} ><Badge title="Edit record"><Glyphicon glyph="pencil" /></Badge></QAction>
                                        <QAction workflow={DeleteConfirmationWF} workflowParams={{entitiesName: "employee"}}>
                                            <Badge title="Delete record"><Glyphicon glyph="trash"/></Badge>
                                        </QAction>
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
                                <QInputFilter targetListCtrlName="departmentsCtrl" filteringField="name" title="Name" placeholder="Enter name" />                                
                                <QDateFilter targetListCtrlName="departmentsCtrl" filteringField="registrationDate" filteringOperation="date_ge" title="Registration date from" placeholder="Select start registration date" />
                                <QDateFilter targetListCtrlName="departmentsCtrl" filteringField="registrationDate" filteringOperation="date_le" title="Registration date to" placeholder="Select end registration date" />
                            </Panel>
                        </Col>
                        <Col md={9}>
                            <Panel header={
                                    <span>Departments
                                        <QAction targetListCtrlName="departmentsCtrl" action={ListControllerEvents.refresh}>
                                            <QProgressIndicator title="Refresh" targetListCtrlName='departmentsCtrl' />
                                        </QAction>
                                    </span>
                                }
                                footer={
                                    <span className="q-bs-pagination">
                                        <span className="pull-left">
                                            <QAction workflow={AddDepartmentWF}>
                                                <Button>
                                                    <Glyphicon title="Add record" glyph="plus"></Glyphicon>
                                                </Button>
                                            </QAction>
                                        </span>
                                        <QPagination targetListCtrlName="departmentsCtrl" />
                                    </span>
                                }>                                
                                <QTable ctrlName='departmentsCtrl' entitiesName='department' pageDataLength={10} useDummyRows={true}>
                                    <QTableHeader className="q-no-left-padding">
                                        <QGroupActions>
                                            <QAction action={ListControllerEvents.deleteRecords} title="Delete records" />
                                        </QGroupActions>
                                    </QTableHeader>
                                    <QTableHeader sortingField="id">ID</QTableHeader>
                                    <QTableHeader sortingField="name">Name</QTableHeader>
                                    <QTableHeader sortingField="registrationDate">Registration date</QTableHeader>
                                    <QTableHeader></QTableHeader>
                                    <QColumn><QRowChecker /></QColumn>
                                    <QColumn fieldName="id" />
                                    <QColumn fieldName="name" />
                                    <QColumn>
                                        {item => moment(parseInt(item.registrationDate, 10)).toString()}
                                    </QColumn>
                                    <QColumn isHoverButtons={true}>
                                        <QAction workflow={EditDepartmentWF}><Badge title="Edit record"><Glyphicon glyph="pencil" /></Badge></QAction>
                                        <QAction workflow={DeleteConfirmationWF} workflowParams={{entitiesName: "department", entitiesToRefresh: ["employee"]}}>
                                            <Badge title="Delete record"><Glyphicon glyph="trash"/></Badge>
                                        </QAction>
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
