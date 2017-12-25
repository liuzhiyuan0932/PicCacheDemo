function isBlank( txt )
{
    if ( txt == null || txt == "" )  {
        return true;
    }
    var spaceCount = 0;
    for( i = 0; i < txt.length; i++ ) {
        if( (txt.substring(i,i + 1) == " ") || (txt.substring(i,i + 1) == "\u3000") ) {
            spaceCount++;
        }
    }
    if( txt.length == spaceCount ) {
        return true;
    } else {
        return false;
    }
}

 function jsAllTerm(str)   
 {
   return str.replace(/[ \t]+/g,"");
 }
 

  function jsTerm(str)
  {
    return str.replace(/(^[ \t]+)|([ \t]+$)/g,"");
  }  
 
 
  function getPageSize(){
    var xScroll, yScroll;
    if (window.innerHeight && window.scrollMaxY) {
      xScroll = document.body.scrollWidth;
      yScroll = window.innerHeight + window.scrollMaxY;
    }else if(document.body.scrollHeight > document.body.offsetHeight){ // all but Explorer Mac
      xScroll = document.body.scrollWidth;
      yScroll = document.body.scrollHeight;
    }else{ // Explorer Mac...would also work in Explorer 6 Strict, Mozilla and Safari
      xScroll = document.body.offsetWidth;
      yScroll = document.body.offsetHeight;
    }
    var windowWidth, windowHeight;
    if(self.innerHeight){ // all except Explorer
      windowWidth = self.innerWidth;
      windowHeight = self.innerHeight;
    }else if(document.documentElement && document.documentElement.clientHeight){ // Explorer 6 Strict Mode
      windowWidth = document.documentElement.clientWidth;
      windowHeight = document.documentElement.clientHeight;
    }else if(document.body){ // other Explorers
      windowWidth = document.body.clientWidth;
      windowHeight = document.body.clientHeight;
    }
    // for small pages with total height less then height of the viewport
    if(yScroll < windowHeight){
      pageHeight = windowHeight;
    }else{
      pageHeight = yScroll;
    }
    // for small pages with total width less then width of the viewport
    if(xScroll < windowWidth){
      pageWidth = windowWidth;
    }else{
      pageWidth = xScroll;
    }
    arrayPageSize = new Array(pageWidth,pageHeight,windowWidth,windowHeight)
    return arrayPageSize;
  }
  

  function getObjWidth(obj){
    return(parseInt(obj.offsetWidth));  
    //return(parseInt(obj.clientWidth));
  }

  function getObjHeight(obj){
    return(parseInt(obj.offsetHeight));  
    //return(parseInt(obj.clientHeight));
  }
  
  function G_Invalid(event){
    var e = window.event || event;
    //alert(e);
    e.k = e.keyCode || e.which;
    //alert(k);
    if((e.k > 113  && e.k < 123) ||             /* fn key press */
       (e.k == 27) ||                         /* esc key press */
       (e.k == 36)) {                         /* Home key press */
         if(window.event){
           window.event.keyCode = 0;
           window.event.returnValue  = false;
         }else{
             e.k = 0;
             event.preventDefault();
         }        
    }
    if(e.ctrlKey == true) {                /* ctrl key press */
      if((e.k == 78) ||                      /* n key press */
         (e.k == 82)) {                      /* r key press */
          if(window.event){
            window.event.keyCode = 0;
            window.event.returnValue  = false;
          }else{
              e.k = 0;
              event.preventDefault();
          }        
      }
    }
    if(e.k == 13){
    }
  }
  
    function fckInsertHTML(fckName, htmlCode){
        // Get the editor instance that we want to interact with.
        var oEditor = CKEDITOR.instances[fckName];
        // Check the active editing mode.
        if( oEditor.mode == "wysiwyg" ){
            // Insert the desired HTML.
            oEditor.insertHtml(htmlCode) ;
        }else{
            alert( 'You must be on WYSIWYG mode!' ) ;
          }
        }

    function fckGetLength(id){
        // This functions shows that you can interact directly with the editor area
        // DOM. In this way you have the freedom to do anything you want with it.
         // Get the editor instance that we want to interact with.
         var oEditor = FCKeditorAPI.GetInstance(id) ;
         // Get the Editor Area DOM (Document object).
         var oDOM = oEditor.EditorDocument ;
        var iLength ;
         // The are two diffent ways to get the text (without HTML markups).
         // It is browser specific.
         if (document.all){// If Internet Explorer.
             iLength = oDOM.body.innerText.length ;
        }else{ // If Gecko.
             var r = oDOM.createRange() ;
            r.selectNodeContents( oDOM.body ) ;
             iLength = r.toString().length ;
        }
        return iLength;
    }
    

    function ExecuteCommand(fckName, commandName ){
        // Get the editor instance that we want to interact with.
        var oEditor = FCKeditorAPI.GetInstance(fckName) ;
        // Execute the command.
      oEditor.Commands.GetCommand( commandName ).Execute() ;
    }
    
                   
     function f_loadjs(scriptid,jsurl){
       var scriptTag = document.getElementById(scriptid);
       if(scriptTag) document.body.removeChild(scriptTag);
       script = document.createElement('script');
       script.setAttribute( "src" , jsurl);
       script.type = 'text/javascript';
       script.id = scriptid;
       document.body.appendChild(script);
     } 
     
     
    // the article's name can't end with a bar adds a digital(eg: xxxx-3 is invalid)
    function isEndWithRailAndNumber(name){
         var pattern = /^.*-\d+$/;
        var xx = pattern.test(name);
        if(xx){
            return true;
        }
        return false;
    } 
     
    // avoid repetition submission 
    var isCommited = false;
    function checkCommit(){
        if(isCommited == false){
            isCommited = true;
            return true;
        }else{
            alert("You have committed , please waitting ...... ");
            return false;
        }
    }
    
    /**
     * get a string's real length
     * if it containt some chinese character, we think the length of a chinese character is 3, 
     * although sometimes it's length is 2 
     */
       function getRealLength(str){
           if(str == ""){
               return 0;
           }
        str =  str.replace(/(^\s*)|(\s*$)/g,"");  // remove blank space
        str = str.replace(/[\u4e00-\u9fa5]/g, "chs");  // one Chinese character's length is 3
        return str.length;        
    } 
     
    // remove blank space 
    function trimStr(str){
        return str.replace(/(^\s*)|(\s*$)/g,"");    
    }
  
  
    folderUnVal = function(path){
      if(path.indexOf("/") == 0){
          return false;
      }else{
          return true;
      }  
  }
  
  function createXMLHttpUtil(){
    if (window.XMLHttpRequest) {
       return new XMLHttpRequest();                  
    } else {
       return new ActiveXObject("Microsoft.XMLHTTP");
    }
  }

   // "str" whether end with "end", case not sensitive
   isEndWith = function(str, end){
          str = trimStr(str); 
          end = trimStr(end);
          if(str == "" || end == "") {
                  return false; 
          }
          str = str.toLowerCase();
          end = end.toLowerCase();
          if(-1 == str.lastIndexOf(end)){  //  "str" not contain "end"
                  return false;   
          }
          if(str.length == (str.lastIndexOf(end) + end.length)) {  // end with "end"
                  return true;   
          }else{
                  return false;
          }
   }
   
    // check the images name
   function isValidImageName(imgName){
          imgName = trimStr(imgName);
          imgName = imgName.toLowerCase();
           if((isEndWith(imgName, ".jpg") && imgName != ".jpg" ) || 
              (isEndWith(imgName, ".gif") && imgName != ".gif" ) || 
              (isEndWith(imgName, ".png") && imgName != ".png" ) || 
              (isEndWith(imgName, ".bmp") && imgName != ".bmp" ) || 
              (isEndWith(imgName, ".jpeg") && imgName != ".jpeg" ) ){
               return     true;
           }
           return false;
   }
    
   function checkImagesName(){
        var res = true;
        var imgNum = document.getElementsByName("imageName").length;
        for(var i = 0; i < imgNum; i++){
            var temp = $("imageName[" + i + "]").value;
            if(isBlank(temp)){
                continue;
            }
            if(!isValidImageName(temp)){
                res = false;
                alert("\u975E\u6CD5\u7684\u56FE\u7247\u540D\u79F0\uFF1A" + temp + "\n" + "\u56FE\u7247\u540D\u79F0\u540E\u7F00\u53EA\u80FD\u4E3A\u4EE5\u4E0B\u4E94\u79CD\uFF1A" + ".jpg, .jpeg, .gif, .png, bmp !");
                break;
            }
        }
        return res;    
    }

    function templateTypeChangeCheck(){
        var currType = $("type").value;
        if(flag){
            if(currType != "TOPIC_CONTENT_T"){
                alert("Forbids to revise the special home page template for other type template! ");
                $("type").value = "TOPIC_CONTENT_T";
            }
        }else{
            if(currType == "TOPIC_CONTENT_T"){
                alert("Forbids to revise the other type template for special home page template! ");
                $("type").value = "NEWS_LAYOUT_T";    
            }
        }
    }

  // show upload image in fckEditor	
  showUpLoadImage = function(imageType, imageSePath, uii, imgWidth, imgHeight, align, toOri){  
    var inserted = "";
    inserted += "<img border='0' src=" + "'" + imageSePath + "' ";
    if(imgWidth != 0){
      inserted += "width='" + imgWidth + "' ";
    }
    if(imgHeight != 0){
      inserted += "height='" + imgHeight + "' ";
    }
    if(align != ''){
      inserted += "align='" + align + "' ";
    }
    inserted += "alt='" + uii + "' title='"+ uii + "'/>";
    if(toOri == 1){
      inserted = "<a href='" + imageSePath + "' target='_blank'>" + inserted + "</a>";
    }
    fckInsertHTML("content", inserted);
  }
