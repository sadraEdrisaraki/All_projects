"use strict"
import {getData} from "./ApiUtils.js";
import {postData} from "./ApiUtils.js";

const API_NAME = "/amenagement";
const API_CLIENT_NAME = "/client";
var tabTypeAmenagement = new Array();
var tabAmenagementDejaUtilise = new Array();
var tabClient = new Array();
var mailClient = null;
let nbr_liste_choisi = 0;




$(document).ready(function(){
    $("#menu_btn_introduire_devis").click(e =>{
        // Si premiere fois que l'on introduit un devis
        if(tabTypeAmenagement.length == 0){
            recupererListeAmenagement();
            
        }
        gererAutoCompletion();
    })
    verifierMail();
    
    //$("#ajout_de_text").append("<input type='text' id='input_nom_amenagement' />");
    let test = document.getElementById("input_nom_amenagement");
    //test.addEventListener('change',envoitNouveauAmenagement);
    
    $("#btn_nouveau_amenagement").click(envoitNouveauAmenagement);
})

function recupererListeAmenagement(){
    tabAmenagementDejaUtilise = new Array();
    tabTypeAmenagement = new Array();
    getData(API_NAME, null , onRecupereListeAmenagement , onErrorRecuperationListeAmenagement);
}

function onRecupereListeAmenagement(response){
    tabTypeAmenagement = response;
    construireListe(); 
    
    console.log("liste d'aménagement récupéré");
}

function onErrorRecuperationListeAmenagement(){
    console.log("erreur lors de la récupération de la liste des aménagement");
}

// Créer les listes d'aménagements pour l'introduction du devis
function construireListe(){
    nbr_liste_choisi++;
    let select = document.createElement('select');
    select.className="select_type_amenagement";
    let nomId = "select_type_amenagement_" + nbr_liste_choisi;
    select.id=nomId;
    // Créer l'option par défaut
    let optionDefault = document.createElement('option');
    optionDefault.setAttribute('value' , 0);
    optionDefault.innerHTML = "--sélectionnez un type d'aménagement--";
    optionDefault.style.color = "grey";
    optionDefault.opacity = "0.8";
    select.appendChild(optionDefault);

    // Rajoute les types à la liste
    tabTypeAmenagement.forEach(element => {
        let option = document.createElement('option');
        option.setAttribute('value' , element.id_amenagement);
        option.innerHTML = element.nom;
        select.appendChild(option);
    });
    document.getElementById("form_introduire_devis").insertBefore(select , document.getElementById("breakpoint"));
    // re-créer une liste de type d'aménagement à chaque fois que l'utilisateur en choisi une
    select.addEventListener('change' , construireListeSuivante);
}

function construireListeSuivante(){
    nbr_liste_choisi++;
    let select_previous_name = "select_type_amenagement_"+(nbr_liste_choisi-1);

    let select = document.createElement('select');
    select.className="select_type_amenagement";
    let nomId = "select_type_amenagement_" + nbr_liste_choisi;
    select.id=nomId;

    // Créer l'option par défaut
    let optionDefault = document.createElement('option');
    optionDefault.setAttribute('value' , 0);
    optionDefault.innerHTML = "--sélectionnez un type d'aménagement--";
    optionDefault.style.color = "grey";
    optionDefault.opacity = "0.8";
    select.appendChild(optionDefault);

    // On retire les éléments déjà séléctionné
    let id_selectionne = document.getElementById(select_previous_name).selectedIndex;
    tabAmenagementDejaUtilise.push(id_selectionne-1);
    tabTypeAmenagement.splice(id_selectionne-1 , 1);
    tabTypeAmenagement.forEach(element => {
        let option = document.createElement('option');
        option.setAttribute('value' , element.id_amenagement);
        option.innerHTML = element.nom;
        select.appendChild(option);
    });
    //
    

    document.getElementById("form_introduire_devis").insertBefore(select , document.getElementById("breakpoint"));
    document.getElementById(select_previous_name).removeEventListener('change' , construireListeSuivante);
    select.addEventListener('change' , construireListeSuivante);

}

function gererAutoCompletion(){
    console.log("Recupere la liste des clients");
    getData(API_CLIENT_NAME , localStorage.getItem("token") , onAutoCompletion , onAutoCompletionError);
}
function onAutoCompletion(response){
    let dataliste = document.getElementById("liste_client");
    for(let i = 0 ; i < response.length ; i++){
        let option = document.createElement('option');
        option.setAttribute('value' , response[i].nom);
        let nomListe = response[i].nom +" " + response[i].prenom + " (" + response[i].email + ")"; 
        option.innerHTML = nomListe;
        option.id = "client_" + i;
        option.className = "client_auto_completion";
        dataliste.appendChild(option);
        tabClient.push(response[i].email);
    }
}

function onAutoCompletionError(response){
    console.log("erreur lors de la récupération de la liste des clients")
}

function selectionnerClient(){
    var options = $("#liste_client")[0].options;
    var val = $("#introduction_devis_nom").val();
    for(var i = 0 ; i < options.length ; i++){
        if(options[i].value === val){
            console.log(options[i].value + " a été choisi");
            console.log("son email : " + tabClient[i]);
            mailClient = tabClient[i];
            break;
        }
    }
}

function verifierMail(){

    var clientNom = document.getElementById("introduction_devis_nom");
    if($("#email_client_ajout_client").val() != ""){
        clientNom.removeEventListener("change" , selectionnerClient);
        mailClient = $("#email_client_ajout_client").val();
    }
    else{
        clientNom.addEventListener("change", selectionnerClient);
    }

}
function envoitNouveauAmenagement(){
    let element = document.getElementById("input_nom_amenagement").value;
    if(element.length==0){
        return;
    }
        
    
    console.log(element);

    let data ={nom:element};
    postData(API_NAME,data,localStorage.getItem("token"),onEnvoitNouveauAmenagement,onErrorEnvoitNouveauAmenagement);
    $("#ajout_de_text").empty();
    $("#ajout_de_text").append("<input type='text' id='input_nom_amenagement'/><button id='btn_nouveau_amenagement' class='btn_form'>+</button>");
    $("#btn_nouveau_amenagement").click(envoitNouveauAmenagement);
}
function onEnvoitNouveauAmenagement(){
    console.log("envoit réussit");
    recupererListeAmenagementApresNouveauAmenagement()
}
function onErrorEnvoitNouveauAmenagement(){
    console.log("envoit aménagment rater");
}
function recupererListeAmenagementApresNouveauAmenagement(){
    getData(API_NAME, localStorage.getItem("token") , onRecupereListeAmenagementApresNouveauAmenagement , onErrorRecuperationListeAmenagement);
}

function onRecupereListeAmenagementApresNouveauAmenagement(response){
    tabTypeAmenagement = response;
    construireListeSuivanteApresNouveauAmenagement();
    
    console.log("liste d'aménagement récupéré");
}
function construireListeSuivanteApresNouveauAmenagement(){
    
    

    let select = document.getElementById("select_type_amenagement_"+nbr_liste_choisi);
    let element = tabTypeAmenagement[tabTypeAmenagement.length-1];
    console.log(element);
    let option = document.createElement('option');
    option.setAttribute('value' , element.id_amenagement);
    option.setAttribute('selected',"selected");
    option.innerHTML = element.nom;
    select.appendChild(option);
    tabAmenagementDejaUtilise.forEach(el=>{
        tabTypeAmenagement.splice(el,1);
    })
    
    

    construireListeSuivante();

}


export{construireListe, nbr_liste_choisi , mailClient ,verifierMail, gererAutoCompletion , selectionnerClient , recupererListeAmenagement};



