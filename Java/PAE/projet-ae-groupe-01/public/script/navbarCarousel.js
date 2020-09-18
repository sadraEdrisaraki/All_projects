"use strict"
import {postData, getData} from "./ApiUtils.js";
import {onGetImageList , onErrorGetImageList} from "./carouselComponent.js";
const API_NAME_IMAGE = "/carousel";
const API_NAME_AMENAGEMENT = "/amenagement"



$(document).ready(function(){
    
    getData(API_NAME_AMENAGEMENT ,null ,onGetAmenagementList , onErrorGetAmenagementList);
    
    
})

function onGetAmenagementList(response){
    console.log(response);
    creerNavBarCarousel(response);
}

function onErrorGetAmenagementList(response){
    console.log(response)
}

function creerNavBarCarousel(response){

    /*
        HTML A GENERER
        --------------

        <li>
            <label for="amenagement1">List Item 1
                <input type="checkbox" id="amenagement1" class="radio_nav_carousel" name="amenagement"/>
                <span class="check"></span>
            </label>
        </li>

    */
    console.log("create carousel nav bar")

    let navBar = document.getElementById("navbar_carousel")
    for(let i = 0 ; i < response.length ; i++){
        let li = document.createElement('li');
        let label = document.createElement('label');
        label.className = "label_nav";
        let item = 'amenagement'+i;
        $(label).attr('for' , item);
        $(label).text(response[i].nom);
        $(li).append(label);

        let input = document.createElement('input');
        $(input).attr('type' , 'radio');
        input.id = item;
        input.name = 'amenagement';
        input.className = "radio_nav_carousel";
        $(label).append(input);

        let span = document.createElement('span');
        span.className = 'check';
        $(label).append(span);

        $(navBar).append(li);

        label.addEventListener("change" , function(){
            console.log(response[i].id_amenagement);
            const data = {id:response[i].id_amenagement};
            postData(API_NAME_IMAGE , data , null ,onGetImageList , onErrorGetImageList);
        })



    }



    

}

