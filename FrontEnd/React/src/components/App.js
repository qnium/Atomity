import React, { Component } from 'react';
import logo from '../media/logo.svg';
import '../css/app.css';
import '../css/bootstrap/css/bootstrap.css';
import Button from 'react-bootstrap/lib/Button';

class App extends Component {
  render() {
    return (
      <div className="App">
        <div className="App-header">
          <img src={logo} className="App-logo" alt="logo" />
          <h2>Welcome to React</h2>
        </div>
        <p className="App-intro">
          To get started, edit <code>src/App.js</code> and save to reload.
        </p>
        <p>
            <Button>Bootstrap button</Button>
        </p>
      </div>
    );
  }
}

export default App;
