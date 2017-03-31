class DemoDataProvider
{
    sessionKey = null;
    apiEndpoint = null;

    sampleData = {};

    constructor(){
        this.setSampleData();
    }
    
    executeAction(entity, action, payload) {
        let req = {
            entityName: entity,
            action: action,
            data: payload,
            sessionKey: this.sessionKey
        };
        
        return new Promise((resolve, reject) => {
            setTimeout(() => {
                switch(req.action){
                    case "read":
                        resolve(this.demoReadAction(req));
                        break;
                    case "delete":
                        resolve(this.demoDeleteAction(req));
                        break;
                    default:
                        throw new Error({result: {error: "Unknown action: " + req.action}});
                }
                
            }, 1000);
        });        
    }

    init(config) {
        this.apiEndpoint = config.apiEndpoint;
    }

    setSessionKey(key) {
        this.sessionKey = key;
    }

    setSampleData()
    {
        this.sampleData.employees = [];
        for(let i = 0; i < 3; i++) {
            this.sampleData.employees.push({id: i, email: 'email_' + i, name: 'Name ' + i});
        }
        
        this.sampleData.departments = [];
        for(let i = 0; i < 81; i++) {
            this.sampleData.departments.push({id: i, type: i + 1, depName: 'Department name ' + i, description: 'Description ' + i});
        }
    }

    demoDeleteAction(req)
    {
        for(let record of req.data)
        {
            let index = this.sampleData[req.entityName].indexOf(this.sampleData[req.entityName].find((value) => value.id === record.id));
            if(index !== -1){
                this.sampleData[req.entityName].splice(index, 1);
            }
        }
    }

    demoReadAction(req)
    {
        let data = this.sampleData[req.entityName];
        
        let allFilteredData = data; // to do filtering

        for(let currentFilter of req.data.filter)
        {
            if(!currentFilter.value){
                continue;
            }

            if(currentFilter.operation === "like")
            {
                let currentFilterResult = [];
                for(let item of allFilteredData)
                {
                    let val = item[currentFilter.field].toLowerCase();
                    if(val.includes(currentFilter.value.toLowerCase())){
                        currentFilterResult.push(item);
                    }
                }
                allFilteredData = currentFilterResult;
            }
        }
        
        if(req.data.count > 0){
            data = allFilteredData.slice(req.data.startIndex, req.data.startIndex + req.data.count);
        }
        return {data: data, totalCounter: allFilteredData.length}
    }
}

export default new DemoDataProvider();