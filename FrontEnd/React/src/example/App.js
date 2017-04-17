import React, { Component } from 'react';
import ReactDOM from 'react-dom';
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
import QDateFilter from '../components/QDateFilter';
import QRowChecker from '../components/QRowChecker';
import QGroupActions from '../components/QGroupActions';

import EditEmployee from './dialogs/EditEmployee';
import {ListControllerEvents} from '../controllers/ListController';
import moment from 'moment';
import 'moment-timezone';

import EditEmployeeForm from './dialogs/EditEmployeeForm';
import EditDepartmentForm from './dialogs/EditDepartmentForm';
import EmployeeNotificationForm from './dialogs/EmployeeNotificationForm';

var DialogService = 
{
    showDialog: (dialogTemplate, dialogData) =>
    {
        dialogData = Object.assign({}, dialogData);
        
        return new Promise(onDialogClose =>
        {        
            let popup = document.createElement("div");
            document.body.appendChild(popup);            
            
            ReactDOM.render(
                React.createElement(dialogTemplate, {val: dialogData, onDialogClose: onDialogClose}),
                popup
            );
        });
    }
}

var EditEmployeeWF =
{
    start: (dialogData, event) =>
    {        
        // event.stopPropagation();
        // console.log(dialogData);
        // console.log(event);

        console.log("show edit employee form");
        
        DialogService.showDialog(EditEmployeeForm, dialogData)
        .then(result =>
        {
            if(result === "'dialogOk'") {
                console.log("edit employee step 1 success", result);
                console.log("show notification form");
                DialogService.showDialog(EmployeeNotificationForm, {recipient: "recipient val"}).then(result => {
                    if(result === "'dialogOk'") {
                        console.log("edit employee step 2 success", result);
                    } else {
                        console.log("edit employee step 2 reject", result);
                    }
                });
            } else {
                console.log("edit employee step 1 reject", result);
            }
        });
    }
}

var EditEmployeeWF1 = function()
{    
    return {start: start}

    function step1(dialogData) {
        console.log("show edit employee form");
        return DialogService.showDialog(EditEmployeeForm, dialogData).then(result =>
        {
            console.log("edit employee step 1 success", result);
            step2(dialogData);
        }).catch(reason => {
            console.log("edit employee step 1 reject", reason);
        });
    };

    function step2(step1Data) {
        console.log("show notification form");
        return DialogService.showDialog(EmployeeNotificationForm, {recipient: "recipient val"}).then(result => {
            if(result === "'back'") {
                step1(step1Data);
            } else {
                console.log("edit employee step 2 success", result);
            }
        }).catch(reason => {
            console.log("edit employee step 2 reject", reason);
        });
    };
    
    function start(dialogData)
    {        
        step1(dialogData);
    }
}

var EditDepartmentWF =
{
    start: dialogData =>
    {
        console.log("show edit department form");
        DialogService.showDialog(EditDepartmentForm, dialogData).then(result =>
        {
            if(result === "'dialogOk'") {
                console.log("edit department step 1 success", result);
            } else {
                console.log("edit department step 1 reject", result);
            }            
        });
    }
}

class App extends Component
{
  constructor(props){
    super(props);
  	this.state = { };
    moment.tz.setDefault("America/New_York");
  }
  
  render() {
    //onClick={EditEmployeeWF.start.bind(this, {id: 11, email: "employee email"})}
    return (
        <div>
            <Tabs id="mainTabs" defaultActiveKey={1} animation={true}>
                <Tab eventKey={1} title="Employees" >
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
                                        <QAction targetListCtrlName="employeesCtrl" action={ListControllerEvents.refresh} title="Refresh">
                                            <QProgressIndicator targetListCtrlName='employeesCtrl' />
                                        </QAction>
                                    </span>
                                }
                                footer={
                                    <QPagination targetListCtrlName="employeesCtrl" />
                                }>                                
                                <QTable ctrlName='employeesCtrl' entitiesName='employee' pageDataLength={5} useDummyRows={true}>
                                    <QTableHeader className="q-no-left-padding">
                                        <QGroupActions>
                                            <QAction action="delete" isCustomAction={true} title="Delete records 1" />
                                            <QAction action="delete" isCustomAction={true} title="Delete records 2" />
                                        </QGroupActions>
                                    </QTableHeader>
                                    <QTableHeader sortingField="id">ID</QTableHeader>
                                    <QTableHeader sortingField="email">Email</QTableHeader>
                                    <QTableHeader sortingField="departmentId">Custom</QTableHeader>
                                    <QTableHeader></QTableHeader>
                                    <QColumn><QRowChecker /></QColumn>
                                    <QColumn fieldName="id" />
                                    <QColumn fieldName="email" />
                                    <QColumn>
                                        <div>ID: {item => item.id}</div>
                                        <div>Email: {item => item.email}</div>
                                        <div>Department ID (Department name): <span>{item => item.departmentId + ' (' + item.department.name + ')'}</span></div>
                                        <div><p>Formatted item - {item => <span key={item.id}>ID: {item.id} (<b>{item.email}</b>)</span>}</p></div>
                                    </QColumn>
                                    <QColumn isHoverButtons={true}>
                                        <Badge><QAction targetListCtrlName="employeesCtrl" action={ListControllerEvents.editRecord} title="Edit record" icon="pencil" workflow={EditEmployeeWF}/></Badge>
                                        <Badge><QAction targetListCtrlName="employeesCtrl" action={ListControllerEvents.deleteRecord} title="Delete record" icon="trash" /></Badge>
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
                                        <QAction targetListCtrlName="departmentsCtrl" action={ListControllerEvents.refresh} title="Refresh">
                                            <QProgressIndicator targetListCtrlName='departmentsCtrl' />
                                        </QAction>
                                    </span>
                                }
                                footer={
                                    <QPagination targetListCtrlName="departmentsCtrl" />
                                }>                                
                                <QTable ctrlName='departmentsCtrl' entitiesName='department' pageDataLength={10} useDummyRows={true}>
                                    <QTableHeader className="q-no-left-padding">
                                        <QGroupActions>
                                            <QAction action="delete" isCustomAction={true} title="Delete records" />
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
                                        {item => moment(parseInt(item.registrationDate, 10)).toString()}<br/>
                                        {item => new Date(parseInt(item.registrationDate, 10)).toString()}<br/>
                                    </QColumn>
                                    <QColumn isHoverButtons={true}>
                                        <Badge><QAction targetListCtrlName="departmentsCtrl" action={ListControllerEvents.editRecord} title="Edit record" icon="pencil" /></Badge>
                                        <Badge><QAction targetListCtrlName="departmentsCtrl" action={ListControllerEvents.deleteRecord} title="Delete record" icon="trash" /></Badge>
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

export {EditDepartmentWF};
export {EditEmployeeWF};
export default App;
