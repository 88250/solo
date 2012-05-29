<script type="text/javascript">
    (function () {
        var $groups =  $("a[rel=group]");
        if ($groups.length > 0) {
            if (document.createStyleSheet) {
                document.createStyleSheet("/plugins/fancybox/jquery.fancybox-1.3.4/jquery.fancybox-1.3.4.css");
            } else {
                $("head").append($("<link rel='stylesheet' href='/plugins/fancybox/jquery.fancybox-1.3.4/jquery.fancybox-1.3.4.css' type='text/css' charset='utf-8' />"));
            } 
            
            $.ajax({
                url: "/plugins/fancybox/jquery.fancybox-1.3.4/jquery.fancybox-1.3.4.pack.js",
                dataType: "script",
                cache: true,
                complete: function() {
                    $groups.fancybox({
                        'transitionIn'		: 'none',
                        'transitionOut'		: 'none',
                        'titlePosition' 	: 'over',
                        'titleFormat'		: function(title, currentArray, currentIndex, currentOpts) {
                            return '<span id="fancybox-title-over">Image ' + (currentIndex + 1) + ' / ' + currentArray.length + (title.length ? ' &nbsp; ' + title : '') + '</span>';
                        }
                    });
                }
            });
        }
    })();
</script>
