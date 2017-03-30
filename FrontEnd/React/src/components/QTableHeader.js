import React, { Component } from 'react';

class QTableHeader extends Component {

    render() {
        return <th>{this.props.children}</th>
    }
}

export default QTableHeader;