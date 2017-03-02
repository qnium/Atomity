(function(angular){
    var app = angular.module('CommonFrontend');
    app.factory('UtilsService', function() {
        return {
            cloneObject : function(obj){
                return angular.copy(obj);
            },
            
            dateTimeNow : function(){
                return new Date();
            },

            dateNow : function(timeZone){
                timeZone = timeZone || moment.tz.guess();
                return moment.tz(new Date(), timeZone).startOf('day').toDate();
            },

            dateWithOffset : function(offset, offsetUnit) {
                return moment().startOf('day').add(offset, offsetUnit).toDate();
            },

            formatTime: function(time, format) {
                return moment(time).clone().format(format);
            },

            dateToString: function(date) {
                return new Date(date).toString();
            }, 
            
            newDateTime: function(values) {
                values = values || {};
                var year = values.year || 0;
                var month = values.month || 1;
                var day = values.day || 1;
                var hours = values.hours || 0;
                var minutes = values.minutes || 0;
                var seconds = values.seconds || 0;
                var milliseconds = values.milliseconds || 0;
                
                var dateTime = new Date(year, month - 1, day, hours, minutes, seconds, milliseconds);
                
                return dateTime;
            }, 

            dateTimeToIso8601: function(dateTime) {
                
                var tzOffset = 0 - dateTime.getTimezoneOffset();
                var sign = tzOffset >= 0 ? '+' : '-';
                tzOffset = Math.abs(tzOffset);
                var offsetHours = parseInt(tzOffset / 60);
                var offsetMinutes = tzOffset % 60;
                
                var formattedDateTime = moment(dateTime).format('YYYY-MM-DDTHH:mm:ss');
                formattedDateTime = formattedDateTime + sign + ("0" + offsetHours).slice(-2) + ":" + ("0" + offsetMinutes).slice(-2);
                
                return formattedDateTime;
            }, 
            
            removeClass: function(id, className) {
                var el = angular.element('#' + id);
                el.removeClass(className);
            },
            
            toggleClass: function(id, className) {
                var el = angular.element('#' + id);
                if(el.hasClass(className)){
                    el.removeClass(className);
                } else {
                    el.addClass(className);
                }
            },
            
            daysBetweenDates: function(firstDate, secondDate)
            {
                var mFirstDate = moment(firstDate);
                var mSecondDate = moment(secondDate);
                return mSecondDate.diff(mFirstDate, 'days');                
            },

            findInArray: function(array, fieldName, value) {
                if(array){
                    return array.find(e => e[fieldName] == value);
                } else {
                    return null;
                }
            },
            
            findIndexInArray: function(array, fieldName, value) {
                if(array){
                    return array.findIndex(e => e[fieldName] == value);
                } else {
                    return null;
                }
            },
            
            ceil: function(number) {
                return Math.ceil(number);
            },
            
            getBirthYearsArray: function() {
                var years = [];
                for(i = new Date().getFullYear() - 1; i >= 1900; i--){
                    years.push(i);
                }
                return years;
            },
            
            createDateFromUnix: function(unixDate){
                return new Date(unixDate);
            },
            
            createDate: function(year, month, day) {
                var date = new Date(year, month - 1, day);
                if(year != date.getFullYear() || month - 1 != date.getMonth() || day != date.getDate()){
                    date = null;
                }
                return date;
            },

            getContrastTextColor: function(hexColor) {
                var rgb = this.hexToRgb(hexColor);
                var o = Math.round(((parseInt(rgb[0]) * 299) +
                                (parseInt(rgb[1]) * 587) +
                                (parseInt(rgb[2]) * 114)) / 1000);
                return (o > 125) ? 'black' : 'white'; //http://www.w3.org/TR/AERT#color-contrast
            },

            hexToRgb: function(hex) {
                // Expand shorthand form (e.g. "03F") to full form (e.g. "0033FF")
                if(!hex){
                    return [0,0,0];
                }
                
                var shorthandRegex = /^#?([a-f\d])([a-f\d])([a-f\d])$/i;
                hex = hex.replace(shorthandRegex, function(m, r, g, b) {
                    return r + r + g + g + b + b;
                });

                var result = /^#?([a-f\d]{2})([a-f\d]{2})([a-f\d]{2})$/i.exec(hex);
                return result ? [
                    parseInt(result[1], 16),
                    parseInt(result[2], 16),
                    parseInt(result[3], 16)
                ] : null;
            },
            
            removeClasses: function(containerId, classForSerach, classToRemove)
            {
                var tabContent = document.getElementById(containerId);
                var tabs = tabContent.getElementsByClassName(classForSerach);
                for(var i = 0; i < tabContent.children.length; i++){
                    tabContent.children[i].classList.remove(classToRemove);
                }
            },
            
            isImageFile: function(fileName){
                var imageExtensions = ['bmp', 'gif', 'jpg', 'jpeg', 'jpe', 'jfif', 'png', 'ico'];
                if(fileName){
                    var parts = fileName.split('.');
                    if(parts.length > 1){
                        var ind = imageExtensions.indexOf(parts[parts.length - 1]);
                        if(ind > -1){
                            return true;
                        }                    
                    }
                }
                return false;
            }
        };
    });
})(angular);
