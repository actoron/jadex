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

  	var navMarginBottom = 56;

	var setStickyPositions = function (stickyHeaderTop) {
		  if( $(window).scrollTop() > stickyHeaderTop ) {
				  $('#stickyheader').css({position: 'fixed', top: '0px', bottom: navMarginBottom + 'px'});
		  } else {
		  		var h = $('#topbar').height()
				var visibleHeaderPx = (h - $(window).scrollTop())
			  	$('#stickyheader').css({position: 'absolute', top: '0px', marginLeft: 'unset', bottom: navMarginBottom + visibleHeaderPx + 'px'});
		  }
	}

  xdLoader.finish = function() {

  	var stickyHeaderTop = $('#stickyheader').offset().top;
//  	$('#stickyheader').css({position: 'fixed', top: '0px', bottom: navMarginBottom + 'px'});
	setStickyPositions(stickyHeaderTop);

	$(window).scroll(function () {
		setStickyPositions(stickyHeaderTop);
	});

	$(window).resize(function () {
		setStickyPositions(stickyHeaderTop);
	});
  }

  xdLoader.execute();

  var currentChapter = $("li .current");
  var searchHeader = $(".wy-side-nav-search");
  console.log(searchHeader.height());
  $("#stickyheader").scrollTop(currentChapter.offset().top-(searchHeader.height()+20));