btoa('Basic JS compatibility check.');

var defroute='/platforms';

var noscriptelement = document.getElementById("noscriptcontent");
noscriptelement.parentElement.removeChild(noscriptelement);

var app = angular.module('acmain', ['ngRoute', 'acplatforms']);
var mobile=detectmobile();

app.config(function($routeProvider)
{
    //$locationProvider.html5Mode(true);
     $routeProvider
     	.when('/', {
     		redirectTo: defroute
     	})
     	.when('/platforms', {
     		templateUrl: 'partials/platforms.html'
     	})
     	.when('/contact', {
     		templateUrl: 'partials/contact.html'
     	})
     	.otherwise({redirectTo: '/'});
});

app.run(function($rootScope, $location, $anchorScroll, $routeParams) {
	$rootScope.$on('$routeChangeSuccess', function(event, current, previous) {
		papp=null;
		$location.hash($routeParams.scrollToId);
//		$anchorScroll();
	});
});

app.directive('routebutton', function($location) {
	return {
		link: function(scope, element, attrs) {
			setNavButtonActiveState(element, $location.path());
			scope.$on('$routeChangeSuccess', function(){
				setNavButtonActiveState(element, $location.path());
			});
		}
	}
});

app.directive('partialize', function($http) {
	return{
		restrict:'E',
		link:function(scope, element, attrs){
			$http.get(attrs.src).then(function successCallback(response){
				var lcontent	= response.data.toLowerCase();
				var content	= '<div' + response.data.substring(lcontent.indexOf('<body') + 5, lcontent.indexOf('</body>')) + '</div>';
				element.html(content);
			}, function errorCallback(response){
				alert('Partialize $http failed: '+response.status);
			});
		}
	}
});

function getElementContent(element,attrs){
	var $injector = angular.element('body').injector();
	var $q=$injector.get('$q');
	var $http=$injector.get('$http');
	var defer=$q.defer();
	if ('src' in attrs){
		$http.get(attrs.src).then(function successCallback(response) {
				defer.resolve(response.data);
			}, function errorCallback(response) {
				defer.reject(response);
			});
	}else
		defer.resolve(element[0].innerHTML);
	return defer.promise;
}

function generateNewsMetaInfo(encinfos){
	var metainfo=new Object();
	metainfo.nln=false;
	metainfo.lang='none';
	if(encinfos){
		encinfos=encinfos.substring(6).split(',');
		for(var i=0;i<encinfos.length;++i){
			var val=encinfos[i].trim();
			if(val.indexOf('=')!=-1){
				var keyval=val.split('=');
				metainfo[keyval[0].trim()]=keyval[1].trim();
			}
			else if(val=='nln')
				metainfo.nln=true;
		}
	}
	return metainfo;
}

function setNavButtonActiveState(element, locpath){
	var navelem=element[0].getElementsByTagName('a')[0];
	var bname='';
	if (navelem.hasAttribute('href'))
		bname=navelem.getAttribute('href').substring(1);
	else if (navelem.hasAttribute('id'))
		bname=navelem.getAttribute('id');
	else return;
	locpath=locpath.substring(1);
	if(locpath.length==0)
		locpath=defroute;
	var active=(bname==locpath);
	while(!active && locpath.lastIndexOf('/')!=-1){
		locpath = locpath.substring(0,locpath.lastIndexOf('/'));
		active=(bname==locpath);
	}
	
	if (active)
		element[0].classList.add('active');
	else
		element[0].classList.remove('active');
}

function detectmobile() {
	var check = false;
	(function(a){if(/(android|bb\d+|meego).+mobile|avantgo|bada\/|blackberry|blazer|compal|elaine|fennec|hiptop|iemobile|ip(hone|od)|iris|kindle|lge |maemo|midp|mmp|mobile.+firefox|netfront|opera m(ob|in)i|palm( os)?|phone|p(ixi|re)\/|plucker|pocket|psp|series(4|6)0|symbian|treo|up\.(browser|link)|vodafone|wap|windows ce|xda|xiino/i.test(a)||/1207|6310|6590|3gso|4thp|50[1-6]i|770s|802s|a wa|abac|ac(er|oo|s\-)|ai(ko|rn)|al(av|ca|co)|amoi|an(ex|ny|yw)|aptu|ar(ch|go)|as(te|us)|attw|au(di|\-m|r |s )|avan|be(ck|ll|nq)|bi(lb|rd)|bl(ac|az)|br(e|v)w|bumb|bw\-(n|u)|c55\/|capi|ccwa|cdm\-|cell|chtm|cldc|cmd\-|co(mp|nd)|craw|da(it|ll|ng)|dbte|dc\-s|devi|dica|dmob|do(c|p)o|ds(12|\-d)|el(49|ai)|em(l2|ul)|er(ic|k0)|esl8|ez([4-7]0|os|wa|ze)|fetc|fly(\-|_)|g1 u|g560|gene|gf\-5|g\-mo|go(\.w|od)|gr(ad|un)|haie|hcit|hd\-(m|p|t)|hei\-|hi(pt|ta)|hp( i|ip)|hs\-c|ht(c(\-| |_|a|g|p|s|t)|tp)|hu(aw|tc)|i\-(20|go|ma)|i230|iac( |\-|\/)|ibro|idea|ig01|ikom|im1k|inno|ipaq|iris|ja(t|v)a|jbro|jemu|jigs|kddi|keji|kgt( |\/)|klon|kpt |kwc\-|kyo(c|k)|le(no|xi)|lg( g|\/(k|l|u)|50|54|\-[a-w])|libw|lynx|m1\-w|m3ga|m50\/|ma(te|ui|xo)|mc(01|21|ca)|m\-cr|me(rc|ri)|mi(o8|oa|ts)|mmef|mo(01|02|bi|de|do|t(\-| |o|v)|zz)|mt(50|p1|v )|mwbp|mywa|n10[0-2]|n20[2-3]|n30(0|2)|n50(0|2|5)|n7(0(0|1)|10)|ne((c|m)\-|on|tf|wf|wg|wt)|nok(6|i)|nzph|o2im|op(ti|wv)|oran|owg1|p800|pan(a|d|t)|pdxg|pg(13|\-([1-8]|c))|phil|pire|pl(ay|uc)|pn\-2|po(ck|rt|se)|prox|psio|pt\-g|qa\-a|qc(07|12|21|32|60|\-[2-7]|i\-)|qtek|r380|r600|raks|rim9|ro(ve|zo)|s55\/|sa(ge|ma|mm|ms|ny|va)|sc(01|h\-|oo|p\-)|sdk\/|se(c(\-|0|1)|47|mc|nd|ri)|sgh\-|shar|sie(\-|m)|sk\-0|sl(45|id)|sm(al|ar|b3|it|t5)|so(ft|ny)|sp(01|h\-|v\-|v )|sy(01|mb)|t2(18|50)|t6(00|10|18)|ta(gt|lk)|tcl\-|tdg\-|tel(i|m)|tim\-|t\-mo|to(pl|sh)|ts(70|m\-|m3|m5)|tx\-9|up(\.b|g1|si)|utst|v400|v750|veri|vi(rg|te)|vk(40|5[0-3]|\-v)|vm40|voda|vulc|vx(52|53|60|61|70|80|81|83|85|98)|w3c(\-| )|webc|whit|wi(g |nc|nw)|wmlb|wonu|x700|yas\-|your|zeto|zte\-/i.test(a.substr(0,4)))check = true})(navigator.userAgent||navigator.vendor||window.opera);
	return check;
}

