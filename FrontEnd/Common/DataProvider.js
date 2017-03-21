class DataProvider {
    sessionKey = null;
    apiEndpoint = null;

    executeAction(entity, action, payload) {
        let req = {
            entityName: entity,
            action: action,
            data: payload,
            sessionKey: this.sessionKey
        };
        return fetch(apiEndpoint, {
            method: 'POST',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(req)
        })
        .then(response => response.json())
        .then(result => {
            if (result.data.error) {
                throw new Error(result.data.error);
            }
            else {
                return result.data;
            }
        });
    }

    init(config) {
        apiEndpoint = config.apiEndpoint;
    }

    setSessionKey(key) {
        this.sessionKey = key;
    }
}

export default dataProvider = new DataProvider();