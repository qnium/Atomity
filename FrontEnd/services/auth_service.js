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
            
            getEmail: function() {
                return sessionEmail;
            },
            
            logout: function(sessionKey) {
                return $http.post(appConfig.apiEndpoint, {
                    entityName: 'auth',
                    action: 'logout',
                    sessionKey: sessionKey
                }).then(function(response){
                    if (!response.data.errorCode != 0) {
                        //return;
                    }
                    else {
                        return $q.reject(response.data);
                    }
                });
            },
            
            checkSession: function(sessionKey) {
                return $http.post(appConfig.apiEndpoint, {
                    entityName: 'auth',
                    action: 'checkSession',
                    sessionKey: sessionKey
                }).then(function(response){
                    if (!response.data.errorCode != 0) {
                        return response.data.result;
                    }
                    else {
                        return $q.reject(response.data);
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
                    var sKey = response.data.result;
                    if(sKey){
                        sessionKey = sKey;
                        sessionEmail = email;
                        return sessionKey;
                    } else {
                        return $q.reject(response.data);
                    }
                });
            },
            
            clearSession: function() {
                sessionKey = null;
                sessionEmail = null;
            },
			
			init: function(config) {
				appConfig = config;
			},

            setSessionInfo: function(info) {
                sessionKey = info.key;
                sessionEmail = info.email;
            }
        };
    });
})(angular);