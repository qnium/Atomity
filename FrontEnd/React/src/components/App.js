import React, { Component } from 'react';
import '../css/bootstrap/css/bootstrap.css';
import Tabs from 'react-bootstrap/lib/Tabs';
import Tab from 'react-bootstrap/lib/Tab';
import Panel from 'react-bootstrap/lib/Panel';
import Row from 'react-bootstrap/lib/Row';
import Col from 'react-bootstrap/lib/Col';
import Glyphicon from 'react-bootstrap/lib/Glyphicon';
//import dataProvider from '../js/DataProvider';
import ListController from '../js/ListController';
import QAction from './QAction';
import QTable from './QTable';
import QTableHeader from './QTableHeader';
import QColumn from './QColumn';
import QProgressIndicator from './QProgressIndicator';
import EventEmitter from '../../node_modules/wolfy87-eventemitter/EventEmitter.min.js';
//import EventEmitter from '../../node_modules/EventEmitter/src/index.js';
import EditEmployee from '../dialogs/EditEmployee';

class App extends Component
{
  constructor(props){
    super(props);
  	this.state = { };
    window.QEventEmitter = new EventEmitter();
    window.helper = {
        val: 'not set',
        getVal: function(){
            return this.val;
        },
        setVal: function(newVal){
            this.val = newVal;
        }
    }
  }
  
  render() {

    const fn = (item) => <span>{item}</span>;

    return (
        <div>
            <Tabs id="mainTabs" defaultActiveKey={1} animation={true}>
                <Tab eventKey={1} title="Employees">
                    <br/>
                    <Row>
                        <Col md={3}>
                            <Panel header="Filters">
                                Filters...
                                {/*<QSimpleFilter name='' target='employees'/>*/}
                            </Panel>
                        </Col>
                        <Col md={9}>
                            <Panel header={
                                    <span>Employees
                                        <QAction targetListCtrlName="employeesCtrl" action="refresh" title="Refresh">
                                            <QProgressIndicator targetListCtrlName='employeesCtrl' />
                                        </QAction>
                                    </span>
                                }>
                                <QTable ctrlName='employeesCtrl' entitiesName='employees'>
                                    
                                    {/*<QTableHeader>Actions</QTableHeader>*/}
                                    <QTableHeader sortable="">ID</QTableHeader>
                                    <QTableHeader sortable="">Email</QTableHeader>
                                    <QTableHeader sortable="">Custom</QTableHeader>
                                    {/*<QColumn>
                                        <QAction targetListCtrlName="employeesCtrl" action="editRecord" title="Edit record" icon="pencil" dialog={EditEmployee} />
                                        <QAction targetListCtrlName="employeesCtrl" action="deleteRecord" title="Delete record" icon="trash" />
                                    </QColumn>*/}
                                    <QColumn fieldName="id" />
                                    <QColumn fieldName="email" />
                                    <QColumn>
                                        <div>ID: {item => item.id}</div>
                                        <div>Email: {item => item.email}</div>
                                        <div>Name (Email): <span>{item => item.name + ' (' + item.email + ')'}</span></div>
                                        <div><p>Formatted item - {item => <span key={item.id}>ID: {item.id} (<b>{item.name}</b>)</span>}</p></div>
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
                                Filters...
                            </Panel>
                        </Col>
                        <Col md={9}>
                        {/*
                            <Panel header={
                                    <span>Departments
                                        <QProgressIndicator targetListCtrlName='departmentsCtrl' />
                                    </span>
                                }>
                                <QTable ctrlName='departmentsCtrl' entitiesName='departments'>
                                    <QTableHeader>Actions</QTableHeader>
                                    <QTableHeader sortable="">ID</QTableHeader>
                                    <QTableHeader sortable="">Type</QTableHeader>
                                    <QTableHeader sortable="">Name</QTableHeader>
                                    <QTableHeader sortable="">Description</QTableHeader>
                                    <QColumn>
                                        <QAction targetListCtrlName="employeesCtrl" action="editRecord" title="Edit record" icon="pencil" dialog={EditEmployee} />
                                        <QAction targetListCtrlName="employeesCtrl" action="deleteRecord" title="Delete record" icon="trash" />
                                    </QColumn>
                                    <QColumn fieldName="id" />
                                    <QColumn fieldName="type" />
                                    <QColumn fieldName="depName" />
                                    <QColumn fieldName="description" />
                                </QTable>
                            </Panel>*/}
                        </Col>
                    </Row>
                </Tab>
            </Tabs>
        </div>            
    //   <div className="App">
    //     <div className="App-header">
    //       <img src={logo} className="App-logo" alt="logo" />
    //       <h2>Welcome to React</h2>
    //     </div>
    //     <p className="App-intro">
    //       To get started, edit <code>src/App.js</code> and save to reload.
    //     </p>
    //     <p>
    //         <Button>Bootstrap button</Button>
    //     </p>
    //   </div>
    );
  }
}

export default App;
