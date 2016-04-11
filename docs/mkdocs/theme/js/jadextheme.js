   var navbarLoaded2 = function(data) {
      var acpath = "https://actoron.com/acmaintest/"
      var acindexpage = acpath + "index.html#/"

//      var $topbar = $("#topbar");
      var $topbar = $(data)
      $topbar.find("a[href^=#]").each(function (index, element) {
          element.href = acindexpage + element.attributes.href.value.substring(1);
        }
      )

      $topbar.find("a[href]:not([href^=http])").each(function (index, element) {
        if (element.attributes.href.value == "index.html#/") {
          element.href = acindexpage;
        } else {
          element.href = acindexpage + element.attributes.href.value.substring(1);
        }
      });

      $topbar.find("img[src]:not([src^=http])").each(function (index, element) {
        element.src = acpath + element.attributes.src.value;
      });

      var docsdropdownli = $topbar.find('li:has(> a[id=docs])');
      docsdropdownli.addClass('active');
      var manualsli = docsdropdownli.find('ul > li:has(> a:contains("Manuals"))');
      manualsli.addClass('active');



      return $topbar;
    };


  var xdLoader = new XDLoader('https://actoron.com/acmaintest/');
  xdLoader.scheduleLoad['topnavbar.html'] = function (data) {
    var element = navbarLoaded2(data);
    $('#topNavBar').html(element);
  };

  xdLoader.scheduleLoad['bottomnavbar.html'] = function (data) {
      var element = navbarLoaded2(data);
      $('#bottomNavBar').html(element);
  };

  xdLoader.finish = function() {
  	var stickyHeaderTop = $('#stickyheader').offset().top;

	$(window).scroll(function(){
	  if( $(window).scrollTop() > stickyHeaderTop ) {
			  $('#stickyheader').css({position: 'fixed', top: '0px'}); //marginLeft: '5%'
			  <!--$('#stickyalias').css('display', 'block');-->
	  } else {
			  $('#stickyheader').css({position: 'absolute', top: '0px', marginLeft: 'unset'});
			  <!--$('#stickyalias').css('display', 'none');-->
	  }
	});
  }

  xdLoader.execute();



//  var app = angular.module('doc', [])
//  app.config(function($sceDelegateProvider) {
//      $sceDelegateProvider.resourceUrlWhitelist([
//          'http://actoron.com/**',
//          '**',
//          'topnavbar.html',
//      ]);
//  });
//
//  function docController($scope) {
//    $scope.navbarLoaded = navbarLoaded;
//  }
//
//  app.controller('docController', [ '$scope', docController]);