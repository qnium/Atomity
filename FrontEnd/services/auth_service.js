(function(angular){
    var app = angular.module('CommonFrontend');
    app.factory('AuthService', function($http, $q) {
        var sessionEmail = null;
        var sessionKey = null;
		var appConfig = null;
        return {
            getSessionKey: function() {
                return sessionKey;
            },
            
            isAuthenticated: function() {
                return sessionKey != null;
            },
            
            getUsername: function() {
                return sessionEmail;
            },
            
            logout: function(sessionKey) {
                return $http.post(appConfig.apiEndpoint, {
                    entityName: 'auth',
                    action: 'logout',
                    sessionKey: sessionKey
                }).then(function(response){
                    if (!response.data.error) {
                        //return;
                    }
                    else {
                        return $q.reject(new Error(response.data.error));
                    }
                });
            },
            
            checkSession: function(sessionKey) {
                return $http.post(appConfig.apiEndpoint, {
                    entityName: 'auth',
                    action: 'checkSession',
                    sessionKey: sessionKey
                }).then(function(response){
                    if (!response.data.error) {
                        return response.data.result;
                    }
                    else {
                        return $q.reject(new Error(response.data.error));
                    }
                });
            },
            
            authenticate: function(application, email, password) {
                return $http.post(appConfig.apiEndpoint, {
                    entityName: 'auth',
                    action: 'login',
                    data: {
						accountType: application,
                        login: email,
                        password: password
                    }
                }).then(function(response){
                    if (!response.data.error) {
                        sessionKey = response.data.result;
                        if (sessionKey != null) {
                            sessionEmail = email;
                        }
                        return sessionKey;
                    }
                    else {
                        return $q.reject(new Error(response.data.error));
                    }
                });
            },
            
            clearSession: function() {
                sessionKey = null;
                sessionEmail = null;
            },
			
			init: function(config) {
				appConfig = config;
			}
        };
    });
})(angular);