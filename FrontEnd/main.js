(function() {    
    angular.module('CommonFrontend', ['ui.bootstrap', 'angularFileUpload', 'ab-base64', 'ngStorage'])
    .run(initApp);
    
    initApp.$inject = ['$rootScope', 'DataProviderService', 'DialogService', 'FileStoreService', 'AuthService', 'ReportsService'];

    function initApp($rootScope, DataProviderService, DialogService, FileStoreService, AuthService, ReportsService) {
        // appConfig is declared in config file separately.
        DataProviderService.init(appConfig);
        FileStoreService.init(appConfig);
		AuthService.init(appConfig);
		ReportsService.init(appConfig);
        DialogService.setTemplatePath(appConfig.templatePath);        
    }    
})();