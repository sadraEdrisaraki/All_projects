"use strict";
import { postData } from "./ApiUtils.js";
import {revenirPageInscription} from "./accueilPage.js";

const API_NAME = "/utilisateur/login";
let token=undefined;

$(document).ready(function(){

	$("#btn_login").click( e =>{
        const data = {pseudo:$("#pseudo").val() , mot_de_passe:$("#mot_de_passe").val()};
        postData(API_NAME,data,token,onPostLogin,onErrorLogin);
    });

    $("#btn_logout").click(e=>{
        localStorage.removeItem("token");
        localStorage.removeItem("user.pseudo");
        localStorage.removeItem("user.name");
        localStorage.removeItem("user.firstname");
        localStorage.removeItem("user.email");
        revenirPageLogin();
    })

    $("#btn_login_to_inscription").click(e=>{
        revenirPageInscription();
    })

////////////////////////////////////// ANIMATION ///////////////////////////////////////
    
    function getRandomInt(min, max) {
      return Math.floor(Math.random() * (max - min)) + min;
    }
    
    function particlesInit() {
        var generator = document.getElementById("particleGenerator");
        var particleCount = 200;
        for (var i = 0; i < particleCount; i++) {
            var size = getRandomInt(2, 6);
            var n = '<div class="particle" style="top:' + getRandomInt(15, 95) + '%; left:' + getRandomInt(5,95) + '%; width:'
            + size + 'px; height:' + size + 'px; animation-delay:' + (getRandomInt(0,30)/10) + 's; background-color:rgba('
            + getRandomInt(80, 160) + ',' + getRandomInt(185, 255) + ',' + getRandomInt(160, 255) + ',' + (getRandomInt(2, 8)/10) + ');"></div>';
            var node = document.createElement("div");
            node.innerHTML = n;
            generator.appendChild(node);
        }
    }
    
    particlesInit();

////////////////////////////////////// /////////// ///////////////////////////////////////
    
});


// function Area

function onPostLogin(response) {
    //Mettre les champs vide:
    $("#pseudo").val("");
    $("#mot_de_passe").val("");

    // faire quelque chose quand l utilisateur est authentifie
    if (response.success === "true") {
        // enregistre le token dans le localstorage ainsi que les donnees
        localStorage.setItem("token", response.data.token)
        localStorage.setItem("user.pseudo", response.data.user.pseudo);
        localStorage.setItem("user.name", response.data.user.name);
        localStorage.setItem("user.firstname", response.data.user.firstname);
        localStorage.setItem("user.email", response.data.user.email);
        console.log("login reussi");
        $("#msg_accueil_client_bonjour").text("Bonjour " + response.data.user.pseudo)
        if(response.data.user.type == "C"){
            allerPageAccueilClient();
        }
        else if(response.data.user.type == "O"){
            allerPageAccueilOuvrier();
        }
        
    }
    // faire quelque chose quand l utilisateur n'est authentifie 
    else {
        console.log("login raté");
        $("#msg_error_login").show();
    }
    changerTextBienvenue();
};

// fonction effectuee apres un login en cas de succes de celui-ci
function onErrorLogin(response){
    // Erreur d'autorisation
    if(response.status == 409){
        console.log("error login : compte non activé");
        $("#msg_error_login").text("Inscription non confirmé, veuillez attendre que l'on confirme votre inscription");
        $("#msg_error_login").show();
    }
    else{
        console.log("error login : nom d'utilisateur ou mdp incorrecte");
        $("#msg_error_login").text("Données incorrectes");
        $("#msg_error_login").show();
    }
    
};

function allerPageAccueilOuvrier(){
    $('body > :not(#page_accueil):not(#header_accueil_ouvrier)').hide();
    $("#page_accueil , #header_accueil_ouvrier").show();
    return false;
}
function allerPageAccueilClient(){
    $('body > :not(#page_accueil):not(#header_accueil_client)').hide();
    $("#page_accueil , #header_accueil_client").show();
    return false;
}
function allerPageAccueilVisiteur(){
    $('body > :not(#page_accueil):not(#header_accueil_visiteur)').hide();
    $("#page_accueil ,  #header_accueil_visiteur").show();
    return false;
}

function revenirPageLogin(){
    $('body > :not(#page_login)').hide();
    $("#page_login").show();
    return false;
}


function changerTextBienvenue(){
    $(".msg_accueil_bonjour").html("Bonjour " + localStorage.getItem("user.pseudo"));
    console.log("chargement du nom : " + localStorage.getItem("user.pseudo"));
}
export {revenirPageLogin , changerTextBienvenue , allerPageAccueilClient , allerPageAccueilVisiteur};


