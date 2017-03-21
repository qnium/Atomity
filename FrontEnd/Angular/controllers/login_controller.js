(function(angular){
    var app = angular.module('CommonFrontend');
    app.controller('LoginCtrl', function($scope, $rootScope, $attrs, $window, AuthService, DataProviderService, $localStorage, DialogService)
    {     
        $scope.accountType = $attrs.accountType;
        $scope.isLoggedIn = false;
        
        $window.addEventListener('load', function()
        {
            var sessionInfo = $localStorage[makeStorageKey()];
            if(sessionInfo != undefined) {
                AuthService.checkSession(sessionInfo.key)
                .then(function(sessionExists){
                    if(sessionExists){
                        initLoginInfo(sessionInfo);
                    } else {
                        clearSessionInfo();
                    }
                }).catch(function(err){
                    setError(err);
                });
            }
        });
        
        function initLoginInfo(sessionInfo){
            DataProviderService.setSessionKey(sessionInfo.key);
            AuthService.setSessionInfo(sessionInfo);
            $scope.password = "";
            $scope.isLoggedIn = true;
            $scope.email = sessionInfo.email;
            $rootScope.$broadcast('LoggedInEvent', sessionInfo);        
        }
        
        $scope.authenticate = function(){
            $scope.errorMessage = null;
            AuthService.authenticate($scope.accountType, $scope.email, $scope.password)
                .then(function(key){
                    var sessionInfo = {
                        key: key,
                        email: $scope.email
                    };
                    $localStorage[makeStorageKey()] = sessionInfo;
                    initLoginInfo(sessionInfo);
                }).catch(function(err){
                    setError(err);
                });
        }
        
        function clearSessionInfo(){
            $scope.email = "";
            $scope.password = "";
            $scope.isLoggedIn = false;
            delete $localStorage[makeStorageKey()];
        }
        
        $scope.logOut = function(){
            var sessionInfo = $localStorage[makeStorageKey()];
            if(sessionInfo != undefined){
                AuthService.logout(sessionInfo.key);
            }
            clearSessionInfo();
//            $scope.email = "";
//            $scope.password = "";
        }
        
        $scope.$on('UnauthorizedAccessAttempt', function (e, arg) {
            var sessionInfo = $localStorage[makeStorageKey()];
            if(sessionInfo != undefined) {
                AuthService.checkSession(sessionInfo.key)
                .then(function(sessionExists){
                    if(sessionExists){
                        DialogService.alert("No permission.", "Error...");
                    } else {
                        clearSessionInfo();
                    }
                }).catch(function(err){
                    setError(err);
                });
            } else {
                clearSessionInfo();
            }
        });

        function setError(err)
        {
            switch(err.errorCode)
            {
                case 3: $scope.errorMessage = 'Wrong email or password.';
                    break;
                case 4: $scope.errorMessage = err.error;
                    break;
                default: $scope.errorMessage = "Server error.";
            }
        }
        
        function makeStorageKey(){
            return "sessionKey-" + $scope.accountType;
        }
        
        $scope.forgotPassword = function(entityName, email) 
        {
            if($scope.isLoggedIn)
            {
                DataProviderService.executeAction(entityName, 'requestPasswordReset', email)
                .then(function(response){
                    var msg = response.error || 'Password reset started. Please check your inbox for futher instructions.';
                    DialogService.alert(msg, 'Password reset');
                });
            } else {
                DialogService.prompt({
                    title: 'Password reset',
                    description: 'Please enter your email address',
                    initialValue: email,
                    acceptLabel: 'Reset password',
                    size: 'sm'
                }).then(function(email){
                    return DataProviderService.executeAction(entityName, 'requestPasswordReset', email);
                }).then(function(response){
                    var msg = response.error || 'Password reset started. Please check your inbox for futher instructions.';
                    DialogService.alert(msg, 'Password reset');
                });
            }
        }    
    });
})(angular);