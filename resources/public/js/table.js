$(document).ready(function() {
    $('#example').DataTable({
        stateSave: true,
        'pagingType': 'full_numbers'
    });
});
function confirm_sig(sig_id){
    var id = sig_id.id;
    var conf = confirm('Replace signature?');
    if(conf){
        $('#'+id+'_box').show();
        $('#'+id+'_img').hide();
    }
}
function inIframe () {
    if(!(window.self !== window.top)){
        $('div#header_div_container, div#header_container, div#SnapABug_Button, div#footer, div#tabs_div_container').show();
    }
}
function insertAtCaret(areaId,text) {
    var text = text.split('|');
    var txtarea = document.getElementById(areaId);
    var scrollPos = txtarea.scrollTop;
    var strPos = 0;
    strPos = txtarea.selectionStart;
    var front = (txtarea.value).substr(0,strPos);
    var back = (txtarea.value).substring(strPos,txtarea.value.length);
    txtarea.value=front+text[0]+text[1]+back;
    strPos = strPos + text[0].length;
    txtarea.selectionStart = strPos;
    txtarea.selectionEnd = strPos;
    txtarea.focus();
    txtarea.scrollTop = scrollPos;
}
function confirm_img(img_id,file){
    var conf = confirm('Replace?');
    if(conf){
        $('#'+img_id+'_select').show();
        var preview = document.getElementById(img_id+'_thumb');
		if(file){
			preview.src = 'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAMgAAADICAIAAAAiOjnJAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAyJpVFh0WE1MOmNvbS5hZG9iZS54bXAAAAAAADw/eHBhY2tldCBiZWdpbj0i77u/IiBpZD0iVzVNME1wQ2VoaUh6cmVTek5UY3prYzlkIj8+IDx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IkFkb2JlIFhNUCBDb3JlIDUuMC1jMDYwIDYxLjEzNDc3NywgMjAxMC8wMi8xMi0xNzozMjowMCAgICAgICAgIj4gPHJkZjpSREYgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIj4gPHJkZjpEZXNjcmlwdGlvbiByZGY6YWJvdXQ9IiIgeG1sbnM6eG1wTU09Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC9tbS8iIHhtbG5zOnN0UmVmPSJodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvc1R5cGUvUmVzb3VyY2VSZWYjIiB4bWxuczp4bXA9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC8iIHhtcE1NOkRvY3VtZW50SUQ9InhtcC5kaWQ6NzA3MzhDNDI0MjlBMTFFMjlCOTdDRDNGRTM1MTAwQjUiIHhtcE1NOkluc3RhbmNlSUQ9InhtcC5paWQ6NzA3MzhDNDE0MjlBMTFFMjlCOTdDRDNGRTM1MTAwQjUiIHhtcDpDcmVhdG9yVG9vbD0iQWRvYmUgUGhvdG9zaG9wIENTNSBNYWNpbnRvc2giPiA8eG1wTU06RGVyaXZlZEZyb20gc3RSZWY6aW5zdGFuY2VJRD0ieG1wLmlpZDo0RTU3QjVDMTQyMjkxMUUyQkY0N0UwQ0IyRjFGN0U5QSIgc3RSZWY6ZG9jdW1lbnRJRD0ieG1wLmRpZDo0RTU3QjVDMjQyMjkxMUUyQkY0N0UwQ0IyRjFGN0U5QSIvPiA8L3JkZjpEZXNjcmlwdGlvbj4gPC9yZGY6UkRGPiA8L3g6eG1wbWV0YT4gPD94cGFja2V0IGVuZD0iciI/Pohpf/0AAAR5SURBVHja7NzLSltBHMDh5qioBMWN+AIu1aXvj2+g7gS3rpVG4wWTDg4Mp1ptNU37nznfb1ECvXByzpe5lTiaTCbfpL/dCCyBJbAElrsgsASWwJLAElgCSwJLYAksCSyBJbAksASWwJLAElgCSwJLYAksCSyBJbAksASWwJLAElgCSwJLYAksCSyBJbAksP5z8/l89FJ6AVagp9J1XX4k+QnlXyvl1X/x031vnZ0Ra1meTIVgLUtY+rU/+uaBCiwtOqEXT0asWMusk5OTGmeWdM0rKyvPz8/5ysfj8cHBwfr6el5XDWSu7CJi7y1sa3wG6VORVeWLv729PT8/f3x8HNTo1dU1EvTXK5EvMpOazWb5xd3d3dnZWbaVp0iwYu228nOKPIz1r62s3Iutp6engYxb1cDqP7Aq5se3s/l0Ok22Hh4ePjjEamb5VeWwXMWHvuhJE2K57GQrrbfu7+/fmxCbGc9CHzekXeGrR7WxsbGzs1PXLb65uUnzYH/Du7m5eXh4mPeJmV17+8TVugaqra2t/f39D/6rJOA1X15eZlhlRZ/HraOjo7W1tVaPHupbY5UVccxHUnYYb9dMeZOYp8i83kpr+fJnGlvUVwOrv1J59fCiXecHC/PyW+lF/wzCiBViVxj58/0p8Q2fbw3isC5yrZ5vgfX/a/J8C6wQO8f2zrfAirLkL7bKMr/qyRGsKCcUxVZay5eDCbC00HDV3vkWWCFGrPbOt8CKewZR9fkWWKFt1Xu+BVbcqj7fAituVZ9vgRV9w1jp+RZYoXeL9Z5vgRV6uKr3fAus0CNWvedbq57fstve3v4yhevr6zRW9W2dnp4eHx+XbyyCNdzxZnd3d29v7wu20l+5uLgosLKktIqPr8pUuCxSZQ3U/3Ffn1WV/+KrL3/X8qMfwPr79Z/9l3/A2gffDANruDNg+R7RIgje+7aPxftwzwgqEmDEqm+BtcgY86lD9oCCwVoir0VWRb/8+uR7/07AVRdYlW0LTIUadGAJLIElsCSwBJbAksASWAJLAktgCSwJLIElsCSwBJbAksASWAJLAktgCSwJLIElsCSwBJbAksASWAJLAktgCSwJLIElsCSwBJbAksASWAJLAktgCSwJLIElsCSwBJbAksASWAJLAktgCSwJLIH1J41GIw8vcqt1Xe50Or26uiqq0ov5fN7qs0lvLb3fSt9jZbC+v5Ru9BBGrPQeZ7NZ/51W9Ma7Gj/HA5kHk6qu6ypdAHR1kRrU6ipPgpXO9V1dN3qAq+DRS2D9u0Gr4WX7L981WEsftAY1J1b6NkeTySTs8jxfW9tnCl9oPB6nRX3eMIa9ORFh5ZuVN9vlDvJUPnXpnsT/pEWEpT/ZKhZeMQetoLDKzarlA/qP1wnxlwdGrHaGMbBauHFuRZW7wrLNRuq3vKyxNKDRCyyBJbAEFlgCS2AJLAksgSWwJLAElsCSwBJYAksCS2AJLAksgSWwJLAElsCSwBJYAksCS2AJLAksgSWwJLAElsCSwBJYAkv6XD8EGAD9LvkdGZgSPQAAAABJRU5ErkJggg==';
		}else{
			preview.src = 'data:image/gif;base64,R0lGODdhgACAAOMAAMzMzJaWlsXFxaOjo7e3t6qqqrGxsb6+vpycnAAAAAAAAAAAAAAAAAAAAAAAAAAAACwAAAAAgACAAAAE/hDISau9OOvNu/9gKI5kaZ5oqq5s675wLM90bd94ru987//AoHBILBqPyKRyyWw6n9CodEqtWq/YrHbL7Xq/4LB4TC6bz+i0es1uu9/wuHxOr9vv+Lx+z+/7/4CBgoOEhYaHiImKi2wCAwETAgUBAQUSjgEDAh+YkZOVl4+aXASUkBKPBgYBBAADCAQIAx6llBOpq62vsbNbBASTEwQGEgHEoMEdv8kAw8XHlsxb0hKluggCshSUv7YV1M2srtjaXdQHAQiXppsTB7KvBxbn6euU7VzS2QHtBbAIlijUajXv1CUE/CT4ixUwn0EAkwgCQPawWMUJ0iJOoOiFGbqGdROjVTSAAAGxgu5AbRTZhcAjgi8PHNgUUaUEdKoCyBP4EhUrmTRZ2dRi6lRRUJhGbeQnwNtSb0ctJcXHqKrVq1izat3KtavXr2DDih1LtqzZs2jTql3Ltq3bt3Djyp1Lt67du3jz6t3Lt6/fv4ADCx5MuPCMCAA7';
		}
    }
}
function hoverBox(box){
    $(box).find('.cross').each(function(){$(this).css('fill', '#fff')});
}
function leaveBox(box){
    $(box).find('.cross').each(function(){$(this).css('fill', 'rgb(255,222,124)')});
}
function string_search(val)
{
	var hits = $('span:contains("'+val.toUpperCase()+'")');
	$('fieldset br, fieldset input, fieldset span').hide();
	hits.each(function(){
		$("[name="+$(this).attr('name')+"]").show();
	});
}
function parseTime(s){
    var part = s.match(/(\d+):(\d+)(?:)(am|pm)?/i);
    var hh = parseInt(part[1], 10);
    var mm = parseInt(part[2], 10);
    var ap = part[3] ? part[3].toUpperCase() : null;
    if(ap == 'AM'){
        if(hh == 12){
            hh = 0;
        }
    }
    if(ap == 'PM'){
        if(hh != 12){
            hh += 12;
        }
    }
    return [hh, mm];
}
String.prototype.repeat = function(num)
{
    return new Array(num+1).join(this);
}
function add_helper(){
    console.log('click');
}
$(function(){
    inIframe();
	$(':input:not([readonly=readonly]).type_date').datepicker({showWeek:true,changeYear:true,changeMonth:true,inline: true,showOtherMonths: true,dayNamesMin: ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'], });$(':input:not(label).type_time').timepicker();
    $('input[value="Print"].grid_button').click(function(){
        setTimeout(function(){
            $('#loader').fadeOut();
        },20);
    });
	$('.tooltip').tooltip();
    $('.menu_option').click(function(){
        var a = $(this).find('a').attr('href');
        window.location.href= a;
    });
    $('.menu_option').mouseenter(function(){
        var a = $(this).find('a');
		a.css('color', '#FFDE7C');
    }).mouseleave(function(){
        var a = $(this).find('a');
		a.css('color', '#FFFFFF');
    });
    $(window).on("keydown keypress", function(e){
        var code = e.keyCode || e.which;
        if(code == 13 && !e.shiftKey){
            e.preventDefault();
            return false;
        }
    });
    var dates = $('input.type_date');
    var times = $('input.type_time');
    for (var i=0;i<dates.length;i++){
        var form_val = dates[i].value.split('-');
        dates[i].value = String(form_val[1])+'/'+String(form_val[2])+'/'+String(form_val[0]);
    }
    $('form').submit(function(){
        $('input[type=submit]').prop('readonly', 'readonly');
        $('input[type=submit]').val('Processing');
        $('input[type=submit]').css('background-color', '#D7D7E2');
        for(var i=0;i<dates.length;i++){
            form_val = dates[i].value.split('/');
            if(form_val.length == 3){
                dates[i].value = String(form_val[2])+'-'+String(form_val[0])+'-'+String(form_val[1]);
            }
        }
        for(var i=0;i<times.length;i++){
            s = times[i].value;
            if(s) {
                part = s.match(/(\d+):(\d+)(?:)(am|pm)?/i);
                hh = parseInt(part[1], 10);
                mm = part[2];
                ap = part[3] ? part[3].toUpperCase() : null;
                if(ap == 'AM'){
                    if(hh == 12){
                        hh = 0;
                    }
                }
                if(ap == 'PM'){
                    if(hh != 12){
                        hh += 12;
                    }
                }
                switch(String(hh).length){
                case 1:
                    times[i].value = '0'+hh+':'+mm+':00';
                    break;
                default:
                    times[i].value = hh+':'+mm+':00';
                    break;
                }
            } else {
                times[i].value = null;
            }
        }
    });
    $('.type_int').keyup(function(){
        var val = this.value;
        var min = this.min;
        var max = this.max;
        val = parseInt(val);
        min = parseInt(min);
        max = parseInt(max);
        if (val < min) this.value = String(min);
        if (val > max) this.value = String(max);
    });
    $('.type_phone').mask('(999) 999-9999');
    $('.type_phone_ext').mask('(999) 999-9999? x9999');
    $('.type_ssn').mask('999-99-9999');
    $('.type_1n').mask('9');
    $('.type_2n').mask('?99');
    $('.type_3n').mask('?999');
    $('.type_4n').mask('?9999');
    $('.type_5n').mask('?99999');
    $('.type_6n').mask('?999999');
    $('.type_7n').mask('?9999999');
    $('.type_8n').mask('?99999999');
    $('.type_9n').mask('?999999999');
    $('.type_date').mask('99/99/9999');
	$('div#body_div_container').css({'height': $(window).innerHeight()-126,'overflow':'auto'});
    window.onresize = function(event){
		$('div#body_div_container').css({'height': $(window).innerHeight()-126,'overflow':'auto'});
	}
	if($('#menu').html().length > 0){
		$('#menu').accordion({
			heightStyle:'content',
			collapsible:true,
			activate: function(event,ui) {
				localStorage.setItem("accIndex",$(this).accordion("option","active"));
			},
			active: parseInt(localStorage.getItem("accIndex"))
		});
	}
    $('#helper').accordion({
        heightStyle:'content',
        collapsible:true
    });
    $('#report').accordion({
        heightStyle:'content',
        collapsible:true
    });
	$('span#left.menu_control').click(function(){
		if($(this).attr('closed') == 1){
			var div = $('div#menu_container');
			$('span#info_left').hide();
			$('span#left.menu_control').css('pointer-events','none');
			$(this).animate({
				'padding-left':'50%'
			});
			setTimeout(function(){
				div.show().css('width', String($('span#left.menu_control').outerWidth()) + 'px').animate({
					'height':($(window).innerHeight()-$('div#header_container').outerHeight())+'px'
				});
				window.onresize=function(){
					div.css({
						'width':String($('span#left.menu_control').outerWidth()) + 'px',
						'height':($(window).innerHeight()-$('div#header_container').outerHeight())+'px'
					});
				}
			},400);
			setTimeout(function(){
				$('span#left.menu_control').css('pointer-events','');
			},800);
			$(this).attr('closed', 0);
		}else{
			var div = $('div#menu_container');
			$('span#left.menu_control').css('pointer-events','none');
			div.animate({'height':'0px'});
			setTimeout(function(){
				$('span#left.menu_control').animate({
					'padding-left':'5px'
				});
				div.hide();
			},400);
			setTimeout(function(){
				$('span#left.menu_control').css('pointer-events','');
				$('span#info_left').fadeIn();
			},800);
			$(this).attr('closed', 1);
		}
	});
	$('span#right.menu_control').click(function(){
		if($(this).attr('closed') == 1){
			var div = $('div#helper_container');
			$('span#info_right').hide();
			$('span#right.menu_control').css('pointer-events','none');
			$(this).animate({
				'padding-right':'50%'
			});
			setTimeout(function(){
				div.show().css('width', String($('span#right.menu_control').outerWidth()) + 'px').animate({
					'height':($(window).innerHeight()-$('div#header_container').outerHeight())+'px'
				});
				window.onresize=function(){
					div.css({
						'width':String($('span#right.menu_control').outerWidth()) + 'px',
						'height':($(window).innerHeight()-$('div#header_container').outerHeight())+'px'
					});
				}
			},400);
			setTimeout(function(){
				$('span#right.menu_control').css('pointer-events','');
			},800);
			$(this).attr('closed', 0);
		}else{
			var div = $('div#helper_container');
			$('span#right.menu_control').css('pointer-events','none');
			div.animate({'height':'0px'});
			setTimeout(function(){
				$('span#right.menu_control').animate({
					'padding-right':'5px'
				});
				div.hide();
			},400);
			setTimeout(function(){
				$('span#right.menu_control').css('pointer-events','');
				$('span#info_right').fadeIn();
			},800);
			$(this).attr('closed', 1);
		}
	});
});
