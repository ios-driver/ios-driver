$(function() {
  $('#capabilities').click(function(){
  	$('#overlay').fadeIn('fast',function(){
      $('#box').animate({'left':'0px'},500);
    });
  });
    
  $('#boxclose').click(function(){
  	$('#box').animate({'left':'-2000px'},500,function(){
      $('#overlay').fadeOut('fast');
    });
  });
  
});