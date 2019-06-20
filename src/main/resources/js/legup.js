var Legup = {

    helpWindow : function(url){
        console.log("Opening window to " + url);
        window.open(url, "", "height=500px,width=400px");
    },

    setCheckedByClassName : function(className){
         var checkBoxes = document.getElementsByClassName(className);
         for(var i=0; i<checkBoxes.length; i++){
            checkBoxes[i].checked = true;
         }
    },

    setUncheckedByClassName : function(className){
         var checkBoxes = document.getElementsByClassName(className);
         for(var i=0; i<checkBoxes.length; i++){
            checkBoxes[i].checked = false;
         }
    }

};