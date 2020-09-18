"use strict"
import {getData} from "./ApiUtils.js";
const API_NAME_CAROUSEL = "/image";
const API_NAME_AMENAGEMENT = "/amenagement"
var tabAmenagement = new Array();

getData(API_NAME_AMENAGEMENT , null , onGetAmenagementList , onErrorGetAmenagementList);



function onGetImageList(response){
    console.log("récupération des images")

    if(response.length > 0){
        clearCarousel();

        let url;
        for(let j = 0; j < response.length; j++) {
            url = response[j].photo;
            let amenagement = tabAmenagement.find( amenagement => amenagement.id_amenagement ===response[j].id_amenagement);
            console.log(amenagement)
            let div = '<div class="carousel-item"><img src="' + url + '" id="'+"photo" +response[j].id_photo+'" width="1000px"> '+
            '<div class="carousel-caption d-none d-md-block"><h5>' + amenagement.nom + '</h5>'
            +
            '  </div>'
            $(div).appendTo('.carousel-inner');
            let target = '<li data-target="#carousel" data-slide-to="'+j+'"></li>';
            $(target).appendTo('.carousel-indicators')
        }
        $('.carousel-item').first().addClass('active');
        $('.carousel-indicators > li').first().addClass('active');
        $('#carousel').carousel();
    }
    else{
        console.log("pas de photo pour ce type");
        $("#msg_error_no_photo").show()
        $("#msg_error_no_photo").fadeTo(0.5 , 0.8)
        setTimeout(function(){
            $("#msg_error_no_photo").fadeTo(0.5 , 0)
        } , 2000);
    }
    

    

}

function onErrorGetImageList(response){
    console.log("récupération des images échouée");
}

function clearCarousel(){
    var myNode = document.getElementById("indicateur");
    while (myNode.firstChild) {
        myNode.removeChild(myNode.lastChild);
    }

    myNode = document.getElementById("inner");
    while (myNode.firstChild) {
        myNode.removeChild(myNode.lastChild);
    }
}

function onGetAmenagementList(response){
    tabAmenagement = response;
    getData(API_NAME_CAROUSEL , null , onGetImageList , onErrorGetImageList);
}

function onErrorGetAmenagementList(){
    console.log("erreur lors de la récupération de la liste d'aménagement pour le carrousel");
}

export {onGetImageList , onErrorGetImageList}