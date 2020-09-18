"use strict"
import {revenirPageLogin , changerTextBienvenue , allerPageAccueilVisiteur} from "./loginPage.js";
import {getData} from "./ApiUtils.js";
const API_NAME_TOKEN = "/utilisateur/verification"

$(document).ready( function(){
	cacherDevis();
	afficherListeDevisClient();
    if(localStorage.getItem("token")){
        const tokenAVerifier = localStorage.getItem("token");
        getData(API_NAME_TOKEN ,tokenAVerifier , onTokenVerifie , onTokenIncorrecte);
    }
    else{
        $("#header_accueil_visiteur").show();
        $("#header_accueil_ouvrier").hide();
        $("#header_accueil_client").hide();
    }
    $(".logo").click(e =>{
        revenirPageAccueil();
    })

    $("#btn_accueil_login").click( e =>{
        revenirPageLogin();
    })

    $("#btn_accueil_inscription").click( e=>{
        revenirPageInscription();
    })

    $(".btn_logout").click( e =>{
        localStorage.removeItem("token");
        localStorage.removeItem("user.email");
        localStorage.removeItem("user.firstname");
        localStorage.removeItem("user.name");
        localStorage.removeItem("user.pseudo");
        allerPageAccueilVisiteur();
    })

    $("#menu_btn_introduire_devis").click( e =>{
        $('body > :not(#header_accueil_ouvrier)').hide();
        $("#page_introduire_devis").show();
    })
     $("#menu_btn_administrer_utilisateur").click(e => {
	    //$("#liste_utilisateur").show();
    })

})

//function qui cache l'affichage des devis
function cacherDevis(){
    $("#liste_devis_client").hide();
}

function afficherListeDevisClient(){
	$('body > :not(#page_accueil):not(#header_accueil_ouvrier)').hide();
	$("#liste_devis_client").show();
}


function revenirPageInscription(){
    $('body > :not(#page_inscription)').hide();
    $("#page_inscription").show();
}

function cacherTout(){ //simplifie le changement de page
    $('body > :not(#page_accueil):not(#header_accueil_ouvrier)').hide();
    $('body > :not(#header_accueil_ouvrier)').hide();
    $("#header_accueil_visiteur").hide();
    $("#header_accueil_ouvrier").hide();
    $("#liste_devis_client").hide();
}

function onTokenVerifie(response){
    console.log("verification du token reussi , action permise")
    switch(response.autorisation){
        case "O" :{
            $("#header_accueil_ouvrier").show();
            $("#header_accueil_visiteur").hide();
            $("#header_accueil_client").hide();
            break;
        }
        case "C" :{
            $("#header_accueil_client").show();
            $("#header_accueil_ouvrier").hide();
            $("#header_accueil_visiteur").hide();
            break;
        }
    }
    changerTextBienvenue();
}

function revenirPageAccueil(){
    $('body > :not(#page_accueil):not(#header_accueil_ouvrier):not(#header_accueil_client):not(#header_accueil_visiteur)').hide();
    $("#page_devis_client").hide();
    console.log($("#page_devis_client"))
    console.log("tentative")
    $("#page_accueil").show();
    
}

function onTokenIncorrecte(response){
    console.log("token incorrecte , action impossible")
    $("#header_accueil_visiteur").show();
    $("#header_accueil_ouvrier").hide();
    localStorage.removeItem("token");
    localStorage.removeItem("user.email");
    localStorage.removeItem("user.firstname");
    localStorage.removeItem("user.name");
    localStorage.removeItem("user.pseudo");
}

export {revenirPageInscription, afficherListeDevisClient};