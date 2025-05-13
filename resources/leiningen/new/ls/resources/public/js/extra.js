/* Display confirmation on clicking a delete button with the value 'Delete' */
$(document).ready(function(){
  $('.confirm').each(function(){
    var href = $(this).attr('href');
    $(this).click(function(){
      if(confirm("Esta usted seguro?")){
        window.location.href = href;
      } else {
        return false;
      }
    });
  });
});
